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
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.data.impl.buffer.MetaByteBufferImpl;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@SuppressWarnings("Duplicates")
public class CompressionElementBuilder implements MetaBuilder<CompressionElement> {

    private final Inflater inflater;

    public CompressionElementBuilder() {
        this(new Inflater());
    }

    public CompressionElementBuilder(Inflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public CompressionElement build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        byte[] buffer = new byte[inputStream.readInt()];
        byte[] raw = new byte[inputStream.readInt()];
        inputStream.readFully(raw);
        inflater.setInput(raw);
        try {
            inflater.inflate(buffer);
            MetaByteBuffer b = new MetaByteBufferImpl(inputStream.getDiscriminatorMap(), ByteBuffer.wrap(buffer));
            return new CompressionElement<>(b.readData());
        } catch (DataFormatException e) {
            CerberusData.getLogger().log(Level.WARNING, "failed to inflate data from Compression Element");
        } finally {
            inflater.reset();
        }
        return null;
    }

    @Override
    public CompressionElement build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        byte[] buf = new byte[buffer.readInt()];
        byte[] raw = new byte[buffer.readInt()];
        buffer.read(raw);
        inflater.setInput(raw);
        try {
            inflater.inflate(buf);
            MetaByteBuffer b = new MetaByteBufferImpl(buffer.getDiscriminatorMap(), ByteBuffer.wrap(buf));
            return new CompressionElement<>(b.readData());
        } catch (DataFormatException e) {
            CerberusData.getLogger().log(Level.WARNING, "failed to inflate data from Compression Element");
        } finally {
            inflater.reset();
        }
        return null;
    }

    @Override
    public Class<CompressionElement> getDataClass() {
        return CompressionElement.class;
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
