
package fr.AxelVatan.CMWLink.Common.WebServer.Injector;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.AbstractFuzzyMatcher;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.FuzzyMatchers;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.FuzzyMethodContract;
import fr.AxelVatan.CMWLink.Common.WebServer.Injector.Fuzzy.FuzzyReflection;
import io.netty.buffer.Unpooled;

public final class MinecraftReflection {

	private static final ClassSource CLASS_SOURCE = ClassSource.fromClassLoader();
	private static final String CANONICAL_REGEX = "(\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)+\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
	private static final String MINECRAFT_CLASS_NAME_REGEX = "net\\.minecraft\\." + CANONICAL_REGEX;
	static CachedPackage minecraftPackage;
	static CachedPackage craftbukkitPackage;
	static CachedPackage libraryPackage;
	private static String DYNAMIC_PACKAGE_MATCHER = null;
	private static String MINECRAFT_PREFIX_PACKAGE = "net.minecraft.server";
	private static String MINECRAFT_FULL_PACKAGE = null;
	private static String CRAFTBUKKIT_PACKAGE = null;
	private static AbstractFuzzyMatcher<Class<?>> fuzzyMatcher;
	private static Boolean cachedWatcherObject;

	private MinecraftReflection() {
	}

	private static void setDynamicPackageMatcher(String regex) {
		DYNAMIC_PACKAGE_MATCHER = regex;
		fuzzyMatcher = null;
	}

	public static String getMinecraftPackage() {
		if (MINECRAFT_FULL_PACKAGE != null) {
			return MINECRAFT_FULL_PACKAGE;
		}
		try {
			Server craftServer = Bukkit.getServer();
			CRAFTBUKKIT_PACKAGE = craftServer.getClass().getPackage().getName();
			if (MinecraftVersion.CAVES_CLIFFS_1.atOrAbove()) {
				MINECRAFT_FULL_PACKAGE = MINECRAFT_PREFIX_PACKAGE = "net.minecraft";
				setDynamicPackageMatcher(MINECRAFT_CLASS_NAME_REGEX);
			} else {
				Method getHandle = getCraftEntityClass().getMethod("getHandle");
				MINECRAFT_FULL_PACKAGE = getHandle.getReturnType().getPackage().getName();
				setDynamicPackageMatcher(MINECRAFT_CLASS_NAME_REGEX);
			}
			return MINECRAFT_FULL_PACKAGE;
		} catch (NoSuchMethodException exception) {
			throw new IllegalStateException("Cannot find getHandle() in CraftEntity", exception);
		}
	}

	static void setMinecraftPackage(String minecraftPackage, String craftBukkitPackage) {
		MINECRAFT_FULL_PACKAGE = minecraftPackage;
		CRAFTBUKKIT_PACKAGE = craftBukkitPackage;
		if (getMinecraftServerClass() == null) {
			throw new IllegalArgumentException("Cannot find MinecraftServer for package " + minecraftPackage);
		}
		setDynamicPackageMatcher(MINECRAFT_CLASS_NAME_REGEX);
	}

	public static boolean isMinecraftObject(Object obj) {
		if (obj == null) {
			return false;
		}
		return obj.getClass().getName().startsWith(MINECRAFT_PREFIX_PACKAGE);
	}

	public static boolean isMinecraftClass(Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("clazz cannot be NULL.");
		}

