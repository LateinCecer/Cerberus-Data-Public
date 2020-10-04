/*
 * Cerberus-Data is a complex data management library
 * Visit https://cerberustek.com for more details
 * Copyright (c)  2020  Adrian Paskert
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. See the file LICENSE included with this
 * distribution for more information.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.cerberustek.usr;

import com.cerberustek.event.Event;

public class PermissionEvent implements Event {

    private final PermissionHolder holder;
    private final String permission;
    private final PermissionEventType type;

    public PermissionEvent(String permission, PermissionHolder holder, PermissionEventType type) {
        this.permission = permission;
        this.holder = holder;
        this.type = type;
    }

    public String getPermission() {
        return permission;
    }

    public PermissionHolder getHolder() {
        return holder;
    }

    public PermissionEventType getType() {
        return type;
    }
}
