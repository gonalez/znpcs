package io.github.znetworkw.znpcservers.command.internal.plugin;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.command.AnnotatedPluginSubCommand;
import io.github.znetworkw.znpcservers.command.PluginSubCommandParam;
import io.github.znetworkw.znpcservers.entity.PluginEntity;
import io.github.znetworkw.znpcservers.npc.Npc;
import io.github.znetworkw.znpcservers.skin.SkinFetcher;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

/**
 * Subcommand for setting the skin of an npc.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@PluginSubCommandParam(
    name = "skin",
    args = {"id", "skin"}
)
public class NpcSetSkinSubCommand extends AnnotatedPluginSubCommand {
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
        if (!npc.getPluginEntity().getPluginEntityType().isPlayer()) {
            commandSender.sendMessage("Npc not found.");
            return;
        }
        final String name = args.get("skin");
        try {
            SkinFetcher.of(
                name,
                onSuccess -> {
                    npc.getPluginEntity().getAttributes().set(
                        PluginEntity.GAME_PROFILE_ATTRIBUTE,
                        getWithProperties(name, onSuccess.getTexture(), onSuccess.getSignature()));
                    commandSender.sendMessage("all success");
                }, onError -> {
                    commandSender.sendMessage(ChatColor.RED + "Error");
            }).request();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static private GameProfile getWithProperties(
        String name, String texture, String signature) {
        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        return gameProfile;
    }
}
