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

package com.cerberustek.querry.trace.impl.replace;

import com.cerberustek.CerberusData;
import com.cerberustek.data.*;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.ResourceLocation;
import com.cerberustek.querry.trace.impl.ComTrace;
import com.cerberustek.querry.trace.impl.SuccessResult;

import java.io.IOException;

public class ReplTraceElement extends ComTrace implements MetaData {

    private final MetaElement key;
    private final MetaElement value;

    public ReplTraceElement(MetaElement key, MetaElement value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public QueryResult pull(ResourceLocation location, MetaData resource) {
        if (resource instanceof MetaMap) {

            //noinspection unchecked
            ((MetaMap) resource).replace(key, value);
            return new SuccessResult(true);
        }
        return new SuccessResult(false);
    }

    @Override
    public String toString() {
        return key.toString() + "{p}";
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeData(key);
        metaOutputStream.writeData(value);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeData(key);
        metaBuffer.writeData(value);
    }

    @Override
    public long byteSize() {
        return CerberusData.totalSize(key) + CerberusData.totalSize(value);
    }

    @Override
    public long finalSize() {
        return -1;
    }
}
