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

import com.cerberustek.CerberusData;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.MetaTag;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.data.impl.MetaDocImpl;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;
import java.util.Map;

public class DocElement extends MetaDocImpl implements MetaElement<Map<String, MetaTag>> {

    public DocElement() {
        super();
    }

    public DocElement(DocElement other) {
        this();
        tags.putAll(other.tags);
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeInt(size());
        for (MetaTag tag : this)
            metaOutputStream.writeData(tag);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(size());
        for (MetaTag tag : this)
            metaBuffer.writeData(tag);
    }

    @Override
    public long byteSize() {
        long size = 4;
        for (MetaTag tag : this)
            size += CerberusData.totalSize(tag);
        return size;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    @Override
    public void set(Map<String, MetaTag> value) {
        this.tags.putAll(value);
    }

    @Override
    public Map<String, MetaTag> get() {
        return this.tags;
    }

    public DocTag toTag(String tag) {
        DocTag output = new DocTag(tag);
        output.tags.putAll(this.tags);
        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof DocElement;
        return ((DocElement) obj).get().equals(get());
    }
}
