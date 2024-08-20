package QKART_TESTNG;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ListenerClass implements ITestListener {
    public void onStart(ITestContext context) {
        System.out.println("onStart method started");
    }

    public void onFinish(ITestContext context) {
        System.out.println("onFinish method started");
    }

    public void onTestStart(ITestResult result) {
        System.out.println("New Test Started" + result.getName());
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println("OnTestSuccess" + result.getName());
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "ONTEST FAILURE", result.getName());
    }

    public void onTestFailure(ITestResult result) {
        System.out.println("OnTestFailure" + result.getName());
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "ONTEST FAILURE", result.getName());
    }

    public void onTestSkipped(ITestResult result) {
        System.out.println("OnTestSkipped" + result.getName());
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "ONTEST Skipped", result.getName());
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("onTestFailedButWithinSuccessPercentage" + result.getName());
        QKART_Tests.takeScreenshot(QKART_Tests.driver, "ONTEST FailedButWithinSuccessPercentage",
                result.getName());
    }
}