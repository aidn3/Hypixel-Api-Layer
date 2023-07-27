package com.aidn5.hypixel.common;

public interface RequestTransformer {
    HttpResponse transform(SingleEndpoint endpoint, HttpResponse response);
}
