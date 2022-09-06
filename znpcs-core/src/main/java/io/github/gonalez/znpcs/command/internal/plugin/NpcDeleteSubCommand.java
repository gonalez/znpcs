package io.github.gonalez.znpcs.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ZNPCs;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.command.AnnotatedPluginSubCommand;
import io.github.gonalez.znpcs.command.PluginSubCommandParam;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Subcommand for delete created npcs.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "delete",
    args = {"id"}
)
public class NpcDeleteSubCommand extends AnnotatedPluginSubCommand {

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
        ConfigurationConstants.NPC_MODELS.remove(npc.getModel());
        try {
            npc.onDisable();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        ZNPCs.SETTINGS.getNpcStore().removeNpc(id);
    }
}
