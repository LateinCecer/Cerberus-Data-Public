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

package com.cerberustek.data.impl.streams;

import com.cerberustek.CerberusData;
import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class MetaInputStreamImpl extends DataInputStream implements MetaInputStream, AutoCloseable {

    private final DiscriminatorMap discriminatorMap;

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     * @param discriminatorMap the discriminator map used to
     *                         read the format of this stream.
     */
    public MetaInputStreamImpl(InputStream in, DiscriminatorMap discriminatorMap) {
        super(new CountingInputStream(in));
        this.discriminatorMap = discriminatorMap;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public long skipData() throws IOException, UnknownDiscriminatorException {
        MetaBuilder builder = discriminatorMap.getBuilder(readShort());
        long size = builder.getFinalSize() >= 0 ? builder.getFinalSize() : readLong();
        if (builder.isTag())
            size += readUnsignedShort();
        skipFully(size);
        return size;
    }

    @Override
    public void skipFully(long length) throws IOException {
        long skipped = 0;
        while (skipped < length)
            skipped += skipBytes((int) length);
    }

    @Override
    public long getByteCount() {
        return ((CountingInputStream) super.in).getByteCount();
    }

    @Override
    public void resetByteCount() {
        ((CountingInputStream) super.in).resetByteCount();
    }

    @Override
    public int read(byte[] buffer, int length) throws IOException {
        return read(buffer, 0, length);
    }

    @Override
    public void readFully(byte[] buffer, int length) throws IOException {
        readFully(buffer, 0, length);
    }

    @Override
    public MetaData readData() throws IOException, UnknownDiscriminatorException {
        short discriminator = readShort();

        if (discriminator != CerberusData.CERBERUS_NULL) {
            long read = getByteCount();
            MetaBuilder builder;
            try {
                builder = discriminatorMap.getBuilder(discriminator);
            } catch (UnknownDiscriminatorException e) {
                skipFully(readLong());
                throw e;
            }
            long size = builder.getFinalSize() >= 0 ? builder.getFinalSize() : readLong();

            return processRaw(builder, read, size);
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    private MetaData processRaw(MetaBuilder builder, long read, long size) throws IOException, UnknownDiscriminatorException {
        MetaData data;
        if (builder.isTag()) {
            String tag = readUTF();
            size += (tag.getBytes().length + 2);
            data = builder.build(tag, this);
        } else
            data = builder.build(null, this);

        long skipping = (size - (getByteCount() - read));
        if (skipping > 0) {
            skipFully(skipping);
            CerberusData.getLogger().log(Level.WARNING, "MetaInputStream has skipped: " + skipping + " bytes!" +
                    " This could be caused by corrupted or lost data!");
        }
        return data;
    }

    @Override
    public DiscriminatorMap getDiscriminatorMap() {
        return discriminatorMap;
    }
}
