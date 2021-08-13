package ak.znetwork.znpcservers.cache;

import ak.znetwork.znpcservers.utility.Utils;
import com.mojang.authlib.GameProfile;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static ak.znetwork.znpcservers.cache.TypeCache.*;

/**
 * A registry of custom {@link BaseCache}s.
 */
public final class CacheRegistry {
    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_IN_USE_ENTITY_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayInUseEntity")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_PLAYER_INFO_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PROTOCOL)
                    .withClassName("Packet")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withClassName("Entity")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_LIVING = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withClassName("EntityLiving")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PLAYER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SERVER_LEVEL)
                    .withClassName("EntityPlayer")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ARMOR_STAND_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("decoration")
                    .withClassName("EntityArmorStand")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_BAT_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("ambient")
                    .withClassName("EntityBat")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_BLAZE_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityBlaze")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CAVE_SPIDER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityCaveSpider")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CHICKEN_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityChicken")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_COW_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityCow")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_CREEPER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityCreeper")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDER_DRAGON_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("boss.enderdragon")
                    .withClassName("EntityEnderDragon")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDERMAN_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityEnderman")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_HUMAN_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("player")
                    .withClassName("EntityHuman")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ENDERMITE_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityEndermite")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GHAST_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityGhast")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_IRON_GOLEM_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityIronGolem")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GIANT_ZOMBIE_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityGiantZombie")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GUARDIAN_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityGuardian")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_HORSE_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal.horse")
                    .withClassName("EntityHorse")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_LLAMA_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal.horse")
                    .withClassName("EntityLlama")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_MAGMA_CUBE_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityMagmaCube")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_MUSHROOM_COW_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityMushroomCow")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_OCELOT_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityOcelot")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PARROT_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityParrot")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PIG_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityPig")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_RABBIT_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityRabbit")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_POLAR_BEAR_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityPolarBear")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_PANDA_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityPanda")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SHEEP_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntitySheep")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SNOWMAN_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntitySnowman")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SHULKER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityShulker")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SILVERFISH_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntitySilverfish")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SKELETON_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntitySkeleton")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SLIME_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntitySlime")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SPIDER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntitySpider")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_SQUID_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntitySquid")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_VILLAGER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("npc")
                    .withClassName("EntityVillager")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WITCH_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityWitch")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WITHER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("boss.wither")
                    .withClassName("EntityWither")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_ZOMBIE_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("monster")
                    .withClassName("EntityZombie")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_WOLF_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityWolf")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_AXOLOTL_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal.axolotl")
                    .withClassName("Axolotl")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_GOAT_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal.goat")
                    .withClassName("Goat")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_FOX_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withAdditionalData("animal")
                    .withClassName("EntityFox")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENTITY_TYPES_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withClassName("EntityTypes")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_CHAT_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withClassName("EnumChatFormat")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_ITEM_SLOT = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withClassName("EnumItemSlot")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> I_CHAT_BASE_COMPONENT = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.CHAT)
                    .withClassName("IChatBaseComponent")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ITEM_STACK_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ITEM)
                    .withClassName("ItemStack")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SYNCHER)
                    .withClassName("DataWatcher")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_OBJECT = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SYNCHER)
                    .withClassName("DataWatcherObject")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_REGISTRY = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SYNCHER)
                    .withClassName("DataWatcherRegistry")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> DATA_WATCHER_SERIALIZER = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SYNCHER)
                    .withClassName("DataWatcherSerializer")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> WORLD_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.WORLD_LEVEL)
                    .withClassName("World")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> CRAFT_ITEM_STACK_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT)
                    .withClassName("inventory.CraftItemStack")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> WORLD_SERVER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SERVER_LEVEL)
                    .withClassName("WorldServer")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> MINECRAFT_SERVER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SERVER)
                    .withClassName("MinecraftServer")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PLAYER_INTERACT_MANAGER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SERVER_LEVEL)
                    .withClassName("PlayerInteractManager")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PLAYER_CONNECTION_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SERVER_NETWORK)
                    .withClassName("PlayerConnection")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> NETWORK_MANAGER_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.NETWORK)
                    .withClassName("NetworkManager")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_OUT_PLAYER_INFO_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutPlayerInfo")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutScoreboardTeam")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntityDestroy")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> SCOREBOARD_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.WORLD_SCORES)
                    .withClassName("Scoreboard")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> SCOREBOARD_TEAM_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.WORLD_SCORES)
                    .withClassName("ScoreboardTeam")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> ENUM_TAG_VISIBILITY = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.WORLD_SCORES)
                    .withClassName("ScoreboardTeamBase$EnumNameTagVisibility")).load();

    /**
     * {@inheritDoc}
     */
    public static final Class<?> CRAFT_CHAT_MESSAGE_CLASS = new BaseCache.ClazzLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT)
                    .withClassName("util.CraftChatMessage")).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> SCOREBOARD_TEAM_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(SCOREBOARD_TEAM_CLASS)
                    .withParameterTypes(SCOREBOARD_CLASS, String.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_CONSTRUCTOR_OLD = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_PLAYER_CLASS)
                    .withParameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class, PLAYER_INTERACT_MANAGER_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_CONSTRUCTOR_NEW = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_PLAYER_CLASS)
                    .withParameterTypes(MINECRAFT_SERVER_CLASS, WORLD_SERVER_CLASS, GameProfile.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(PACKET_PLAY_OUT_PLAYER_INFO_CLASS)
                    .withParameterTypes(ENUM_PLAYER_INFO_CLASS, Utils.BUKKIT_VERSION > 16 ? Collection.class : Iterable.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntity$PacketPlayOutEntityLook")
                    .withParameterTypes(int.class, byte.class, byte.class, boolean.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntityHeadRotation")
                    .withParameterTypes(CacheRegistry.ENTITY_CLASS, byte.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntityTeleport").
                    withParameterTypes(ENTITY_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntityMetadata")
                    .withParameterTypes(int.class, CacheRegistry.DATA_WATCHER_CLASS, boolean.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutNamedEntitySpawn")
                    .withParameterTypes(ENTITY_HUMAN_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS)
                    .withParameterTypes(int.class)
                    .withParameterTypes(int[].class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutSpawnEntityLiving")
                    .withParameterTypes(ENTITY_LIVING)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PlayerInteractManager")
                    .withParameterTypes(WORLD_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PlayerInteractManager")
                    .withParameterTypes(WORLD_SERVER_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntityEquipment")
                    .withParameterTypes(int.class, int.class, CacheRegistry.ITEM_STACK_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntityEquipment")
                    .withParameterTypes(int.class, CacheRegistry.ENUM_ITEM_SLOT, CacheRegistry.ITEM_STACK_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_V1 = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutEntityEquipment")
                    .withParameterTypes(int.class, List.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.CHAT)
                    .withClassName("ChatComponentText")
                    .withParameterTypes(String.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> ENTITY_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_ARMOR_STAND_CLASS)
                    .withParameterTypes(CacheRegistry.WORLD_CLASS, double.class, double.class, double.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Constructor<?> DATA_WATCHER_OBJECT_CONSTRUCTOR = new BaseCache.ConstructorLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(DATA_WATCHER_OBJECT)
                    .withParameterTypes(int.class, DATA_WATCHER_SERIALIZER)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method AS_NMS_COPY_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT).
                    withClassName("inventory.CraftItemStack").
                    withMethodName("asNMSCopy")
                    .withParameterTypes(ItemStack.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_PROFILE_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_HUMAN_CLASS)
                    .withMethodName("getProfile")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_ENTITY_ID = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("getId")).
            load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_HANDLE_PLAYER_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT)
                    .withClassName("entity.CraftPlayer")
                    .withMethodName("getHandle")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_HANDLE_WORLD_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT)
                    .withClassName("CraftWorld")
                    .withMethodName("getHandle")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_SERVER_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT)
                    .withClassName("CraftServer")
                    .withMethodName("getServer")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SEND_PACKET_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(PLAYER_CONNECTION_CLASS)
                    .withMethodName("sendPacket")
                    .withParameterTypes(PACKET_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_OLD_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("setCustomName")
                    .withParameterTypes(String.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_NEW_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("setCustomName")
                    .withParameterTypes(I_CHAT_BASE_COMPONENT)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_CUSTOM_NAME_VISIBLE_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("setCustomNameVisible")
                    .withParameterTypes(boolean.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_INVISIBLE_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_ARMOR_STAND_CLASS)
                    .withMethodName("setInvisible")
                    .withParameterTypes(boolean.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_LOCATION_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("setPositionRotation")
                    .withParameterTypes(double.class, double.class, double.class, float.class, float.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SET_DATA_WATCHER_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(DATA_WATCHER_CLASS)
                    .withMethodName("set")
                    .withParameterTypes(DATA_WATCHER_OBJECT, Object.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method WATCH_DATA_WATCHER_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(DATA_WATCHER_CLASS)
                    .withMethodName("watch")
                    .withParameterTypes(int.class, Object.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_DATA_WATCHER_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("getDataWatcher")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_BUKKIT_ENTITY_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("getBukkitEntity")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_ENUM_CHAT_ID_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENUM_CHAT_CLASS)
                    .withMethodName("b")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method ENUM_CHAT_TO_STRING_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENUM_CHAT_CLASS)
                    .withMethodName("toString")).load();

    /**
     * Used to get nms-entityType by its name.
     */
    public static final Method ENTITY_TYPES_A_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.ENTITY)
                    .withClassName(ENTITY_TYPES_CLASS)
                    .withMethodName("a")
                    .withParameterTypes(String.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1 = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS)
                    .withMethodName("a")
                    .withParameterTypes(SCOREBOARD_TEAM_CLASS)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(PACKET_PLAY_OUT_SCOREBOARD_TEAM_CLASS)
                    .withMethodName("a")
                    .withParameterTypes(SCOREBOARD_TEAM_CLASS, boolean.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method SCOREBOARD_PLAYER_LIST = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(SCOREBOARD_TEAM_CLASS)
                    .withMethodName("getPlayerNameSet")).load();

    /**
     * {@inheritDoc}
     */
    public static final Method ENUM_CHAT_FORMAT_FIND = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withClassName(ENUM_CHAT_CLASS)
                    .withMethodName("b")
                    .withParameterTypes(String.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method CRAFT_CHAT_MESSAGE_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT)
                    .withClassName(CRAFT_CHAT_MESSAGE_CLASS)
                    .withMethodName("fromStringOrNull")
                    .withParameterTypes(String.class)).load();

    /**
     * {@inheritDoc}
     */
    public static final Method GET_UNIQUE_ID_METHOD = new BaseCache.MethodLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withClassName(ENTITY_CLASS)
                    .withMethodName("getUniqueID"))
                    .load();

    /**
     * {@inheritDoc}
     */
    public static final Field DATA_WATCHER_REGISTER_ENUM_FIELD = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(DATA_WATCHER_REGISTRY)
                    .withFieldName("a")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field ADD_PLAYER_FIELD = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")
                    .withFieldName(Utils.BUKKIT_VERSION > 16 ? "a" : "ADD_PLAYER")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field REMOVE_PLAYER_FIELD = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")
                    .withFieldName(Utils.BUKKIT_VERSION > 16 ? "e" : "REMOVE_PLAYER")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field PLAYER_CONNECTION_FIELD = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SERVER_LEVEL)
                    .withClassName(ENTITY_PLAYER_CLASS)
                    .withFieldName(Utils.BUKKIT_VERSION > 16 ? "b" : "playerConnection")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field NETWORK_MANAGER_FIELD = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(PLAYER_CONNECTION_CLASS)
                    .withFieldName(Utils.BUKKIT_VERSION > 16 ? "a" : "networkManager")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field CHANNEL_FIELD = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.SERVER_NETWORK)
                    .withClassName(NETWORK_MANAGER_CLASS)
                    .withFieldName(Utils.BUKKIT_VERSION > 16 ? "k" : "channel")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field PACKET_IN_USE_ENTITY_ID_FIELD = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName("PacketPlayInUseEntity")
                    .withFieldName("a")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field BUKKIT_COMMAND_MAP = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.CRAFT_BUKKIT)
                    .withClassName("CraftServer")
                    .withFieldName("commandMap")).load();

    /**
     * {@inheritDoc}
     */
    public static final Field ENUM_TAG_VISIBILITY_NEVER = new BaseCache.FieldLoader(
            new CacheBuilder(CachePackage.MINECRAFT_SERVER)
                    .withCategory(CacheCategory.PACKET)
                    .withClassName(ENUM_TAG_VISIBILITY)
                    .withFieldName("b")).load();

    /** Default constructor */
    private CacheRegistry() {
        throw new AssertionError("This class is not intended to be initialized.");
    }
}
