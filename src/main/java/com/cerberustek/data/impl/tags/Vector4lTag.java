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

import com.cerberustek.data.impl.elements.Vector4lElement;
import com.cerberustek.data.MetaTag;
import com.cerberustek.logic.math.Vector4l;
import org.jetbrains.annotations.NotNull;

public class Vector4lTag extends Vector4lElement implements MetaTag {

    private String tag;

    public Vector4lTag(@NotNull String tag) {
        this.tag = tag;
    }

    public Vector4lTag(@NotNull String tag, @NotNull Vector4l value) {
        super(value);
        this.tag = tag;
    }

    public Vector4lTag(@NotNull String tag, long x, long y, long z, long w) {
        super(x, y, z, w);
        this.tag = tag;
    }

    @Override
    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }

    @Override
    public @NotNull String getTag() {
        return tag;
    }

    @Override
    public @NotNull Vector4lElement toElement() {
        return new Vector4lElement(get());
    }
}
