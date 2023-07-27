package com.aidn5.hypixel.server;

import com.aidn5.hypixel.cache.HypixelCache;
import com.aidn5.hypixel.common.HttpResponse;
import com.aidn5.hypixel.common.RequestTransformer;
import com.aidn5.hypixel.common.SingleEndpoint;
import com.aidn5.hypixel.endpoint.*;
import com.aidn5.hypixel.hypixel.HypixelApi;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(5, 30,
            30L, TimeUnit.MINUTES,
            new SynchronousQueue<>());
    private final HttpServer server;
    private final HypixelApi hypixelApi;
    private final HypixelCache hypixelCache;
    private final EndPointManager endPointManager;
    private final RequestTransformer requestTransformer;

    private final String indexPage;

    public Server(HypixelApi hypixelApi, HypixelCache hypixelCache,
                  EndPointManager endPointManager, RequestTransformer requestTransformer,
                  int port) throws IOException {

        this.hypixelApi = hypixelApi;
        this.hypixelCache = hypixelCache;
        this.endPointManager = endPointManager;
        this.requestTransformer = requestTransformer;

        try (var is = this.getClass().getClassLoader().getResourceAsStream("index.html")) {
            assert is != null;
            this.indexPage = IOUtils.toString(is, StandardCharsets.UTF_8);
        }

        server = HttpServer.create();
        server.bind(new InetSocketAddress(port), 0);
        server.setExecutor(EXECUTOR);
        server.createContext("/", new MyHttpHandler());
    }

    @Override
    public void run() {
        server.start();
        try {
            while (!Thread.interrupted()) {
                //noinspection BusyWait
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread() + ": Server interrupted.");
        }
    }

    private class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            var headers = httpExchange.getResponseHeaders();
            headers.add("x-name", "HypixelApiLayer");
            headers.add("x-version", "0.0.2");

            if ("GET".equals(httpExchange.getRequestMethod())) {
                try {
                    handleResponse(httpExchange);
                } catch (Exception e) {
                    e.printStackTrace();

                    setMimeAsJson(httpExchange);
                    respond(httpExchange, 500, "{\"success\": false, \"cause\": \"Internal Error encountered. Admins have been notified\"}");
                    return;
                }
            } else {
                setMimeAsJson(httpExchange);
                final String error = "{\"success\": false, \"cause\": \"Only GET method is allowed\"}";
                httpExchange.sendResponseHeaders(405, error.length());
                try (var os = httpExchange.getResponseBody()) {
                    os.write(error.getBytes());
                }
            }
        }

        private void handleResponse(HttpExchange httpExchange) throws IOException {
            String path = httpExchange.getRequestURI().getPath();
            System.out.println("Request: " + path);

            if (path.equals("/")) {
                setMimeAsHtml(httpExchange);
                respond(httpExchange, 200, indexPage);
                return;
            }

            setMimeAsJson(httpExchange);
            SingleEndpoint endpoint;
            try {
                var query = URLEncodedUtils.parse(httpExchange.getRequestURI(), StandardCharsets.UTF_8);
                endpoint = endPointManager.getEndpoint(path, query);
            } catch (NoSuchEndpoint e) {
                respond(httpExchange, 404, "{\"success\": false, \"cause\": \"Endpoint not implemented\"}");
                return;
            } catch (NoSuchParameters e) {
                respond(httpExchange, 400, "{\"success\": false, \"cause\": \"Parameter(s) required for this endpoint\"}");
                return;
            } catch (ParamTooBig e) {
                respond(httpExchange, 414, "{\"success\": false, \"cause\": \"Parameter(s) value is too long\"}");
                return;
            }


            var cachedData = hypixelCache.getCache(endpoint);
            if (cachedData != null) {
                setDataAge(httpExchange, cachedData.timestamp());
                respond(httpExchange, 200, cachedData.data());
                return;
            }

            long age = System.currentTimeMillis();
            setDataAge(httpExchange, age);

            HttpResponse hypixelResponse = hypixelApi.request(endpoint);
            if (hypixelResponse.responseCode() == 200) {
                hypixelCache.addCache(endpoint, hypixelResponse.response(), age);
            }

            respond(httpExchange, hypixelResponse.responseCode(), hypixelResponse.response());
        }

        private void respond(HttpExchange httpExchange, int code, String message) throws IOException {
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(code, bytes.length);
            try (var os = httpExchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void setDataAge(HttpExchange httpExchange, long age) {
            var headers = httpExchange.getResponseHeaders();
            headers.add("x-age", String.valueOf(age));
        }

        private void setMimeAsJson(HttpExchange httpExchange) {
            var headers = httpExchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");
        }

        private void setMimeAsHtml(HttpExchange httpExchange) {
            var headers = httpExchange.getResponseHeaders();
            headers.add("Content-Type", "text/html");
        }
    }
}
