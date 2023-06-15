package be.raft.launcher;

import be.raft.launcher.file.GameFileManager;
import be.raft.launcher.file.SettingsManager;
import be.raft.launcher.game.account.Account;
import be.raft.launcher.game.account.AccountManager;
import be.raft.launcher.resources.Text;
import be.raft.launcher.resources.theme.DefaultTheme;
import be.raft.launcher.resources.theme.Theme;
import be.raft.launcher.resources.theme.ThemeManager;
import be.raft.launcher.ui.UIManager;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CraftedLauncher {
    public static final long startTime = System.currentTimeMillis();
    public static boolean devEnv;
    public static CraftedLauncher instance;
    public static final Logger logger = LoggerFactory.getLogger("Crafted-Launcher");

    public static void main(String[] args) {
        devEnv = Arrays.stream(args).toList().contains("-launcher:dev");
        try {
            Class.forName("javafx.application.Application");
            CraftedLauncher.logger.info("JavaFX validation successful!");
            System.setProperty("prism.lcdtext", "false"); //Enhance smoothness of the text
            new CraftedLauncher();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error:\n" + e.getMessage() + "\nnot found!" +
                    "\nUnable to start the launcher!", "error", JOptionPane.ERROR_MESSAGE);
            CraftedLauncher.logger.error("JavaFX validation failed!", e);
        }
    }

    private SettingsManager settingsManager;
    private UIManager uiManager;
    private Theme theme;
    private List<Theme> loadedThemes;
    private Account selectedAccount;
    private List<Account> availableAccounts;

    public CraftedLauncher() {
        instance = this;

        CraftedLauncher.logger.info("Initializing Crafted Launcher...");

        if (!GameFileManager.getGameDirectory().isDirectory()) {
            GameFileManager.getGameDirectory().mkdirs();
        }

        //Load Themes
        long themeLoadingStartTime = System.currentTimeMillis();
        CompletableFuture<Void> themeLoadingFuture = ThemeManager.loadThemes().thenAccept(themes -> {
            this.loadedThemes = themes;
            CraftedLauncher.logger.info("Themes took {}ms to load!", (System.currentTimeMillis() - themeLoadingStartTime));
        });

        //Load accounts
        AccountManager.loadClass();
        long accountLoadingStartTime = System.currentTimeMillis();
        CompletableFuture<Void> accountLoadingFuture = AccountManager.loadAccounts().thenAccept(accounts -> {
            this.availableAccounts = accounts;
            CraftedLauncher.logger.info("Accounts took {}ms to load!", (System.currentTimeMillis() - accountLoadingStartTime));
        });

        //Load Settings
        this.settingsManager = new SettingsManager();
        if (!this.settingsManager.settingsFileExists()) {
            this.settingsManager.createSettings();
        }
        this.settingsManager.load();

        //Block main thread until themes are loaded
        if (!themeLoadingFuture.isDone()) {
            CraftedLauncher.logger.warn("Theme are still loading, blocking main thread until finished.");
            themeLoadingFuture.join();
        }

        //Select the theme
        this.selectTheme();

        //Load the language
        Text.loadLocales(this.settingsManager, this.theme).join();
        CraftedLauncher.logger.info("Language '{}' loaded!", Text.getActiveLocale());

        //Block main thread until accounts are loaded
        if (!accountLoadingFuture.isDone()) {
            CraftedLauncher.logger.warn("Accounts are still loading, blocking main thread until finished.");
            accountLoadingFuture.join();
        }

        this.selectAccount();

        CraftedLauncher.logger.info("Launcher loaded in {}ms", (System.currentTimeMillis() - CraftedLauncher.startTime));

        Application.launch(UIManager.class);
    }

    private void selectTheme() {
        if (this.settingsManager.has("theme")) {
            String themeId = this.settingsManager.getString("theme");
            Optional<Theme> possibleTheme = this.loadedThemes.stream().filter(loadedTheme ->
                    loadedTheme.getId().equals(themeId)).findFirst();

            if (possibleTheme.isEmpty()) {
                CraftedLauncher.logger.warn("Unable to find theme '{}' setting default theme.", themeId);
                this.settingsManager.setString("theme", "default");
                this.settingsManager.save();
                this.theme = DefaultTheme.theme;
                return;
            }

            this.theme = possibleTheme.get();
        } else {
            this.settingsManager.setString("theme", "default");
            this.settingsManager.save();
            this.theme = DefaultTheme.theme;
        }

        CraftedLauncher.logger.info("Theme '{}' loaded!", this.theme.getName());
    }

    private void selectAccount() {
        if (this.settingsManager.has("selectedAccount")) {
            String accountId = this.settingsManager.getString("selectedAccount");
            Optional<Account> possibleAccount = this.availableAccounts.stream().filter(account ->
                    account.getUniqueId().toString().equals(accountId)).findFirst();

            if (possibleAccount.isEmpty()) {
                CraftedLauncher.logger.warn("Cannot find account '{}'", accountId);
                if (availableAccounts.isEmpty()) {
                    this.selectedAccount = null;
                    CraftedLauncher.logger.warn("No other available accounts, user will be prompted to login.");
                    return;
                }

                this.selectedAccount = this.availableAccounts.get(0);
                this.settingsManager.setString("selectedAccount", this.selectedAccount.getUniqueId().toString());
                this.settingsManager.save();
                CraftedLauncher.logger.info("{}({}) has been set as selected account", this.selectedAccount.getUsername(),
                        this.selectedAccount.getUniqueId());
                return;
            }

            this.selectedAccount = possibleAccount.get();
            CraftedLauncher.logger.info("Selected account: {}({})", this.selectedAccount.getUsername(),
                    this.selectedAccount.getUniqueId());
        } else {
            if (!availableAccounts.isEmpty()) {
                this.selectedAccount = this.availableAccounts.get(0);
                this.settingsManager.setString("selectedAccount", this.selectedAccount.getUniqueId().toString());
                this.settingsManager.save();
                CraftedLauncher.logger.info("{}({}) has been set as selected account", this.selectedAccount.getUsername(),
                        this.selectedAccount.getUniqueId());
            }
        }
    }

    public Theme getTheme() {
        return theme;
    }

    public List<Theme> getLoadedThemes() {
        return loadedThemes;
    }

    public UIManager getUIManager() {
        return uiManager;
    }

    public void setUiManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public Account getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public List<Account> getAvailableAccounts() {
        return availableAccounts;
    }
}
