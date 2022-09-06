package io.github.gonalez.znpcs.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ZNPCs;
import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.utility.PluginLocation;
import io.github.gonalez.znpcs.command.AnnotatedPluginSubCommand;
import io.github.gonalez.znpcs.command.PluginSubCommandParam;
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
