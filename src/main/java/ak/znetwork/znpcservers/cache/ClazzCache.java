package ak.znetwork.znpcservers.cache;

import ak.znetwork.znpcservers.cache.type.ClazzType;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

public enum ClazzCache {

    // Methods
    GET_SERVER_METHOD(ClazzType.METHOD,"getServer" , Bukkit.getServer().getClass()),
    GET_HANDLE_METHOD(ClazzType.METHOD,"getHandle" , "org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".CraftWorld"),

    // Classes
    WORLD_CLASS(ClazzType.CLASS,"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".World" , null),

    PLAYER_INTERACT_MANAGER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PlayerInteractManager" , null),

    ENTITY_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity", null),
    ENTITY_LIVING_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLiving", null),

    ENTITY_PLAYER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer" , null),
    ENTITY_PLAYER_ARRAY_CLASS(ClazzType.CLASS, "[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;" , null),

    ENTITY_ARMOR_STAND_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityArmorStand" , null),
    ENTITY_HUMAN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHuman" , null),

    ITEM_STACK_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ItemStack" , null),
    CRAFT_ITEM_STACK_CLASS(ClazzType.CLASS, "org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".inventory.CraftItemStack" , null),

    PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy", null),
    PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn", null),
    PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntityLiving", null),

    PACKET_PLAY_IN_USE_ENTITY_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayInUseEntity", null),

    PACKET_PLAY_OUT_ENTITY_METADATA_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityMetadata", null),

    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityEquipment", null),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutScoreboardTeam", null),

    ENUM_ITEM_SLOT_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(9) ? "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumItemSlot" : ""), null),
    ENUM_PLAYER_INFO_ACTION_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction", null),

    PACKET_PLAY_OUT_PLAYER_INFO(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo", null),

    PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityTeleport", null),
    PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityHeadRotation", null),
    PACKET_PLAY_OUT_ENTITY_LOOK_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntity$PacketPlayOutEntityLook", null),

    I_CHAT_BASE_COMPONENT_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".IChatBaseComponent", null),

    DATA_WATCHER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcher", null),
    DATA_WATCHER_OBJECT_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(9) ? "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherObject" : ""), null),
    DATA_WATCHER_REGISTRY_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(9) ?"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherRegistry" : ""), null);

    public final ClazzType clazzType;

    public final String name;

    public Object object;

    public Class<?> aClass;

    public final Class<?>[] classes;

    public Method method;

    ClazzCache(final ClazzType clazzType, final String name , Object object , final Class<?>... classes)  {
        this.clazzType = clazzType;

        this.name = name;
        this.object = object;

        this.classes = classes;
    }

    public static void load() throws NoSuchMethodException, ClassNotFoundException {
        for (final ClazzCache clazzCache : ClazzCache.values()) {
            switch (clazzCache.clazzType) {
                case METHOD:
                    if (clazzCache.object instanceof Class) clazzCache.method  = ((Class<?>)clazzCache.object).getMethod(clazzCache.name , clazzCache.classes);
                    else clazzCache.method  = Class.forName((String) clazzCache.object).getMethod(clazzCache.name , clazzCache.classes);
                    break;
                case CLASS:
                    if (clazzCache.name.length() > 0) clazzCache.aClass = Class.forName(clazzCache.name);
                    break;
            }
        }
    }
}
