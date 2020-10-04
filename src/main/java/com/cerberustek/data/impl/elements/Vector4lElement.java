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
import com.cerberustek.data.impl.tags.Vector4lTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.logic.math.Vector4l;

import java.io.IOException;

public class Vector4lElement extends MetaElementImpl<Vector4l> {

    public Vector4lElement() {
    }

    public Vector4lElement(Vector4l value) {
        super(value);
    }

    public Vector4lElement(long x, long y, long z, long w) {
        super(new Vector4l(x, y, z, w));
    }

    @Override
    public MetaTag toTag(String tag) {
        return new Vector4lTag(tag, get());
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeLong(get().getX());
        metaOutputStream.writeLong(get().getY());
        metaOutputStream.writeLong(get().getZ());
        metaOutputStream.writeLong(get().getW());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeLong(get().getX());
        metaBuffer.writeLong(get().getY());
        metaBuffer.writeLong(get().getZ());
        metaBuffer.writeLong(get().getW());
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
