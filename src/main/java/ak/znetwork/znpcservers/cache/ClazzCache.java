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
    ENTITY_ARMOR_STAND_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityArmorStand" , null),
    ENTITY_BAT_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBat" , null),
    ENTITY_BLAZE_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBlaze" , null),
    ENTITY_CAVE_SPIDER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCaveSpider" , null),
    ENTITY_CHICKEN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityChicken" , null),
    ENTITY_COW_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCow" , null),
    ENTITY_CREEPER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCreeper" , null),
    ENTITY_ENDER_DRAGON_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderDragon" , null),
    ENTITY_ENDERMAN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderman" , null),
    ENTITY_ENDERMITE_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEndermite" , null),
    ENTITY_GHAST_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGhast" , null),
    ENTITY_IRON_GOLEM_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityIronGolem" , null),
    ENTITY_GIANT_ZOMBIE_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGiantZombie" , null),
    ENTITY_GUARDIAN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGuardian" , null),
    ENTITY_HORSE_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHorse" , null),
    ENTITY_LLAMA_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(11) ? "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLlama" : null), null),
    ENTITY_MAGMA_CUBE_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMagmaCube" , null),
    ENTITY_MUSHROOM_COW_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMushroomCow" , null),
    ENTITY_OCELOT_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityOcelot" , null),
    ENTITY_PARROT_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(12) ?"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityParrot": null) , null),
    ENTITY_PIG_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPig" , null),
    ENTITY_PIG_ZOMBIE_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPigZombie" , null),
    ENTITY_POLAR_BEAR_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(10) ?"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPolarBear" : null), null),
    ENTITY_SHEEP_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySheep" , null),
    ENTITY_SILVERFISH_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySilverfish" , null),
    ENTITY_SKELETON_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySkeleton" , null),
    ENTITY_SLIME_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySlime" , null),
    ENTITY_SPIDER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySpider" , null),
    ENTITY_SQUID_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySquid" , null),
    ENTITY_VILLAGER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityVillager" , null),
    ENTITY_WITCH_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWitch" , null),
    ENTITY_WITHER_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWither" , null),
    ENTITY_ZOMBIE_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityZombie" , null),
    ENTITY_WOLF_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWolf" , null),
    ENTITY_END_CRYSTAL_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderCrystal" , null),

    ENTITY_TYPES_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(12) ? "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityTypes" : null), null),
    ENTITY_TYPES_A_METHOD(ClazzType.METHOD, "a", (Utils.isVersionNewestThan(13) ? "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityTypes" : null) , String.class),

    ENTITY_PLAYER_ARRAY_CLASS(ClazzType.CLASS, "[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;" , null),

    ENTITY_HUMAN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHuman" , null),

    ITEM_STACK_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ItemStack" , null),
    CRAFT_ITEM_STACK_CLASS(ClazzType.CLASS, "org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".inventory.CraftItemStack" , null),

    PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy", null),
    PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn", null),
    PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntityLiving", null),

    PACKET_PLAY_OUT_ENTITY_SPAWN_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntity", null),

    PACKET_PLAY_IN_USE_ENTITY_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayInUseEntity", null),

    PACKET_PLAY_OUT_ENTITY_METADATA_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityMetadata", null),

    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityEquipment", null),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutScoreboardTeam", null),

    ENUM_ITEM_SLOT_CLASS(ClazzType.CLASS, (Utils.isVersionNewestThan(9) ? "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumItemSlot" : ""), null),
    ENUM_PLAYER_INFO_ACTION_CLASS(ClazzType.CLASS, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction", null),
    ENUM_CHAT_FORMAT_CLASS(ClazzType.CLASS,  (Utils.isVersionNewestThan(9) ?"net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumChatFormat" : ""), null),

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
                    else {
                        if (clazzCache.object != null && clazzCache.name != null) clazzCache.method = Class.forName((String) clazzCache.object).getMethod(clazzCache.name , clazzCache.classes);
                    }
                    break;
                case CLASS:
                    if (clazzCache.name != null && clazzCache.name.length() > 0) clazzCache.aClass = Class.forName(clazzCache.name);
                    break;
            }
        }
    }
}
