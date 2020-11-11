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
package ak.znetwork.znpcservers.commands.impl;

import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public abstract class ZNCommandAbstract<T extends CommandSender> {

    private final Object object;
    private final Method method;

    private final String permission;

    public ZNCommandAbstract(final Object object, final Method method, final String permission) {
        this.object = object;
        this.method = method;

        this.permission = permission;
    }

    protected Object getObject() {
        return object;
    }

    protected String getPermission() {
        return permission;
    }

    protected Method getMethod() {
        return method;
    }

    public abstract void execute(T sender, Object args) throws CommandPermissionException;
}
