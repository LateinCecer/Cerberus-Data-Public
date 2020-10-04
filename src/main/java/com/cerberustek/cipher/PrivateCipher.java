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

package com.cerberustek.cipher;

import java.security.PrivateKey;

public interface PrivateCipher extends DecryptionCerberusCipher {

    /**
     * Returns the private key of this decrypter.
     * @return private key
     */
    PrivateKey getPrivateKey();

    /**
     * Will generate a signature for a message (clear
     * text).
     * @param data message to sign
     * @return signature
     */
    byte[] genSignature(byte[] data);
    byte[] genSignature(byte[] data, int off, int len);

    /**
     * Will generate a signature for a message (clear
     * text) and append it to the message.
     * @param data message to sign
     * @return message with signature
     */
    byte[] sign(byte[] data);
    byte[] sign(byte[] data, int off, int len);
}
