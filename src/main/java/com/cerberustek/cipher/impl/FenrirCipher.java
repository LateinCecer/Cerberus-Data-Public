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

import com.cerberustek.cipher.CerberusCipher;
import com.cerberustek.cipher.ContinuousCipher;
import com.cerberustek.cipher.SymmetricCipher;

import javax.crypto.SecretKey;

public class FenrirCipher implements ContinuousCipher, SymmetricCipher {

    private final byte start;
    private final int start_key;
    private final byte[] keySet;

    private int key;
    private byte last;

    public FenrirCipher(byte[] keySet) {
        if (keySet.length < 6)
            throw new IllegalArgumentException("Key set has to have a length of at least 6!");

        start_key = clamp(((keySet[0] & 0xFF) << 24) +
                ((keySet[1] & 0xFF) << 16) +
                ((keySet[2] & 0xFF) << 8) +
                (keySet[3] & 0xFF), keySet.length - 5);
        start = keySet[4];
        this.keySet = new byte[keySet.length - 5];
        System.arraycopy(keySet, 5, this.keySet, 0, this.keySet.length);
        reset();
    }

    @Override
    public byte encrypt(byte value) {
        value = cycleData(value, keySet[key] + last);
        cycle(value + last);
        return last = value;
    }

    @Override
    public byte decrypt(byte value) {
        byte prev = value;
        value = cycleBackData(value, keySet[key] + last);
        cycle(prev + last);
        last = prev;
        return value;
    }

    @Override
    public CerberusCipher reset() {
        key = start_key;
        last = start;
        return this;
    }

    @Override
    public byte[] encrypt(byte[] value, int offset, int length) {
        for (int index = offset; index < offset + length; index++)
            value[index] = encrypt(value[index]);
        return value;
    }

    @Override
    public byte[] encrypt(byte[] value) {
        return encrypt(value, 0, value.length);
    }

    @Override
    public byte[] decrypt(byte[] value, int offset, int length) {
        for (int index = offset; index < offset + length; index++)
            value[index] = decrypt(value[index]);
        return value;
    }

    @Override
    public byte[] decrypt(byte[] value) {
        return decrypt(value, 0, value.length);
    }

    private byte cycleData(byte input, int length) {
        return signed(clamp(unsigned(input) + length, 256));
    }

    private byte cycleBackData(byte input, int length) {
        return signed(reclamp(unsigned(input) - length, 256));
    }

    private void cycle(int amount) {
        key = clamp(key + Math.abs(amount), keySet.length);
    }

    private byte signed(int value) {
        return (byte) (value & 0xFF);
    }

    private int unsigned(byte value) {
        return Byte.toUnsignedInt(value);
    }

    private int clamp(int value, int max) {
        return value % max;
    }

    private int reclamp(int value, int max) {
        return value % max + max;
    }

    @Override
    public SecretKey getKeySet() {
        return null;
    }
}
