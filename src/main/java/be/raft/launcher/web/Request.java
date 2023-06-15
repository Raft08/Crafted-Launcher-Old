package be.raft.launcher.web;

import be.raft.launcher.file.loader.StringFileLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
                    String bodyData = response.body().string();

                    if (cache != null) {
                        StringFileLoader loader = new StringFileLoader(cache);
                        if (loader.fileExists()) {
                            loader.getFile().delete();
                        }

                        loader.getFile().getParentFile().mkdirs();
                        loader.createFile();
                        loader.save(bodyData);
                    }

                    JsonElement data = JsonParser.parseString(bodyData);
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

    public CompletableFuture<File> download(File dest) {
        this.callback.accept(0);

        //Check for cache
        if (this.cache != null && !this.updateCache) {
            if (this.cache.isFile()) {
                if (!dest.getParentFile().isDirectory()) {
                    dest.getParentFile().mkdirs();
                }

                try {
                    Files.copy(this.cache.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    this.callback.accept(100);
                    return CompletableFuture.completedFuture(dest);
                } catch (IOException e) {
                    this.callback.accept(-1);
                    return CompletableFuture.failedFuture(e);
                }
            }
        }

        CompletableFuture<File> future = new CompletableFuture<>();

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
                    throw new IOException("Unexpected response code: " + response);
                }

                File downloadDest;

                if (cache != null) {
                    downloadDest = cache;
                } else {
                    downloadDest = dest;
                }

                if (!downloadDest.getParentFile().isDirectory()) {
                    downloadDest.getParentFile().mkdirs();
                }

                try (ResponseBody responseBody = response.body(); InputStream inputStream = responseBody.byteStream();
                     BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                     FileOutputStream outputStream = new FileOutputStream(downloadDest);
                     BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {

                    long totalBytesRead = 0;
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long fileSize = responseBody.contentLength();

                    while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                        bufferedOutputStream.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;

                        int progress = (int) (totalBytesRead * 100 / fileSize);
                        callback.accept(progress);
                    }

                    bufferedOutputStream.flush();
                }

                if (cache != null) {
                    if (!dest.getParentFile().isDirectory()) {
                        dest.getParentFile().mkdirs();
                    }
                    try {
                        Files.copy(cache.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        callback.accept(-1);
                        future.completeExceptionally(e);
                    }
                }

                future.complete(dest);
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
