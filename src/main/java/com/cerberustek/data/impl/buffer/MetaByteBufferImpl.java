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

package com.cerberustek.data.impl.buffer;

import com.cerberustek.CerberusData;
import com.cerberustek.data.*;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.nio.ByteBuffer;
import java.util.logging.Level;

public class MetaByteBufferImpl implements MetaByteBuffer {

    private final DiscriminatorMap discriminatorMap;
    private final ByteBuffer buffer;

    public MetaByteBufferImpl(DiscriminatorMap discriminatorMap, int length) {
        this(discriminatorMap, ByteBuffer.allocateDirect(length));
    }

    public MetaByteBufferImpl(DiscriminatorMap discriminatorMap, ByteBuffer buffer) {
        this.discriminatorMap = discriminatorMap;
        this.buffer = buffer;
    }

    @Override
    public void rewind() {
        buffer.rewind();
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public long skipData() throws UnknownDiscriminatorException {
        MetaBuilder builder = discriminatorMap.getBuilder(readShort());
        long size = builder.getFinalSize() >= 0 ? builder.getFinalSize() : readLong();
        if (builder.isTag())
            size += readUnsignedShort();
        skipFully(size);
        return size;
    }

    @Override
    public int skipBytes(int length) {
        int pos = buffer.position();
        int left = buffer.capacity() - pos;
        int skip = left >= length ? length : left;
        buffer.position(pos + skip);
        return skip;
    }

    @Override
    public void skipFully(long length) {
        long size = 0;
        while (size < length)
            size += skipBytes((int) length);
    }

    @Override
    public void read(byte[] buffer) {
        this.buffer.get(buffer);
    }

    @Override
    public void read(byte[] buffer, int length) {
        this.buffer.get(buffer, 0, length);
    }

    @Override
    public void read(byte[] buffer, int offset, int length) {
        this.buffer.get(buffer, offset, length);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public MetaData readData() throws UnknownDiscriminatorException {
        short discriminator = readShort();

        if (discriminator != CerberusData.CERBERUS_NULL) {
            long read = buffer.position();
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
    private MetaData processRaw(MetaBuilder builder, long read, long size) throws UnknownDiscriminatorException {
        MetaData data;
        if (builder.isTag()) {
            String tag = readUTF();
            size += (tag.getBytes().length + 2);
            data = builder.build(tag, this);
        } else
            data = builder.build(null, this);

        long skipping = (size - (buffer.position() - read));
        if (skipping > 0) {
            skipFully(skipping);
            CerberusData.getLogger().log(Level.WARNING, "MetaInputStream has skipped: " + skipping + " bytes!" +
                    " This could caused by corrupted or lost data!");
        }
        return data;
    }

    @Override
    public byte readByte() {
        return buffer.get();
    }

    @Override
    public boolean readBoolean() {
        return buffer.get() > 0;
    }

    @Override
    public char readChar() {
        return buffer.getChar();
    }

    @Override
    public short readShort() {
        return buffer.getShort();
    }

    @Override
    public int readInt() {
        return buffer.getInt();
    }

    @Override
    public int readUnsignedByte() {
        return (int) buffer.get();
    }

    @Override
    public int readUnsignedShort() {
        return (int) buffer.getShort();
    }

    @Override
    public float readFloat() {
        return buffer.getFloat();
    }

    @Override
    public long readLong() {
        return buffer.getLong();
    }

    @Override
    public double readDouble() {
        return buffer.getDouble();
    }

    @Override
    public String readUTF() {
        byte[] buffer = new byte[readUnsignedShort()];
        this.buffer.get(buffer);
        return new String(buffer);
    }

    @Override
    public void write(byte[] buffer) {
        this.buffer.put(buffer);
    }

    @Override
    public void write(byte[] buffer, int length) {
        this.buffer.put(buffer, 0, length);
    }

    @Override
    public void write(byte[] buffer, int offset, int length) {
        this.buffer.put(buffer, offset, length);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void writeData(MetaData data) throws NoMatchingDiscriminatorException {
        if (data != null) {
            short discriminator = discriminatorMap.getDiscriminator(data.getClass());

            if (discriminator != CerberusData.CERBERUS_NULL) {
                writeShort(discriminator);
                if (data.finalSize() < 0)
                    writeLong(CerberusData.size(data));
                if (data instanceof MetaTag)
                    writeUTF(((MetaTag) data).getTag());
                data.serialize(this);
            } else
                throw new NoMatchingDiscriminatorException(data.getClass());
        } else {
            writeShort(CerberusData.CERBERUS_NULL);
        }
    }

    @Override
    public void writeByte(int value) {
        this.buffer.put((byte) value);
    }

    @Override
    public void writeBoolean(boolean value) {
        buffer.put((byte) (value ? 1 : 0));
    }

    @Override
    public void writeChar(int value) {
        this.buffer.putChar((char) value);
    }

    @Override
    public void writeShort(int value) {
        this.buffer.putShort((short) value);
    }

    @Override
    public void writeInt(int value) {
        this.buffer.putInt(value);
    }

    @Override
    public void writeFloat(float value) {
        this.buffer.putFloat(value);
    }

    @Override
    public void writeLong(long value) {
        this.buffer.putLong(value);
    }

    @Override
    public void writeDouble(double value) {
        this.buffer.putDouble(value);
    }

    @Override
    public void writeUTF(String value) {
        writeShort(value.length());
        write(value.getBytes());
    }

    @Override
    public DiscriminatorMap getDiscriminatorMap() {
        return discriminatorMap;
    }

    @Override
    public int remaining() {
        return buffer.remaining();
    }

    @Override
    public int capacity() {
        return buffer.capacity();
    }
}
