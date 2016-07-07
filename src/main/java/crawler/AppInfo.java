package crawler;

import com.mongodb.BasicDBObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that stores extended info about an app.
 */
public class AppInfo {
    private static final int STARS = 5;
    private static final int PRICE_MULTIPLIER = 100;

    private final String name;
    private final String id;
    private final String linkName;
    private double starRating;
    private String category;
    private String badge;
    private String author;
    private long totalNrOfReviews;
    private long[] reviewsPerStars = new long[STARS];
    private String description;
    private String whatsNew;
    private int price;
    private String lastUpdated;
    private String size;
    private String installs;
    private String currentVersion;
    private String requiredAndroidVersion;
    private String contentRating;
    private String permissions;
    private String inApppProducts;
    private long timestamp;

    public AppInfo(String name, String linkName, String id) {
        this.name = name;
        this.linkName = linkName;
        this.id = id;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLinkName() {
        return linkName;
    }

    public double getStarRating() {
        return starRating;
    }

    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getBadge() {
        return badge;
    }

    public void setAuthor(String company) {
        this.author = company;
    }

    public String getAuthor() {
        return author;
    }

    public void setTotalNrOfReviews(long totalNrOfReviews) {
        this.totalNrOfReviews = totalNrOfReviews;
    }

    public long getTotalNrOfReviews() {
        return totalNrOfReviews;
    }

    public void setReviewsPerStars(long[] reviewsPerStars) {
        this.reviewsPerStars = reviewsPerStars;
    }

    public long[] getReviewsPerStars() {
        return reviewsPerStars;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setWhatsNew(String whatsNew) {
        this.whatsNew = whatsNew;
    }

    public String getWhatsNew() {
        return whatsNew;
    }

    public void setPrice(double price) {
        this.price = (int) (price * PRICE_MULTIPLIER);
    }

    public double getPrice() {
        return ((double) price) / PRICE_MULTIPLIER;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdates(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setInstalls(String installs) {
        this.installs = installs;
    }

    public String getInstalls() {
        return installs;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setRequiredAndroidVersion(String requiredAndroidVersion) {
        this.requiredAndroidVersion = requiredAndroidVersion;
    }

    public String getRequiredAndroidVersion() {
        return requiredAndroidVersion;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setInAppProducts(String inAppProducts) {
        this.inApppProducts = inAppProducts;
    }

    public String getInAppProducts() {
        return inApppProducts;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id: " + getId());
        sb.append("\nname: " + getName());
        sb.append("\nlinkName: " + getLinkName());
        sb.append("\nprice: " + getPrice());
        sb.append("\nstarRating: " + getStarRating());
        sb.append("\ncategory: " + getCategory());
        sb.append("\nbadge: " + getBadge());
        sb.append("\nauthor: " + getAuthor());
        sb.append("\ntotalNrOfReviews: " + getTotalNrOfReviews());
        sb.append("\nreviewsPerStarRating: " + getReviewsPerStarString());
        sb.append("\ndescription: " + getDescription());
        sb.append("\nwhatsNew: " + getWhatsNew());
        return sb.toString();
    }

    private String getReviewsPerStarString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < STARS; i++) {
            sb.append((i + 1) + " stars: " + reviewsPerStars[i] + " ");
        }
        return sb.toString();
    }


    public Document convertToDocument() {
        List<BasicDBObject> starsRatings = new ArrayList();
        for (int i = 0; i < STARS; i++) {
            starsRatings.add(new BasicDBObject(String.valueOf(i + 1), reviewsPerStars[i]));
        }

        return new Document()
                .append("id", getId())
                .append("timestamp", getTimestamp())
                .append("name", getName())
                .append("linkName", getLinkName())
                .append("price", getPrice())
                .append("starRating", getStarRating())
                .append("category", getCategory())
                .append("badge", getBadge())
                .append("author", getAuthor())
                .append("totalNrOfReviews", getTotalNrOfReviews())
                .append("reviewsPerStarRating", starsRatings)
                .append("description", getDescription())
                .append("whatsNew", getWhatsNew())
                .append("lastUpdated", getLastUpdated())
                .append("size", getSize())
                .append("installs", getInstalls())
                .append("currentVersion", getCurrentVersion())
                .append("requiredAndroidVersion", getRequiredAndroidVersion())
                .append("contentRating", getContentRating())
                .append("permissions", getPermissions())
                .append("inAppProducts", getInAppProducts());
    }
}
