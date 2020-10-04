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
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.MetaMapImpl;
import com.cerberustek.data.impl.tags.MapTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class MapElement<T extends MetaElement, D extends MetaElement> extends MetaMapImpl<T, D> implements
        MetaElement<Map<T, D>> {

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeInt(size());
        for (T t : keySet()) {
            long size = 0;

            if (t != null) {
                size += CerberusData.size(t);
                D value = get(t);

                if (value != null) {
                    size += CerberusData.size(value);

                    /* Both key and value are != null */
                    metaOutputStream.writeLong(size);
                    metaOutputStream.writeShort(metaOutputStream.getDiscriminatorMap().getDiscriminator(t.getClass()));
                    metaOutputStream.writeShort(metaOutputStream.getDiscriminatorMap().getDiscriminator(value.getClass()));
                    t.serialize(metaOutputStream);
                    value.serialize(metaOutputStream);
                } else {
                    size += CerberusData.DISCRIMINATOR_SIZE;

                    metaOutputStream.writeLong(size);
                    metaOutputStream.writeShort(metaOutputStream.getDiscriminatorMap().getDiscriminator(t.getClass()));
                    metaOutputStream.writeShort(CerberusData.CERBERUS_NULL);
                    t.serialize(metaOutputStream);
                }
            }
        }
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(size());
        for (T t : keySet()) {
            long size = 0;

            if (t != null) {
                size += CerberusData.size(t);
                D value = get(t);

                if (value != null) {
                    size += CerberusData.size(value);
                    size += 8 + CerberusData.DISCRIMINATOR_SIZE * CerberusData.DISCRIMINATOR_SIZE; // Size dec and stuff like that

                    /* Both key and value are != null */
                    metaBuffer.writeLong(size);
                    metaBuffer.writeShort(metaBuffer.getDiscriminatorMap().getDiscriminator(t.getClass()));
                    metaBuffer.writeShort(metaBuffer.getDiscriminatorMap().getDiscriminator(value.getClass()));
                    t.serialize(metaBuffer);
                    value.serialize(metaBuffer);
                } else {
                    size += 8 + CerberusData.DISCRIMINATOR_SIZE * CerberusData.DISCRIMINATOR_SIZE; // Size dec and stuff like that

                    metaBuffer.writeLong(size);
                    metaBuffer.writeShort(metaBuffer.getDiscriminatorMap().getDiscriminator(t.getClass()));
                    metaBuffer.writeShort(CerberusData.CERBERUS_NULL);
                    t.serialize(metaBuffer);
                }
            }
        }
    }

    @Override
    public long byteSize() {
        int size = 4;
        for (T t : keySet()) {
            if (t != null) {
                size += CerberusData.size(t);
                D value = get(t);

                if (value != null)
                    size += 8 + CerberusData.DISCRIMINATOR_SIZE * CerberusData.DISCRIMINATOR_SIZE + CerberusData.size(value); // Size dec and stuff like that
                else
                    size += 8 + CerberusData.DISCRIMINATOR_SIZE * CerberusData.DISCRIMINATOR_SIZE; // Size dec and stuff like that
            }
        }
        return size;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    @Override
    public void set(Map<T, D> value) {
        putAll(value);
    }

    @Override
    public Map<T, D> get() {
        return this;
    }

    public MapTag<T, D> toTag(String tag) {
        MapTag<T, D> output = new MapTag<>(tag);
        output.putAll(this);
        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof MapElement;
        return ((MapElement) obj).get().equals(get());
    }
}
