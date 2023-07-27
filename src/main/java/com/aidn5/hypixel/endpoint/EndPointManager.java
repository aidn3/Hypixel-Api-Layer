package com.aidn5.hypixel.endpoint;

import com.aidn5.hypixel.common.SingleEndpoint;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EndPointManager {
    private static final Gson GSON = new Gson();
    private static final int MAX_PARAM_SIZE = 36;

    private final Map<String, GroupEndpoint> groupEndpoint;

    public static EndPointManager loadInternalFile() throws IOException {
        try (var is = EndPointManager.class.getClassLoader().getResourceAsStream("endpoints.json")) {
            assert is != null;
            String str = IOUtils.toString(is, StandardCharsets.UTF_8);
            return EndPointManager.loadFromJson(str);
        }
    }

    public static EndPointManager loadFromJson(String json) {
        Type type = new TypeToken<Map<String, GroupEndpoint>>() {
        }.getType();
        Map<String, GroupEndpoint> map = GSON.fromJson(json, type);
        return new EndPointManager(map);
    }

    private EndPointManager(Map<String, GroupEndpoint> groupEndpoint) {
        this.groupEndpoint = Objects.requireNonNull(groupEndpoint);
    }

    public SingleEndpoint getEndpoint(String path, List<NameValuePair> params) throws NoSuchEndpoint, NoSuchParameters, ParamTooBig {
        if (path == null) throw new NoSuchEndpoint();

        path = path.trim().toLowerCase(Locale.ROOT);
        if (params.isEmpty()) throw new NoSuchEndpoint();

        GroupEndpoint group = this.groupEndpoint.get(path);
        if (group == null) throw new NoSuchEndpoint();

        for (String validKey : group.params()) {
            if (validKey == null) return new SingleEndpoint(path, null, null, group.maxLife());

            for (var set : params) {
                String value = set.getValue();
                if (value == null) continue;
                value = value.trim().toLowerCase();
                if (value.isEmpty()) continue;

                if (validKey.equalsIgnoreCase(set.getName())) {
                    if (value.length() > MAX_PARAM_SIZE) throw new ParamTooBig();
                    return new SingleEndpoint(path, validKey, value, group.maxLife());
                }
            }
        }

        throw new NoSuchParameters();
    }
}
