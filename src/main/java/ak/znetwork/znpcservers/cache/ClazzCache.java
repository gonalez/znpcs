/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
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
import java.util.List;

public enum ClazzCache {

    // Classes
    WORLD_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".World", null),
    CRAFT_WORLD_CLASS(ClazzType.CLASS, 8, 20, "org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".CraftWorld", null),

    PLAYER_INTERACT_MANAGER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PlayerInteractManager", null),

    ENTITY_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity", null),
    ENTITY_LIVING_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLiving", null),

    ENTITY_PLAYER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer", null),
    ENTITY_ARMOR_STAND_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityArmorStand", null),
    ENTITY_BAT_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBat", null),
    ENTITY_BLAZE_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityBlaze", null),
    ENTITY_CAVE_SPIDER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCaveSpider", null),
    ENTITY_CHICKEN_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityChicken", null),
    ENTITY_COW_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCow", null),
    ENTITY_CREEPER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityCreeper", null),
    ENTITY_ENDER_DRAGON_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderDragon", null),
    ENTITY_ENDERMAN_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderman", null),
    ENTITY_ENDERMITE_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEndermite", null),
    ENTITY_GHAST_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGhast", null),
    ENTITY_IRON_GOLEM_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityIronGolem", null),
    ENTITY_GIANT_ZOMBIE_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGiantZombie", null),
    ENTITY_GUARDIAN_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityGuardian", null),
    ENTITY_HORSE_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHorse", null),
    ENTITY_LLAMA_CLASS(ClazzType.CLASS, 11, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLlama", null),
    ENTITY_MAGMA_CUBE_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMagmaCube", null),
    ENTITY_MUSHROOM_COW_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityMushroomCow", null),
    ENTITY_OCELOT_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityOcelot", null),
    ENTITY_PARROT_CLASS(ClazzType.CLASS, 12, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityParrot", null),
    ENTITY_PIG_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPig", null),
    ENTITY_PIG_ZOMBIE_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPigZombie", null),
    ENTITY_POLAR_BEAR_CLASS(ClazzType.CLASS, 10, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPolarBear", null),
    ENTITY_SHEEP_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySheep", null),
    ENTITY_SILVERFISH_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySilverfish", null),
    ENTITY_SKELETON_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySkeleton", null),
    ENTITY_SLIME_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySlime", null),
    ENTITY_SPIDER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySpider", null),
    ENTITY_SQUID_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntitySquid", null),
    ENTITY_VILLAGER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityVillager", null),
    ENTITY_WITCH_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWitch", null),
    ENTITY_WITHER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWither", null),
    ENTITY_ZOMBIE_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityZombie", null),
    ENTITY_WOLF_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityWolf", null),
    ENTITY_END_CRYSTAL_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityEnderCrystal", null),

    ENTITY_TYPES_CLASS(ClazzType.CLASS, 12, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityTypes", null),

    ENTITY_PLAYER_ARRAY_CLASS(ClazzType.CLASS, 8, 20, "[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;", null),

    ENTITY_HUMAN_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHuman", null),

    ITEM_STACK_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ItemStack", null),
    CRAFT_ITEM_STACK_CLASS(ClazzType.CLASS, 8, 20, "org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".inventory.CraftItemStack", null),

    CRAFT_PLAYER_CLASS(ClazzType.CLASS, 8, 20, "org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".entity.CraftPlayer", null),

    PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy", null),
    PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn", null),
    PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntityLiving", null),

    PACKET_PLAY_OUT_ENTITY_SPAWN_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntity", null),

    PACKET_PLAY_IN_USE_ENTITY_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayInUseEntity", null),

    PACKET_PLAY_OUT_ENTITY_METADATA_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityMetadata", null),

    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityEquipment", null),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutScoreboardTeam", null),

    ENUM_COLOR_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumColor", null),

    ENUM_ITEM_SLOT_CLASS(ClazzType.CLASS, 9, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumItemSlot", null),
    ENUM_PLAYER_INFO_ACTION_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction", null),
    ENUM_CHAT_FORMAT_CLASS(ClazzType.CLASS, 9, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumChatFormat", null),

    PACKET_PLAY_OUT_PLAYER_INFO(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo", null),

    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD(ClazzType.CONSTRUCTOR, 8, 8, "", PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.getCacheClass(), int.class, int.class, ITEM_STACK_CLASS.getCacheClass()),
    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD(ClazzType.CONSTRUCTOR, 9, 15, "", PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.getCacheClass(), int.class, ENUM_ITEM_SLOT_CLASS.getCacheClass(), ITEM_STACK_CLASS.getCacheClass()),
    PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEW(ClazzType.CONSTRUCTOR, 16, 20, "", PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.getCacheClass(), int.class, List.class),

    PLAYER_CONNECTION_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PlayerConnection", null),
    NETWORK_MANAGER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".NetworkManager", null),

    PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityTeleport", null),
    PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityHeadRotation", null),
    PACKET_PLAY_OUT_ENTITY_LOOK_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntity$PacketPlayOutEntityLook", null),

    I_CHAT_BASE_COMPONENT_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".IChatBaseComponent", null),

    DATA_WATCHER_CLASS(ClazzType.CLASS, 8, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcher", null),
    DATA_WATCHER_OBJECT_CLASS(ClazzType.CLASS, 9, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherObject", null),
    DATA_WATCHER_REGISTRY_CLASS(ClazzType.CLASS, 9, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherRegistry", null),

    DATA_WATCHER_SERIALIZER_CLASS(ClazzType.CLASS, 9, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherSerializer", null),

    CHAT_COMPONENT_TEXT_CLASS(ClazzType.CLASS, 9, 20, "net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ChatComponentText", null),

    //METHODS
    GET_SERVER_METHOD(ClazzType.METHOD, 8, 20, "getServer", Bukkit.getServer().getClass()),
    GET_HANDLE_METHOD(ClazzType.METHOD, 8, 20, "getHandle", CRAFT_WORLD_CLASS.getCacheClass()),

    GET_HANDLE_PLAYER_METHOD(ClazzType.METHOD, 8, 20, "getHandle", CRAFT_PLAYER_CLASS.getCacheClass()),

    ENTITY_TYPES_A_METHOD(ClazzType.METHOD, 13, 20, "a", ENTITY_TYPES_CLASS.getCacheClass(), String.class),
    SET_CUSTOM_NAME_OLD_METHOD(ClazzType.METHOD, 8, 12, "setCustomName", ENTITY_CLASS.getCacheClass(), String.class),
    SET_CUSTOM_NAME_NEW_METHOD(ClazzType.METHOD, 13, 20, "setCustomName", ENTITY_CLASS.getCacheClass(), I_CHAT_BASE_COMPONENT_CLASS.getCacheClass()),
    SET_LOCATION_METHOD(ClazzType.METHOD, 8, 20, "setLocation", ENTITY_CLASS.getCacheClass(), double.class, double.class, double.class, float.class, float.class),

    SET_CUSTOM_NAME_VISIBLE_METHOD(ClazzType.METHOD, 8, 20, "setCustomNameVisible", ENTITY_ARMOR_STAND_CLASS.getCacheClass(), boolean.class),
    SET_INVISIBLE_METHOD(ClazzType.METHOD, 8, 20, "setInvisible", ENTITY_ARMOR_STAND_CLASS.getCacheClass(), boolean.class),

    ICHAT_BASE_COMPONENT_A_METHOD(ClazzType.METHOD, 8, 20, "a", I_CHAT_BASE_COMPONENT_CLASS.getCacheClass(), String.class),

    DATA_WATCHER_OBJECT_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 9, 20, "", DATA_WATCHER_OBJECT_CLASS.getCacheClass(), int.class, DATA_WATCHER_SERIALIZER_CLASS.getCacheClass()),

    GET_DATA_WATCHER_METHOD(ClazzType.METHOD, 8, 20, "getDataWatcher", ENTITY_LIVING_CLASS.getCacheClass()),

    SET_DATA_WATCHER_METHOD(ClazzType.METHOD, 9, 20, "set", DATA_WATCHER_CLASS.getCacheClass(), DATA_WATCHER_OBJECT_CLASS.getCacheClass(), Object.class),
    WATCH_DATA_WATCHER_METHOD(ClazzType.METHOD, 8, 8, "watch", DATA_WATCHER_CLASS.getCacheClass(), int.class, Object.class),

    GET_ENUM_CHAT_ID(ClazzType.METHOD, 9, 20, "b", ENUM_CHAT_FORMAT_CLASS.getCacheClass()),
    GET_ENUM_CHAT_NAME(ClazzType.METHOD, 9, 20, "a", ENUM_CHAT_FORMAT_CLASS.getCacheClass(), int.class),

    GET_ENUM_CHAT_TO_STRING(ClazzType.METHOD, 9, 20, "toString", ENUM_CHAT_FORMAT_CLASS.getCacheClass()),

    AS_NMS_COPY_METHOD(ClazzType.METHOD, 8, 20, "asNMSCopy", CRAFT_ITEM_STACK_CLASS.getCacheClass(), ItemStack.class),

    GET_PROFILE_METHOD(ClazzType.METHOD, 8, 20, "getProfile", ENTITY_HUMAN_CLASS.getCacheClass()),

    GET_ID_METHOD(ClazzType.METHOD, 8, 20, "getId", ENTITY_LIVING_CLASS.getCacheClass()),

    // CONSTRUCTOR
    CHAT_COMPONENT_TEXT_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 9, 20, "", CHAT_COMPONENT_TEXT_CLASS.getCacheClass(), String.class),

    PLAYER_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", ENTITY_PLAYER_CLASS.getCacheClass()),
    PLAYER_INTERACT_MANAGER_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "z", PLAYER_INTERACT_MANAGER_CLASS.getCacheClass()),

    PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "a", PACKET_PLAY_OUT_SCOREBOARD_TEAM.getCacheClass()),

    PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_PLAYER_INFO.getCacheClass(), ENUM_PLAYER_INFO_ACTION_CLASS.getCacheClass(), ENTITY_PLAYER_ARRAY_CLASS.getCacheClass()),
    PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_ENTITY_LOOK_CLASS.getCacheClass(), int.class, byte.class, byte.class, boolean.class),
    PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS.getCacheClass(), ENTITY_CLASS.getCacheClass(), byte.class),
    PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS.getCacheClass(), ENTITY_CLASS.getCacheClass()),
    PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_ENTITY_METADATA_CLASS.getCacheClass(), int.class, DATA_WATCHER_CLASS.getCacheClass(), boolean.class),
    PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS.getCacheClass(), ENTITY_HUMAN_CLASS.getCacheClass()),
    PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS.getCacheClass(), int[].class),

    PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS.getCacheClass(), ENTITY_LIVING_CLASS.getCacheClass()),

    ARMOR_STAND_ENTITY_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", ENTITY_ARMOR_STAND_CLASS.getCacheClass(), WORLD_CLASS.getCacheClass(), double.class, double.class, double.class),

    PACKET_PLAY_OUT_SPAWN_ENTITY_NO_ID_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS.getCacheClass(), ClazzCache.ENTITY_LIVING_CLASS.getCacheClass()),
    PACKET_PLAY_OUT_SPAWN_ENTITY_ID_CONSTRUCTOR(ClazzType.CONSTRUCTOR, 8, 20, "", PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS.getCacheClass(), int.class),

    //FIELDS
    DATA_WATCHER_REGISTER_ENUM_FIELD(ClazzType.FIELD, 9, 20, "a", DATA_WATCHER_REGISTRY_CLASS.getCacheClass(), null),
    PLAYER_CONNECTION_FIELD(ClazzType.FIELD, 8, 20, "playerConnection", ENTITY_PLAYER_CLASS.getCacheClass()),

    NETWORK_MANAGER_FIELD(ClazzType.FIELD, 8, 20, "networkManager", PLAYER_CONNECTION_CLASS.getCacheClass()),
    CHANNEL_FIELD(ClazzType.FIELD, 8, 20, "channel", NETWORK_MANAGER_CLASS.getCacheClass()),

    ID_FIELD(ClazzType.FIELD, 8, 20, "a", PACKET_PLAY_IN_USE_ENTITY_CLASS.getCacheClass()),

    ADD_PLAYER_FIELD(ClazzType.FIELD, 8, 20, "ADD_PLAYER", ENUM_PLAYER_INFO_ACTION_CLASS.getCacheClass()),

    REMOVE_PLAYER_FIELD(ClazzType.FIELD, 8, 20, "REMOVE_PLAYER", ENUM_PLAYER_INFO_ACTION_CLASS.getCacheClass());

    private final ClazzType clazzType;

    private final String name;

    private final int minVersion, maxVersion;

    private final Class<?> clazz;

    private final Class<?>[] classes;

    private Class<?> cacheClass;
    private Constructor<?> cacheConstructor;
    private Method cacheMethod;
    private Field cacheField;

    ClazzCache(ClazzType clazzType, int minVersion, int maxVersion, String name, Class<?> clazz, Class<?>... classes) {
        this.clazzType = clazzType;

        this.minVersion = minVersion;
        this.maxVersion = maxVersion;

        this.name = name;
        this.clazz = clazz;

        this.classes = classes;
    }

    public void load() throws ClassLoadException {
        if (Utils.getVersion() < this.minVersion || Utils.getVersion() > this.maxVersion) return;

        if (this.clazzType == ClazzType.CLASS) { // Class
            try {
                this.cacheClass = Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new ClassLoadException("Class could not be loaded: " + name(), e.getCause());
            }
        } else if (this.clazzType == ClazzType.METHOD) { // Method
            try {
                // Skip
                if (name().startsWith("ICHAT")) throw new NoSuchMethodException();

                this.cacheMethod = this.clazz.getMethod(this.name, this.classes);
            } catch (NoSuchMethodException e) {
                try {
                    this.cacheMethod = this.clazz.getDeclaredClasses()[0].getMethod(this.name, this.classes);
                } catch (NoSuchMethodException noSuchMethodException) {
                    throw new ClassLoadException("Method could not be loaded: " + name(), e.getCause());
                }
            }
        } else if (this.clazzType == ClazzType.FIELD) { // Field
            if (this.name != null && this.name.length() > 0) {
                try {
                    this.cacheField = this.clazz.getField(this.name);
                } catch (NoSuchFieldException e) {
                    try {
                        this.cacheField = this.clazz.getDeclaredField(this.name);
                        this.cacheField.setAccessible(true);
                    } catch (NoSuchFieldException noSuchFieldException) {
                        throw new ClassLoadException("Field could not be loaded: " + name(), e.getCause());
                    }
                }
            }
        } else { // Constructor
            try {
                this.cacheConstructor = this.clazz.getConstructor(this.classes);
            } catch (NoSuchMethodException e) {
                try {
                    this.cacheConstructor = this.clazz.getConstructor();
                } catch (NoSuchMethodException noSuchMethodException) {
                    this.cacheConstructor = this.clazz.getDeclaredConstructors()[0];
                }
            }
        }
    }

    public Constructor<?> getCacheConstructor() {
        return cacheConstructor;
    }

    public Field getCacheField() {
        return cacheField;
    }

    public Method getCacheMethod() {
        return cacheMethod;
    }

    public Class<?> getCacheClass() {
        if (cacheClass == null) {
            try {
                load();
            } catch (ClassLoadException e) {
                e.printStackTrace();
            }
        }
        return cacheClass;
    }
}
