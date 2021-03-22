package ak.znetwork.znpcservers.configuration.impl;

import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import org.bukkit.command.CommandSender;

import java.io.IOException;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface ZNConfigImpl {

    /**
     * Responsible of loading the configuration.
     */
    void load();

    /**
     * Sends an configuration message to a player.
     *
     * @param player The player to send the message to.
     * @param znConfigValue The configuration message value.
     */
    void sendMessage(CommandSender player, ZNConfigValue znConfigValue);

    /**
     * Saves configuration into database.
     *
     * @throws IOException If configuration could not be saved.
     */
    void save() throws IOException;

    /**
     * Returns the value for the config value.
     */
    <T> T getValue(ZNConfigValue znConfigValue);
}
