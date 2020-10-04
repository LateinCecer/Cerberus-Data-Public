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
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class PublicKeyElementBuilder implements MetaBuilder<PublicKeyElement> {

    @SuppressWarnings("DuplicatedCode")
    @Override
    public PublicKeyElement build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        byte type = inputStream.readByte();
        if (type == PrivateKeyElement.RSA) {

            BigInteger modulus = readBigInteger(inputStream);
            BigInteger exponent = readBigInteger(inputStream);

            return new PublicKeyElement(genRSAKey(modulus, exponent));
        } else { // assert type = PrivateKeyElement.DSA

            BigInteger y = readBigInteger(inputStream);
            BigInteger p = readBigInteger(inputStream);
            BigInteger q = readBigInteger(inputStream);
            BigInteger g = readBigInteger(inputStream);

            return new PublicKeyElement(genDSAKey(y, p, q, g));
        }
    }

    private BigInteger readBigInteger(MetaInputStream inputStream) throws IOException {
        byte[] bytes = new byte[inputStream.readInt()];
        inputStream.readFully(bytes);
        return new BigInteger(bytes);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public PublicKeyElement build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        byte type = buffer.readByte();
        if (type == PrivateKeyElement.RSA) {

            BigInteger modulus = readBigInteger(buffer);
            BigInteger exponent = readBigInteger(buffer);

            return new PublicKeyElement(genRSAKey(modulus, exponent));
        } else {

            BigInteger y = readBigInteger(buffer);
            BigInteger p = readBigInteger(buffer);
            BigInteger q = readBigInteger(buffer);
            BigInteger g = readBigInteger(buffer);

            return new PublicKeyElement(genDSAKey(y, p, q, g));
        }
    }

    private BigInteger readBigInteger(MetaByteBuffer buffer) {
        byte[] bytes = new byte[buffer.readInt()];
        buffer.read(bytes);
        return new BigInteger(bytes);
    }

    public static RSAPublicKey genRSAKey(BigInteger modulus, BigInteger exponent) {
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);

        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("System does not support RSA encryption!");
        }
    }


    public static DSAPublicKey genDSAKey(BigInteger y, BigInteger p, BigInteger q, BigInteger g) {
        DSAPublicKeySpec spec = new DSAPublicKeySpec(y, p, q, g);

        try {
            KeyFactory factory = KeyFactory.getInstance("DSA");
            return (DSAPublicKey) factory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("System does not support DSA encryption");
        }
    }

    @Override
    public Class<PublicKeyElement> getDataClass() {
        return PublicKeyElement.class;
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
