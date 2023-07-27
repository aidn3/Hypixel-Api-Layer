package com.aidn5.hypixel.cache;

import com.aidn5.hypixel.common.SingleEndpoint;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;

class MemoryCache implements HypixelCache {
    Cache<String, String> cache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(Duration.ofMinutes(2))
            .maximumSize(1000)
            .build();

    @Override
    public CachedData getCache(SingleEndpoint endpoint) {
        String data = cache.getIfPresent(endpoint.formQuery());
        if (data == null) return null;
        return new CachedData(data, System.currentTimeMillis());
    }

    @Override
    public void addCache(SingleEndpoint endpoint, String data, long currentTimestamp) {
        cache.put(endpoint.formQuery(), data);
    }
}
