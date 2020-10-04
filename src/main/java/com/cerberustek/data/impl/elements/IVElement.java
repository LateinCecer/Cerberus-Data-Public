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
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.MetaTag;
import com.cerberustek.data.impl.tags.IVTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;

public class IVElement implements MetaElement<IvParameterSpec> {

    private byte[] data;

    public IVElement(byte[] data) {
        this.data = data;
    }

    public IVElement(IvParameterSpec ivSpec) {
        this.data = ivSpec.getIV();
    }

    public IVElement(GCMParameterSpec gcmSpec) {
        this.data = gcmSpec.getIV();
    }

    @Override
    public void set(IvParameterSpec value) {
        data = value.getIV();
    }

    public void set(GCMParameterSpec value) {
        data = value.getIV();
    }

    public void set(byte[] iv) {
        data = iv.clone();
    }

    @Override
    public IvParameterSpec get() {
        return getIvParameterSpec();
    }

    public IvParameterSpec getIvParameterSpec() {
        return new IvParameterSpec(data);
    }

    public GCMParameterSpec getGCMParameterSpec() {
        return new GCMParameterSpec(data.length * 8, data);
    }

    public byte[] getIV() {
        return data;
    }

    @Override
    public MetaTag toTag(String tag) {
        return new IVTag(tag, get());
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeInt(data.length);
        metaOutputStream.write(data);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(data.length);
        metaBuffer.write(data);
    }

    @Override
    public long byteSize() {
        return 4 + data.length;
    }

    @Override
    public long finalSize() {
        return -1;
    }
}
