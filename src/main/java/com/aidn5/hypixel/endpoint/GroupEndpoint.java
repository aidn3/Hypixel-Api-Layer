package com.aidn5.hypixel.endpoint;

import java.util.Objects;

record GroupEndpoint(String[] params, long maxLife) {
    GroupEndpoint(String[] params, long maxLife) {
        this.params = Objects.requireNonNull(params);
        this.maxLife = maxLife;
    }
}
