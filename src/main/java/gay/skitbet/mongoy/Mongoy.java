package gay.skitbet.mongoy;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

/**
 * Mongoy provides utility methods to initialize MongoDB client, handle database
 * connections, and close resources properly.
 */
public class Mongoy {

    private static MongoClient client;
    private static MongoDatabase database;

    /**
     * Initializes the MongoDB connection using default connection settings.
     * @param databaseName The name of the database to connect to.
     */
    public static void init(String databaseName) {
        if (client != null) {
            throw new IllegalStateException("Mongo client already initialized.");
        }
        client = MongoClients.create();

        database = client.getDatabase(databaseName);
    }

    /**
     * Initializes the MongoDB connection using a custom connection URI.
     * @param uri The MongoDB URI string for connecting to the database.
     * @param databaseName The name of the database to connect to.
     */
    public static void init(String uri, String databaseName) {
        client = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build());

        database = client.getDatabase(databaseName);
    }

    /**
     * Returns the connected MongoDatabase instance.
     * @return The current MongoDatabase.
     */
    public static MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Closes the MongoDB client and releases resources.
     */
    public static void close()  {
        if (client != null) {
            client.close();
            client = null;
            database = null;
        }
    }
}
