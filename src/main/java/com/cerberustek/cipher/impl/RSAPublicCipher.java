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

package com.cerberustek.cipher.impl;

import com.cerberustek.CerberusRegistry;
import com.cerberustek.cipher.PublicCipher;

import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPublicKey;

public class RSAPublicCipher implements PublicCipher {

    private final RSAPublicKey key;
    private final Cipher cipher;
    private final Signature signature;

    public RSAPublicCipher(RSAPublicKey key) throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        signature = Signature.getInstance("SHA256withRSA");

        this.key = key;
    }

    @Override
    public PublicKey getPublicKey() {
        return key;
    }

    @Override
    public byte[] encrypt(byte[] value, int offset, int length) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(length);
            CipherOutputStream outputStream = new CipherOutputStream(byteStream, cipher);

            outputStream.write(value, offset, length);
            outputStream.flush();
            outputStream.close();

            return byteStream.toByteArray();
        } catch (InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] encrypt(byte[] value) {
        return encrypt(value, 0, value.length);
    }

    @Override
    public boolean verifySignature(byte[] signature, byte[] message) {
        try {
            this.signature.initVerify(key);
            this.signature.update(message);
            return this.signature.verify(signature);
        } catch (InvalidKeyException | SignatureException e) {
            CerberusRegistry.getInstance().warning("Unable to verify signature");
        }
        return false;
    }

    @Override
    public boolean verifySignature(byte[] signature, int signatureOffset, int signatureLength,
                                   byte[] message, int messageOffset, int messageLength) {

        try {
            this.signature.initVerify(key);
            this.signature.update(message, messageOffset, messageLength);
            return this.signature.verify(signature, signatureOffset, signatureLength);
        } catch (InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean verify(byte[] data) {
        int signatureLength =
                        ((data[0] & 0xFF) << 24) |
                        ((data[1] & 0xFF) << 16) |
                        ((data[2] & 0xFF) << 8)  |
                        (data[3] & 0xFF);
        return verifySignature(data, 4, signatureLength,
                data, 4 + signatureLength, data.length - 4 - signatureLength);
    }
}
