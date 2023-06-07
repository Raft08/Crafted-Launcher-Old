package be.raft.launcher.game.account;

import be.raft.launcher.CraftedLauncher;
import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.loader.JsonFileLoader;
import be.raft.launcher.game.login.LoginOption;
import be.raft.launcher.game.login.MicrosoftLoginOption;
import be.raft.launcher.game.login.OfflineLoginOption;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AccountManager {
    public static final String ACCOUNTS_DIR = "accounts";

    //Register the login option here
    public static final List<LoginOption> LOGIN_OPTIONS = List.of(new MicrosoftLoginOption(), new OfflineLoginOption());

    public static CompletableFuture<List<Account>> loadAccounts() {
        return CompletableFuture.supplyAsync(() -> {
            if (!GameFileManager.getFileInGameDirectory(ACCOUNTS_DIR).isDirectory()) {
                CraftedLauncher.logger.info("No accounts to load found.");
                return new ArrayList<>();
            }

            File[] accountsFiles = GameFileManager.getFileInGameDirectory(ACCOUNTS_DIR).listFiles(file -> file.isFile() && file.getName().endsWith(".json"));

            if (accountsFiles == null) {
                CraftedLauncher.logger.info("No accounts to load found.");
                return new ArrayList<>();
            }

            List<Account> accounts = new ArrayList<>();

            for (File accountFile : accountsFiles) {
                JsonFileLoader accountLoader = new JsonFileLoader(accountFile);
                JsonObject json = accountLoader.load().getAsJsonObject();

                String accountType = json.get("type").getAsString();

                //Get the login option to parse the json
                Optional<LoginOption> loginOption = LOGIN_OPTIONS.stream().filter(option ->
                        option.getIdentifier().equalsIgnoreCase(accountType)).findFirst();
                if (loginOption.isEmpty()) {
                    CraftedLauncher.logger.error("Unable to load '{}' cannot find any login options for the file type!",
                            accountFile.getName());
                    continue;
                }
                Account account = loginOption.get().parseAccount(json);

                accounts.add(account);
            }
            return accounts;
        });
    }

    /*
     * Load the class before loading the accounts,
     * It's only to represent more accurately the time that takes the accounts took to load.
     */
    public static void loadClass() {
    }
}
