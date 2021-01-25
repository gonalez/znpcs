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

import ak.znetwork.znpcservers.commands.annotation.CMDInfo;
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandNotFoundException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;
import ak.znetwork.znpcservers.commands.invoker.CommandInvoker;
import com.google.common.collect.Maps;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * COMMAND API
 *
 * @author ZNetwork
 * <p>
 * TODO
 * - *
 */
public class ZNCommand {

    private final Object object;

    private final HashMap<CMDInfo, CommandInvoker<? extends CommandSender>> consumerSet;

    public ZNCommand(final Object object) {
        this.object = object;

        this.consumerSet = new HashMap<>();

        this.load();
    }

    public <T extends CommandSender> void execute(final T sender, final String[] args) throws CommandPermissionException, CommandNotFoundException, CommandExecuteException {
        Optional<Map.Entry<CMDInfo, CommandInvoker<? extends CommandSender>>> entryOptional = this.consumerSet.entrySet().stream().filter(cmdInfo -> cmdInfo.getKey().required().contentEquals(args.length > 0 ? args[0] : "")).findFirst();

        if (!entryOptional.isPresent()) throw new CommandNotFoundException("Command not found...");

        CommandInvoker<T> command = (CommandInvoker<T>) entryOptional.get().getValue();
        command.execute(sender, loadArgs(entryOptional.get().getKey(), args));
    }

    public Map<String, String> loadArgs(final CMDInfo cmdInfo, final String[] args) {
        final Map<String, String> argsMap = Maps.newHashMap();

        for (int i = 1; i <= args.length; i++) {
            final String input = args[i - 1];

            if (contains(cmdInfo, input)) {
                final StringBuilder value = new StringBuilder();

                for (int text = (i); text < args.length; ) {
                    if (!contains(cmdInfo, args[text++])) value.append(args[i++]).append(" ");
                    else break;
                }

                argsMap.put(input.replace("-", ""), (value.toString()));
            }
        }
        return argsMap;
    }

    public boolean contains(final CMDInfo cmdInfo, final String input) {
        return Stream.of(cmdInfo.aliases()).anyMatch(s -> s.equalsIgnoreCase(input));
    }

    public void load() {
        for (Method method : object.getClass().getMethods()) {
            if (method.isAnnotationPresent(CMDInfo.class)) {
                final CMDInfo cmdInfo = method.getAnnotation(CMDInfo.class);

                this.consumerSet.put(cmdInfo, new CommandInvoker<>(this.object, method, cmdInfo.permission()));
            }
        }
    }

    public HashMap<CMDInfo, CommandInvoker<? extends CommandSender>> getConsumerSet() {
        return consumerSet;
    }
}
