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
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.MetaArrayImpl;
import com.cerberustek.data.impl.tags.SpecificArrayTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;

public class SpecificArrayElement<T extends MetaElement> extends MetaArrayImpl<T> implements MetaElement<T[]> {

    protected final Class<T> clazz;

    public SpecificArrayElement(Class<T> clazz, T[] values) {
        super(values);
        this.clazz = clazz;
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeShort(metaOutputStream.getDiscriminatorMap().getDiscriminator(clazz));
        metaOutputStream.writeInt(length());
        for (T t : values)
            t.serialize(metaOutputStream);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeShort(metaBuffer.getDiscriminatorMap().getDiscriminator(clazz));
        metaBuffer.writeInt(length());
        for (T t : values)
            t.serialize(metaBuffer);
    }

    @Override
    public long byteSize() {
        long size = 6;
        for (T t : values)
            size += t.byteSize();
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

    public SpecificArrayTag<T> toTag(String tag) {
        return new SpecificArrayTag<>(tag, clazz, values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof SpecificArrayElement;
        if (((SpecificArrayElement) obj).byteSize() != byteSize())
            return false;

        if (((SpecificArrayElement) obj).get().length != get().length)
            return false;

        for (int i = 0; i < get().length; i++) {
            if (get()[i] == null) {
                if (((SpecificArrayElement) obj).get()[i] != null)
                    return false;
                continue;
            }
            if (!get()[i].equals(((SpecificArrayElement) obj).get()[i]))
                return false;
        }
        return false;
    }
}
