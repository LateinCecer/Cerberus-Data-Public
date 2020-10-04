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

package com.cerberustek.querry.trace.impl;

import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.querry.QueryResult;

import java.io.IOException;

public class SuccessResult implements QueryResult, MetaData {

    private final boolean successful;

    public SuccessResult(boolean successful) {
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        return "Success result: " + successful;
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeBoolean(successful);
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeBoolean(successful);
    }

    @Override
    public long byteSize() {
        return 1;
    }

    @Override
    public long finalSize() {
        return 1;
    }
}
