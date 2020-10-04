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

package com.cerberustek.data.impl.tags;

import com.cerberustek.data.impl.elements.Vector2lElement;
import com.cerberustek.data.MetaTag;
import com.cerberustek.logic.math.Vector2l;

import java.util.Objects;

public class Vector2lTag extends Vector2lElement implements MetaTag {

    private String tag;

    public Vector2lTag(String tag, Vector2l value) {
        super(value);
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
    public Vector2lElement toElement() {
        return new Vector2lElement(get());
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            assert obj instanceof MetaTag;
            return ((MetaTag) obj).getTag().equals(getTag());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, get());
    }
}
