package be.raft.launcher.utilities;

public class SystemUtils {
    public static OperatingSystem getOS() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("mac")) {
            return OperatingSystem.MACOS;
        } else {
            return OperatingSystem.LINUX;
        }
    }

    public static int getArchitecture() {
        String osArch = System.getProperty("os.arch");

        if (osArch.contains("64")) {
            return 64;
        } else {
            return 32;
        }
    }
}
