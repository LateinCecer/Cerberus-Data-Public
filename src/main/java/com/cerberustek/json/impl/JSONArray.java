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
import com.cerberustek.data.impl.elements.ArrayElement;
import com.cerberustek.exception.JSONFormatException;
import com.cerberustek.json.JSONElement;
import com.cerberustek.json.JSONReader;
import com.cerberustek.json.JSONUtil;
import com.cerberustek.json.StringStream;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

public class JSONArray implements JSONElement, Iterable<JSONElement> {

    protected JSONElement[] values;

    public JSONArray() {
        values = null;
    }

    public JSONArray(int len) {
        values = new JSONElement[len];
    }

    public JSONArray(JSONElement[] values) {
        this.values = values;
    }

    public JSONElement get(int index) {
        if (values == null)
            throw new ArrayIndexOutOfBoundsException();
        if (index < 0 || index >= values.length)
            throw new ArrayIndexOutOfBoundsException();
        return values[index];
    }

    public void set(int index, JSONElement element) {
        if (values == null)
            throw new ArrayIndexOutOfBoundsException();
        if (index < 0 || index >= values.length)
            throw new ArrayIndexOutOfBoundsException();
        values[index] = element;
    }

    public int length() {
        if (values != null)
            return values.length;
        return -1;
    }

    @Override
    public void readContents(StringStream stringStream) throws JSONFormatException {
        boolean readMode = false;
        ArrayList<JSONElement> list = new ArrayList<>();

        char c;
        while (true) {
            c = stringStream.getChar();

            if (readMode) {

                if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == JSONReader.SEPERATOR) {
                    stringStream.advance();
                    continue;
                }

                if (c == JSONReader.END_LIST) {
                    stringStream.advance();
                    break;
                } else {
                    // read object if possible
                    JSONElement element = JSONUtil.readElement(stringStream);
                    list.add(element);
                }

            } else {

                if (c == JSONReader.BEGIN_LIST) {
                    readMode = true;
                    stringStream.advance();
                } else if (c == ' ' || c == '\t' || c == '\n' || c == '\r')
                    stringStream.advance();
                else
                    throw new JSONFormatException("Unexpected character: " + c);

            }
        }

        values = new JSONElement[list.size()];
        list.toArray(values);
    }

    @NotNull
    @Override
    public Iterator<JSONElement> iterator() {
        return new JSONArrayIterator(this);
    }

    @Override
    public void forEach(Consumer<? super JSONElement> action) {
        if (values == null)
            return;

        for (JSONElement value : values)
            action.accept(value);
    }

    @Override
    public String toString() {
        if (values == null || values.length == 0)
            return "[]";

        StringBuilder builder = new StringBuilder();
        builder.append('[').append('\n');

        for (JSONElement value : values) {
            if (builder.length() > 2) // add separator, if this is not the first entry
                builder.append(",\n");

            if (value == null)
                continue;

            String valueString = value.toString();
            valueString = valueString.replace("\n", "\n\t");

            builder.append('\t')
                    .append(valueString);
        }

        builder.append("\n]");
        return builder.toString();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetaElement toMetaElement() {
        if (values == null)
            return null;

        MetaData[] data = new MetaData[values.length];
        for (int i = 0; i < values.length; i++)
            data[i] = values[i].toMeta();

        return new ArrayElement<>(data);
    }

    @Override
    public MetaData toMeta() {
        return toMetaElement();
    }

    private static class JSONArrayIterator implements Iterator<JSONElement> {

        private final JSONArray array;
        private int index;

        private JSONArrayIterator(JSONArray array) {
            this.array = array;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < array.length();
        }

        @Override
        public JSONElement next() {
            return array.get(index++);
        }

        @Override
        public void remove() {
            array.set(index, null);
        }

        @Override
        public void forEachRemaining(Consumer<? super JSONElement> action) {
            for (; index < array.length(); index++)
                action.accept(array.get(index));
        }
    }
}
