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
import com.cerberustek.data.impl.tags.PrivateKeyTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;

public class PrivateKeyElement extends MetaElementImpl<PrivateKey> {

    public static final byte RSA = 0;
    public static final byte DSA = 1;

    public PrivateKeyElement(PrivateKey key) {
        super(key);
        if (!(key instanceof RSAPrivateKey) && !(key instanceof DSAPrivateKey))
            throw new IllegalArgumentException("Private key has to be RSA or DSA type!");
    }

    @Override
    public void set(PrivateKey value) {
        if (!(value instanceof RSAPrivateKey) && !(value instanceof DSAPrivateKey))
            throw new IllegalArgumentException("Private key has to be RSA or DSA type!");
        super.set(value);
    }

    @Override
    public MetaTag toTag(String tag) {
        return new PrivateKeyTag(tag, get());
    }

    public String getTypeString() {
        return get() instanceof RSAPrivateKey ? "RSA" : "DSA";
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        if (get() instanceof RSAPrivateKey) {
            RSAPrivateKey key = (RSAPrivateKey) get();
            metaOutputStream.writeByte(RSA);

            BigInteger modulus = key.getModulus();
            BigInteger exponent = key.getPrivateExponent();

            byte[] modulusBytes = modulus.toByteArray();
            byte[] exponentBytes = exponent.toByteArray();

            metaOutputStream.writeInt(modulusBytes.length);
            metaOutputStream.write(modulusBytes);
            metaOutputStream.writeInt(exponentBytes.length);
            metaOutputStream.write(exponentBytes);
        } else {
            assert get() instanceof DSAPrivateKey;
            DSAPrivateKey key = (DSAPrivateKey) get();
            metaOutputStream.writeByte(DSA);

            BigInteger x = key.getX();
            BigInteger p = key.getParams().getP();
            BigInteger q = key.getParams().getQ();
            BigInteger g = key.getParams().getG();

            byte[] xBytes = x.toByteArray();
            byte[] pBytes = p.toByteArray();
            byte[] qBytes = q.toByteArray();
            byte[] gBytes = g.toByteArray();

            metaOutputStream.writeInt(xBytes.length);
            metaOutputStream.write(xBytes);
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
        if (get() instanceof RSAPrivateKey) {
            RSAPrivateKey key = (RSAPrivateKey) get();
            metaBuffer.writeByte(RSA);

            BigInteger modulus = key.getModulus();
            BigInteger exponent = key.getPrivateExponent();

            byte[] modulusBytes = modulus.toByteArray();
            byte[] exponentBytes = exponent.toByteArray();

            metaBuffer.writeInt(modulusBytes.length);
            metaBuffer.write(modulusBytes);
            metaBuffer.writeInt(exponentBytes.length);
            metaBuffer.write(exponentBytes);
        } else {
            assert get() instanceof DSAPrivateKey;
            DSAPrivateKey key = (DSAPrivateKey) get();
            metaBuffer.writeByte(DSA);

            BigInteger x = key.getX();
            BigInteger p = key.getParams().getP();
            BigInteger q = key.getParams().getQ();
            BigInteger g = key.getParams().getG();

            byte[] xBytes = x.toByteArray();
            byte[] pBytes = p.toByteArray();
            byte[] qBytes = q.toByteArray();
            byte[] gBytes = g.toByteArray();

            metaBuffer.writeInt(xBytes.length);
            metaBuffer.write(xBytes);
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
        if (get() instanceof RSAPrivateKey) {

            RSAPrivateKey key = (RSAPrivateKey) get();
            sum += 4; // modulus key length
            sum += key.getModulus().toByteArray().length; // modulus
            sum += 4; // exponent key length
            sum += key.getPrivateExponent().toByteArray().length; // exponent

        } else {
            assert get() instanceof DSAPrivateKey;

            DSAPrivateKey key = (DSAPrivateKey) get();
            sum += 4; // x key length
            sum += key.getX().toByteArray().length; // x key
            sum += 4; // p key length
            sum += key.getParams().getP().toByteArray().length; // p key
            sum += 4; // q key length
            sum += key.getParams().getQ().toByteArray().length; // q key
            sum += 4; // g key length
            sum += key.getParams().getG().toByteArray().length; // g key
        }
        return sum;
    }

    @Override
    public long finalSize() {
        return -1;
    }
}
