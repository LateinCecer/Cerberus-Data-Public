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

import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;

public class PrivateKeyElementBuilder implements MetaBuilder<PrivateKeyElement> {

    @SuppressWarnings("DuplicatedCode")
    @Override
    public PrivateKeyElement build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        byte type = inputStream.readByte();
        if (type == PrivateKeyElement.RSA) {

            BigInteger modulus = readBigInteger(inputStream);
            BigInteger exponent = readBigInteger(inputStream);

            return new PrivateKeyElement(genRSAKey(modulus, exponent));
        } else { // assert type = PrivateKeyElement.DSA

            BigInteger x = readBigInteger(inputStream);
            BigInteger p = readBigInteger(inputStream);
            BigInteger q = readBigInteger(inputStream);
            BigInteger g = readBigInteger(inputStream);

            return new PrivateKeyElement(genDSAKey(x, p, q, g));
        }
    }

    private BigInteger readBigInteger(MetaInputStream inputStream) throws IOException {
        byte[] bytes = new byte[inputStream.readInt()];
        inputStream.readFully(bytes);
        return new BigInteger(bytes);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public PrivateKeyElement build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        byte type = buffer.readByte();
        if (type == PrivateKeyElement.RSA) {

            BigInteger modulus = readBigInteger(buffer);
            BigInteger exponent = readBigInteger(buffer);

            return new PrivateKeyElement(genRSAKey(modulus, exponent));
        } else { // assert type = PrivateKeyElement.DSA

            BigInteger x = readBigInteger(buffer);
            BigInteger p = readBigInteger(buffer);
            BigInteger q = readBigInteger(buffer);
            BigInteger g = readBigInteger(buffer);

            return new PrivateKeyElement(genDSAKey(x, p, q, g));
        }
    }

    private BigInteger readBigInteger(MetaByteBuffer buffer) {
        byte[] bytes = new byte[buffer.readInt()];
        buffer.read(bytes);
        return new BigInteger(bytes);
    }

    public static RSAPrivateKey genRSAKey(BigInteger modulus, BigInteger exponent) {
        RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, exponent);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) factory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("System does not support RSA encryption!");
        }
    }

    public static DSAPrivateKey genDSAKey(BigInteger x, BigInteger p, BigInteger q, BigInteger g) {
        DSAPrivateKeySpec spec = new DSAPrivateKeySpec(x, p, q, g);
        try {
            KeyFactory factory = KeyFactory.getInstance("DSA");
            return (DSAPrivateKey) factory.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("System does not support DSA encryption!");
        }
    }

    @Override
    public Class<PrivateKeyElement> getDataClass() {
        return PrivateKeyElement.class;
    }

    @Override
    public int getFinalSize() {
        return -1;
    }

    @Override
    public boolean isTag() {
        return false;
    }
}
