package utils;

import com.google.common.base.Strings;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.logging.Logger;

/**
 * WebDriver utility methods.
 */
public final class SafariDriverUtils {
    private static final int BATCH_SIZE = 50;
    private static final Logger log = Logger.getLogger(SafariDriverUtils.class.getName());
    public static final int DEFAULT_SLEEP_MILLIS = 2000;

    private SafariDriverUtils() {

    }

    public static WebDriver createSafariDriver() {
        WebDriver driver = new SafariDriver();
        driver.manage().window().setPosition(new Point(-2000, -2000));
        driver.manage().window().maximize();
        return driver;
    }

    public static void goToUrl(WebDriver driver, String url) {
        driver.navigate().to(url);
    }

    public static void sleep() {
        sleep(DEFAULT_SLEEP_MILLIS);
    }

    public static String getTextContent(WebDriver driver, WebElement webElement) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        return String.valueOf(executor.executeScript("return arguments[0].textContent", webElement));
    }

    public static void scrollToElement(WebDriver driver, String className) {
        WebElement element = driver.findElement(By.className(className));
        Actions actions = new Actions(driver);
        actions.moveToElement(element);
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getText(WebElement element) {
        String text = element.getAttribute("innerText");
        if (Strings.isNullOrEmpty(text)) {
            return "";
        }
        return text.trim().toLowerCase();
    }

    public static void clickOnElementWithJs(WebDriver driver, WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public static String cleanupText(String text, String[] startsWithStrings, String endsWith) {
        text = text.trim();
        for (String startsWith : startsWithStrings) {
            if (!Strings.isNullOrEmpty(startsWith) && text.toLowerCase().startsWith(startsWith.toLowerCase())) {
                text = text.substring(startsWith.length());
                text = text.trim();
                continue;
            }
        }
        if (!Strings.isNullOrEmpty(endsWith) && text.toLowerCase().endsWith(endsWith.toLowerCase())) {
            text = text.substring(0, text.length() - endsWith.length());
            text = text.trim();
        }
        return text;
    }

    public static String cleanupText(String text, String startsWith, String endsWith) {
        return cleanupText(text, new String[]{startsWith}, endsWith);
    }

    public static void goToAppUrl(WebDriver driver, String appUrl) {
        log.info("Navigating to " + appUrl);
        goToUrl(driver, appUrl);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // some apps might not have reviewers, but we ignore those
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("id-app-title")));
    }

    public static String extractElementContent(WebDriver driver, By by) {
        try {
            WebElement element = driver.findElement(by);
            return getTextContent(driver, element);
        } catch (Exception e) {

        }
        return "";
    }
}
