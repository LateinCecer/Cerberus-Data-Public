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
import com.cerberustek.data.MetaTag;
import com.cerberustek.data.impl.tags.ArrayTag;
import com.cerberustek.json.JSONElement;
import com.cerberustek.json.JSONTag;

public class JSONTagArray extends JSONArray implements JSONTag {

    private String tag;

    public JSONTagArray(String tag) {
        super();
        this.tag = tag;
    }

    public JSONTagArray(String tag, int len) {
        super(len);
        this.tag = tag;
    }

    public JSONTagArray(String tag, JSONElement[] data) {
        super(data);
        this.tag = tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public MetaTag toMetaTag() {
        if (values == null)
            return null;

        MetaData[] data = new MetaData[values.length];
        for (int i = 0; i < values.length; i++)
            data[i] = values[i].toMeta();
        return new ArrayTag<>(tag, data);
    }

    @Override
    public MetaData toMeta() {
        return toMetaTag();
    }

    @Override
    public String toString() {
        return "\"" + tag + "\": " + super.toString();
    }
}
