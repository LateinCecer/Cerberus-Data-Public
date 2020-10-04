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
import com.cerberustek.data.impl.tags.Matrix4fTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.logic.math.Matrix4f;

import java.io.IOException;

public class Matrix4fElement extends MetaElementImpl<Matrix4f> {

    public Matrix4fElement(Matrix4f value) {
        super(value);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                metaOutputStream.writeFloat(get().get(x, y));
            }
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                metaBuffer.writeFloat(get().get(x, y));
            }
        }
    }

    @Override
    public long byteSize() {
        return finalSize();
    }

    @Override
    public long finalSize() {
        return 64;
    }

    @Override
    public Matrix4fTag toTag(String tag) {
        return new Matrix4fTag(tag, get());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof Matrix4fElement;
        return ((Matrix4fElement) obj).get().equals(get());
    }
}
