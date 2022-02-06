package io.github.gonalez.znpcservers.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.gonalez.znpcservers.ZNPCs;
import io.github.gonalez.znpcservers.npc.Npc;
import io.github.gonalez.znpcservers.command.AnnotatedPluginSubCommand;
import io.github.gonalez.znpcservers.command.PluginSubCommandParam;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Subcommand for teleporting players to an npc.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "teleport",
    args = {"id"}
)
public class NpcTeleportCommand extends AnnotatedPluginSubCommand {

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
        ((Player)commandSender).teleport(npc.getPluginEntity().getLocation());
    }
}
