package be.raft.launcher.web;

import be.raft.launcher.file.loader.StringFileLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class Request <T> {
    private final OkHttpClient client;
    private String url;
    private final HashMap<String, String> params;
    private final HashMap<String, String> header;
    private File cache;
    private Consumer<Integer> callback = progress -> {};
    private boolean updateCache = false;

    public Request(OkHttpClient client) {
        this.client = client;
        this.params = new HashMap<>();
        this.header = new HashMap<>();
    }

    public Request<T> url(String url) {
        this.url = url;
        return this;
    }

    public Request<T> params(String parameter, String data) {
        this.params.put(parameter, data);
        return this;
    }

    public Request<T> params(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    public Request<T> header(String parameter, String data) {
        this.header.put(parameter, data);
        return this;
    }

    public Request<T> header(Map<String, String> header) {
        this.header.putAll(header);
        return this;
    }

    public Request<T> cache(File cache) {
        this.cache = cache;
        return this;
    }

    public Request<T> updateCache() {
        this.updateCache = true;
        return this;
    }

    public Request<T> callBack(Consumer<Integer> callback) {
        this.callback = callback;
        return this;
    }

    public CompletableFuture<T> execute(Function<JsonElement, T> function) {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.callback.accept(0);

        //Check for cache
        if (this.cache != null && !this.updateCache) {
            StringFileLoader loader = new StringFileLoader(this.cache);
            if (loader.fileExists()) {
                try {
                    JsonElement data = JsonParser.parseString(loader.load());
                    future.complete(function.apply(data));
                } catch (Throwable t) {
                    future.completeExceptionally(t);
                    callback.accept(-1);
                }
            }
        }

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(this.buildUrl())
                .headers(Headers.of(this.header))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.accept(-1);
                    throw new IOException("Unexpected response code: " + response);
                }

                try {
                    if (cache != null) {
                        StringFileLoader loader = new StringFileLoader(cache);
                        if (loader.fileExists()) {
                            loader.getFile().delete();
                        }

                        loader.createFile();
                        loader.save(response.body().string());
                    }

                    JsonElement data = JsonParser.parseString(response.body().string());
                    future.complete(function.apply(data));
                    callback.accept(100);
                } catch (Throwable t) {
                    callback.accept(-1);
                    future.completeExceptionally(t);
                }
            }
        });

        return future;
    }

    private String buildUrl() {
        if (this.params.isEmpty()) {
            return this.url;
        }

        StringBuilder urlBuilder = new StringBuilder(this.url);

        urlBuilder.append("?");

        this.params.forEach((key, value) -> {
            urlBuilder.append("&");
            urlBuilder.append(key);
            urlBuilder.append("=");
            urlBuilder.append(value);
        });

        return urlBuilder.toString();
    }
}
