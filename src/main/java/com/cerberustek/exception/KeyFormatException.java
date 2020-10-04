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

package com.cerberustek.exception;

import java.security.Key;
import java.util.UUID;

public class KeyFormatException extends SecurityException {

    private final UUID uuid;

    public KeyFormatException(UUID keyId, Class<? extends Key> clazz) {
        super("Key with id " + keyId + " is not an instance of " + clazz + "!");
        this.uuid = keyId;
    }

    public UUID getUuid() {
        return uuid;
    }
}
