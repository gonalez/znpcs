package io.github.gonalez.znpcs.command;

import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginSubCommand {
    String name();
    String permission();
    String[] args();

    void execute(CommandSender commandSender, Map<String, String> args) throws Exception;
}