		return getMinecraftObjectMatcher().isMatch(clazz, null);
	}

	public static boolean isMinecraftObject(Object obj, String className) {
		if (obj == null) {
			return false;
		}
		String javaName = obj.getClass().getName();
		return javaName.startsWith(MINECRAFT_PREFIX_PACKAGE) && javaName.endsWith(className);
	}

	public static boolean is(Class<?> clazz, Object object) {
		if (clazz == null || object == null) {
			return false;
		}
		return clazz.isAssignableFrom(object.getClass());
	}

	public static boolean is(Class<?> clazz, Class<?> test) {
		if (clazz == null || test == null) {
			return false;
		}

		return clazz.isAssignableFrom(test);
	}

	public static boolean isBlockPosition(Object obj) {
		return is(getBlockPositionClass(), obj);
	}

	public static boolean isChunkCoordIntPair(Object obj) {
		return is(getChunkCoordIntPair(), obj);
	}

	public static boolean isPacketClass(Object obj) {
		return is(getPacketClass(), obj);
	}

	public static boolean isServerHandler(Object obj) {
		return is(getPlayerConnectionClass(), obj);
	}

	public static boolean isMinecraftEntity(Object obj) {
		return is(getEntityClass(), obj);
	}

	public static boolean isCraftPlayer(Object value) {
		return is(getCraftPlayerClass(), value);
	}

	public static boolean isMinecraftPlayer(Object obj) {
		return is(getEntityPlayerClass(), obj);
	}

	public static boolean isDataWatcher(Object obj) {
		return is(getDataWatcherClass(), obj);
	}

	public static boolean isIntHashMap(Object obj) {
		return is(getIntHashMapClass(), obj);
	}

	public static Class<?> getEntityPlayerClass() {
		try {
			return getMinecraftClass("server.level.EntityPlayer", "server.level.ServerPlayer", "EntityPlayer");
		} catch (RuntimeException e) {
			try {
				Method getHandle = FuzzyReflection
						.fromClass(getCraftBukkitClass("entity.CraftPlayer"))
						.getMethodByName("getHandle");
				return setMinecraftClass("EntityPlayer", getHandle.getReturnType());
			} catch (IllegalArgumentException e1) {
				throw new RuntimeException("Could not find EntityPlayer class.", e1);
			}
		}
	}

	public static Class<?> getGameProfileClass() {
		return getClass("com.mojang.authlib.GameProfile");
	}

	public static Class<?> getEntityClass() {
		try {
			return getMinecraftClass("server.level.Entity", "server.level.ServerEntity", "Entity");
		} catch (RuntimeException e) {
			return fallbackMethodReturn("Entity", "entity.CraftEntity", "getHandle");
		}
	}

	public static Class<?> getCraftChatMessage() {
		return getCraftBukkitClass("util.CraftChatMessage");
	}

	public static Class<?> getWorldServerClass() {
		try {
			return getMinecraftClass("server.level.WorldServer", "server.level.ServerLevel", "WorldServer");
		} catch (RuntimeException e) {
			return fallbackMethodReturn("WorldServer", "CraftWorld", "getHandle");
		}
	}

	public static Class<?> getNmsWorldClass() {
		try {
			return getMinecraftClass("world.level.World", "world.level.Level", "World");
		} catch (RuntimeException e) {
			return setMinecraftClass("World", getWorldServerClass().getSuperclass());
		}
	}

	private static Class<?> fallbackMethodReturn(String nmsClass, String craftClass, String methodName) {
		Class<?> result = FuzzyReflection.fromClass(getCraftBukkitClass(craftClass))
				.getMethodByName(methodName)
				.getReturnType();
		return setMinecraftClass(nmsClass, result);
	}

	public static Class<?> getPacketClass() {
		return getMinecraftClass("network.protocol.Packet", "Packet");
	}

	public static Class<?> getByteBufClass() {
		return getClass("io.netty.buffer.ByteBuf");
	}

	public static Class<?> getEnumProtocolClass() {
		return getMinecraftClass("network.EnumProtocol", "network.ConnectionProtocol", "EnumProtocol");
	}

	public static Class<?> getIChatBaseComponentClass() {
		return getMinecraftClass("network.chat.IChatBaseComponent", "network.chat.IChatbaseComponent", "network.chat.Component", "IChatBaseComponent");
	}

	public static Class<?> getIChatBaseComponentArrayClass() {
		return getArrayClass(getIChatBaseComponentClass());
	}

	public static Class<?> getChatComponentTextClass() {
		return getMinecraftClass("network.chat.ChatComponentText", "network.chat.TextComponent", "ChatComponentText");
	}

	public static Class<?> getChatSerializerClass() {
		return getMinecraftClass("network.chat.IChatBaseComponent$ChatSerializer", "network.chat.Component$Serializer", "IChatBaseComponent$ChatSerializer");
	}

	public static Class<?> getServerPingClass() {
		return getMinecraftClass("network.protocol.status.ServerPing", "network.protocol.status.ServerStatus", "ServerPing");
	}

	public static Class<?> getServerPingServerDataClass() {
		return getMinecraftClass("network.protocol.status.ServerPing$ServerData", "network.protocol.status.ServerStatus$Version", "ServerPing$ServerData");
	}

	public static Class<?> getServerPingPlayerSampleClass() {
		return getMinecraftClass(
				"network.protocol.status.ServerPing$ServerPingPlayerSample",
				"network.protocol.status.ServerStatus$Players",
				"ServerPing$ServerPingPlayerSample");
	}

	public static Class<?> getMinecraftServerClass() {
		try {
			return getMinecraftClass("server.MinecraftServer", "MinecraftServer");
		} catch (RuntimeException e) {
			// Reset cache and try again
			setMinecraftClass("MinecraftServer", null);

			useFallbackServer();
			return getMinecraftClass("MinecraftServer");
		}
	}

	public static Class<?> getStatisticClass() {
		return getMinecraftClass("stats.Statistic", "stats.Stat", "Statistic");
	}

	public static Class<?> getStatisticListClass() {
		return getMinecraftClass("stats.StatisticList", "stats.Stats", "StatisticList");
	}

	public static Class<?> getPlayerListClass() {
		try {
			return getMinecraftClass("server.players.PlayerList", "PlayerList");
		} catch (RuntimeException e) {
			// Reset cache and try again
			setMinecraftClass("PlayerList", null);

			useFallbackServer();
			return getMinecraftClass("PlayerList");
		}
	}

	public static Class<?> getPlayerConnectionClass() {
		return getMinecraftClass("server.network.PlayerConnection", "server.network.ServerGamePacketListenerImpl", "PlayerConnection");
	}

	public static Class<?> getNetworkManagerClass() {
		return getMinecraftClass("network.NetworkManager", "network.Connection", "NetworkManager");
	}

	public static Class<?> getBlockClass() {
		return getMinecraftClass("world.level.block.Block", "Block");
	}

	public static Class<?> getItemClass() {
		return getNullableNMS("world.item.Item", "Item");
	}

	public static Class<?> getFluidTypeClass() {
		return getNullableNMS("world.level.material.FluidType", "world.level.material.Fluid", "FluidType");
	}

	public static Class<?> getParticleTypeClass() {
		return getNullableNMS("core.particles.ParticleType", "core.particles.SimpleParticleType", "ParticleType");
	}

	public static Class<?> getWorldTypeClass() {
		return getMinecraftClass("WorldType");
	}

	public static Class<?> getDataWatcherClass() {
		return getMinecraftClass("network.syncher.DataWatcher", "network.syncher.SynchedEntityData", "DataWatcher");
	}

	public static Class<?> getBlockPositionClass() {
		return getMinecraftClass("core.BlockPosition", "core.BlockPos", "BlockPosition");
	}

	public static Class<?> getVec3DClass() {
		return getMinecraftClass("world.phys.Vec3D", "world.phys.Vec3", "Vec3D");
	}

	public static Class<?> getChunkCoordIntPair() {
		return getMinecraftClass("world.level.ChunkCoordIntPair", "world.level.ChunkPos", "ChunkCoordIntPair");
	}

	public static Class<?> getDataWatcherItemClass() {
		return getMinecraftClass("network.syncher.DataWatcher$Item", "network.syncher.SynchedEntityData$DataItem", "DataWatcher$Item", "DataWatcher$WatchableObject");
	}

	public static Class<?> getDataWatcherObjectClass() {
		return getNullableNMS("network.syncher.DataWatcherObject", "network.syncher.EntityDataAccessor", "DataWatcherObject");
	}

	public static boolean watcherObjectExists() {
		if (cachedWatcherObject == null) {
			cachedWatcherObject = getDataWatcherObjectClass() != null;
		}

		return cachedWatcherObject;
	}

	public static Class<?> getDataWatcherSerializerClass() {
		return getNullableNMS("network.syncher.DataWatcherSerializer", "network.syncher.EntityDataSerializer", "DataWatcherSerializer");
	}

	public static Class<?> getDataWatcherRegistryClass() {
		return getMinecraftClass("network.syncher.DataWatcherRegistry", "network.syncher.EntityDataSerializers", "DataWatcherRegistry");
	}

	public static Class<?> getMinecraftKeyClass() {
		return getMinecraftClass("resources.MinecraftKey", "resources.ResourceLocation", "MinecraftKey");
	}

	public static Class<?> getMobEffectListClass() {
		return getMinecraftClass("world.effect.MobEffectList", "world.effect.MobEffect", "MobEffectList");
	}

	public static Class<?> getSoundEffectClass() {
		return getNullableNMS("sounds.SoundEffect", "sounds.SoundEvent", "SoundEffect");
	}

	public static Class<?> getServerConnectionClass() {
		return getMinecraftClass("server.network.ServerConnection", "server.network.ServerConnectionListener", "ServerConnection");
	}

	public static Class<?> getNBTBaseClass() {
		return getMinecraftClass("nbt.NBTBase", "nbt.Tag", "NBTBase");
	}

	public static Class<?> getNBTReadLimiterClass() {
		return getMinecraftClass("nbt.NBTReadLimiter", "nbt.NbtAccounter", "NBTReadLimiter");
	}

	public static Class<?> getNBTCompoundClass() {
		return getMinecraftClass("nbt.NBTTagCompound", "nbt.CompoundTag", "NBTTagCompound");
	}

	public static Class<?> getEntityTrackerClass() {
		return getMinecraftClass("server.level.PlayerChunkMap$EntityTracker", "server.level.ChunkMap$TrackedEntity", "EntityTracker");
	}

	public static Class<?> getAttributeSnapshotClass() {
		return getMinecraftClass(
				"network.protocol.game.PacketPlayOutUpdateAttributes$AttributeSnapshot",
				"network.protocol.game.ClientboundUpdateAttributesPacket$AttributeSnapshot",
				"AttributeSnapshot",
				"PacketPlayOutUpdateAttributes$AttributeSnapshot");
	}

	public static Class<?> getIntHashMapClass() {
		return getNullableNMS("IntHashMap");
	}

	public static Class<?> getAttributeModifierClass() {
		return getMinecraftClass("world.entity.ai.attributes.AttributeModifier", "AttributeModifier");
	}

	public static Class<?> getMobEffectClass() {
		return getMinecraftClass("world.effect.MobEffect", "world.effect.MobEffectInstance", "MobEffect");
	}

	public static Class<?> getPacketDataSerializerClass() {
		return getMinecraftClass("network.PacketDataSerializer", "network.FriendlyByteBuf", "PacketDataSerializer");
	}

	public static Class<?> getNbtCompressedStreamToolsClass() {
		return getMinecraftClass("nbt.NBTCompressedStreamTools", "nbt.NbtIo", "NBTCompressedStreamTools");
	}

	public static Class<?> getTileEntityClass() {
		return getMinecraftClass("world.level.block.entity.TileEntity", "world.level.block.entity.BlockEntity", "TileEntity");
	}

	public static Class<?> getMinecraftGsonClass() {
		return getMinecraftLibraryClass("com.google.gson.Gson");
	}

	public static Class<?> getArrayClass(Class<?> componentType) {
		return Array.newInstance(componentType, 0).getClass();
	}

	public static Class<?> getCraftPlayerClass() {
		return getCraftBukkitClass("entity.CraftPlayer");
	}

	public static Class<?> getCraftWorldClass() {
		return getCraftBukkitClass("CraftWorld");
	}

	public static Class<?> getCraftEntityClass() {
		return getCraftBukkitClass("entity.CraftEntity");
	}

	public static Class<?> getCraftMessageClass() {
		return getCraftBukkitClass("util.CraftChatMessage");
	}

	public static Class<?> getPlayerInfoDataClass() {
		return getMinecraftClass(
				"network.protocol.game.PacketPlayOutPlayerInfo$PlayerInfoData",
				"network.protocol.game.ClientboundPlayerInfoPacket$PlayerUpdate",
				"PacketPlayOutPlayerInfo$PlayerInfoData", "PlayerInfoData");
	}

	public static boolean isPlayerInfoData(Object obj) {
		return is(getPlayerInfoDataClass(), obj);
	}

	public static Class<?> getIBlockDataClass() {
		return getMinecraftClass("world.level.block.state.IBlockData", "world.level.block.state.BlockState", "IBlockData");
	}

	public static Class<?> getMultiBlockChangeInfoClass() {
		return getMinecraftClass("MultiBlockChangeInfo", "PacketPlayOutMultiBlockChange$MultiBlockChangeInfo");
	}

	public static Class<?> getMultiBlockChangeInfoArrayClass() {
		return getArrayClass(getMultiBlockChangeInfoClass());
	}

	public static boolean signUpdateExists() {
		return getNullableNMS("PacketPlayOutUpdateSign") != null;
	}

	public static Class<?> getNonNullListClass() {
		return getMinecraftClass("core.NonNullList", "NonNullList");
	}

	public static Class<?> getCraftSoundClass() {
		return getCraftBukkitClass("CraftSound");
	}

	public static Class<?> getSectionPositionClass() {
		return getMinecraftClass("core.SectionPosition", "core.SectionPos", "SectionPosition");
	}

	private static Class<?> getClass(String className) {
		try {
			return getClassSource().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Cannot find class " + className, e);
		}
	}

	public static Class<?> getCraftBukkitClass(String className) {
		if (craftbukkitPackage == null) {
			craftbukkitPackage = new CachedPackage(CRAFTBUKKIT_PACKAGE, getClassSource());
		}

		return craftbukkitPackage.getPackageClass(className)
				.orElseThrow(() -> new RuntimeException("Failed to find CraftBukkit class: " + className));
	}

	public static Class<?> getMinecraftClass(String className) {
		if (minecraftPackage == null) {
			minecraftPackage = new CachedPackage(getMinecraftPackage(), getClassSource());
		}

		return minecraftPackage.getPackageClass(className)
				.orElseThrow(() -> new RuntimeException("Failed to find NMS class: " + className));
	}

	public static Class<?> getNullableNMS(String className, String... aliases) {
		try {
			return getMinecraftClass(className, aliases);
		} catch (RuntimeException ex) {
			return null;
		}
	}

	private static Class<?> setMinecraftClass(String className, Class<?> clazz) {
		if (minecraftPackage == null) {
			minecraftPackage = new CachedPackage(getMinecraftPackage(), getClassSource());
		}

		minecraftPackage.setPackageClass(className, clazz);
		return clazz;
	}

	private static ClassSource getClassSource() {
		return CLASS_SOURCE;
	}

	public static Class<?> getMinecraftClass(String className, String... aliases) {
		if (minecraftPackage == null) {
			minecraftPackage = new CachedPackage(getMinecraftPackage(), getClassSource());
		}

		return minecraftPackage.getPackageClass(className).orElseGet(() -> {
			Class<?> resolved = null;
			for (String alias : aliases) {
				resolved = minecraftPackage.getPackageClass(alias).orElse(null);
				if (resolved != null) {
					break;
				}
			}
			if (resolved != null) {
				minecraftPackage.setPackageClass(className, resolved);
				return resolved;
			}
			throw new RuntimeException(String.format("Unable to find %s (%s)", className, String.join(", ", aliases)));
		});
	}

	public static Class<?> getMinecraftLibraryClass(String className) {
		if (libraryPackage == null) {
			libraryPackage = new CachedPackage("", getClassSource());
		}

		return libraryPackage.getPackageClass(className)
				.orElseThrow(() -> new RuntimeException("Failed to find class: " + className));
	}

	private static Class<?> setMinecraftLibraryClass(String className, Class<?> clazz) {
		if (libraryPackage == null) {
			libraryPackage = new CachedPackage("", getClassSource());
		}

		libraryPackage.setPackageClass(className, clazz);
		return clazz;
	}

	public static String getNetworkManagerName() {
		return getNetworkManagerClass().getSimpleName();
	}

	public static Object getPacketDataSerializer(Object buffer) {
		try {
			Class<?> packetSerializer = getPacketDataSerializerClass();
			return packetSerializer.getConstructor(getByteBufClass()).newInstance(buffer);
		} catch (Exception e) {
			throw new RuntimeException("Cannot construct packet serializer.", e);
		}
	}

	public static Object createPacketDataSerializer(int initialSize) {
		if (initialSize <= 0) {
			initialSize = 256;
		}

		Object buffer = Unpooled.buffer(initialSize);
		return getPacketDataSerializer(buffer);
	}

	public static Class<?> getNbtTagTypes() {
		return getMinecraftClass("nbt.NBTTagTypes", "nbt.TagTypes", "NBTTagTypes");
	}

	public static Class<?> getChatDeserializer() {
		return getMinecraftClass("util.ChatDeserializer", "util.GsonHelper", "ChatDeserializer");
	}

	public static Class<?> getChatMutableComponentClass() {
		return getMinecraftClass("network.chat.IChatMutableComponent", "network.chat.MutableComponent");
	}

	public static Class<?> getDimensionManager() {
		return getMinecraftClass("world.level.dimension.DimensionManager", "world.level.dimension.DimensionType", "DimensionManager");
	}

	public static Class<?> getMerchantRecipeList() {
		return getMinecraftClass("world.item.trading.MerchantRecipeList", "world.item.trading.MerchantOffers", "MerchantRecipeList");
	}

	public static Class<?> getResourceKey() {
		return getMinecraftClass("resources.ResourceKey", "ResourceKey");
	}

	public static Class<?> getEntityTypes() {
		return getMinecraftClass("world.entity.EntityTypes", "world.entity.EntityType", "EntityTypes");
	}

	public static Class<?> getParticleParam() {
		return getMinecraftClass("core.particles.ParticleParam", "core.particles.ParticleOptions", "ParticleParam");
	}

	public static Class<?> getSectionPosition() {
		return getMinecraftClass("core.SectionPosition", "core.SectionPos", "SectionPosition");
	}

	public static Class<?> getChunkProviderServer() {
		return getMinecraftClass("server.level.ChunkProviderServer", "server.level.ServerChunkCache", "ChunkProviderServer");
	}

	public static Class<?> getPlayerChunkMap() {
		return getMinecraftClass("server.level.PlayerChunkMap", "server.level.ChunkMap", "PlayerChunkMap");
	}

	public static Class<?> getIRegistry() {
		return getNullableNMS("core.IRegistry", "core.Registry", "IRegistry");
	}

	public static Class<?> getAttributeBase() {
		return getMinecraftClass("world.entity.ai.attributes.AttributeBase", "world.entity.ai.attributes.Attribute", "AttributeBase");
	}

	public static Class<?> getProfilePublicKeyClass() {
		return getMinecraftClass("world.entity.player.ProfilePublicKey");
	}

	public static Class<?> getProfilePublicKeyDataClass() {
		return getProfilePublicKeyClass().getClasses()[0];
	}

	public static Class<?> getFastUtilClass(String className) {
		return getLibraryClass("it.unimi.dsi.fastutil." + className);
	}

	public static Class<?> getInt2ObjectMapClass() {
		return getFastUtilClass("ints.Int2ObjectMap");
	}

	public static Class<?> getIntArrayListClass() {
		return getFastUtilClass("ints.IntArrayList");
	}

	public static Class<?> getLibraryClass(String classname) {
		try {
			return getMinecraftLibraryClass(classname);
		} catch (RuntimeException ex) {
			Class<?> clazz = getMinecraftLibraryClass("org.bukkit.craftbukkit.libs." + classname);
			setMinecraftLibraryClass(classname, clazz);
			return clazz;
		}
	}

	private static void useFallbackServer() {
		Constructor<?> selected = FuzzyReflection.fromClass(getCraftBukkitClass("CraftServer"))
				.getConstructor(FuzzyMethodContract.newBuilder()
						.parameterMatches(getMinecraftObjectMatcher(), 0)
						.parameterCount(2)
						.build());
		Class<?>[] params = selected.getParameterTypes();
		setMinecraftClass("MinecraftServer", params[0]);
		setMinecraftClass("PlayerList", params[1]);
	}

	public static Class<?> getLevelChunkPacketDataClass() {
		return getNullableNMS("network.protocol.game.ClientboundLevelChunkPacketData");
	}

	public static Class<?> getLightUpdatePacketDataClass() {
		return getNullableNMS("network.protocol.game.ClientboundLightUpdatePacketData");
	}

	public static AbstractFuzzyMatcher<Class<?>> getMinecraftObjectMatcher() {
		if (fuzzyMatcher == null) {
			fuzzyMatcher = FuzzyMatchers.matchRegex(getMinecraftObjectRegex(), 0);
		}
		return fuzzyMatcher;
	}

	public static String getMinecraftObjectRegex() {
		if (DYNAMIC_PACKAGE_MATCHER == null) {
			getMinecraftPackage();
		}
		return DYNAMIC_PACKAGE_MATCHER;
	}

	public static Class<?> getBlockEntityTypeClass() {
		return getMinecraftClass("world.level.block.entity.BlockEntityType", "world.level.block.entity.TileEntityTypes", "TileEntityTypes");
	}
}