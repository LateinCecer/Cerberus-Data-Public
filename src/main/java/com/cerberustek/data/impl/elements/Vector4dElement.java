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

package com.cerberustek.data.impl.elements;

import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.MetaTag;
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.tags.Vector4dTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.logic.math.Vector4d;

import java.io.IOException;

public class Vector4dElement extends MetaElementImpl<Vector4d> {

    public Vector4dElement() {
    }

    public Vector4dElement(Vector4d value) {
        super(value);
    }

    public Vector4dElement(double x, double y, double z, double w) {
        super(new Vector4d(x, y, z, w));
    }

    @Override
    public MetaTag toTag(String tag) {
        return new Vector4dTag(tag, get());
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeDouble(get().getX());
        metaOutputStream.writeDouble(get().getY());
        metaOutputStream.writeDouble(get().getZ());
        metaOutputStream.writeDouble(get().getW());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeDouble(get().getX());
        metaBuffer.writeDouble(get().getY());
        metaBuffer.writeDouble(get().getZ());
        metaBuffer.writeDouble(get().getW());
    }

    @Override
    public long byteSize() {
        return 32;
    }

    @Override
    public long finalSize() {
        return 32;
    }
}
