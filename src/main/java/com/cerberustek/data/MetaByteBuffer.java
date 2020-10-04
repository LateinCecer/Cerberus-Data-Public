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

package com.cerberustek.data;

import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;

public interface MetaByteBuffer {

    void rewind();
    void reset();

    /**
     * This method will skipBytes the next MetaData entry contained in this stream
     * @return the amount of bytes skipped
     */
    long skipData() throws UnknownDiscriminatorException;

    int skipBytes(int length);
    void skipFully(long length);

    /**
     * This method will attempt to read all available stream data for the length
     * of the provided byte buffer to set byte buffer. If there is not enough stream
     * data available, this method will only read so much data from the stream as
     * there is available at the current point in time.
     * This method will return the amount of bytes that have actually been written
     * to the provided byte array. So if there is enough data in the stream to
     * fill the whole buffer, this method will return the length of the byte array.
     *
     * If the stream is closed or no data can be extracted from the stream for some
     * other reason that does not cause an Exception to be thrown, this method will
     * return -1. In that case no more data can be read from this stream ever again.
     * This is not to confuse with this method returning 0, which, in contrast to
     * the event stated over top, does only mean that no data can be pulled from the
     * stream at the current time, but will may not prevent you from reading data
     * some time in the future.
     * @param buffer byte array used as a buffer
     */
    void read(byte[] buffer);

    /**
     * This method will attempt to read all available stream data for the length
     * provided to the byte buffer. If there is not enough stream
     * data available, this method will only read so much data from the stream as
     * there is available at the current point in time.
     * This method will return the amount of bytes that have actually been written
     * to the provided byte array. So if there is enough data in the stream to
     * fill the whole buffer, this method will return the provided length integer.
     *
     * If the stream is closed or no data can be extracted from the stream for some
     * other reason that does not cause an Exception to be thrown, this method will
     * return -1. In that case no more data can be read from this stream ever again.
     * This is not to confuse with this method returning 0, which, in contrast to
     * the event stated over top, does only mean that no data can be pulled from the
     * stream at the current time, but will may not prevent you from reading data
     * some time in the future.
     * @param buffer byte array used as a buffer
     * @param length the amount of bytes that are to be read from the stream a.i.
     *               written to the byte buffer, when possible
     */
    void read(byte[] buffer, int length);

    /**
     * This method will attempt to read all available stream data for the length
     * provided to the byte buffer. If there is not enough stream
     * data available, this method will only read so much data from the stream as
     * there is available at the current point in time.
     * This method will return the amount of bytes that have actually been written
     * to the provided byte array. So if there is enough data in the stream to
     * fill the whole buffer, this method will return the provided length integer.
     *
     * In contrast to the two methods above, this method will write the bytes pulled
     * from the stream starting at the offset position provided.
     *
     * If the stream is closed or no data can be extracted from the stream for some
     * other reason that does not cause an Exception to be thrown, this method will
     * return -1. In that case no more data can be read from this stream ever again.
     * This is not to confuse with this method returning 0, which, in contrast to
     * the event stated over top, does only mean that no data can be pulled from the
     * stream at the current time, but will may not prevent you from reading data
     * some time in the future.
     * @param buffer byte array used as a buffer
     * @param offset the index inside of the byte buffer from which on to start
     *               writing data to
     * @param length the amount of bytes that are to be read from the stream a.i.
     *               written to the byte buffer, when possible
     */
    void read(byte[] buffer, int offset, int length);
    MetaData readData() throws UnknownDiscriminatorException;
    byte readByte();
    boolean readBoolean();
    char readChar();
    short readShort();
    int readInt();
    int readUnsignedByte();
    int readUnsignedShort();
    float readFloat();
    long readLong();
    double readDouble();
    String readUTF();

    void write(byte[] buffer);
    void write(byte[] buffer, int length);
    void write(byte[] buffer, int offset, int length);
    void writeData(MetaData data) throws NoMatchingDiscriminatorException;
    void writeByte(int value);
    void writeBoolean(boolean value);
    void writeChar(int value);
    void writeShort(int value);
    void writeInt(int value);
    void writeFloat(float value);
    void writeLong(long value);
    void writeDouble(double value);
    void writeUTF(String value);

    DiscriminatorMap getDiscriminatorMap();

    int remaining();
    int capacity();
}
