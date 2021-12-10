package io.github.znetworkw.znpcservers.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.command.AnnotatedPluginSubCommand;
import io.github.znetworkw.znpcservers.command.PluginSubCommandParam;
import io.github.znetworkw.znpcservers.npc.Npc;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

/**
 * Subcommand for removing lines for an npc hologram.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "removeLine",
    args = {"id", "line"}
)

public class NpcRemoveLineCommand extends AnnotatedPluginSubCommand {
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
        final Integer line = Ints.tryParse(args.get("line"));
        if (line == null) {
            commandSender.sendMessage("Invalid number.");
            return;
        }
        final List<String> hologramLines = npc.getModel().getHologramLines();
        if (line > hologramLines.size()) {
            commandSender.sendMessage("err");
            return;
        }
        hologramLines.remove((int)line);
        try {
            npc.onDisable();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
