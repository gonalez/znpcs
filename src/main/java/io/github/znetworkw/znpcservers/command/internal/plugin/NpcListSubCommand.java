package io.github.znetworkw.znpcservers.command.internal.plugin;

import com.google.common.collect.Iterables;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.command.AnnotatedPluginSubCommand;
import io.github.znetworkw.znpcservers.command.PluginSubCommandParam;
import io.github.znetworkw.znpcservers.configuration.Configuration;
import io.github.znetworkw.znpcservers.configuration.ConfigurationValue;
import io.github.znetworkw.znpcservers.npc.Npc;
import io.github.znetworkw.znpcservers.npc.NpcModel;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Subcommand for listing all npcs.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "list"
)
public class NpcListSubCommand extends AnnotatedPluginSubCommand {

    @Override
    public void execute(CommandSender commandSender, Map<String, String> args) {
        final Iterable<Npc> npcs = ZNPCs.SETTINGS.getNpcStore().getNpcs();
        if (Iterables.isEmpty(npcs)) {
            Configuration.MESSAGES.sendMessage(commandSender, ConfigurationValue.NO_NPC_FOUND);
        } else {
            for (Npc npc : npcs) {
                final NpcModel npcModel = npc.getModel();
                commandSender.sendMessage(
                    Utils.toColor("&f&l *; &a" + npcModel.getId() + " " + npcModel.getHologramLines().toString()
                    + " &7(&e" + npcModel.getLocation().getWorldName() + " " + npc.getLocation().getX()
                    + " " + npc.getLocation().getY() + " " + npc.getLocation().getZ() + "&7)"));
            }
        }
    }
}
