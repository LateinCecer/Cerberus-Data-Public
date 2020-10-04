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
import com.cerberustek.data.impl.elements.IntElement;
import com.cerberustek.exception.JSONFormatException;
import com.cerberustek.json.JSONUtil;
import com.cerberustek.json.StringStream;

public class JSONInteger extends JSONValue<Integer> {

    public JSONInteger() {

    }

    public JSONInteger(int value) {
        this.value = value;
    }

    @Override
    public void readContents(StringStream inputStream) throws JSONFormatException {

        Number number = JSONUtil.readNumber(inputStream);
        if (number instanceof Integer)
            set((Integer) number);
        else
            throw new JSONFormatException("Wrong number format!");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetaElement toMetaElement() {
        return new IntElement(value);
    }

    @Override
    public MetaData toMeta() {
        return toMetaElement();
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
