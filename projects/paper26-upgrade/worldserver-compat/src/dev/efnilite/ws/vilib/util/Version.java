package dev.efnilite.ws.vilib.util;

import org.bukkit.Bukkit;

import java.util.Arrays;

public enum Version {

    V1_16(16), V1_17(17), V1_18(18), V1_19(19), V1_20(20), V1_20_5(20, 5),
    V1_21(21), V1_22(22), V26_3(26, 3);

    public static Version VERSION;
    public final int major;
    public final int minor;

    Version(int major) {
        this.major = major;
        this.minor = 0;
    }

    Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static boolean isHigherOrEqual(Version compareTo) {
        if (VERSION.major == compareTo.major) {
            return VERSION.minor >= compareTo.minor;
        } else {
            return VERSION.major > compareTo.major;
        }
    }

    public static String getPrettyVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static Version getVersion() {
        VERSION = fromMinecraftVersion(getPrettyVersion());
        return VERSION;
    }

    public static Version fromMinecraftVersion(String versionString) {
        var parts = versionString.split("-")[0].split("\\.");
        var first = Integer.parseInt(parts[0]);
        var major = first == 1 ? Integer.parseInt(parts[1]) : first;
        var minor = first == 1 ? (parts.length >= 3 ? Integer.parseInt(parts[2]) : 0)
                : (parts.length >= 2 ? Integer.parseInt(parts[1]) : 0);

        var lowerVersions = Arrays.stream(Version.values()).filter(version -> {
            if (version.major == major) {
                return version.minor <= minor;
            } else {
                return version.major < major;
            }
        }).toList();

        if (lowerVersions.isEmpty()) throw new IllegalArgumentException("Unsupported Minecraft version: " + versionString);
        return lowerVersions.get(lowerVersions.size() - 1);
    }

    @Deprecated(forRemoval = true)
    public static String getInternalVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }
}
