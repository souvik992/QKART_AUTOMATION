package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;

public class QKART_Tests {

    static WebDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new ChromeDriver(capabilities);
        System.out.println("createDriver()");
    }

    @AfterSuite(alwaysRun = true)
    public void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType,
                    description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
    @Test(priority = 1, groups = {"Sanity_test"},
            description = "Verify registration happens correctly")
    @Parameters({"TC1_Username", "TC1_Password"})
    public void TestCase01(@Optional("testUser") String TC1_Username,
            @Optional("abc@123") String TC1_Password) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase1");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(TC1_Username, TC1_Password, true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");

        assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        // logStatus("End TestCase", "Test Case 1: Verify user Registration : ", status
        // ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase1");
    }


    /*
     * Verify that an existing user is not allowed to re-register on QKart
     */
    @Test(priority = 2, groups = {"Sanity_test"},
            description = "Verify re-registering an already registered user fails.")
    public void TestCase02() throws InterruptedException {
        Boolean status;

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User registration failed");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously registered user's
        // credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);

        // If status is true, then registration succeeded, else registration has failed. In this
        // case, registration failure means success
        Assert.assertFalse(status, "Reregistration with existing username error");
    }

    /*
     * Verify the functinality of the search text box
     */
    @Test(priority = 3, groups = {"Sanity_test"},
            description = "Verify the functionality of search text box")
    public void TestCase03() throws InterruptedException {
        // logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");
        // boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        boolean status = homePage.searchForProduct("YONEX");
        // if (!status) {
        // logStatus("TestCase 3", "Test Case Failure. Unable to search for given product", "FAIL");
        // //return false;
        // }

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        // if (searchResults.size() == 0) {
        // // logStatus("TestCase 3", "Test Case Failure. There were no results for the given search
        // string", "FAIL");
        // //return false;
        // }

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            // if (!elementText.toUpperCase().contains("YONEX")) {
            // logStatus("TestCase 3", "Test Case Failure. Test Results contains un-expected values:
            // " + elementText,
            // "FAIL");
            // //return false;
            // }
        }

        // logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        // if (status) {
        // logStatus("TestCase 3", "Test Case Failure. Invalid keyword returned results", "FAIL");
        // return false;
        // }

        // Verify no search results are found
        List<WebElement> SecondsearchResults = homePage.getSearchResults();
        // Check if search results are empty
        if (SecondsearchResults.size() == 0) {
            // Verify that the "no results found" message is displayed
            Assert.assertTrue(homePage.isNoResultFound(),
                    "Expected 'no results found' message to be displayed.");

            // Test case pass if no results are found
            Assert.assertTrue(true,
                    "Verified that no search results were found for the given text.");
        } else {
            // Fail the test if results were found
            Assert.fail("Test Case Fail. Expected: no results, actual: Results were available.");
        }

        // return true;
    }

    /*
     * Verify the presence of size chart and check if the size chart content is as expected
     */
    @Test(priority = 4, groups = {"Regression_Test"},
            description = "Verify the existence of size chart for certain items and validate contents of size chart")
    public void TestCase04() throws InterruptedException {
        // logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            if (result.verifySizeChartExists()) {
                // logStatus("Step Success", "Successfully validated presence of Size Chart Link",
                // "PASS");

                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
                // logStatus("Step Success", "Validated presence of drop down", status ? "PASS" :
                // "FAIL");

                // Open the size chart
                if (result.openSizechart()) {
                    // Verify if the size chart contents matches the expected values
                    if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody,
                            driver)) {
                        // logStatus("Step Success", "Successfully validated contents of Size Chart
                        // Link", "PASS");
                    } else {
                        // logStatus("Step Failure", "Failure while validating contents of Size
                        // Chart Link", "FAIL");
                        status = false;
                    }

                    // Close the size chart modal
                    status = result.closeSizeChart(driver);

                } else {
                    // logStatus("TestCase 4", "Test Case Fail. Failure to open Size Chart",
                    // "FAIL");
                    // return false;
                }

            } else {
                // logStatus("TestCase 4", "Test Case Fail. Size Chart Link does not exist",
                // "FAIL");
                // return false;
            }
        }
        // logStatus("TestCase 4", "End Test Case: Validated Size Chart Details", status ? "PASS" :
        // "FAIL");
        // return status;
    }

    /*
     * Verify the complete flow of checking out and placing order for products is working correctly
     */
    @Test(priority = 5, groups = {"Sanity_test"},
            description = "Verify that a new user can add multiple products in to the cart and Checkout")
    @Parameters({"TC5_ProductNameToSearchFor", "TC5_ProductNameToSearchFor2", "TC5_AddressDetails"})
    public void TestCase05(String TC5_ProductNameToSearchFor, String TC5_ProductNameToSearchFor2,
            String TC5_AddressDetails) throws InterruptedException {
        Boolean status;
        // logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 5", "Test Case Failure. Happy Flow Test Failed", "FAIL");
        // }

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Failed : ", status ? "PASS" :
        // "FAIL");
        // }

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor);
        homePage.addProductToCart(TC5_ProductNameToSearchFor);
        status = homePage.searchForProduct(TC5_ProductNameToSearchFor2);
        homePage.addProductToCart(TC5_ProductNameToSearchFor2);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(TC5_AddressDetails);
        checkoutPage.selectAddress(TC5_AddressDetails);

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        // logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" :
        // "FAIL");
        // return status;
    }

    /*
     * Verify the quantity of items in cart can be updated
     */
    @Test(priority = 6, groups = {"Regression_Test"},
            description = "Verify that the contents of the cart can be edited")
    @Parameters({"TC6_ProductNameToSearch1", "TC6_ProductNameToSearch2"})
    public void TestCase06(String TC6_ProductNameToSearch1, String TC6_ProductNameToSearch2)
            throws InterruptedException {
        Boolean status;
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User login failed");

        Thread.sleep(3000);

        homePage.navigateToHome();
        status = homePage.searchForProduct(TC6_ProductNameToSearch1);
        Assert.assertTrue(status, "Product search failed for: " + TC6_ProductNameToSearch1);
        homePage.addProductToCart(TC6_ProductNameToSearch1);

        status = homePage.searchForProduct(TC6_ProductNameToSearch2);
        Assert.assertTrue(status, "Product search failed for: " + TC6_ProductNameToSearch2);
        homePage.addProductToCart(TC6_ProductNameToSearch2);

        // Update product quantities
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearch1, 2);
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearch2, 0);
        homePage.changeProductQuantityinCart(TC6_ProductNameToSearch1, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.placeOrder();

        // Wait for the "Thank You" page
        WebDriverWait wait = new WebDriverWait(driver, 5);
        try {
            wait.until(
                    ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            Assert.fail("Error while placing order: " + e.getMessage());
        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "Order was not placed successfully");

        homePage.navigateToHome();
        homePage.PerformLogout();
    }


    @Test(priority = 7, groups = {"Sanity_test"},
            description = "Verify that insufficient balance error is thrown when the wallet balance is not enough")
    @Parameters({"TC7_ProductName", "TC7_Qty"})
    public void TestCase07(String TC7_ProductName, int TC7_Qty) throws InterruptedException {
        Boolean status;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "User registration failed");

        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User login failed");

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(TC7_ProductName);
        Assert.assertTrue(status, "Product search failed for: " + TC7_ProductName);
        homePage.addProductToCart(TC7_ProductName);

        homePage.changeProductQuantityinCart(TC7_ProductName, TC7_Qty);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(2000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        Assert.assertTrue(status, "Insufficient balance error message was not displayed");

        homePage.navigateToHome();
        homePage.PerformLogout();
    }

    @Test(priority = 8, groups = {"Regression_Test"},
            description = "Verify that a product added to a cart is available when a new tab is added")
    public void TestCase08() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 8",
        // "Test Case Failure. Verify that product added to cart is available when a new tab is
        // opened",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        // status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "EndTestCase", "TestCase08");

        // return status;
    }

    @Test(priority = 9, groups = {"Regression_Test"},
            description = "Verify that privacy policy and about us links are working fine")
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        // logStatus("Start TestCase",
        // "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        // if (!status) {
        // logStatus("TestCase 09",
        // "Test Case Failure. Verify that the Privacy Policy, About Us are displayed correctly ",
        // "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // }
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        // if (!status) {
        // logStatus("Step Failure", "User Perform Login Failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);

        // if (!status) {
        // logStatus("Step Failure", "Verifying parent page url didn't change on privacy policy link
        // click failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase09");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading =
                driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Privacy Policy page heading
        // failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        // if (!status) {
        // logStatus("Step Failure", "Verifying new tab opened has Terms Of Service page heading
        // failed", status ? "PASS" : "FAIL");
        // takeScreenshot(driver, "Failure", "TestCase9");
        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // status ? "PASS" : "FAIL");
        // }

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        // logStatus("End TestCase",
        // "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        // "PASS");
        // takeScreenshot(driver, "EndTestCase", "TestCase9");

        // return status;
    }

    @Test(priority = 10, groups = {"Regression_Test"},
            description = "Verify that the contact us dialog works fine")
    public void TestCase10() throws InterruptedException {
        // logStatus("Start TestCase",
        // "Test Case 10: Verify that contact us option is working correctly ",
        // "DONE");
        // takeScreenshot(driver, "StartTestCase", "TestCase10");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys("crio user");
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys("criouser@gmail.com");
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys("Testing the contact us page");

        WebElement contactUs = driver.findElement(By.xpath(
                "/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        // logStatus("End TestCase",
        // "Test Case 10: Verify that contact us option is working correctly ",
        // "PASS");

        // takeScreenshot(driver, "EndTestCase", "TestCase10");

        // return true;
    }

    @Test(priority = 11, groups = {"Sanity_test"},
            description = "Ensure that the Advertisement Links on the QKART page are clickable")
    public void TestCase11() throws InterruptedException {

        Boolean status;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status,
                "Test Case Failure. Ensure that the links on the QKART advertisement are clickable.");

        String lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        Assert.assertTrue(status, "User Perform Login Failed");
        Thread.sleep(5000);

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
        Assert.assertTrue(status, "Failed to search for product 'YONEX Smash Badminton Racquet'");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
        Thread.sleep(5000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> advertisements = driver.findElements(By.tagName("iframe"));
        Assert.assertEquals(advertisements.size(), 3, "Expected 3 advertisements to be available.");

        for(int i=0; i<2; i++){
            driver.switchTo().frame(i);
            WebElement buyNow = driver.findElement(By.xpath("//button[text()='Buy Now']"));
            buyNow.click();
            WebDriverWait wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.urlContains("/checkout"));
            String currentUrl = driver.getCurrentUrl();
            status = currentUrl.endsWith("/checkout");
            Assert.assertTrue(status, "Ads are not clickable");
            driver.navigate().back();
            driver.switchTo().parentFrame();
        }
    }
}

