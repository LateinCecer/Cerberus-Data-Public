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

import java.security.PublicKey;

public interface PublicCipher extends EncryptionCerberusCipher {

    /**
     * Returns the public RSA key of this encrypter.
     * @return Public key
     */
    PublicKey getPublicKey();

    /**
     * Will verify just the signature bytes of a
     * message.
     * @param signature signature
     * @param message message to verify
     * @return true if verified, false otherwise
     */
    boolean verifySignature(byte[] signature, byte[] message);

    boolean verifySignature(byte[] signature, int signatureOffset, int signatureLength,
                            byte[] message, int messageOffset, int messageLength);

    /**
     * Will extract the signature from the message
     * and verify it.
     * @param data entire message (clear text)
     * @return true if verified, false otherwise
     */
    boolean verify(byte[] data);
}
