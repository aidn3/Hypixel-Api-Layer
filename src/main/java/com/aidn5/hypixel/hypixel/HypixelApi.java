package com.aidn5.hypixel.hypixel;

import com.aidn5.hypixel.common.HttpResponse;
import com.aidn5.hypixel.common.SingleEndpoint;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class HypixelApi {
    private final static String BASE_URL = "https://api.hypixel.net";

    private final String apiKey;

    public HypixelApi(String apiKey) {
        this.apiKey = UUID.fromString(Objects.requireNonNull(apiKey)).toString();
    }

    public HttpResponse request(SingleEndpoint endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint.formQuery());
        var connection = (HttpURLConnection) url.openConnection();

        connection.addRequestProperty("API-Key", this.apiKey);
        connection.setDoInput(true);
        if (connection.getResponseCode() == 200) {
            try (var is = connection.getInputStream()) {
                String json = IOUtils.toString(is, StandardCharsets.UTF_8);
                return new HttpResponse(connection.getResponseCode(), json);
            }
        } else {
            try (var is = connection.getErrorStream()) {
                String json = IOUtils.toString(is, StandardCharsets.UTF_8);
                return new HttpResponse(connection.getResponseCode(), json);
            }
        }
    }

}
