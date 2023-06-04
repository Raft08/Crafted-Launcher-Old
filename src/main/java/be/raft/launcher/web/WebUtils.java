package be.raft.launcher.web;

import be.raft.launcher.CraftedLauncher;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class WebUtils {
    public static CompletableFuture<Boolean> ping(String url) {
        return ping(url, 5 * 1000); // 5 x 1000ms = 5sec
    }

    public static CompletableFuture<Boolean> ping(String url, int timeout) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InetAddress inetAddress = InetAddress.getByName(url);
                return inetAddress.isReachable(timeout);
            } catch (UnknownHostException ignored) {

            } catch (Exception e) {
                CraftedLauncher.logger.error("Unable to ping '{}'", url, e);
            }
            return false;
        });
    }

    public static CompletableFuture<Boolean> ping(String... urls) {
        return ping(5 * 1000, urls); // 5 x 1000ms = 5sec
    }

    public static CompletableFuture<Boolean> ping(int timeout, String... urls) {
        CompletableFuture<Boolean>[] pingFutures = new CompletableFuture[urls.length];
        for (int i = 0; i < urls.length; i++) {
            pingFutures[i] = ping(urls[i], timeout);
        }

        return CompletableFuture.allOf(pingFutures)
                .thenApply(ignored -> {
                    for (CompletableFuture<Boolean> future : pingFutures) {
                        if (!future.join()) {
                            return false;
                        }
                    }
                    return true;
                });
    }
}
