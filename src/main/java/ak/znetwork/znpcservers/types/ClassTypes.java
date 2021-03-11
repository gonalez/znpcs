package ak.znetwork.znpcservers.types;

import ak.znetwork.znpcservers.cache.builder.ClassCacheBuilder;
import ak.znetwork.znpcservers.cache.enums.PackageType;
import ak.znetwork.znpcservers.cache.impl.ClassCacheImpl;
import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Contains the constants for cache classes.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ClassTypes {

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_IN_USE_ENTITY_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayInUseEntity")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityEquipment")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_PLAYER_INFO_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Packet")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Entity")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_LIVING = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityLiving")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PLAYER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityPlayer")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ARMOR_STAND_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityArmorStand")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_BAT_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityBat")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_BLAZE_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityBlaze")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CAVE_SPIDER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityCaveSpider")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CHICKEN_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityChicken")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_COW_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityCow")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CREEPER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityCreeper")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDER_DRAGON_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityEnderDragon")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDERMAN_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityEnderman")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_HUMAN_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityHuman")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDER_CRYSTAL_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityEnderCrystal")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDERMITE_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityEndermite")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GHAST_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityGhast")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_IRON_GOLEM_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityIronGolem")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GIANT_ZOMBIE_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityGiantZombie")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GUARDIAN_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityGuardian")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_HORSE_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityHorse")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_LLAMA_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityLlama")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_MAGMA_CUBE_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityMagmaCube")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_MUSHROOM_COW_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityMushroomCow")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_OCELOT_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityOcelot")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PARROT_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityParrot")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PIG_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityPig")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_RABBIT_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityRabbit")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PIG_ZOMBIE_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityPigZombie")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_POLAR_BEAR_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityPolarBear")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PANDA_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityPanda")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SHEEP_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntitySheep")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SNOWMAN_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntitySnowman")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SHULKER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityShulker")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SILVERFISH_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntitySilverfish")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SKELETON_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntitySkeleton")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SLIME_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntitySlime")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SPIDER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntitySpider")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SQUID_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntitySquid")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_VILLAGER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityVillager")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WITCH_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityWitch")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WITHER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityWither")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ZOMBIE_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityZombie")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WOLF_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityWolf")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_TYPES_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityTypes")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_CHAT_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EnumChatFormat")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_ITEM_SLOT = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EnumItemSlot")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> I_CHAT_BASE_COMPONENT = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("IChatBaseComponent")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ITEM_STACK_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("ItemStack")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("DataWatcher")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_OBJECT = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("DataWatcherObject")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_SERIALIZER = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("DataWatcherSerializer")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> WORLD_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("World")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> CRAFT_ITEM_STACK_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.CRAFT_BUKKIT).
            className("inventory.CraftItemStack")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> WORLD_SERVER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("WorldServer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> MINECRAFT_SERVER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("MinecraftServer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PLAYER_INTERACT_MANAGER_CLASS = new ClassCacheImpl.Default.ClassLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PlayerInteractManager")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityPlayer").
            parameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class, PLAYER_INTERACT_MANAGER_CLASS)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutPlayerInfo").
            parameterTypes(ENUM_PLAYER_INFO_CLASS, Iterable.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntity$PacketPlayOutEntityLook").
            parameterTypes(int.class, byte.class, byte.class, boolean.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityHeadRotation").
            parameterTypes(ClassTypes.ENTITY_CLASS, byte.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityTeleport").
            parameterTypes(ENTITY_CLASS)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityMetadata").
            parameterTypes(int.class, ClassTypes.DATA_WATCHER_CLASS, boolean.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutNamedEntitySpawn").
            parameterTypes(ENTITY_HUMAN_CLASS)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityDestroy").
            parameterTypes(int[].class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutSpawnEntityLiving").
            parameterTypes(ENTITY_LIVING)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PlayerInteractManager").
            parameterTypes(WORLD_CLASS)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PlayerInteractManager").
            parameterTypes(WORLD_SERVER_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutScoreboardTeam")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityEquipment").
            parameterTypes(int.class, int.class, ClassTypes.ITEM_STACK_CLASS)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityEquipment").
            parameterTypes(int.class, ClassTypes.ENUM_ITEM_SLOT, ClassTypes.ITEM_STACK_CLASS)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("ChatComponentText").
            parameterTypes(String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEW = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutEntityEquipment").
            parameterTypes(int.class, List.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> ENTITY_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityArmorStand").
            parameterTypes(ClassTypes.WORLD_CLASS, double.class, double.class, double.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> DATA_WATCHER_OBJECT_CONSTRUCTOR = new ClassCacheImpl.Default.ConstructorLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("DataWatcherObject").
            parameterTypes(int.class, DATA_WATCHER_SERIALIZER)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method AS_NMS_COPY_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.CRAFT_BUKKIT).
            className("inventory.CraftItemStack").
            methodName("asNMSCopy").
            parameterTypes(ItemStack.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_PROFILE_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityHuman").
            methodName("getProfile")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_ENTITY_ID = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Entity").
            methodName("getId")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_HANDLE_PLAYER_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.CRAFT_BUKKIT).
            className("entity.CraftPlayer").
            methodName("getHandle")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_HANDLE_WORLD_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.CRAFT_BUKKIT).
            className("CraftWorld").
            methodName("getHandle")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_SERVER_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.CRAFT_BUKKIT).
            className("CraftServer").
            methodName("getServer")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SEND_PACKET_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PlayerConnection").
            methodName("sendPacket").
            parameterTypes(PACKET_CLASS)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_OLD_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Entity").
            methodName("setCustomName").
            parameterTypes(String.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_NEW_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Entity").
            methodName("setCustomName").
            parameterTypes(I_CHAT_BASE_COMPONENT)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_VISIBLE_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Entity").
            methodName("setCustomNameVisible").
            parameterTypes(boolean.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_INVISIBLE_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityArmorStand").
            methodName("setInvisible").
            parameterTypes(boolean.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_LOCATION_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Entity").
            methodName("setPositionRotation").
            parameterTypes(double.class, double.class, double.class, float.class, float.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_DATA_WATCHER_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("DataWatcher").
            methodName("set").
            parameterTypes(DATA_WATCHER_OBJECT, Object.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method WATCH_DATA_WATCHER_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("DataWatcher").
            methodName("watch").
            parameterTypes(int.class, Object.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_DATA_WATCHER_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("Entity").
            methodName("getDataWatcher")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_ENUM_CHAT_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().packageType(PackageType.MINECRAFT_SERVER).
            className("EnumChatFormat").
            methodName("c").
            parameterTypes(String.class)).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_ENUM_CHAT_ID_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EnumChatFormat").
            methodName("b")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method ENUM_CHAT_TO_STRING_METHOD = new ClassCacheImpl.Default.MethodLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EnumChatFormat").
            methodName("toString")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field DATA_WATCHER_REGISTER_ENUM_FIELD = new ClassCacheImpl.Default.FieldLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("DataWatcherRegistry").
            fieldName("a")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field ADD_PLAYER_FIELD = new ClassCacheImpl.Default.FieldLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").
            fieldName("ADD_PLAYER")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field REMOVE_PLAYER_FIELD = new ClassCacheImpl.Default.FieldLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").
            fieldName("REMOVE_PLAYER")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field PLAYER_CONNECTION_FIELD = new ClassCacheImpl.Default.FieldLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EntityPlayer").
            fieldName("playerConnection")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field NETWORK_MANAGER_FIELD = new ClassCacheImpl.Default.FieldLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PlayerConnection").
            fieldName("networkManager")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field CHANNEL_FIELD = new ClassCacheImpl.Default.FieldLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("NetworkManager").
            fieldName("channel")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field PACKET_IN_USE_ENTITY_ID_FIELD = new ClassCacheImpl.Default.FieldLoader(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("PacketPlayInUseEntity").
            fieldName("a")).
    typeOf();

    /**
     * {@inheritDoc}
     */
    private static final Object ENUM_CHAT_FORMAT = new ClassCacheImpl.Default.MultipleLoad(new ClassCacheBuilder().
            packageType(PackageType.MINECRAFT_SERVER).
            className("EnumChatFormat")).
    typeOf();
}
