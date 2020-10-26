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
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
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

    public Map<String, String> getAnnotations(final String[] cmd) {
        final Map<String, String> valueMap = Maps.newHashMap();

        for (int i = 0; i < cmd.length; i++) {
            final String input = cmd[i];

            final Optional<ZNArgument> znArgument = this.argumentSet.stream().filter(znArgument1 -> znArgument1.name.equalsIgnoreCase("getArguments") && Stream.of(((String[])znArgument1.value)).anyMatch(input::contains)).findFirst();

            if (znArgument.isPresent()) {
                if (i++ > cmd.length) break; // The command does not contain a value

                final String value = cmd[i];

                valueMap.put(input, value);
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

