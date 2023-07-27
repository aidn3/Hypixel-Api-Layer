package com.aidn5.hypixel.cache;


import com.aidn5.hypixel.common.SingleEndpoint;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.io.Closeable;
import java.util.Arrays;

class MongoCache implements HypixelCache, Closeable {
    private static final String DATABASE_NAME = "HYPIXEL_API_LAYER";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_DATA = "data";
    private final MongoClient mongoClient;
    private final MongoDatabase database;

    MongoCache(String mongoUri) {
        this.mongoClient = MongoClients.create(mongoUri);
        this.database = this.mongoClient.getDatabase(DATABASE_NAME);
    }

    @Override
    public CachedData getCache(SingleEndpoint endpoint) {
        var filter = Filters.and(
                Filters.eq("_id", endpoint.formQuery()),
                Filters.gte(FIELD_CREATED_AT, System.currentTimeMillis() - endpoint.maxLife())
        );

        Document cached = this.database.getCollection(endpoint.path()).find(filter).first();
        if (cached == null) return null;

        return new CachedData(cached.getString(FIELD_DATA), cached.getLong(FIELD_CREATED_AT));
    }

    @Override
    public void addCache(SingleEndpoint endpoint, String data, long currentTimestamp) {
        var filter = Filters.eq("_id", endpoint.formQuery());
        var update = Arrays.asList(Updates.set(FIELD_CREATED_AT, currentTimestamp), Updates.set(FIELD_DATA, data));

        var options = new UpdateOptions();
        options.upsert(true);

        this.database.getCollection(endpoint.path())
                .updateOne(filter, update, options);
    }

    @Override
    public void close() {
        this.mongoClient.close();
    }
}
