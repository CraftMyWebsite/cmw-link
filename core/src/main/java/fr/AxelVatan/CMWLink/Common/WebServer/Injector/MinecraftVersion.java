package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public final class MinecraftVersion implements Comparable<MinecraftVersion>, Serializable {

	public static final MinecraftVersion WILD_UPDATE = new MinecraftVersion("1.19");
	public static final MinecraftVersion CAVES_CLIFFS_2 = new MinecraftVersion("1.18");
	public static final MinecraftVersion CAVES_CLIFFS_1 = new MinecraftVersion("1.17");
	public static final MinecraftVersion NETHER_UPDATE_2 = new MinecraftVersion("1.16.2");
	public static final MinecraftVersion NETHER_UPDATE = new MinecraftVersion("1.16");
	public static final MinecraftVersion BEE_UPDATE = new MinecraftVersion("1.15");
	public static final MinecraftVersion VILLAGE_UPDATE = new MinecraftVersion("1.14");
	public static final MinecraftVersion AQUATIC_UPDATE = new MinecraftVersion("1.13");
	public static final MinecraftVersion COLOR_UPDATE = new MinecraftVersion("1.12");
	public static final MinecraftVersion EXPLORATION_UPDATE = new MinecraftVersion("1.11");
	public static final MinecraftVersion FROSTBURN_UPDATE = new MinecraftVersion("1.10");
	public static final MinecraftVersion COMBAT_UPDATE = new MinecraftVersion("1.9");
	public static final MinecraftVersion BOUNTIFUL_UPDATE = new MinecraftVersion("1.8");
	public static final MinecraftVersion SKIN_UPDATE = new MinecraftVersion("1.7.8");
	public static final MinecraftVersion WORLD_UPDATE = new MinecraftVersion("1.7.2");
	public static final MinecraftVersion HORSE_UPDATE = new MinecraftVersion("1.6.1");
	public static final MinecraftVersion REDSTONE_UPDATE = new MinecraftVersion("1.5.0");
	public static final MinecraftVersion SCARY_UPDATE = new MinecraftVersion("1.4.2");
	public static final MinecraftVersion LATEST = WILD_UPDATE;
	private static final long serialVersionUID = -8695133558996459770L;
	private static final Pattern VERSION_PATTERN = Pattern.compile(".*\\(.*MC.\\s*([a-zA-z0-9\\-.]+).*");
	private static MinecraftVersion currentVersion;
	private final int major;
	private final int minor;
	private final int build;
	private final String development;
	private final SnapshotVersion snapshot;
	private volatile Boolean atCurrentOrAbove;

	public MinecraftVersion(Server server) {
		this(extractVersion(server.getVersion()));
	}

	public MinecraftVersion(String versionOnly) {
		this(versionOnly, true);
	}

	private MinecraftVersion(String versionOnly, boolean parseSnapshot) {
		String[] section = versionOnly.split("-");
		SnapshotVersion snapshot = null;
		int[] numbers = new int[3];

		try {
			numbers = this.parseVersion(section[0]);
		} catch (NumberFormatException cause) {
			if (!parseSnapshot) {
				throw cause;
			}
			try {
				snapshot = new SnapshotVersion(section[0]);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				MinecraftVersion latest = new MinecraftVersion(MinecraftVersion.LATEST.getVersion(), false);
				boolean newer = snapshot.getSnapshotDate().compareTo(format.parse("2022-08-05")) > 0;
				numbers[0] = latest.getMajor();
				numbers[1] = latest.getMinor() + (newer ? 1 : -1);
			} catch (Exception e) {
				throw new IllegalStateException("Cannot parse " + section[0], e);
			}
		}
		this.major = numbers[0];
		this.minor = numbers[1];
		this.build = numbers[2];
		this.development = section.length > 1 ? section[1] : (snapshot != null ? "snapshot" : null);
		this.snapshot = snapshot;
	}

	public MinecraftVersion(int major, int minor, int build) {
		this(major, minor, build, null);
	}

	public MinecraftVersion(int major, int minor, int build, String development) {
		this.major = major;
		this.minor = minor;
		this.build = build;
		this.development = development;
		this.snapshot = null;
	}

	public static String extractVersion(String text) {
		Matcher version = VERSION_PATTERN.matcher(text);
		if (version.matches() && version.group(1) != null) {
			return version.group(1);
		} else {
			throw new IllegalStateException("Cannot parse version String '" + text + "'");
		}
	}

	public static MinecraftVersion fromServerVersion(String serverVersion) {
		return new MinecraftVersion(extractVersion(serverVersion));
	}

	public static MinecraftVersion getCurrentVersion() {
		if (currentVersion == null) {
			currentVersion = fromServerVersion(Bukkit.getVersion());
		}

		return currentVersion;
	}

	public static void setCurrentVersion(MinecraftVersion version) {
		currentVersion = version;
	}

	public static boolean atOrAbove(MinecraftVersion version) {
		return getCurrentVersion().isAtLeast(version);
	}

	private int[] parseVersion(String version) {
		String[] elements = version.split("\\.");
		int[] numbers = new int[3];
		if (elements.length < 1) {
			throw new IllegalStateException("Corrupt MC version: " + version);
		}
		for (int i = 0; i < Math.min(numbers.length, elements.length); i++) {
			numbers[i] = Integer.parseInt(elements[i].trim());
		}
		return numbers;
	}

	public int getMajor() {
		return this.major;
	}

	public int getMinor() {
		return this.minor;
	}

	public int getBuild() {
		return this.build;
	}

	public String getDevelopmentStage() {
		return this.development;
	}

	public SnapshotVersion getSnapshot() {
		return this.snapshot;
	}

	public boolean isSnapshot() {
		return this.snapshot != null;
	}

	public boolean atOrAbove() {
		if (this.atCurrentOrAbove == null) {
			this.atCurrentOrAbove = MinecraftVersion.atOrAbove(this);
		}

		return this.atCurrentOrAbove;
	}

	public String getVersion() {
		if (this.getDevelopmentStage() == null) {
			return String.format("%s.%s.%s", this.getMajor(), this.getMinor(), this.getBuild());
		} else {
			return String.format("%s.%s.%s-%s%s", this.getMajor(), this.getMinor(), this.getBuild(), this.getDevelopmentStage(), this.isSnapshot() ? this.snapshot : "");
		}
	}

	@Override
	public int compareTo(MinecraftVersion o) {
		if (o == null) {
			return 1;
		}
		return ComparisonChain.start()
				.compare(this.getMajor(), o.getMajor())
				.compare(this.getMinor(), o.getMinor())
				.compare(this.getBuild(), o.getBuild())
				.compare(this.getDevelopmentStage(), o.getDevelopmentStage(), Ordering.natural().nullsLast())
				.compare(this.getSnapshot(), o.getSnapshot(), Ordering.natural().nullsFirst())
				.result();
	}

	public boolean isAtLeast(MinecraftVersion other) {
		if (other == null) {
			return false;
		}
		return this.compareTo(other) >= 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof MinecraftVersion) {
			MinecraftVersion other = (MinecraftVersion) obj;
			return this.getMajor() == other.getMajor() &&
					this.getMinor() == other.getMinor() &&
					this.getBuild() == other.getBuild() &&
					Objects.equals(this.getDevelopmentStage(), other.getDevelopmentStage());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getMajor(), this.getMinor(), this.getBuild());
	}

	@Override
	public String toString() {
		return String.format("(MC: %s)", this.getVersion());
	}
}