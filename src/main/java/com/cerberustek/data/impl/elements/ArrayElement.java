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

import com.cerberustek.CerberusData;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.MetaArrayImpl;
import com.cerberustek.data.impl.tags.ArrayTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;

public class ArrayElement<T extends MetaData> extends MetaArrayImpl<T> implements MetaData, MetaElement<T[]> {

    public ArrayElement(T[] values) {
        super(values);
    }

    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        if (values == null) {
            metaOutputStream.writeInt(0);
            return;
        }

        metaOutputStream.writeInt(values.length);
        for (T t : values)
            metaOutputStream.writeData(t);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        if (values == null) {
            metaBuffer.writeInt(0);
            return;
        }

        metaBuffer.writeInt(values.length);
        for (T t : values)
            metaBuffer.writeData(t);
    }

    public long byteSize() {
        long size = 4;
        if (values == null)
            return size;

        for (T t : values) {
            if (t != null)
                size += CerberusData.totalSize(t);
        }
        return size;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    @Override
    public void set(T[] value) {
        values = value;
    }

    @Override
    public T[] get() {
        return values;
    }

    @Override
    public ArrayTag<T> toTag(String tag) {
        return new ArrayTag<>(tag, values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof ArrayElement;
        if (((ArrayElement) obj).byteSize() != byteSize())
            return false;

        if (((ArrayElement) obj).get().length != get().length)
            return false;

        for (int i = 0; i < get().length; i++) {
            if (get()[i] == null) {
                if (((ArrayElement) obj).get()[i] != null)
                    return false;
                continue;
            }
            if (!get()[i].equals(((ArrayElement) obj).get()[i]))
                return false;
        }
        return true;
    }
}
