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
package ak.znetwork.znpcservers.configuration.enums;

import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;

public enum ZNConfigValue {

    // Config
    VIEW_DISTANCE(ZNConfigType.CONFIG, 32, Integer.class), // by Block distance

    REPLACE_SYMBOL(ZNConfigType.CONFIG, "-", String.class), // Replace spaces symbol , default = " ' "

    SAVE_NPCS_DELAY_SECONDS(ZNConfigType.CONFIG, 60 * (10), Integer.class), // Save NPC delay (10 minutes)

    // Messages
    NO_PERMISSION(ZNConfigType.MESSAGES, "&cYou do not have permission to execute this command.", String.class),
    SUCCESS(ZNConfigType.MESSAGES, "&aDone...", String.class),
    INCORRECT_USAGE(ZNConfigType.MESSAGES, "&cIncorrect use of command.", String.class),
    COMMAND_NOT_FOUND(ZNConfigType.MESSAGES, "&cThis command was not found.", String.class),
    COMMAND_ERROR(ZNConfigType.MESSAGES, "&cThere was an error executing the command, see the console for more information.", String.class),

    INVALID_NUMBER(ZNConfigType.MESSAGES, "&cHey!, The inserted number/id does not look like a number..", String.class),
    NPC_NOT_FOUND(ZNConfigType.MESSAGES, "&cHey!, I couldnt find a npc with this id.", String.class);

    public final ZNConfigType znConfigType;

    public final Object value;

    public final Class<?> ext;

    ZNConfigValue(ZNConfigType znConfigType, Object value, Class<?> ext) {
        this.znConfigType = znConfigType;

        this.ext = ext;

        this.value = value;
    }
}
