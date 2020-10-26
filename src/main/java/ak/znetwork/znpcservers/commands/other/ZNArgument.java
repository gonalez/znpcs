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
*/
package ak.znetwork.znpcservers.commands.other;

import java.lang.annotation.Annotation;

public class ZNArgument {

    public final Annotation annotation;
    public final String name;
    public final Object value;

    public ZNArgument(final Annotation annotation, final String name, final Object value) {
        this.annotation = annotation;
        this.name = name;
        this.value = value;
    }
}
