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
import com.cerberustek.data.impl.tags.SecretKeyTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import javax.crypto.SecretKey;
import java.io.IOException;

public class SecretKeyElement extends MetaElementImpl<SecretKey> {

    public SecretKeyElement(SecretKey key) {
        super(key);
    }

    @Override
    public MetaTag toTag(String tag) {
        return new SecretKeyTag(tag, get());
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeUTF(get().getAlgorithm());

        byte[] key = get().getEncoded();
        metaOutputStream.writeInt(key.length);
        metaOutputStream.write(key);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeUTF(get().getAlgorithm());

        byte[] key = get().getEncoded();
        metaBuffer.writeInt(key.length);
        metaBuffer.write(key);
    }

    @Override
    public long byteSize() {
        return 2 + get().getAlgorithm().getBytes().length + 4 + get().getEncoded().length;
    }

    @Override
    public long finalSize() {
        return -1;
    }
}
