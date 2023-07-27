package com.aidn5.hypixel;

import com.aidn5.hypixel.cache.HypixelCache;
import com.aidn5.hypixel.cache.HypixelCacheManager;
import com.aidn5.hypixel.common.RequestTransformer;
import com.aidn5.hypixel.endpoint.EndPointManager;
import com.aidn5.hypixel.hypixel.HypixelApi;
import com.aidn5.hypixel.server.Server;

import java.io.IOException;

public class HypixelApiLayer {

    public static void main(String[] args) throws IOException {
        // For testing
        int port = Integer.parseInt(args[0]);
        System.out.println("Port: " + port);
        String apikey = args[1];
        System.out.println("API-Key: " + apikey);
        String mongoUri = args[2];
        System.out.println("MongoDB: " + apikey);

        HypixelApi hypixelApi = new HypixelApi(apikey);
        HypixelCache hypixelCache = HypixelCacheManager.getMongoCache(mongoUri);
        EndPointManager endPointManager = EndPointManager.loadInternalFile();
        RequestTransformer requestTransformer = Transformer.getDefault();

        Server server = new Server(hypixelApi, hypixelCache, endPointManager, requestTransformer, port);
        server.run();
    }
}
