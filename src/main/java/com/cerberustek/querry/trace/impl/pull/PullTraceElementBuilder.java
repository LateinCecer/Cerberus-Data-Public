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

import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;
import com.cerberustek.querry.trace.QueryTrace;

import java.io.IOException;

public class PullTraceElementBuilder implements MetaBuilder<PullTraceElement> {

    @Override
    public PullTraceElement build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        PullTraceElement output = new PullTraceElement((MetaElement) inputStream.readData());
        if (inputStream.readBoolean())
            output.stack((QueryTrace) inputStream.readData());
        return output;
    }

    @Override
    public PullTraceElement build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        PullTraceElement output = new PullTraceElement((MetaElement) buffer.readData());
        if (buffer.readBoolean())
            output.stack((QueryTrace) buffer.readData());
        return output;
    }

    @Override
    public Class<PullTraceElement> getDataClass() {
        return PullTraceElement.class;
    }

    @Override
    public int getFinalSize() {
        return -1;
    }

    @Override
    public boolean isTag() {
        return false;
    }
}
