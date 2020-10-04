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

package com.cerberustek.querry.trace.impl.pull;

import com.cerberustek.CerberusData;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.ResourceLocation;
import com.cerberustek.querry.trace.TraceElement;
import com.cerberustek.querry.trace.impl.QueryTraceImpl;

import java.io.IOException;

public class PullTraceElement extends QueryTraceImpl implements TraceElement, MetaData {

    private final MetaElement key;

    public PullTraceElement(MetaElement key) {
        this.key = key;
    }

    @Override
    public QueryResult pull(ResourceLocation location, MetaData resource) {
        return new PullResult<>(resource);
    }

    @Override
    public MetaElement getElement() {
        return key;
    }

    @Override
    public String toString() {
        String head = "." + super.toString();
        if (hasNext())
            return head + next().toString();
        return head;
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeData(key);
        metaOutputStream.writeBoolean(hasNext());
        if (hasNext())
            metaOutputStream.writeData(next());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeData(key);
        metaBuffer.writeBoolean(hasNext());
        if (hasNext())
            metaBuffer.writeData(next());
    }

    @Override
    public long byteSize() {
        if (hasNext())
            return CerberusData.totalSize(key) + 1 + CerberusData.totalSize(next());
        return CerberusData.totalSize(key) + 1;
    }

    @Override
    public long finalSize() {
        return -1;
    }
}
