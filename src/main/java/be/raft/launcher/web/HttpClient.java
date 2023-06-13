package be.raft.launcher.web;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class HttpClient {
    private final OkHttpClient client;

    public HttpClient(OkHttpClient client) {
        this.client = client;
    }

    public CompletableFuture<JsonElement> jsonRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .header("Connection", "keep-alive")
                .build();

        CompletableFuture<JsonElement> future = new CompletableFuture<>();
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

                JsonElement jsonElement = JsonParser.parseString(response.body().string());
                future.complete(jsonElement);
            }
        });

        return future;
    }
}
