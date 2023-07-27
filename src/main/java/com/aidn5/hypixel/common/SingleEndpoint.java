package com.aidn5.hypixel.common;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record SingleEndpoint(String path, String key, String value, long maxLife) {
    public SingleEndpoint(String path, String key, String value, long maxLife) {
        this.path = Objects.requireNonNull(path);
        this.key = key;
        this.value = value;
        this.maxLife = maxLife;
    }

    public String formQuery() {
        if (key == null) return path;
        return this.path + "?" + key + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
