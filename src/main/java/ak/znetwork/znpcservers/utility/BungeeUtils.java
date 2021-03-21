package ak.znetwork.znpcservers.utility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class BungeeUtils {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Initializes bungee utils.
     *
     * @param plugin The plugin instance.
     */
    public BungeeUtils(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends a player to a bungee server.
     *
     * @param player The player to send to the server.
     * @param server The server name.
     */
    public void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }
}
