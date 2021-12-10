package io.github.znetworkw.znpcservers.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.command.AnnotatedPluginSubCommand;
import io.github.znetworkw.znpcservers.command.PluginSubCommandParam;
import io.github.znetworkw.znpcservers.npc.Npc;
import io.github.znetworkw.znpcservers.npc.function.NpcFunction;
import io.github.znetworkw.znpcservers.npc.function.NpcFunctions;
import io.github.znetworkw.znpcservers.npc.function.NpcFunctionContext;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Subcommand for toggling the function of an npc.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "toggle",
    args = {"id", "name", "data"}
)
public class NpcToggleSubCommand extends AnnotatedPluginSubCommand {

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
        final NpcFunction npcFunction = NpcFunctions.findFunctionForName(args.get("name"));
        if (npcFunction == null) {
            commandSender.sendMessage("Function not found.");
            return;
        }
        npcFunction.executeFunction(npc, NpcFunctionContext.builder().addAttribute("data", args.get("data")).build());
        commandSender.sendMessage("Done");
    }
}
