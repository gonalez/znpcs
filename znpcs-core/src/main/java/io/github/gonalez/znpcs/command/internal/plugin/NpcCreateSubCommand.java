package io.github.gonalez.znpcs.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.gonalez.znpcs.ZNPCs;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.npc.NpcModel;
import io.github.gonalez.znpcs.utility.PluginLocation;
import io.github.gonalez.znpcs.command.AnnotatedPluginSubCommand;
import io.github.gonalez.znpcs.command.PluginSubCommandParam;
import io.github.gonalez.znpcs.entity.PluginEntityFactory;
import io.github.gonalez.znpcs.entity.PluginEntityTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;

/**
 * Subcommand for creating npcs.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "create",
    args = {"id", "type", "name"}
)
public class NpcCreateSubCommand extends AnnotatedPluginSubCommand {

    @Override
    public void execute(CommandSender commandSender, Map<String, String> args) {
        final Integer id = Ints.tryParse(args.get("id"));
        if (id == null) {
            commandSender.sendMessage("Invalid number.");
            return;
        }
        final String name = args.get("name").trim();
        if (name.length() < 3 || name.length() > 16) {
            commandSender.sendMessage("Invalid name length.");
            return;
        }
        final NpcModel npcModel = new NpcModel(id)
            .withNpcType(PluginEntityTypes.PLAYER.name())
            .withHologramLines(Collections.singletonList(name))
            .withLocation(new PluginLocation(((Player)commandSender).getLocation()));
        try {
            final Npc npc = Npc.of(PluginEntityFactory.of(), npcModel);
            ZNPCs.SETTINGS.getNpcStore().addNpc(npcModel.getId(), npc);
            ConfigurationConstants.NPC_MODELS.add(npcModel);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
