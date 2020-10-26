/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package ak.znetwork.znpcservers.commands;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.commands.annotations.CMDInfo;
import ak.znetwork.znpcservers.commands.enums.CommandType;
import ak.znetwork.znpcservers.commands.other.ZNArgument;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public abstract class ZNCommand {

    public ServersNPC serversNPC;

    protected String cmd;

    protected CommandType commandType;

    protected String permission;

    protected Set<ZNArgument> argumentSet;

    public ZNCommand(final ServersNPC serversNPC , final String cmd , final String permission, CommandType commandType , String... usages) {
        this.serversNPC = serversNPC;

        this.cmd = cmd;
        this.permission = permission;
        this.commandType = commandType;

        this.argumentSet = new HashSet<>();

        loadAnnotations();
    }

    public abstract boolean dispatchCommand(CommandSender sender, String... args) throws Exception;

    public String getPermission() {
        return permission;
    }

    public String getCmd() {
        return cmd;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getUsage() {
        return ChatColor.RED + "/znpcs " + this.cmd + " " + String.join(" ", getAll("getArguments"));
    }

    public final String[] getAll(final String name) {
        for (final ZNArgument znArgument : argumentSet) {
            if (!znArgument.name.equalsIgnoreCase(name)) continue;

            return (String[]) znArgument.value;
        }
        return new String[0];
    }

    public Optional<ZNArgument> findArgument(final String first) {
        return this.argumentSet.stream().filter(znArgument1 -> znArgument1.name.equalsIgnoreCase("getArguments") && Stream.of(((String[])znArgument1.value)).anyMatch(first::contains)).findFirst();
    }


    public Map<String, String> getAnnotations(final String[] cmd) {
        final Map<String, String> valueMap = Maps.newHashMap();

        for (int i = 0; i < cmd.length; i++) {
            final String input = cmd[i];

            final Optional<ZNArgument> znArgument = findArgument(input);

            if (znArgument.isPresent()) {
                boolean can = argumentSet.stream().filter(znArgument1 -> znArgument1.name.equalsIgnoreCase("autoFill")).map(znArgument1 -> ((Boolean) znArgument1.value).booleanValue()).findFirst().orElse(false);

                StringBuilder value;

                if (!input.contains("list") && ((i+1) < cmd.length || input.contains("id") || !can)) {
                    if (i++ > cmd.length - 1) break; // The command does not contain a value

                    value = new StringBuilder(cmd[i] + " ");

                    for (int text = (i + 1); text < cmd.length; text++) {
                        Optional<ZNArgument> znArgument1 = Optional.empty();

                        if (findArgument(cmd[text]).isPresent()) znArgument1 = findArgument(cmd[text]);

                        if (!znArgument1.isPresent()) {
                            i++;

                            value.append(cmd[i]).append(" ");
                        } else break;
                    }
                } else value = new StringBuilder(cmd[i] + " true");
                valueMap.put(input.replace("-" , ""), value.toString());
            }
        }
        return valueMap;
    }

    public void loadAnnotations() {
        for (final Annotation annotation : getClass().getAnnotationsByType(CMDInfo.class)) {
            for (final Method method : annotation.annotationType().getDeclaredMethods()) {
                try {
                    Object value = method.invoke(annotation);

                    this.argumentSet.add(new ZNArgument(annotation, method.getName(), value));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Could not load annotation -> " + method.getName(), e);
                }
            }
        }
    }
}

