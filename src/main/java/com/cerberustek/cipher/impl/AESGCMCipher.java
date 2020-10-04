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
import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.cipher.CerberusCipher;
import com.cerberustek.cipher.SymmetricCipher;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AESGCMCipher implements SymmetricCipher {

    private Cipher encrypt;
    private Cipher decrypt;
    private SecretKey key;
    private int TLLength;
    private int TLByteLength;

    private CerberusEncrypt encryptionService;

    public AESGCMCipher(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this(key, 128, 16);
    }

    public AESGCMCipher(SecretKey key, int TLLength, int TLByteLength) throws NoSuchPaddingException, NoSuchAlgorithmException {
        encrypt = Cipher.getInstance("AES/GCM/NoPadding");
        decrypt = Cipher.getInstance("AES/GCM/NoPadding");

        this.TLLength = TLLength;
        this.TLByteLength = TLByteLength;
        this.key = key;
    }

    @Override
    public CerberusCipher reset() {
        return this;
    }

    @Override
    public SecretKey getKeySet() {
        return key;
    }

    @Override
    public byte[] encrypt(byte[] value, int offset, int length) {
        try {
            byte[] gcmBytes = getEncryptionService().randomIV(TLByteLength);
            encrypt.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TLLength, gcmBytes));


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1 + TLByteLength + length);
            outputStream.write((byte) TLByteLength);
            outputStream.write(gcmBytes);

            CipherOutputStream cipherStream = new CipherOutputStream(outputStream, encrypt);
            cipherStream.write(value, offset, length);
            cipherStream.flush();
            cipherStream.close();

            Arrays.fill(gcmBytes, (byte) 0); // wipe gcm from memory
            return outputStream.toByteArray();

        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IOException e) {
            CerberusRegistry.getInstance().warning("Failed to encrypt AES/GCM data!");
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusEncrypt.class, e));
        }
        return null;
    }

    @Override
    public byte[] encrypt(byte[] value) {
        return encrypt(value, 0, value.length);
    }

    @Override
    public byte[] decrypt(byte[] value, int offset, int length) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(value, offset, length);
            int gcmLength = Byte.toUnsignedInt((byte) inputStream.read());
            byte[] gcmBytes = new byte[gcmLength];
            int read = inputStream.read(gcmBytes);
            if (read < gcmLength)
                throw new IllegalStateException("Invalid gcm head size AES stream!");

            GCMParameterSpec gcm = new GCMParameterSpec(TLLength, gcmBytes);
            decrypt.init(Cipher.DECRYPT_MODE, key, gcm);

            CipherInputStream cipherStream = new CipherInputStream(inputStream, decrypt);
            byte[] data = new byte[length - 1 - gcmLength];
            read = cipherStream.read(data);
            inputStream.close();
            if (read < 0)
                throw new IllegalStateException("Invalid data set length!");

            byte[] finalBytes = new byte[read];
            System.arraycopy(data, 0, finalBytes, 0, read);
            return finalBytes;
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public byte[] decrypt(byte[] value) {
        return decrypt(value, 0, value.length);
    }

    private CerberusEncrypt getEncryptionService() {
        if (encryptionService == null)
            encryptionService = CerberusRegistry.getInstance().getService(CerberusEncrypt.class);
        return encryptionService;
    }
}
