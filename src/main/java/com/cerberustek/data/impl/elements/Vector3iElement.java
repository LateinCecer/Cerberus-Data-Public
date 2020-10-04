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
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.tags.Vector3iTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.logic.math.Vector3i;

import java.io.IOException;

public class Vector3iElement extends MetaElementImpl<Vector3i> {

    public Vector3iElement(Vector3i value) {
        super(value);
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException {
        metaOutputStream.writeInt(get().getX());
        metaOutputStream.writeInt(get().getY());
        metaOutputStream.writeInt(get().getZ());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(get().getX());
        metaBuffer.writeInt(get().getY());
        metaBuffer.writeInt(get().getZ());
    }

    @Override
    public long byteSize() {
        return finalSize();
    }

    @Override
    public long finalSize() {
        return 12;
    }

    @Override
    public Vector3iTag toTag(String tag) {
        return new Vector3iTag(tag, get());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof Vector3iElement;
        return ((Vector3iElement) obj).get().equals(get());
    }
}
