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
import com.cerberustek.data.impl.tags.DoubleTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;

public class DoubleElement extends MetaElementImpl<Double> {

    public DoubleElement(Double value) {
        super(value);
    }

    public void serialize(MetaOutputStream metaOutputStream) throws IOException {
        metaOutputStream.writeDouble(get());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeDouble(get());
    }

    public long byteSize() {
        return finalSize();
    }

    @Override
    public long finalSize() {
        return 8;
    }

    @Override
    public DoubleTag toTag(String tag) {
        return new DoubleTag(tag, get());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof DoubleElement;
        return ((DoubleElement) obj).get().equals(get());
    }
}
