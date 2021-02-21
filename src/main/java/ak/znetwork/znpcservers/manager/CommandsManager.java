package ak.znetwork.znpcservers.manager;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.ZNCommand;
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandNotFoundException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.logging.Level;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class CommandsManager implements CommandExecutor {

    /**
     * A set of commands.
     */
    private final LinkedHashSet<ZNCommand> znCommands;

    /**
     * Initializes commands.
     *
     * @param serversNPC The plugin instance.
     * @param command The command name.
     */
    public CommandsManager(ServersNPC serversNPC,
                           String command) {
        this.znCommands = new LinkedHashSet<>();

        serversNPC.getCommand(command).setExecutor(this);
    }

    /**
     * Adds a new command.
     *
     * @param commands Commands to add.
     */
    public final void addCommand(final ZNCommand... commands) {
        znCommands.addAll(Arrays.asList(commands));
    }

    /**
     * Get commands.
     *
     * @return command list
     */
    public LinkedHashSet<ZNCommand> getZnCommands() {
        return znCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        try {
            final Optional<ZNCommand> znCommand = znCommands.stream().findFirst();

            if (znCommand.isPresent()) znCommand.get().execute(sender, args);
        } catch (CommandExecuteException e) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.COMMAND_ERROR);

            // Logs enabled.
            Bukkit.getLogger().log(Level.WARNING, "", e);
        } catch (CommandPermissionException e) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NO_PERMISSION);
        } catch (CommandNotFoundException e) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.COMMAND_NOT_FOUND);
        }
        return true;
    }
}
