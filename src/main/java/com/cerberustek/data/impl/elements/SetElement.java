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
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.MetaSetImpl;
import com.cerberustek.data.impl.tags.SetTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;
import java.util.Set;

public class SetElement<T extends MetaData> extends MetaSetImpl<T> implements MetaElement<Set<T>> {

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeInt(size());
        for (T t : this)
            metaOutputStream.writeData(t);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeInt(size());
        for (T t : this)
            metaBuffer.writeData(t);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public long byteSize() {
        long size = 4;
        for (T t : this) {
            if (t != null)
                size += CerberusData.totalSize(t);
        }
        return size;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    @Override
    public void set(Set<T> value) {
        addAll(value);
    }

    @Override
    public Set<T> get() {
        return this;
    }

    public SetTag<T> toTag(String tag) {
        SetTag<T> set = new SetTag<>(tag);
        set.addAll(this);
        return set;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof SetElement;
        return ((SetElement) obj).get().equals(get());
    }
}
