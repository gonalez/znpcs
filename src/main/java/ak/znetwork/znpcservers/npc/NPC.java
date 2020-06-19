package ak.znetwork.znpcservers.npc;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NPC {

    protected Object entityPlayer;
    protected Object entityPlayerArray;

    protected Object enumPlayerInfoAction;
    protected Constructor<?> getPacketPlayOutPlayerInfoConstructor;

    protected boolean hasGlow = false;

    public NPC(final Location location) {
        try {
            Object nmsServer = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            Object nmsWorld = location.getWorld().getClass().getMethod("getHandle").invoke(location.getWorld());

            Class<?> playerInteractManager = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PlayerInteractManager");
            Constructor<?> getPlayerInteractManagerConstructor = playerInteractManager.getDeclaredConstructors()[0];

            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + getBukkitPackage() + ".EntityPlayer");
            Constructor<?> getPlayerConstructor = entityPlayerClass.getDeclaredConstructors()[0];

            GameProfile gameProfile = new GameProfile(Bukkit.getOfflinePlayer("Notch").getUniqueId() , "test");

            entityPlayer = getPlayerConstructor.newInstance(nmsServer , nmsWorld , gameProfile , getPlayerInteractManagerConstructor.newInstance(nmsWorld));
            entityPlayer.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(entityPlayer , location.getX() , location.getY(),
                    location.getZ() , location.getYaw() , location.getPitch());

            Class<?> enumPlayerInfoActionClass = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            enumPlayerInfoAction = enumPlayerInfoActionClass.getField("ADD_PLAYER").get(null);

            Class<?> packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PacketPlayOutPlayerInfo");
            getPacketPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass , Class.forName("[Lnet.minecraft.server." + getBukkitPackage() + ".EntityPlayer;"));

            entityPlayerArray = Array.newInstance(entityPlayerClass, 1);
            Array.set(entityPlayerArray, 0, entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void toggleGlow()  {
        hasGlow = !hasGlow;

        try {
            Object dataWatcherObject = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void spawn(final Player player) {
        try {
            Object packetPlayOutPlayerInfoConstructor = getPacketPlayOutPlayerInfoConstructor.newInstance(enumPlayerInfoAction , entityPlayerArray);

            sendPacket(player ,packetPlayOutPlayerInfoConstructor);

            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getDeclaredConstructor(Class.forName("net.minecraft.server." + getBukkitPackage() + ".EntityHuman"));

            Object entityPlayerPacketSpawn = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayer);

            sendPacket(player ,entityPlayerPacketSpawn);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(final Player player , final Object object) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);

            Method sendPacket = playerConnection.getClass().getMethod("sendPacket" , Class.forName("net.minecraft.server." + getBukkitPackage() + ".Packet"));

            sendPacket.invoke(playerConnection , object);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
