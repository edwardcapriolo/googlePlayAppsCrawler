package crawler;

import db.DBWriter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Crawler that collects information about apps that are specified in a .csv file.
 */
public class MainCrawler {
    private static final int BATCH_SIZE = 50;
    private static final String DB_NAME = "applicationsInfos";
    private static final Logger log = Logger.getLogger(MainCrawler.class.getName());

    private final AppsInfosCrawler extendedAppInfoCrawler;
    private final DBWriter dbWriter;

    private BufferedReader reader;

    public MainCrawler(String collectionName, String filename) throws IOException {
        this.reader = new BufferedReader(new FileReader(filename));
        this.extendedAppInfoCrawler = new AppsInfosCrawler();
        this.dbWriter = new DBWriter(DB_NAME, collectionName);
    }

    public void crawlAppsMetadata() throws IOException {
        try {
            while (true) {
                List<String> batchOfAppIds = getBatchOfAppIds();
                if(batchOfAppIds.isEmpty()) {
                    break;
                }

                List<AppInfo> appInfos = extendedAppInfoCrawler.crawlAppInfos(batchOfAppIds);
                log.info("Writing or updating " + appInfos.size() + " appInfos rows to db.");
                dbWriter.writeAppInfosToDb(appInfos);
            }
        } finally {
            cleanup();
        }
    }

    private List<String> getBatchOfAppIds() throws IOException {
        List<String> appIds = new ArrayList<String>();
        String appId;
        for (int i = 0; i < BATCH_SIZE; i++) {
            appId = reader.readLine();
            if (appId == null) {
                break;
            }
            appIds.add(appId);
        }
        return appIds;
    }

    public void cleanup() throws IOException {
        extendedAppInfoCrawler.finish();
        if (reader != null) {
            reader.close();
        }
    }

    public static void main(String[] args) throws Exception {
        MainCrawler crawler = new MainCrawler("applicationsInfos", "./resources/appsIds.csv");
        crawler.crawlAppsMetadata();
    }

}
