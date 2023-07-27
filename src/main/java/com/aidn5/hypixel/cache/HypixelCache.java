package com.aidn5.hypixel.cache;

import com.aidn5.hypixel.common.SingleEndpoint;

public interface HypixelCache {
    CachedData getCache(SingleEndpoint endpoint);

    void addCache(SingleEndpoint endpoint, String data, long currentTimestamp);
}
