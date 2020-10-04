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
import com.cerberustek.data.*;
import com.cerberustek.data.impl.tags.ListTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.ResourceUnavailableException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.trace.TraceIndex;
import com.cerberustek.querry.trace.QueryTrace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListElement<T extends MetaData> extends ArrayList<T> implements MetaList<T>, MetaElement<List<T>> {

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

    @SuppressWarnings("Duplicates")
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[').append(getClass().getName()).append(']');
        if (this instanceof MetaTag)
            stringBuilder.append("<").append(((MetaTag) this).getTag()).append(">");
        stringBuilder.append(':').append(' ').append('{');

        forEach(value ->
                stringBuilder.append("\n\t").append(value.toString().replace("\n", "\n\t")));
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    @Override
    public QueryResult trace(QueryTrace request) throws ResourceUnavailableException {
        if (request instanceof TraceIndex)
            return CerberusData.pullResult(request, this, get(((TraceIndex) request).getIndex()));
        return request.pull(this, this);
    }

    @Override
    public void set(List<T> value) {
        addAll(value);
    }

    @Override
    public List<T> get() {
        return this;
    }

    public ListTag<T> toTag(String tag) {
        ListTag<T> output = new ListTag<>(tag);
        output.addAll(this);
        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        assert obj instanceof ListElement;
        return ((ListElement) obj).get().equals(get());
    }
}
