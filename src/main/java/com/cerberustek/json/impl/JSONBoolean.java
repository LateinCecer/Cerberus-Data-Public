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

package com.cerberustek.json.impl;

import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.impl.elements.BooleanElement;
import com.cerberustek.exception.JSONFormatException;
import com.cerberustek.json.JSONUtil;
import com.cerberustek.json.StringStream;

public class JSONBoolean extends JSONValue<Boolean> {

    public JSONBoolean() {

    }

    public JSONBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public void readContents(StringStream stringStream) throws JSONFormatException {
        set(JSONUtil.readBoolean(stringStream));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetaElement toMetaElement() {
        return new BooleanElement(value);
    }

    @Override
    public MetaData toMeta() {
        return toMetaElement();
    }

    @Override
    public String toString() {
        return Boolean.toString(value).toUpperCase();
    }
}
