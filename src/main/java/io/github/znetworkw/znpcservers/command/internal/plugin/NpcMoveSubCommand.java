package io.github.znetworkw.znpcservers.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.command.AnnotatedPluginSubCommand;
import io.github.znetworkw.znpcservers.command.PluginSubCommandParam;
import io.github.znetworkw.znpcservers.npc.Npc;
import io.github.znetworkw.znpcservers.utility.PluginLocation;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Subcommand for moving an npc to another location.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "move",
    args = {"id"}
)
public class NpcMoveSubCommand extends AnnotatedPluginSubCommand {
    @Override
    public void execute(CommandSender commandSender, Map<String, String> args) {
        final Integer id = Ints.tryParse(args.get("id"));
        if (id == null) {
            commandSender.sendMessage("Invalid number.");
            return;
        }
        final Npc npc = ZNPCs.SETTINGS.getNpcStore().getNpc(id);
        if (npc == null) {
            commandSender.sendMessage("Npc not found.");
            return;
        }
        final Location location = ((Player)commandSender).getLocation();
        try {
            npc.getPluginEntity().setLocation(location);
            npc.getModel().setLocation(new PluginLocation(location));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
