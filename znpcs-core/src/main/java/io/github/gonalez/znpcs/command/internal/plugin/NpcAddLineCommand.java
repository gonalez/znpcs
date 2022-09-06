package io.github.gonalez.znpcs.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ZNPCs;
import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.command.AnnotatedPluginSubCommand;
import io.github.gonalez.znpcs.command.PluginSubCommandParam;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Subcommand for adding new lines for an npc hologram.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "addLine",
    args = {"id", "line"}
)
public class NpcAddLineCommand extends AnnotatedPluginSubCommand {

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
        npc.getModel().getHologramLines().add(args.get("line"));
        commandSender.sendMessage("done");
    }
}
