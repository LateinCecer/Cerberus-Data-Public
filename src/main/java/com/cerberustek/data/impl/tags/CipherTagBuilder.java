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

package com.cerberustek.data.impl.tags;

import com.cerberustek.data.impl.buffer.MetaByteBufferImpl;
import com.cerberustek.cipher.SymmetricCipher;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;

public class CipherTagBuilder implements MetaBuilder<CipherTag> {

    private final SymmetricCipher cipher;

    public CipherTagBuilder(SymmetricCipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public CipherTag build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        byte[] data = new byte[inputStream.readInt()];
        inputStream.readFully(data);
        // cipher.reset();
        cipher.decrypt(data);

        MetaByteBuffer buffer = new MetaByteBufferImpl(inputStream.getDiscriminatorMap(), data.length);
        buffer.write(data);
        buffer.rewind();
        //noinspection unchecked
        return new CipherTag(tag, cipher, buffer.readData());
    }

    @Override
    public CipherTag build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        byte[] data = new byte[buffer.readInt()];
        buffer.read(data);
        // cipher.reset();
        cipher.decrypt(data);

        MetaByteBuffer buf = new MetaByteBufferImpl(buffer.getDiscriminatorMap(), data.length);
        buf.write(data);
        buf.rewind();
        //noinspection unchecked
        return new CipherTag(tag, cipher, buf.readData());
    }

    @Override
    public Class<CipherTag> getDataClass() {
        return CipherTag.class;
    }

    @Override
    public int getFinalSize() {
        return -1;
    }

    @Override
    public boolean isTag() {
        return true;
    }
}
