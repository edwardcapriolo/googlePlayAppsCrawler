package db;

import crawler.AppInfo;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class DBWriter {

    private MongoDatabase db;
    private String databaseName;
    private MongoCollection mongoCollection;

    public DBWriter(String databaseName, String collectionName) {
        this.databaseName = cleanName(databaseName);
        MongoClient mongoClient = new MongoClient();
        db = mongoClient.getDatabase(this.databaseName);
        mongoCollection = db.getCollection(cleanName(collectionName));
    };

    private String cleanName(String databaseName) {
       String cleanName = databaseName.replaceAll("[^a-zA-Z ]", "").toLowerCase();
        return cleanName.replaceAll(" ", "");
    }

    public void writeAppInfosToDb(List<AppInfo> appInfos) {
        if (appInfos.isEmpty()) {
            return;
        }
        for (Document appInfoDocument : convertToDocuments(appInfos)) {
            String appInfoId = (String) appInfoDocument.get("id");
            FindIterable<Document> iterable = mongoCollection.find(new Document("id", appInfoId));
            Document first = iterable.first();
            if (first != null) {
                mongoCollection.updateOne(eq("id", appInfoId), new Document("$set", appInfoDocument));
            } else {
                mongoCollection.insertOne(appInfoDocument);
            }
        }
    }

    public Long getAppInfoTimestamp(String appLink) {
        FindIterable<Document> iterable = mongoCollection.find(new Document("id", appLink));
        Document document = iterable.first();
        return document == null ? null : document.getLong("timestamp");
    }


    public int writeAppLinksToDb(String collectionName, Iterable<String> appLinks) {
        MongoCollection mongoCollection = db.getCollection(collectionName);
        List<Document> appLinksDocuments = new ArrayList<Document>();
        for (String appLink : appLinks) {
            FindIterable<Document> iterable = mongoCollection.find(new Document("appLink", appLink));
            if (iterable.first() == null) {
                appLinksDocuments.add(convertToDocument(appLink));
            }
        }
        if (!appLinksDocuments.isEmpty()) {
            mongoCollection.insertMany(appLinksDocuments);
        }
        return appLinksDocuments.size();
    }

    private Document convertToDocument(String appLink) {
        return new Document().append("appLink", appLink);
    }

    private List<Document> convertToDocuments(List<AppInfo> appInfos) {
        List<Document> documents = new ArrayList();
        for (AppInfo appInfo : appInfos) {
            documents.add(appInfo.convertToDocument());
        }
        return documents;
    }
}
