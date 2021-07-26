package ak.znetwork.znpcservers.types;

import ak.znetwork.znpcservers.cache.builder.ClassCacheBuilder;
import ak.znetwork.znpcservers.utility.Utils;

import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static ak.znetwork.znpcservers.cache.impl.ClassCacheImpl.Default.*;
import static ak.znetwork.znpcservers.cache.impl.PackageImpl.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ClassTypes {
    /**
     * Returns true if the current version is v1.17+.
     */
    private static final boolean V17 = Utils.BUKKIT_VERSION > 16;

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_IN_USE_ENTITY_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayInUseEntity")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_PLAYER_INFO_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PROTOCOL)).
            className("Packet")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY)).
            className("Entity")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_LIVING = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY)).
            className("EntityLiving")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PLAYER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SERVER_LEVEL)).
            className("EntityPlayer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ARMOR_STAND_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "decoration")).
            className("EntityArmorStand")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_BAT_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "ambient")).
            className("EntityBat")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_BLAZE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityBlaze")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CAVE_SPIDER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityCaveSpider")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CHICKEN_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityChicken")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_COW_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityCow")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CREEPER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityCreeper")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDER_DRAGON_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "boss.enderdragon")).
            className("EntityEnderDragon")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDERMAN_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityEnderman")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_HUMAN_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "player")).
            className("EntityHuman")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDER_CRYSTAL_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "boss.enderdragon")).
            className("EntityEnderCrystal")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDERMITE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityEndermite")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GHAST_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityGhast")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_IRON_GOLEM_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityIronGolem")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GIANT_ZOMBIE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityGiantZombie")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GUARDIAN_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityGuardian")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_HORSE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal.horse")).
            className("EntityHorse")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_LLAMA_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal.horse")).
            className("EntityLlama")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_MAGMA_CUBE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityMagmaCube")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_MUSHROOM_COW_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityMushroomCow")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_OCELOT_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityOcelot")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PARROT_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityParrot")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PIG_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityPig")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_RABBIT_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityRabbit")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_POLAR_BEAR_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityPolarBear")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PANDA_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityPanda")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SHEEP_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntitySheep")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SNOWMAN_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntitySnowman")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SHULKER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityShulker")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SILVERFISH_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntitySilverfish")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SKELETON_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntitySkeleton")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SLIME_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntitySlime")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SPIDER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntitySpider")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SQUID_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntitySquid")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_VILLAGER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "npc")).
            className("EntityVillager")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WITCH_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityWitch")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WITHER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "boss.wither")).
            className("EntityWither")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ZOMBIE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "monster")).
            className("EntityZombie")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WOLF_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityWolf")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_AXOLOTL_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal.axolotl")).
            className("Axolotl")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GOAT_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal.goat")).
            className("Goat")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_FOX_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY, "animal")).
            className("EntityFox")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_TYPES_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY)).
            className("EntityTypes")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_CHAT_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.NONE)).
            className("EnumChatFormat")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_ITEM_SLOT = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY)).
            className("EnumItemSlot")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> I_CHAT_BASE_COMPONENT = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.CHAT)).
            className("IChatBaseComponent")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ITEM_STACK_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ITEM)).
            className("ItemStack")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SYNCHER)).
            className("DataWatcher")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_OBJECT = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SYNCHER)).
            className("DataWatcherObject")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_REGISTRY = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SYNCHER)).
            className("DataWatcherRegistry")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_SERIALIZER = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SYNCHER)).
            className("DataWatcherSerializer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> WORLD_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.WORLD_LEVEL)).
            className("World")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> CRAFT_ITEM_STACK_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("inventory.CraftItemStack")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> WORLD_SERVER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SERVER_LEVEL)).
            className("WorldServer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> MINECRAFT_SERVER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SERVER)).
            className("MinecraftServer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PLAYER_INTERACT_MANAGER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SERVER_LEVEL)).
            className("PlayerInteractManager")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> CRAFT_SERVER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("CraftServer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PLAYER_CONNECTION_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SERVER_NETWORK)).
            className("PlayerConnection")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> NETWORK_MANAGER_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.NETWORK)).
            className("NetworkManager")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_OUT_PLAYER_INFO_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutPlayerInfo")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutScoreboardTeam")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntityDestroy")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> SCOREBOARD_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.WORLD_SCORES)).
            className("Scoreboard")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> SCOREBOARD_TEAM_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.WORLD_SCORES)).
            className("ScoreboardTeam")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> CHAT_MESSAGE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.CHAT)).
            className("ChatMessage")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_TAG_VISIBILITY = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.WORLD_SCORES)).
            className("ScoreboardTeamBase$EnumNameTagVisibility")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = new ClazzLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("util.CraftChatMessage")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> CHAT_MESSAGE_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.CHAT)).
            className(CHAT_MESSAGE_CLASS).
            parameterTypes(String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> SCOREBOARD_TEAM_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(SCOREBOARD_TEAM_CLASS).
            parameterTypes(SCOREBOARD_CLASS, String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_CONSTRUCTOR_OLD = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_PLAYER_CLASS).
            parameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class, PLAYER_INTERACT_MANAGER_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_CONSTRUCTOR_NEW = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_PLAYER_CLASS).
            parameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(PACKET_PLAY_OUT_PLAYER_INFO_CLASS).
            parameterTypes(ENUM_PLAYER_INFO_CLASS, Utils.BUKKIT_VERSION > 16 ? Collection.class : Iterable.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntity$PacketPlayOutEntityLook").
            parameterTypes(int.class, byte.class, byte.class, boolean.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntityHeadRotation").
            parameterTypes(ClassTypes.ENTITY_CLASS, byte.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntityTeleport").
            parameterTypes(ENTITY_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntityMetadata").
            parameterTypes(int.class, ClassTypes.DATA_WATCHER_CLASS, boolean.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutNamedEntitySpawn").
            parameterTypes(ENTITY_HUMAN_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS).
            parameterTypes(int.class).
            parameterTypes(int[].class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutSpawnEntityLiving").
            parameterTypes(ENTITY_LIVING)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PlayerInteractManager").
            parameterTypes(WORLD_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PlayerInteractManager").
            parameterTypes(WORLD_SERVER_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntityEquipment").
            parameterTypes(int.class, int.class, ClassTypes.ITEM_STACK_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntityEquipment").
            parameterTypes(int.class, ClassTypes.ENUM_ITEM_SLOT, ClassTypes.ITEM_STACK_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_V1 = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutEntityEquipment").
            parameterTypes(int.class, List.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.CHAT)).
            className("ChatComponentText").
            parameterTypes(String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> ENTITY_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_ARMOR_STAND_CLASS).
            parameterTypes(ClassTypes.WORLD_CLASS, double.class, double.class, double.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> DATA_WATCHER_OBJECT_CONSTRUCTOR = new ConstructorLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(DATA_WATCHER_OBJECT).
            parameterTypes(int.class, DATA_WATCHER_SERIALIZER)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method AS_NMS_COPY_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("inventory.CraftItemStack").
            methodName("asNMSCopy").
            parameterTypes(ItemStack.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_PROFILE_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_HUMAN_CLASS).
            methodName("getProfile")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_ENTITY_ID = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_CLASS).
            methodName("getId")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_HANDLE_PLAYER_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("entity.CraftPlayer").
            methodName("getHandle")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_HANDLE_WORLD_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("CraftWorld").
            methodName("getHandle")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_SERVER_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("CraftServer").
            methodName("getServer")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SEND_PACKET_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(PLAYER_CONNECTION_CLASS).
            methodName("sendPacket").
            parameterTypes(PACKET_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_OLD_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_CLASS).
            methodName("setCustomName").
            parameterTypes(String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_NEW_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_CLASS).
            methodName("setCustomName").
            parameterTypes(I_CHAT_BASE_COMPONENT)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_VISIBLE_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_CLASS).
            methodName("setCustomNameVisible").
            parameterTypes(boolean.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_INVISIBLE_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_ARMOR_STAND_CLASS).
            methodName("setInvisible").
            parameterTypes(boolean.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_LOCATION_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_CLASS).
            methodName("setPositionRotation").
            parameterTypes(double.class, double.class, double.class, float.class, float.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_DATA_WATCHER_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(DATA_WATCHER_CLASS).
            methodName("set").
            parameterTypes(DATA_WATCHER_OBJECT, Object.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method WATCH_DATA_WATCHER_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(DATA_WATCHER_CLASS).
            methodName("watch").
            parameterTypes(int.class, Object.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_DATA_WATCHER_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_CLASS).
            methodName("getDataWatcher")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_BUKKIT_ENTITY_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENTITY_CLASS).
            methodName("getBukkitEntity")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_ENUM_CHAT_ID_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENUM_CHAT_CLASS).
            methodName("b")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method ENUM_CHAT_TO_STRING_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENUM_CHAT_CLASS).
            methodName("toString")).
            typeOf();

    /**
     * Used to get nms-entityType by its name.
     */
    public static final Method ENTITY_TYPES_A_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.ENTITY)).
            className(ENTITY_TYPES_CLASS).
            methodName("a").
            parameterTypes(String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1 = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS).
            methodName("a").
            parameterTypes(SCOREBOARD_TEAM_CLASS)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS).
            methodName("a").
            parameterTypes(SCOREBOARD_TEAM_CLASS, boolean.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method SCOREBOARD_PLAYER_LIST = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(SCOREBOARD_TEAM_CLASS).
            methodName("getPlayerNameSet")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method ENUM_CHAT_FORMAT_FIND = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.NONE)).
            className(ENUM_CHAT_CLASS).
            methodName("b").
            parameterTypes(String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Method CRAFT_CHAT_MESSAGE_METHOD = new MethodLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className(CRAFT_CHAT_MESSAGE_CLASS).
            methodName("fromStringOrNull").
            parameterTypes(String.class)).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field DATA_WATCHER_REGISTER_ENUM_FIELD = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(DATA_WATCHER_REGISTRY).
            fieldName("a")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field ADD_PLAYER_FIELD = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").
            fieldName(V17 ? "a" : "ADD_PLAYER")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field REMOVE_PLAYER_FIELD = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").
            fieldName(V17 ? "e" : "REMOVE_PLAYER")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field PLAYER_CONNECTION_FIELD = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SERVER_LEVEL)).
            className(ENTITY_PLAYER_CLASS).
            fieldName(V17 ? "b" : "playerConnection")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field NETWORK_MANAGER_FIELD = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(PLAYER_CONNECTION_CLASS).
            fieldName(V17 ? "a" : "networkManager")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field CHANNEL_FIELD = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.SERVER_NETWORK)).
            className(NETWORK_MANAGER_CLASS).
            fieldName(V17 ? "k" : "channel")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field PACKET_IN_USE_ENTITY_ID_FIELD = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className("PacketPlayInUseEntity").
            fieldName("a")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field BUKKIT_COMMAND_MAP = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.CRAFT_BUKKIT.getFixedPackageName()).
            className("CraftServer").
            fieldName("commandMap")).
            typeOf();

    /**
     * {@inheritDoc}
     */
    public static final Field ENUM_TAG_VISIBILITY_NEVER = new FieldLoader(new ClassCacheBuilder().
            packageType(TypePackage.MINECRAFT_SERVER.getForCategory(PacketCategory.PACKET)).
            className(ENUM_TAG_VISIBILITY).
            fieldName("b")).
            typeOf();
}
