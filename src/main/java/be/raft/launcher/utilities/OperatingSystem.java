package be.raft.launcher.utilities;

public enum OperatingSystem {
    WINDOWS("windows"), LINUX("linux"), MACOS("osx");

    private final String mojangValue;

    OperatingSystem(String mojangValue) {
        this.mojangValue = mojangValue;
    }

    public String getMojangValue() {
        return mojangValue;
    }
}
