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
import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.buffer.MetaByteBufferImpl;
import com.cerberustek.data.impl.tags.ContainerTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ContainerElement extends MetaElementImpl<byte[]> {

    public ContainerElement(byte[] values) {
        set(values);
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeInt(get().length);
        metaOutputStream.write(get());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(get().length);
        metaBuffer.write(get());
    }

    @Override
    public long byteSize() {
        return get().length + 4;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    public MetaData get(@NotNull DiscriminatorMap map)
            throws UnknownDiscriminatorException {

        if (get().length == 0)
            return null;

        MetaByteBuffer buffer = new MetaByteBufferImpl(map, ByteBuffer.wrap(get()));
        buffer.rewind();
        return buffer.readData();
    }

    @SuppressWarnings("DuplicatedCode")
    public boolean set(@NotNull MetaData data, @NotNull DiscriminatorMap map)
            throws NoMatchingDiscriminatorException {

        long size = CerberusData.totalSize(data);

        if (size < Integer.MAX_VALUE - 3) {
            MetaByteBuffer outputBuffer = new MetaByteBufferImpl(map, (int) size);
            outputBuffer.writeData(data);

            byte[] raw = new byte[outputBuffer.capacity()];
            outputBuffer.rewind();
            outputBuffer.read(raw);

            set(raw);
            return true;
        }
        return false;
    }

    @Override
    public ContainerTag toTag(String tag) {
        return new ContainerTag(tag, get());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof ContainerElement;
        return Arrays.equals(((ContainerElement) obj).get(), get());
    }
}
