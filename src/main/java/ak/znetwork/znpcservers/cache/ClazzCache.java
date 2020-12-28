package ak.znetwork.znpcservers.cache;

import ak.znetwork.znpcservers.cache.exception.ClassLoadException;
import ak.znetwork.znpcservers.cache.type.ClazzType;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public enum ClazzCache {

    // Classes
    WORLD_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".World" , null),
    CRAFT_WORLD_CLASS(ClazzType.CLASS, 8, 20,"org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".CraftWorld", null),

    PLAYER_INTERACT_MANAGER_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PlayerInteractManager" , null),

    ENTITY_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity", null),
    ENTITY_LIVING_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLiving", null),

    ENTITY_PLAYER_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer" , null),
    ENTITY_ARMOR_STAND_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityArmorStand" , null),
    ENTITY_BAT_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBat" , null),
    ENTITY_BLAZE_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBlaze" , null),
    ENTITY_CAVE_SPIDER_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCaveSpider" , null),
    ENTITY_CHICKEN_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityChicken" , null),
    ENTITY_COW_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCow" , null),
    ENTITY_CREEPER_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCreeper" , null),
    ENTITY_ENDER_DRAGON_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderDragon" , null),
    ENTITY_ENDERMAN_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderman" , null),
    ENTITY_ENDERMITE_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEndermite" , null),
    ENTITY_GHAST_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGhast" , null),
    ENTITY_IRON_GOLEM_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityIronGolem" , null),
    ENTITY_GIANT_ZOMBIE_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGiantZombie" , null),
    ENTITY_GUARDIAN_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGuardian" , null),
    ENTITY_HORSE_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHorse" , null),
    ENTITY_LLAMA_CLASS(ClazzType.CLASS, 11, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLlama", null),
    ENTITY_MAGMA_CUBE_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMagmaCube" , null),
    ENTITY_MUSHROOM_COW_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMushroomCow" , null),
    ENTITY_OCELOT_CLASS(ClazzType.CLASS,8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityOcelot" , null),
    ENTITY_PARROT_CLASS(ClazzType.CLASS, 12, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityParrot", null),
    ENTITY_PIG_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPig" , null),
    ENTITY_PIG_ZOMBIE_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPigZombie" , null),
    ENTITY_POLAR_BEAR_CLASS(ClazzType.CLASS, 10, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPolarBear", null),
    ENTITY_SHEEP_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySheep" , null),
    ENTITY_SILVERFISH_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySilverfish" , null),
    ENTITY_SKELETON_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySkeleton" , null),
    ENTITY_SLIME_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySlime" , null),
    ENTITY_SPIDER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySpider" , null),
    ENTITY_SQUID_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySquid" , null),
    ENTITY_VILLAGER_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityVillager" , null),
    ENTITY_WITCH_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWitch" , null),
    ENTITY_WITHER_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWither" , null),
    ENTITY_ZOMBIE_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityZombie" , null),
    ENTITY_WOLF_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWolf" , null),
    ENTITY_END_CRYSTAL_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderCrystal" , null),

    ENTITY_TYPES_CLASS(ClazzType.CLASS, 12, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityTypes", null),

    ENTITY_PLAYER_ARRAY_CLASS(ClazzType.CLASS, 8, 20, "[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;" , null),

    ENTITY_HUMAN_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHuman" , null),

    ITEM_STACK_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ItemStack" , null),
    CRAFT_ITEM_STACK_CLASS(ClazzType.CLASS, 8, 20,"org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".inventory.CraftItemStack" , null),

    CRAFT_PLAYER_CLASS(ClazzType.CLASS, 8, 20,"org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".entity.CraftPlayer" , null),

    PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy", null),
    PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS(ClazzType.CLASS, 8,20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn", null),
    PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntityLiving", null),

    PACKET_PLAY_OUT_ENTITY_SPAWN_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntity", null),

    PACKET_PLAY_IN_USE_ENTITY_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayInUseEntity", null),

    PACKET_PLAY_OUT_ENTITY_METADATA_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityMetadata", null),

    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityEquipment", null),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM(ClazzType.CLASS,8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutScoreboardTeam", null),

    ENUM_ITEM_SLOT_CLASS(ClazzType.CLASS,9, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumItemSlot", null),
    ENUM_PLAYER_INFO_ACTION_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction", null),
    ENUM_CHAT_FORMAT_CLASS(ClazzType.CLASS,  9, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumChatFormat", null),

    PACKET_PLAY_OUT_PLAYER_INFO(ClazzType.CLASS,8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo", null),

    PLAYER_CONNECTION_CLASS(ClazzType.CLASS,8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PlayerConnection", null),
    NETWORK_MANAGER_CLASS(ClazzType.CLASS,8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".NetworkManager", null),

    PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS(ClazzType.CLASS,8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityTeleport", null),
    PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS(ClazzType.CLASS,8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityHeadRotation", null),
    PACKET_PLAY_OUT_ENTITY_LOOK_CLASS(ClazzType.CLASS,8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntity$PacketPlayOutEntityLook", null),

    I_CHAT_BASE_COMPONENT_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".IChatBaseComponent", null),

    DATA_WATCHER_CLASS(ClazzType.CLASS, 8, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcher", null),
    DATA_WATCHER_OBJECT_CLASS(ClazzType.CLASS, 9, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherObject", null),
    DATA_WATCHER_REGISTRY_CLASS(ClazzType.CLASS,9, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherRegistry", null),

    DATA_WATCHER_SERIALIZER_CLASS(ClazzType.CLASS, 9, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherSerializer", null),

    CHAT_COMPONENT_TEXT_CLASS(ClazzType.CLASS, 9, 20,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ChatComponentText", null),

    //METHODS
    GET_SERVER_METHOD(ClazzType.METHOD,8, 20,"getServer" , Bukkit.getServer().getClass()),
    GET_HANDLE_METHOD(ClazzType.METHOD,8, 20,"getHandle" , ClazzCache.CRAFT_WORLD_CLASS.getAClass()),

    GET_HANDLE_PLAYER_METHOD(ClazzType.METHOD, 8, 20,"getHandle", ClazzCache.CRAFT_PLAYER_CLASS.getAClass()),

    ENTITY_TYPES_A_METHOD(ClazzType.METHOD, 13,20,"a", ClazzCache.ENTITY_TYPES_CLASS.getAClass(), String.class),
    SET_CUSTOM_NAME_OLD_METHOD(ClazzType.METHOD, 8,12,"setCustomName", ClazzCache.ENTITY_CLASS.getAClass(), String.class),
    SET_CUSTOM_NAME_NEW_METHOD(ClazzType.METHOD, 13,20,"setCustomName", ClazzCache.ENTITY_CLASS.getAClass(), ClazzCache.I_CHAT_BASE_COMPONENT_CLASS.getAClass()),
    SET_LOCATION_METHOD(ClazzType.METHOD, 8,20,"setLocation", ClazzCache.ENTITY_CLASS.getAClass(), double.class , double.class , double.class , float.class , float.class),

    SET_CUSTOM_NAME_VISIBLE_METHOD(ClazzType.METHOD, 8,20,"setCustomNameVisible", ClazzCache.ENTITY_ARMOR_STAND_CLASS.getAClass(), boolean.class),
    SET_INVISIBLE_METHOD(ClazzType.METHOD, 8,20,"setInvisible", ClazzCache.ENTITY_ARMOR_STAND_CLASS.getAClass(), boolean.class),

    ICHAT_BASE_COMPONENT_A_METHOD(ClazzType.METHOD, 8,20,"a", ClazzCache.I_CHAT_BASE_COMPONENT_CLASS.getAClass(), String.class),

    DATA_WATCHER_OBJECT_CONSTRUCTOR(ClazzType.CONSTRUCTOR,9, 20, "", ClazzCache.DATA_WATCHER_OBJECT_CLASS.getAClass(), int.class, ClazzCache.DATA_WATCHER_SERIALIZER_CLASS.getAClass()),

    GET_DATA_WATCHER_METHOD(ClazzType.METHOD, 8, 20, "getDataWatcher", ClazzCache.ENTITY_LIVING_CLASS.getAClass()),

    SET_DATA_WATCHER_METHOD(ClazzType.METHOD, 9,20, "set", ClazzCache.DATA_WATCHER_CLASS.getAClass(), ClazzCache.DATA_WATCHER_OBJECT_CLASS.getAClass(), Object.class),
    WATCH_DATA_WATCHER_METHOD(ClazzType.METHOD, 8,8, "watch", ClazzCache.DATA_WATCHER_CLASS.getAClass(), int.class, Object.class),

    GET_ENUM_CHAT_ID(ClazzType.METHOD, 9, 20, "b", ClazzCache.ENUM_CHAT_FORMAT_CLASS.getAClass()),
    GET_ENUM_CHAT_NAME(ClazzType.METHOD, 9,20, "a", ClazzCache.ENUM_CHAT_FORMAT_CLASS.getAClass(), int.class),

    GET_ENUM_CHAT_TO_STRING(ClazzType.METHOD, 9, 20, "toString", ClazzCache.ENUM_CHAT_FORMAT_CLASS.getAClass()),

    AS_NMS_COPY_METHOD(ClazzType.METHOD, 8,20,"asNMSCopy", ClazzCache.CRAFT_ITEM_STACK_CLASS.getAClass(), ItemStack.class),

    GET_PROFILE_METHOD(ClazzType.METHOD, 8, 20,"getProfile", ClazzCache.ENTITY_HUMAN_CLASS.getAClass()),

    GET_ID_METHOD(ClazzType.METHOD, 8, 20,"getId", ClazzCache.ENTITY_LIVING_CLASS.getAClass()),

    // CONSTRUCTOR
    CHAT_COMPONENT_TEXT_CONSTRUCTOR(ClazzType.CONSTRUCTOR,9, 20,"", ClazzCache.CHAT_COMPONENT_TEXT_CLASS.getAClass(), String.class),

    PLAYER_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8,20, "", ClazzCache.ENTITY_PLAYER_CLASS.getAClass()),
    PLAYER_INTERACT_MANAGER_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8, 20,"z", ClazzCache.PLAYER_INTERACT_MANAGER_CLASS.getAClass()),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8,20, "a", ClazzCache.PACKET_PLAY_OUT_SCOREBOARD_TEAM.getAClass()),

    PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8, 20,"", ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO.getAClass(), ClazzCache.ENUM_PLAYER_INFO_ACTION_CLASS.getAClass() , ClazzCache.ENTITY_PLAYER_ARRAY_CLASS.getAClass()),
    PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,20,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_LOOK_CLASS.getAClass(), int.class , byte.class , byte.class , boolean.class),
    PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", ClazzCache.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS.getAClass(), ClazzCache.ENTITY_CLASS.getAClass(), byte.class),
    PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,20,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS.getAClass(), ClazzCache.ENTITY_CLASS.getAClass()),
    PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,20,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_METADATA_CLASS.getAClass(), int.class, ClazzCache.DATA_WATCHER_CLASS.getAClass(), boolean.class),
    PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,20,"", ClazzCache.PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS.getAClass(), ClazzCache.ENTITY_HUMAN_CLASS.getAClass()),
    PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,20,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS.getAClass(), int[].class),

    PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,20,"", ClazzCache.PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS.getAClass(), ClazzCache.ENTITY_LIVING_CLASS.getAClass()),

    ARMOR_STAND_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,20,"", ClazzCache.ENTITY_ARMOR_STAND_CLASS.getAClass(), ClazzCache.WORLD_CLASS.getAClass(), double.class, double.class, double.class),

    //FIELDS
    DATA_WATCHER_REGISTER_ENUM_FIELD(ClazzType.FIELD, 9, 20, "a", ClazzCache.DATA_WATCHER_REGISTRY_CLASS.getAClass(), null),
    PLAYER_CONNECTION_FIELD(ClazzType.FIELD, 8, 20, "playerConnection", ClazzCache.ENTITY_PLAYER_CLASS.getAClass()),

    NETWORK_MANAGER_FIELD(ClazzType.FIELD, 8, 20, "networkManager", ClazzCache.PLAYER_CONNECTION_CLASS.getAClass()),
    CHANNEL_FIELD(ClazzType.FIELD, 8, 20, "channel", ClazzCache.NETWORK_MANAGER_CLASS.getAClass()),

    ID_FIELD(ClazzType.FIELD, 8, 20, "a", ClazzCache.PACKET_PLAY_IN_USE_ENTITY_CLASS.getAClass()),

    REMOVE_PLAYER_FIELD(ClazzType.FIELD, 8, 20, "REMOVE_PLAYER", ClazzCache.ENUM_PLAYER_INFO_ACTION_CLASS.getAClass());

    public final ClazzType clazzType;

    public final String name;

    public Object object;

    public int minVersion,maxVersion;

    public Class<?> aClass;

    public final Class<?>[] classes;

    public Constructor<?> constructor;
    public Method method;
    public Field field;

    ClazzCache(final ClazzType clazzType, final int minVersion, final int maxVersion, final String name , Object object , final Class<?>... classes)  {
        this.clazzType = clazzType;

        this.minVersion = minVersion;
        this.maxVersion = maxVersion;

        this.name = name;
        this.object = object;

        this.classes = classes;
    }

    public Class<?> getAClass() {
        try {
            return (aClass != null ? aClass : Class.forName(name));
        } catch (ClassNotFoundException e) { 
            return null;
        }
    }

    public void load() throws ClassLoadException {
        if (Utils.getVersion() < this.minVersion  || Utils.getVersion() > this.maxVersion) return;

        if (this.clazzType == ClazzType.CLASS) { // Class
            try {
                this.aClass = Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new ClassLoadException("Class could not be loaded: " + name(), e.getCause());
            }
        } else if (this.clazzType == ClazzType.METHOD) { // Method
            try {
                this.method  = ((Class<?>) this.object).getMethod(this.name , this.classes);
            } catch (NoSuchMethodException e) {
                try {
                    this.method = ((Class<?>) this.object).getDeclaredClasses()[0].getMethod(this.name, this.classes);
                } catch (NoSuchMethodException noSuchMethodException) {
                    throw new ClassLoadException("Method could not be loaded: " + name(), e.getCause());
                }
            }
        } else if (this.clazzType == ClazzType.FIELD) { // Field
            if (this.name != null && this.name.length() > 0) {
                try {
                    this.field = ((Class<?>) this.object).getField(this.name);
                } catch (NoSuchFieldException e) {
                    try {
                        this.field = ((Class<?>) this.object).getDeclaredField(this.name);
                        this.field.setAccessible(true);
                    } catch (NoSuchFieldException noSuchFieldException) {
                        throw new ClassLoadException("Field could not be loaded: " + name(), e.getCause());
                    }
                }
            }
        } else { // Constructor
            if (this.object instanceof Class) {
                try {
                    this.constructor = ((Class<?>) this.object).getConstructor(this.classes);
                } catch (NoSuchMethodException e) {
                    try {
                        this.constructor = ((Class<?>) this.object).getConstructor();
                    } catch (NoSuchMethodException noSuchMethodException) {
                        this.constructor = ((Class<?>) this.object).getDeclaredConstructors()[0];
                    }
                }
            }
        }
    }
}
