package ro.kmagic.kreports.utils;

import org.bukkit.Bukkit;

public final class VersionUtility {
    public static String getMinecraftVersion() {
        String bukkitVersion = Bukkit.getBukkitVersion();
        int firstDash = bukkitVersion.indexOf('-');
        return bukkitVersion.substring(0, firstDash);
    }

    public static String getMajorMinorVersion() {
        String minecraftVersion = getMinecraftVersion();
        int lastPeriodIndex = minecraftVersion.lastIndexOf('.');
        return (lastPeriodIndex < 2 ? minecraftVersion : minecraftVersion.substring(0, lastPeriodIndex));
    }


    public static int getMinorVersion() {
        String majorMinorVersion = getMajorMinorVersion();
        int periodIndex = majorMinorVersion.indexOf('.');
        int nextIndex = (periodIndex + 1);

        String minorString = majorMinorVersion.substring(nextIndex);
        return Integer.parseInt(minorString);
    }
}
