package com.aidn5.hypixel;

import com.aidn5.hypixel.common.HttpResponse;
import com.aidn5.hypixel.common.RequestTransformer;
import com.aidn5.hypixel.common.SingleEndpoint;

public class Transformer {
    public static RequestTransformer getDefault() {
        return new DefaultTransformer();
    }

    private static class DefaultTransformer implements RequestTransformer {
        @Override
        public HttpResponse transform(SingleEndpoint endpoint, HttpResponse response) {
            return response;
        }
    }
}
