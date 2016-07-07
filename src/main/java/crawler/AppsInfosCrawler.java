package crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.SafariDriverUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static utils.SafariDriverUtils.*;

/**
 * WebDriver crawler that crawls the extended apps info for a given url.
 */
public class AppsInfosCrawler {
    public static final String PLAY_PREFIX = "https://play.google.com/store/apps/details?id=";
    private static final Logger log = Logger.getLogger(AppsInfosCrawler.class.getName());
    private static final String INSTALL = "install";
    private static final String BUY = " buy";
    public static final String COMMA = ",";
    public static final String EMPTY = "";

    private WebDriver driver;

    public AppsInfosCrawler() {
        this(createSafariDriver());
    }

    public AppsInfosCrawler(WebDriver driver) {
        this.driver = driver;
    }

    public List<AppInfo> crawlAppInfos(Iterable<String> appIds) {
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        for (String appId : appIds) {
            appId = PLAY_PREFIX + appId;
            AppInfo appInfo = crawlAppInfo(appId);
            if (appInfo != null) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    private AppInfo crawlAppInfo(String appUrl) {
        try {
            goToAppUrl(driver, appUrl);
            return extractAppInfo(appUrl);
        } catch (Exception e) {
            log.info("Skipping app with url " + appUrl + " that caused exception: " + e.getMessage());
        }
        return null;
    }

    private AppInfo extractAppInfo(String appUrl) {
        String googleAppName = extractGoogleAppName(appUrl);
        String appTitle = extractAppTitle();
        AppInfo appInfo = new AppInfo(appTitle, googleAppName, googleAppName);
        appInfo.setPrice(extractPrice());
        appInfo.setStarRating(Double.valueOf(extractStarRating()));
        appInfo.setBadge(extractBadge());
        appInfo.setAuthor(extractAuthor());
        appInfo.setCategory(extractCategory());
        appInfo.setTotalNrOfReviews(Long.valueOf(extractTotalNrOfReviews()));
        appInfo.setReviewsPerStars(extractReviewsPerStar());
        appInfo.setDescription(extractDescription());
        appInfo.setWhatsNew(extractWhatsNew());
        extractAdditionalInfo(appInfo);
        return appInfo;
    }

    private void extractAdditionalInfo(AppInfo appInfo) {
        scrollToBottomOfPage();
        appInfo.setLastUpdates(extractLastUpdated());
        appInfo.setSize(extractSize());
        appInfo.setInstalls(extractInstalls());
        appInfo.setCurrentVersion(extractCurrentVersion());
        appInfo.setRequiredAndroidVersion(extractRequiredAndroidVersion());
        appInfo.setContentRating(extractContentRating());
        appInfo.setPermissions(extractPermissions());
        appInfo.setInAppProducts(extractInAppProducts());
    }

    private void scrollToBottomOfPage() {
        scrollToElement(driver, "vat-footer-link");
        sleep();
    }

    private String extractElementContent(By by) {
        return SafariDriverUtils.extractElementContent(driver, by);
    }

    private String extractLastUpdated() {
        return extractElementContent(By.cssSelector("div[itemprop='datePublished']"));
    }

    private String extractSize() {
        return extractElementContent(By.cssSelector("div[itemprop='fileSize']"));
    }

    private String extractInstalls() {
        return extractElementContent(By.cssSelector("div[itemprop='numDownloads']"));
    }

    private String extractCurrentVersion() {
        return extractElementContent(By.cssSelector("div[itemprop='softwareVersion']"));
    }

    private String extractRequiredAndroidVersion() {
        return extractElementContent(By.cssSelector("div[itemprop='operatingSystems']"));
    }

    private String extractContentRating() {
        return extractElementContent(By.cssSelector("div[itemprop='contentRating']"));
    }

    private String extractPermissions() {
        try {
            WebElement viewPermissionsButton = driver.findElement(By.className("id-view-permissions-details"));
            clickOnElementWithJs(driver, viewPermissionsButton);
            sleep();
            WebElement permissionsDetails = driver.findElement(By.className("id-permission-buckets"));
            String permissionsDetailsString = getTextContent(driver, permissionsDetails);
            WebElement closeButton = driver.findElement(By.id("close-dialog-button"));
            clickOnElementWithJs(driver, closeButton);
            sleep();
            return permissionsDetailsString;
        } catch (Exception e) {

        }
        return "";
    }

    private String extractInAppProducts() {
        try {
            List<WebElement> metaInfos = driver.findElements(By.className("meta-info"));
            for (WebElement metaInfo : metaInfos) {
                WebElement title = metaInfo.findElement(By.className("title"));
                if ("In-app Products".equals(getTextContent(driver, title))) {
                    WebElement content = metaInfo.findElement(By.className("content"));
                    return getTextContent(driver, content);
                }
            }
        } catch (Exception e) {

        }
        return "";
    }

    private double extractPrice() {
        WebElement priceElement = driver.findElement(By.className("price"));
        String priceText = getText(priceElement);
        if (priceText.indexOf(INSTALL) != -1) {
            return 0;
        }
        // Price text is of the form CHF 4,89 Buy
        int endIndex = priceText.indexOf(BUY);
        double price = Double.valueOf(priceText.substring(4, endIndex).replace(COMMA, EMPTY));
        return price;
    }

    private String extractGoogleAppName(String appUrl) {
        int index = appUrl.indexOf("id=");
        return appUrl.substring(index + 3);
    }

    private String extractAppTitle() {
        return extractElementContent(By.className("id-app-title"));
    }

    private String extractStarRating() {
        WebElement starRating = driver.findElement(By.className("star-rating-non-editable-container"));
        String text = starRating.getAttribute("aria-label");
        int startIndex = 6;
        int endIndex = text.indexOf(" stars");
        return text.substring(startIndex, endIndex);
    }

    private String extractBadge() {
        return extractElementContent(By.className("badge-title"));
    }

    private String extractAuthor() {
        return extractElementContent(By.className("primary"));
    }

    private String extractCategory() {
        return extractElementContent(By.className("category"));
    }

    private String extractTotalNrOfReviews() {
        return extractElementContent(By.className("rating-count")).replace(",", "");
    }

    private long[] extractReviewsPerStar() {
        String[] classNames = {"five", "four", "three", "two", "one"};
        long[] reviewsPerStar = new long[5];

        for (int i = 0; i < reviewsPerStar.length; i++) {
            String className = classNames[i];
            WebElement element = driver.findElement(By.className(className));
            reviewsPerStar[i] = Long.valueOf(element.getText().replaceAll("\\s", "").replace(",", ""));
        }
        return reviewsPerStar;
    }

    private String extractDescription() {
        WebElement description = driver.findElement(By.className("description"));
        return cleanupText(getTextContent(driver, description), "", "Read more");
    }

    private String extractWhatsNew() {
        try {
            scrollToElement(driver, "whatsnew");
            WebElement whatsnew = driver.findElement(By.className("whatsnew"));
            return cleanupText(getTextContent(driver, whatsnew), new String[]{"What's New", "Whats new"}, "Read more");
        } catch (Exception e) {
            return "";
        }
    }

    public void finish() {
        log.info("Quitting the driver.");
        driver.quit();
    }
}
