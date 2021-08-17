package com.revature.johnKimAPI.util;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;

/**
    This is a Singleton Factory that pumps out connection data for the Mongo Database.
    Its goal is to reduce the amount of time a developer needs to repeat themselves a million-fold.
    Simply call the 'generate()' method to have an instance of the class,
    then request a uri through 'getConnection'.
 */

public class GetMongoClient {

    // Initialization of variables
    private static final GetMongoClient connection = new GetMongoClient();
    private final String ipAddress;
    private final int port;
    private MongoClient mongoClient;

    // Static implementation of Logger to work with the getConnection() method.
    static Logger logger = LoggerFactory.getLogger(GetMongoClient.class);

    private GetMongoClient() {

        ipAddress = System.getProperty("ipAddress");
        port = Integer.parseInt(System.getProperty("port"));
        String dbName = System.getProperty("dbName");
        String username = System.getProperty("username");
        String password = System.getProperty("password");

        // Compile all of the data from the properties environment variables into a MongoClient called,
        // for simplicity, 'mongoClient'.
        try {
            this.mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress(ipAddress, port))))
                            .credential(MongoCredential.createScramSha1Credential(username, dbName, password.toCharArray()))
                            .build());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Threw an Exception at GetMongoClient::Constructor, full StackTrace follows: " + e);
        }
    }

    // Shuts down the Mongo connection
    public void cleanUp() { mongoClient.close(); }

    // Singleton Design; More than one 'GetConnection' cannot exist at any given time.
    public static GetMongoClient generate() {
        return connection;
    }

    // This will return the actual "uri" information which allows access to the database.
    public MongoClient getConnection() {
        return mongoClient;
    }
}