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
import com.cerberustek.cipher.SymmetricCipher;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.buffer.MetaByteBufferImpl;
import com.cerberustek.data.impl.tags.CipherTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;

public class CipherElement<T extends MetaData> extends MetaElementImpl<T> {

    protected final SymmetricCipher cipher;

    public CipherElement(SymmetricCipher cipher, T data) {
        this.cipher = cipher;
        set(data);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        long byteSize = CerberusData.totalSize(get());
        if (byteSize > Integer.MAX_VALUE)
            throw new ArrayIndexOutOfBoundsException();

        MetaByteBuffer buffer = new MetaByteBufferImpl(metaOutputStream.getDiscriminatorMap(), (int) byteSize);
        buffer.writeData(get());
        buffer.rewind();
        byte[] data = new byte[buffer.remaining()];
        buffer.read(data);
        // cipher.reset();
        cipher.encrypt(data);
        metaOutputStream.writeInt((int) byteSize);
        metaOutputStream.write(data);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        long byteSize = CerberusData.totalSize(get());
        if (byteSize > Integer.MAX_VALUE)
            throw new ArrayIndexOutOfBoundsException();

        MetaByteBuffer buffer = new MetaByteBufferImpl(metaBuffer.getDiscriminatorMap(), (int) byteSize);
        buffer.writeData(get());
        buffer.rewind();
        byte[] data = new byte[buffer.remaining()];
        buffer.read(data);
        // cipher.reset();
        cipher.encrypt(data);
        metaBuffer.writeInt((int) byteSize);
        metaBuffer.write(data);
    }

    @Override
    public long byteSize() {
        return CerberusData.totalSize(get());
    }

    @Override
    public long finalSize() {
        return -1;
    }

    @Override
    public CipherTag<T> toTag(String tag) {
        return new CipherTag<>(tag, cipher, get());
    }
}
