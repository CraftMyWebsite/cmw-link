package fr.CraftMyWebsite.CMWLink.Common.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Versions {

	V1_21_4("1.21.4", "v1_21_R3"),
	V1_21_3("1.21.3", "v1_21_R2"),
	V1_21_2("1.21.2", "v1_21_R2"),
    V1_21_1("1.21.1", "v1_21_R1"),
    V1_21("1.21", "v1_21_R1"),

    V1_20_6("1.20.6", "v1_20_R4"),
    V1_20_5("1.20.5", "v1_20_R4"),
    V1_20_4("1.20.4", "v1_20_R3"),
    V1_20_3("1.20.3", "v1_20_R3"),
    V1_20_2("1.20.2", "v1_20_R2"),
    V1_20_1("1.20.1", "v1_20_R1"),
    V1_20("1.20", "v1_20_R1"),

    V1_19_4("1.19.4", "v1_19_R3"),
    V1_19_3("1.19.3", "v1_19_R2"),
    V1_19_2("1.19.2", "v1_19_R1"),
    V1_19_1("1.19.1", "v1_19_R1"),
    V1_19("1.19", "v1_19_R1"),

    V1_18_2("1.18.2", "v1_18_R2"),
    V1_18_1("1.18.1", "v1_18_R1"),
    V1_18("1.18", "v1_18_R1"),

    V1_17_1("1.17.1", "v1_17_R1"),
    V1_17("1.17", "v1_17_R1"),

    V1_16_5("1.16.5", "v1_16_R3"),
    V1_16_4("1.16.4", "v1_16_R3"),
    V1_16_3("1.16.3", "v1_16_R2"),
    V1_16_2("1.16.2", "v1_16_R2"),
    V1_16_1("1.16.1", "v1_16_R1"),
    V1_16("1.16", "v1_16_R1");

    private final String name;
    private final String packageName;

    Versions(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public String getMinecraftVersion() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public static Versions fromVersion(String bukkitVersion) {
        Pattern versionPattern = Pattern.compile("(?i)\\(MC:? ([0-9]+\\.[0-9]+(?:\\.[0-9]+)?)\\)");
        Matcher matcher = versionPattern.matcher(bukkitVersion);
        if (matcher.find()) {
            String minecraftVersion = matcher.group(1);
            for (Versions version : values()) {
                if (minecraftVersion.equals(version.getMinecraftVersion())) {
                    return version;
                }
            }
        }
        return null;
    }
}
