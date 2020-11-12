package ak.znetwork.znpcservers.cache;

import ak.znetwork.znpcservers.cache.type.ClazzType;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public enum ClazzCache {

    // Methods
    GET_SERVER_METHOD(ClazzType.METHOD,8, "getServer" , Bukkit.getServer().getClass()),
    GET_HANDLE_METHOD(ClazzType.METHOD,8, "getHandle" , "org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".CraftWorld"),

    // Classes
    WORLD_CLASS(ClazzType.CLASS, 8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".World" , null),

    PLAYER_INTERACT_MANAGER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PlayerInteractManager" , null),

    ENTITY_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity", null),
    ENTITY_LIVING_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLiving", null),

    ENTITY_PLAYER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer" , null),
    ENTITY_ARMOR_STAND_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityArmorStand" , null),
    ENTITY_BAT_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBat" , null),
    ENTITY_BLAZE_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBlaze" , null),
    ENTITY_CAVE_SPIDER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCaveSpider" , null),
    ENTITY_CHICKEN_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityChicken" , null),
    ENTITY_COW_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCow" , null),
    ENTITY_CREEPER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCreeper" , null),
    ENTITY_ENDER_DRAGON_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderDragon" , null),
    ENTITY_ENDERMAN_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderman" , null),
    ENTITY_ENDERMITE_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEndermite" , null),
    ENTITY_GHAST_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGhast" , null),
    ENTITY_IRON_GOLEM_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityIronGolem" , null),
    ENTITY_GIANT_ZOMBIE_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGiantZombie" , null),
    ENTITY_GUARDIAN_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGuardian" , null),
    ENTITY_HORSE_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHorse" , null),
    ENTITY_LLAMA_CLASS(ClazzType.CLASS, 11, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLlama", null),
    ENTITY_MAGMA_CUBE_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMagmaCube" , null),
    ENTITY_MUSHROOM_COW_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMushroomCow" , null),
    ENTITY_OCELOT_CLASS(ClazzType.CLASS,8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityOcelot" , null),
    ENTITY_PARROT_CLASS(ClazzType.CLASS, 12, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityParrot", null),
    ENTITY_PIG_CLASS(ClazzType.CLASS, 8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPig" , null),
    ENTITY_PIG_ZOMBIE_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPigZombie" , null),
    ENTITY_POLAR_BEAR_CLASS(ClazzType.CLASS, 10, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPolarBear", null),
    ENTITY_SHEEP_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySheep" , null),
    ENTITY_SILVERFISH_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySilverfish" , null),
    ENTITY_SKELETON_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySkeleton" , null),
    ENTITY_SLIME_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySlime" , null),
    ENTITY_SPIDER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySpider" , null),
    ENTITY_SQUID_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySquid" , null),
    ENTITY_VILLAGER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityVillager" , null),
    ENTITY_WITCH_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWitch" , null),
    ENTITY_WITHER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWither" , null),
    ENTITY_ZOMBIE_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityZombie" , null),
    ENTITY_WOLF_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWolf" , null),
    ENTITY_END_CRYSTAL_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderCrystal" , null),

    ENTITY_TYPES_CLASS(ClazzType.CLASS, 12, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityTypes", null),

    ENTITY_PLAYER_ARRAY_CLASS(ClazzType.CLASS, 8,"[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;" , null),

    ENTITY_HUMAN_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHuman" , null),

    ITEM_STACK_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ItemStack" , null),
    CRAFT_ITEM_STACK_CLASS(ClazzType.CLASS, 8,"org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".inventory.CraftItemStack" , null),

    PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy", null),
    PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn", null),
    PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntityLiving", null),

    PACKET_PLAY_OUT_ENTITY_SPAWN_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntity", null),

    PACKET_PLAY_IN_USE_ENTITY_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayInUseEntity", null),

    PACKET_PLAY_OUT_ENTITY_METADATA_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityMetadata", null),

    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityEquipment", null),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM(ClazzType.CLASS,8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutScoreboardTeam", null),

    ENUM_ITEM_SLOT_CLASS(ClazzType.CLASS,9, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumItemSlot", null),
    ENUM_PLAYER_INFO_ACTION_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction", null),
    ENUM_CHAT_FORMAT_CLASS(ClazzType.CLASS,  9, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumChatFormat", null),

    PACKET_PLAY_OUT_PLAYER_INFO(ClazzType.CLASS,8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo", null),

    PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS(ClazzType.CLASS,8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityTeleport", null),
    PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS(ClazzType.CLASS,8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityHeadRotation", null),
    PACKET_PLAY_OUT_ENTITY_LOOK_CLASS(ClazzType.CLASS,8, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntity$PacketPlayOutEntityLook", null),

    I_CHAT_BASE_COMPONENT_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".IChatBaseComponent", null),

    DATA_WATCHER_CLASS(ClazzType.CLASS, 8,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcher", null),
    DATA_WATCHER_OBJECT_CLASS(ClazzType.CLASS, 9, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherObject", null),
    DATA_WATCHER_REGISTRY_CLASS(ClazzType.CLASS,9, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherRegistry", null),

    DATA_WATCHER_SERIALIZER_CLASS(ClazzType.CLASS, 9, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherSerializer", null),

    //METHODS
    ENTITY_TYPES_A_METHOD(ClazzType.METHOD, 13,"a", ClazzCache.ENTITY_TYPES_CLASS.getOrLoad(), String.class),
    SET_CUSTOM_NAME_OLD_METHOD(ClazzType.METHOD, 8,"setCustomName", ClazzCache.ENTITY_CLASS.getOrLoad(), String.class),
    SET_CUSTOM_NAME_NEW_METHOD(ClazzType.METHOD, 13,"setCustomName", ClazzCache.ENTITY_CLASS.getOrLoad(), ClazzCache.I_CHAT_BASE_COMPONENT_CLASS.getOrLoad()),
    SET_LOCATION_METHOD(ClazzType.METHOD, 8,"setLocation", ClazzCache.ENTITY_CLASS.getOrLoad(), double.class , double.class , double.class , float.class , float.class),

    DATA_WATCHER_OBJECT_CONSTRUCTOR(ClazzType.CONSTRUCTOR,9, "", ClazzCache.DATA_WATCHER_OBJECT_CLASS.getOrLoad(), int.class, ClazzCache.DATA_WATCHER_SERIALIZER_CLASS.getOrLoad()),

    // CONSTRUCTOR
    PLAYER_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8, "z", ClazzCache.ENTITY_PLAYER_CLASS.getOrLoad()),
    PLAYER_INTERACT_MANAGER_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8, "z", ClazzCache.PLAYER_INTERACT_MANAGER_CLASS.getOrLoad()),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8, "a", ClazzCache.PACKET_PLAY_OUT_SCOREBOARD_TEAM.getOrLoad()),

    PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR(ClazzType.CONSTRUCTOR,8, "", ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO.getOrLoad(), ClazzCache.ENUM_PLAYER_INFO_ACTION_CLASS.getOrLoad() , ClazzCache.ENTITY_PLAYER_ARRAY_CLASS.getOrLoad()),
    PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_LOOK_CLASS.getOrLoad(), int.class , byte.class , byte.class , boolean.class),
    PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS.getOrLoad(), ClazzCache.ENTITY_CLASS.getOrLoad(), byte.class),
    PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS.getOrLoad(), ClazzCache.ENTITY_CLASS.getOrLoad()),
    PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_METADATA_CLASS.getOrLoad(), int.class, ClazzCache.DATA_WATCHER_CLASS.getOrLoad(), boolean.class),
    PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS.getOrLoad(), ClazzCache.ENTITY_HUMAN_CLASS.getOrLoad()),
    PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS.getOrLoad(), int[].class),

    PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS.getOrLoad(), ClazzCache.ENTITY_LIVING_CLASS.getOrLoad()),

    ARMOR_STAND_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8,"", ClazzCache.ENTITY_ARMOR_STAND_CLASS.getOrLoad(), ClazzCache.WORLD_CLASS.getOrLoad(), double.class, double.class, double.class),

    //FIELDS
    DATA_WATCHER_REGISTER_ENUM_FIELD(ClazzType.FIELD, 9, "a", ClazzCache.DATA_WATCHER_REGISTRY_CLASS.getOrLoad(), null);

    public final ClazzType clazzType;

    public final String name;

    public Object object;

    public int minVersion;

    public Class<?> aClass;

    public final Class<?>[] classes;

    public Constructor<?> constructor;
    public Method method;
    public Object field;

    ClazzCache(final ClazzType clazzType, final int minVersion, final String name , Object object , final Class<?>... classes)  {
        this.clazzType = clazzType;

        this.minVersion = minVersion;
        this.name = name;
        this.object = object;

        this.classes = classes;
    }

    public Class<?> getOrLoad() {
        if (this.name == null || this.name.length() <= 0) return null;
        if (Utils.getVersion() < minVersion) return null;

        try { return aClass == null ? aClass = Class.forName(name) : aClass;
        } catch (ClassNotFoundException e) {e.printStackTrace();} return null;
    }

    public static void load() throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        for (final ClazzCache clazzCache : ClazzCache.values()) {
            if (Utils.getVersion() < clazzCache.minVersion) continue;
            switch (clazzCache.clazzType) {
                case METHOD:
                    if (clazzCache.object instanceof Class) clazzCache.method  = ((Class<?>)clazzCache.object).getMethod(clazzCache.name , clazzCache.classes);
                    else {
                        if (clazzCache.object != null && clazzCache.name != null) clazzCache.method = Class.forName((String) clazzCache.object).getMethod(clazzCache.name , clazzCache.classes);
                    }
                    break;
                case CLASS:
                    if (clazzCache.aClass == null) clazzCache.getOrLoad(); // Try
                    break;
                case CONSTRUCTOR:
                    if (clazzCache.object instanceof Class) {
                        if (clazzCache.name.length() > 0) {
                            if (clazzCache.name.equalsIgnoreCase("z")) clazzCache.constructor = ((Class<?>)clazzCache.object).getDeclaredConstructors()[0];
                            else clazzCache.constructor = ((Class<?>)clazzCache.object).getConstructor();
                        }
                        else clazzCache.constructor = ((Class<?>)clazzCache.object).getConstructor(clazzCache.classes);
                    }
                    break;
                case FIELD:
                    if (clazzCache.name != null && clazzCache.name.length() > 0) clazzCache.field = clazzCache.aClass.getField(clazzCache.name).get(null);
                    break;
            }
        }
    }
}
