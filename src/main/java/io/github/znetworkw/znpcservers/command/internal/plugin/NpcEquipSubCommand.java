package io.github.znetworkw.znpcservers.command.internal.plugin;

import com.google.common.primitives.Ints;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.command.AnnotatedPluginSubCommand;
import io.github.znetworkw.znpcservers.command.PluginSubCommandParam;
import io.github.znetworkw.znpcservers.entity.PluginEntityEquipmentSlot;
import io.github.znetworkw.znpcservers.npc.Npc;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Subcommand for listing all npcs.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "equip",
    args = {"id", "slot"}
)
public class NpcEquipSubCommand extends AnnotatedPluginSubCommand {

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
        final PluginEntityEquipmentSlot pluginEntityEquipmentSlot =
            PluginEntityEquipmentSlot.valueOf(args.get("slot").toUpperCase());
        try {
            npc.getPluginEntity().equip(pluginEntityEquipmentSlot, ((Player)commandSender).getInventory().getItemInMainHand());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
