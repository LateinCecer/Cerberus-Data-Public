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
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.ResourceLocation;
import com.cerberustek.querry.trace.TraceTag;
import com.cerberustek.querry.trace.impl.QueryTraceImpl;

import java.io.IOException;

public class PullTraceTag extends QueryTraceImpl implements TraceTag, MetaData {

    private final String tag;

    public PullTraceTag(String tag) {
        this.tag = tag;
    }

    @Override
    public QueryResult pull(ResourceLocation location, MetaData resource) {
        return new PullResult<>(resource);
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        if (hasNext())
            return "." + tag + next().toString();
        return "." + tag;
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeUTF(tag);
        metaOutputStream.writeBoolean(hasNext());
        if (hasNext())
            metaOutputStream.writeData(next());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeUTF(tag);
        metaBuffer.writeBoolean(hasNext());
        if (hasNext())
            metaBuffer.writeData(next());
    }

    @Override
    public long byteSize() {
        if (hasNext())
            return 1 + CerberusData.totalSize(tag) + CerberusData.totalSize(next());
        return 1 + CerberusData.totalSize(tag);
    }

    @Override
    public long finalSize() {
        return -1;
    }
}
