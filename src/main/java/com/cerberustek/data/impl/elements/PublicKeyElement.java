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
import com.cerberustek.data.MetaTag;
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.tags.PublicKeyTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class PublicKeyElement extends MetaElementImpl<PublicKey> {

    public PublicKeyElement(PublicKey key) {
        super(key);
        if (!(key instanceof RSAPublicKey) && !(key instanceof DSAPublicKey))
            throw new IllegalArgumentException("Key has to be RSA or DSA type!");
    }

    @Override
    public void set(PublicKey value) {
        super.set(value);
        if (!(value instanceof RSAPublicKey) && !(value instanceof DSAPublicKey))
            throw new IllegalArgumentException("Key has to be RSA or DSA type!");
    }

    @Override
    public MetaTag toTag(String tag) {
        return new PublicKeyTag(tag, get());
    }

    public String getTypeString() {
        return get() instanceof RSAPrivateKey ? "RSA" : "DSA";
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        if (get() instanceof RSAPublicKey) {
            metaOutputStream.writeByte(PrivateKeyElement.RSA);
            RSAPublicKey key = (RSAPublicKey) get();

            BigInteger modulus = key.getModulus();
            BigInteger exponent = key.getPublicExponent();

            byte[] modulusBytes = modulus.toByteArray();
            byte[] exponentBytes = exponent.toByteArray();

            metaOutputStream.writeInt(modulusBytes.length);
            metaOutputStream.write(modulusBytes);
            metaOutputStream.writeInt(exponentBytes.length);
            metaOutputStream.write(exponentBytes);

        } else {
            assert get() instanceof DSAPublicKey;
            metaOutputStream.writeByte(PrivateKeyElement.DSA);
            DSAPublicKey key = (DSAPublicKey) get();

            BigInteger y = key.getY();
            BigInteger p = key.getParams().getP();
            BigInteger q = key.getParams().getQ();
            BigInteger g = key.getParams().getG();

            byte[] yBytes = y.toByteArray();
            byte[] pBytes = p.toByteArray();
            byte[] qBytes = q.toByteArray();
            byte[] gBytes = g.toByteArray();

            metaOutputStream.writeInt(yBytes.length);
            metaOutputStream.write(yBytes);
            metaOutputStream.writeInt(pBytes.length);
            metaOutputStream.write(pBytes);
            metaOutputStream.writeInt(qBytes.length);
            metaOutputStream.write(qBytes);
            metaOutputStream.writeInt(gBytes.length);
            metaOutputStream.write(gBytes);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        if (get() instanceof RSAPublicKey) {
            metaBuffer.writeByte(PrivateKeyElement.RSA);
            RSAPublicKey key = (RSAPublicKey) get();

            BigInteger modulus = key.getModulus();
            BigInteger exponent = key.getPublicExponent();

            byte[] modulusBytes = modulus.toByteArray();
            byte[] exponentBytes = exponent.toByteArray();

            metaBuffer.writeInt(modulusBytes.length);
            metaBuffer.write(modulusBytes);
            metaBuffer.writeInt(exponentBytes.length);
            metaBuffer.write(exponentBytes);
        } else {
            metaBuffer.writeByte(PrivateKeyElement.DSA);
            DSAPublicKey key = (DSAPublicKey) get();

            BigInteger y = key.getY();
            BigInteger p = key.getParams().getP();
            BigInteger q = key.getParams().getQ();
            BigInteger g = key.getParams().getG();

            byte[] yBytes = y.toByteArray();
            byte[] pBytes = p.toByteArray();
            byte[] qBytes = q.toByteArray();
            byte[] gBytes = g.toByteArray();

            metaBuffer.writeInt(yBytes.length);
            metaBuffer.write(yBytes);
            metaBuffer.writeInt(pBytes.length);
            metaBuffer.write(pBytes);
            metaBuffer.writeInt(qBytes.length);
            metaBuffer.write(qBytes);
            metaBuffer.writeInt(gBytes.length);
            metaBuffer.write(gBytes);
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public long byteSize() {
        long sum = 1; // type byte
        if (get() instanceof RSAPublicKey) {
            RSAPublicKey key = (RSAPublicKey) get();

            sum += 4;
            sum += key.getModulus().toByteArray().length;
            sum += 4;
            sum += key.getPublicExponent().toByteArray().length;
        } else {
            DSAPublicKey key = (DSAPublicKey) get();

            sum += 4;
            sum += key.getY().toByteArray().length;
            sum += 4;
            sum += key.getParams().getP().toByteArray().length;
            sum += 4;
            sum += key.getParams().getQ().toByteArray().length;
            sum += 4;
            sum += key.getParams().getG().toByteArray().length;
        }
        return sum;
    }

    @Override
    public long finalSize() {
        return -1;
    }
}
