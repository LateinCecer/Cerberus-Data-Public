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
import com.cerberustek.data.impl.tags.Vector4fTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.logic.math.Vector4f;

import java.io.IOException;

public class Vector4fElement extends MetaElementImpl<Vector4f> {

    public Vector4fElement() {
    }

    public Vector4fElement(Vector4f value) {
        super(value);
    }

    public Vector4fElement(float x, float y, float z, float w) {
        super(new Vector4f(x, y, z, w));
    }

    @Override
    public MetaTag toTag(String tag) {
        return new Vector4fTag(tag, get());
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeFloat(get().getX());
        metaOutputStream.writeFloat(get().getY());
        metaOutputStream.writeFloat(get().getZ());
        metaOutputStream.writeFloat(get().getW());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeFloat(get().getX());
        metaBuffer.writeFloat(get().getY());
        metaBuffer.writeFloat(get().getZ());
        metaBuffer.writeFloat(get().getW());
    }

    @Override
    public long byteSize() {
        return 16;
    }

    @Override
    public long finalSize() {
        return 16;
    }
}
