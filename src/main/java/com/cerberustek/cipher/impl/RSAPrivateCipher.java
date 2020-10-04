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

import com.cerberustek.CerberusEncrypt;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.cipher.PrivateCipher;

import javax.crypto.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;

public class RSAPrivateCipher implements PrivateCipher {

    private final RSAPrivateKey key;
    private Cipher cipher;
    private Signature signature;

    private CerberusEncrypt encrypt;

    public RSAPrivateCipher(RSAPrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        signature = Signature.getInstance("SHA256withRSA");

        this.key = key;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return key;
    }

    @Override
    public byte[] decrypt(byte[] value, int offset, int length) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(value, offset, length);
            CipherInputStream inputStream = new CipherInputStream(byteStream, cipher);

            byte[] out = new byte[length];
            int read = inputStream.read(out);
            inputStream.close();
            if (read < 0)
                throw new IllegalStateException("Invalid data set length!");

            byte[] finalBytes = new byte[read];
            System.arraycopy(out, 0, finalBytes, 0, read);
            return finalBytes;
        } catch (InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] value) {
        return decrypt(value, 0, value.length);
    }

    @Override
    public byte[] genSignature(byte[] data) {
        try {
            signature.initSign(key, getService().getRandom());
            signature.update(data);
            return signature.sign();
        } catch (InvalidKeyException | SignatureException e) {
            CerberusRegistry.getInstance().warning("Unable to sign data! " + e);
        }
        return null;
    }

    @Override
    public byte[] genSignature(byte[] data, int off, int len) {
        try {
            signature.initSign(key, getService().getRandom());
            signature.update(data, off, len);
            return signature.sign();
        } catch (InvalidKeyException | SignatureException e) {
            CerberusRegistry.getInstance().warning("Unable to sign data! " + e);
        }
        return null;
    }

    @Override
    public byte[] sign(byte[] data, int off, int len) {
        byte[] signature = genSignature(data, off, len);
        if (signature == null)
            return null;

        byte[] out = new byte[4 + signature.length + len];

        out[0] = (byte) ((signature.length >> 24) & 0xFF);
        out[1] = (byte) ((signature.length >> 16) & 0xFF);
        out[2] = (byte) ((signature.length >> 8) & 0xFF);
        out[3] = (byte) (signature.length & 0xFF);

        System.arraycopy(signature, 0, out, 4, signature.length);
        System.arraycopy(data, 0, out, 4 + signature.length, len);
        return out;
    }

    @Override
    public byte[] sign(byte[] data) {
        return sign(data, 0, data.length);
    }

    private CerberusEncrypt getService() {
        if (encrypt == null)
            encrypt = CerberusRegistry.getInstance().getService(CerberusEncrypt.class);
        return encrypt;
    }
}
