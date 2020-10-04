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

import com.cerberustek.data.impl.elements.Vector4fElement;
import com.cerberustek.data.MetaTag;
import com.cerberustek.logic.math.Vector4f;
import org.jetbrains.annotations.NotNull;

public class Vector4fTag extends Vector4fElement implements MetaTag {

    private String tag;

    public Vector4fTag(@NotNull String tag) {
        this.tag = tag;
    }

    public Vector4fTag(@NotNull String tag, @NotNull Vector4f value) {
        super(value);
        this.tag = tag;
    }

    public Vector4fTag(@NotNull String tag, float x, float y, float z, float w) {
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
    public @NotNull Vector4fElement toElement() {
        return new Vector4fElement(get());
    }
}
