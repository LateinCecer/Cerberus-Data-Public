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

import com.cerberustek.data.impl.elements.PrivateKeyElement;
import com.cerberustek.data.impl.elements.PrivateKeyElementBuilder;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.math.BigInteger;

public class PrivateKeyTagBuilder implements MetaBuilder<PrivateKeyTag> {

    @SuppressWarnings("DuplicatedCode")
    @Override
    public PrivateKeyTag build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        byte type = inputStream.readByte();
        if (type == PrivateKeyElement.RSA) {

            BigInteger modulus = readBigInteger(inputStream);
            BigInteger exponent = readBigInteger(inputStream);

            return new PrivateKeyTag(tag, PrivateKeyElementBuilder.genRSAKey(modulus, exponent));

        } else { // assert type == PrivateKeyElement.DSA

            BigInteger x = readBigInteger(inputStream);
            BigInteger p = readBigInteger(inputStream);
            BigInteger q = readBigInteger(inputStream);
            BigInteger g = readBigInteger(inputStream);

            return new PrivateKeyTag(tag, PrivateKeyElementBuilder.genDSAKey(x, p, q, g));
        }
    }

    private BigInteger readBigInteger(MetaInputStream inputStream) throws IOException {
        byte[] bytes = new byte[inputStream.readInt()];
        inputStream.readFully(bytes);
        return new BigInteger(bytes);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public PrivateKeyTag build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        byte type = buffer.readByte();
        if (type == PrivateKeyElement.RSA) {

            BigInteger modulus = readBigInteger(buffer);
            BigInteger exponent = readBigInteger(buffer);

            return new PrivateKeyTag(tag, PrivateKeyElementBuilder.genRSAKey(modulus, exponent));

        } else { // assert type == PrivateKeyElement.DSA

            BigInteger x = readBigInteger(buffer);
            BigInteger p = readBigInteger(buffer);
            BigInteger q = readBigInteger(buffer);
            BigInteger g = readBigInteger(buffer);

            return new PrivateKeyTag(tag, PrivateKeyElementBuilder.genDSAKey(x, p, q, g));
        }
    }

    private BigInteger readBigInteger(MetaByteBuffer buffer) {
        byte[] bytes = new byte[buffer.readInt()];
        buffer.read(bytes);
        return new BigInteger(bytes);
    }

    @Override
    public Class<PrivateKeyTag> getDataClass() {
        return PrivateKeyTag.class;
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
