package com.aidn5.hypixel.cache;

public class HypixelCacheManager {
    public static HypixelCache getMemoryCache() {
        return new MemoryCache();
    }

    public static HypixelCache getMongoCache(String mongoUri) {
        return new MongoCache(mongoUri);
    }
}
