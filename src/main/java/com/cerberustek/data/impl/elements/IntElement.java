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
import com.cerberustek.data.impl.tags.IntTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;

public class IntElement extends MetaElementImpl<Integer> {

    public IntElement(Integer value) {
        super(value);
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException {
        metaOutputStream.writeInt(get());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(get());
    }

    @Override
    public long byteSize() {
        return finalSize();
    }

    @Override
    public long finalSize() {
        return 4;
    }

    @Override
    public IntTag toTag(String tag) {
        return new IntTag(tag, get());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof IntElement;
        return ((IntElement) obj).get().equals(get());
    }
}
