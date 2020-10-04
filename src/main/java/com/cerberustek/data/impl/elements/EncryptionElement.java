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
import com.cerberustek.cipher.CerberusCipher;
import com.cerberustek.cipher.DecryptionCerberusCipher;
import com.cerberustek.cipher.EncryptionCerberusCipher;
import com.cerberustek.data.*;
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.buffer.MetaByteBufferImpl;
import com.cerberustek.data.impl.tags.EncryptionTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class EncryptionElement extends MetaElementImpl<byte[]> {

    public EncryptionElement(@NotNull MetaData value, @NotNull CerberusCipher cipher, @NotNull DiscriminatorMap map)
            throws NoMatchingDiscriminatorException {
        super(new byte[0]);
        set(value, cipher, map);
    }

    public EncryptionElement(byte[] raw) {
        super(raw);
    }

    public EncryptionElement() {
        super(new byte[0]);
    }

    @Override
    public MetaTag toTag(String tag) {
        return new EncryptionTag(tag, get());
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        if (get().length == 0) {
            metaOutputStream.writeInt(0);
        } else {
            metaOutputStream.writeInt(get().length);
            metaOutputStream.write(get());
        }
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        if (get().length == 0) {
            metaBuffer.writeInt(0);
        } else {
            metaBuffer.writeInt(get().length);
            metaBuffer.write(get());
        }
    }

    public MetaData get(@NotNull DecryptionCerberusCipher cipher, @NotNull DiscriminatorMap map)
            throws UnknownDiscriminatorException {

        if (get().length == 0)
            return null;

        byte[] raw = cipher.decrypt(get());
        MetaByteBuffer buffer = new MetaByteBufferImpl(map, ByteBuffer.wrap(raw));
        buffer.rewind();
        return buffer.readData();
    }

    @SuppressWarnings("DuplicatedCode")
    public boolean set(@NotNull MetaData data, @NotNull EncryptionCerberusCipher cipher, @NotNull DiscriminatorMap map)
            throws NoMatchingDiscriminatorException {

        long size = data.byteSize();
        long cap = size + CerberusData.DISCRIMINATOR_SIZE;
        short discriminator = map.getDiscriminator(data.getClass());
        if (data.finalSize() < 0)
            cap += CerberusData.SIZE_DEC;
        if (data instanceof MetaTag)
            cap += ((MetaTag) data).getTag().getBytes().length + 2;

        if (size < Integer.MAX_VALUE - 3) {
            MetaByteBuffer outputBuffer = new MetaByteBufferImpl(map, (int) cap);
            outputBuffer.writeShort(discriminator);
            if (data.finalSize() < 0)
                outputBuffer.writeLong(size);
            if (data instanceof MetaTag)
                outputBuffer.writeUTF(((MetaTag) data).getTag());
            data.serialize(outputBuffer);

            byte[] raw = new byte[outputBuffer.capacity()];
            outputBuffer.rewind();
            outputBuffer.read(raw);

            set(cipher.encrypt(raw));
            return true;
        }
        return false;
    }

    @Override
    public long byteSize() {
        return (get() == null ? 0 : get().length) + 4;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        return Arrays.equals(((EncryptionElement) obj).get(), get());
    }
}
