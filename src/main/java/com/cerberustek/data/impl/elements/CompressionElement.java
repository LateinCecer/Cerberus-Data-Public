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
import com.cerberustek.data.*;
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.buffer.MetaByteBufferImpl;
import com.cerberustek.data.impl.tags.CompressionTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.util.zip.Deflater;

public class CompressionElement<T extends MetaData> extends MetaElementImpl<T> implements MetaCompression<T> {

    private final Deflater deflater;

    private byte[] buffer = null;
    private int deflatedLength = 0;

    public CompressionElement(T data) {
        this(data, new Deflater());
    }

    public CompressionElement(T data, Deflater deflater) {
        super(data);
        this.deflater = deflater;
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeInt(buffer.length);
        metaOutputStream.writeInt(deflatedLength);
        metaOutputStream.write(buffer, 0, deflatedLength);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(buffer.length);
        metaBuffer.writeInt(deflatedLength);
        metaBuffer.write(buffer, 0, deflatedLength);
    }

    @Override
    public long byteSize() {
        return deflatedLength + 8;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    @Override
    public boolean deflate(DiscriminatorMap discriminatorMap) throws IOException, NoMatchingDiscriminatorException, UnknownDiscriminatorException {
        if (get() != null) {
            long size = CerberusData.totalSize(get()) + 4;

            if (size < Integer.MAX_VALUE - 3) {
                MetaByteBuffer outputBuffer = new MetaByteBufferImpl(discriminatorMap, (int) size);
                outputBuffer.writeData(get());

                byte[] raw = new byte[outputBuffer.capacity()];
                buffer = new byte[outputBuffer.capacity()];
                outputBuffer.rewind();
                outputBuffer.read(raw);

                deflater.setInput(raw);
                deflater.finish();
                deflatedLength = deflater.deflate(buffer);
                deflater.reset();
                return true;
            }
        }
        return false;
    }

    @Override
    public CompressionTag<T> toTag(String tag) {
        return new CompressionTag<>(tag, get());
    }
}
