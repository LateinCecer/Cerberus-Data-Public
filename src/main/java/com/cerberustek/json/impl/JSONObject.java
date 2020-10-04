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
import com.cerberustek.data.impl.elements.DocElement;
import com.cerberustek.exception.JSONFormatException;
import com.cerberustek.json.*;

import java.util.HashMap;

public class JSONObject implements JSONElement {

    protected final HashMap<String, JSONElement> data = new HashMap<>();

    @Override
    public void readContents(StringStream stringStream) throws JSONFormatException {
        boolean readMode = false;
        data.clear();

        char c;
        while (true) {
            c = stringStream.getChar();
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == JSONReader.SEPERATOR) {
                stringStream.advance();
                continue;
            }

            if (readMode) {

                if (c == JSONReader.END_OBJ) {
                    stringStream.advance();
                    break;
                } else {
                    // attempt to read object
                    JSONElement obj = JSONUtil.readElement(stringStream);
                    if (!(obj instanceof JSONTag))
                        throw new JSONFormatException("Element inside of object has to have a tag/name!");

                    data.put(((JSONTag) obj).getTag(), obj);
                }

            } else {

                if (c == JSONReader.BEGIN_OBJ) {
                    readMode = true;
                    stringStream.advance();
                } else
                    throw new JSONFormatException("Unexpected character " + c + " at index " + stringStream.getIndex());

            }
        }
    }

    /**
     * Returns the amount of elements contained inside the
     * JSON object.
     * @return size of the object
     */
    public int size() {
        return data.size();
    }

    /**
     * Returns the JSON element with the corresponding tag.
     *
     * If the element with the tag does not exist, this
     * will return null.
     *
     * @param tag to look for
     * @return element corresponding to the tag
     */
    public JSONElement getElement(String tag) {
        return data.get(tag);
    }

    /**
     * Returns the JSON element with the corresponding tag.
     *
     * If the element with the tag does not exist, this
     * will return null. This method will cast the element
     * to the specified class type.
     * If the retrieved element does not inherit the specified
     * class, this method will return null.
     *
     * @param tag tag of the element to find
     * @param clazz superclass to cast to
     * @param <T> class Type
     * @return retrieved element of Type <T>
     */
    public <T extends JSONElement> T getElement(String tag, Class<T> clazz) {
        try {
            JSONElement element = data.get(tag);
            return clazz.cast(element);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Will clear all elements from the object.
     */
    public void clear() {
        data.clear();
    }

    /**
     * Will add all elements in the <code>values</code> map
     * to the elements of the object.
     *
     * @param values values
     */
    public void putAll(HashMap<String, JSONElement> values) {
        data.putAll(values);
    }

    public void put(String tag, JSONElement element) {
        data.put(tag, element);
    }

    /**
     * Replaces all elements in the object with the elements
     * in the <code>values</code> map.
     *
     * @param values new elements
     */
    public void refill(HashMap<String, JSONElement> values) {
        data.clear();
        data.putAll(values);
    }

    @Override
    public String toString() {
        if (data.isEmpty())
            return "null";

        StringBuilder builder = new StringBuilder();
        builder.append('{').append('\n');

        data.forEach((key, value) -> {
            if (builder.length() > 2) // add separator, if this is not the first entry
                builder.append(",\n");

            if (value == null)
                return;
            String valueString = value.toString();
            valueString = valueString.replace("\n", "\n\t");

            builder.append('\t')
                    .append(valueString);
        });

        builder.append("\n}");
        return builder.toString();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetaElement toMetaElement() {
        DocElement meta = new DocElement();
        data.forEach((key, value) -> meta.insert(value instanceof JSONTag ? ((JSONTag) value).toMetaTag() :
                value.toMetaElement().toTag(key)));
        return meta;
    }

    @Override
    public MetaData toMeta() {
        return toMetaElement();
    }
}
