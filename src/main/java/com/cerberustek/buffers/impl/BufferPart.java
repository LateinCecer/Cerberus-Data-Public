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

package com.cerberustek.buffers.impl;

import com.cerberustek.buffers.DataBuffer;

public class BufferPart implements DataBuffer {

    private final BufferRoot root;

    private byte[] data;

    private int readHead;
    private int writeHead;

    private BufferPart next;
    private BufferPart parent;

    BufferPart(BufferRoot root, int size) {
        this.root = root;
        data = new byte[size];
        next = null;
        parent = null;
    }

    @Override
    public int write(byte[] bufferData) {
        return write(bufferData, 0, bufferData.length);
    }

    @Override
    public int write(byte[] bufferData, int length) {
        return write(bufferData, 0, length);
    }

    @Override
    public int write(byte[] bufferData, int offset, int length) {
        getRoot().setCurrentWriter(this);
        int remaining = data.length - writeHead;
        if (remaining < length) {
            System.arraycopy(bufferData, offset, data, writeHead, remaining);
            writeHead += remaining;

            synchronized (this) {
                this.notifyAll();
            }
            if (hasNext() && !next.equals(getRoot().currentReader()))
                return remaining + next.write(bufferData, offset + remaining, length - remaining);
            return remaining;
        }

        System.arraycopy(bufferData, offset, data, writeHead, length);
        writeHead += length;

        synchronized (this) {
            this.notifyAll();
        }
        return length;
    }

    @Override
    public int read(byte[] bufferData) {
        return read(bufferData, 0, bufferData.length);
    }

    @Override
    public int read(byte[] bufferData, int length) {
        return read(bufferData, 0, length);
    }

    @Override
    public int read(byte[] bufferData, int offset, int length) {
        getRoot().setCurrentReader(this);
        int available = writeHead - readHead;

        if (available < length) {
            System.arraycopy(data, readHead, bufferData, offset, available);
            readHead += available;

            if (hasNext() && readHead == data.length) {
                writeHead = 0;
                readHead = 0;
                int value = available + next.read(bufferData, offset + available, length - available);
                remove();
                return value;
            }
            return available;
        }

        System.arraycopy(data, readHead, bufferData, offset, length);
        readHead += length;
        return length;
    }

    @Override
    public int readFully(int halt, byte[] bufferData) throws InterruptedException {
        return readFully(halt, bufferData, 0, bufferData.length);
    }

    @Override
    public int readFully(int halt, byte[] bufferData, int length) throws InterruptedException {
        return readFully(halt, bufferData, 0, length);
    }

    @Override
    public int readFully(int halt, byte[] bufferData, int offset, int length) throws InterruptedException {
        int available = writeHead - readHead;
        if (available < length) {

            long currentTime = System.currentTimeMillis();
            synchronized (this) {
                this.wait(halt);
            }
            return readFully((int) (halt - (System.currentTimeMillis() - currentTime)), bufferData, offset, length);
        }
        return read(bufferData, offset, length);
    }

    @Override
    public int readFully(byte[] bufferData) throws InterruptedException {
        return readFully(bufferData, 0, bufferData.length);
    }

    @Override
    public int readFully(byte[] bufferData, int length) throws InterruptedException {
        return readFully(bufferData, 0, length);
    }

    @Override
    public int readFully(byte[] bufferData, int offset, int length) throws InterruptedException {
        int available = writeHead - readHead;
        if (available < length) {

            synchronized (this) {
                this.wait();
            }
            return readFully(bufferData, offset, length);
        }
        return read(bufferData, offset, length);
    }

    @Override
    public int skip(int length) {
        int remaining = writeHead - readHead;
        if (remaining <= length) {
            readHead = writeHead;

            if (hasNext() && writeHead == data.length) {
                readHead = 0;
                writeHead = 0;
                remove();
                return remaining + next.skip(length - remaining);
            }
            return remaining;
        }
        readHead += length;
        return length;
    }

    @Override
    public int skipFully(int length) throws InterruptedException {
        int remaining = writeHead - readHead;
        if (length < remaining) {
            readHead += length;
            return length;
        }

        if (writeHead < data.length) {

            synchronized (this) {
                this.wait();
            }
            return skipFully(length);
        }
        return skip(length);
    }

    @Override
    public int skipFully(int length, int halt) throws InterruptedException {
        int remaining = writeHead - readHead;
        if (length <= remaining) {
            readHead += length;
            return length;
        }

        if (writeHead < data.length) {

            long currentTime = System.currentTimeMillis();
            synchronized (this) {
                this.wait();
            }
            return skipFully(length, (int) (halt - (System.currentTimeMillis() - currentTime)));
        }
        return skip(length);
    }

    @Override
    public int remaining() {
        return writeHead - readHead;
    }

    @Override
    public int free() {
        return data.length - writeHead;
    }

    @Override
    public int sizeof() {
        return data.length;
    }

    @Override
    public void reset() {
        resetRead();
        resetWrite();
    }

    @Override
    public void resetRead() {
        readHead = 0;
    }

    @Override
    public void resetWrite() {
        writeHead = 0;
    }

    void resetReadAll(BufferPart til) {
        resetRead();
        if (!til.equals(this) && hasNext())
            next().resetReadAll(til);
    }

    void resetWriteAll(BufferPart til) {
        resetWrite();
        if (!til.equals(this) && hasNext())
            next().resetWriteAll(til);
    }

    BufferPart getByLocation(int location) {
        if (location < data.length)
            return this;

        if (hasNext())
            return next.getByLocation(location - data.length);
        return null;
    }

    int sizeofAll(BufferPart til) {
        if (til.equals(this))
            return sizeof();

        if (hasNext())
            return sizeof() + next.sizeofAll(til);
        return sizeof();
    }

    int remainingAll(BufferPart til) {
        if (til.equals(this))
            return remaining();

        if (hasNext())
            return remaining() + next.remainingAll(til);
        return remaining();
    }

    int freeAll(BufferPart til) {
        if (til.equals(this))
            return free();

        if (hasNext())
            return free() + next.freeAll(til);
        return free();
    }

    void insert(BufferPart parent, BufferPart part) {
        if (parent.equals(this))
            insert(part);
        else if (hasNext())
            next().insert(parent, part);
    }

    void insertAll(int loc, byte[] data, int offset, int length) {
        if (this.data.length > loc)
            insert(loc, data, offset, length);
        else if (hasNext())
            next.insertAll(loc - data.length, data, offset, length);
    }


    void insert(BufferPart parent, int loc, byte[] data, int offset, int length) {
        if (parent.equals(this))
            insert(loc, data, offset, length);
        else if (hasNext())
            next.insert(parent, loc, data, offset, length);
    }

    private void insert(BufferPart part) {
        part.next = next;
        part.parent = this;
        next = part;
    }

    private void insert(int loc, byte[] data, int offset, int length) {
        if (writeHead < loc)
            throw new IllegalArgumentException("Cannot insert at " + loc + " there is no data to split; insertion" +
                    " would create fragments!");

        if (writeHead == loc) {
            BufferPart part = new BufferPart(getRoot(), length - this.data.length + writeHead);
            insert(part);
            write(data, offset, length);
        } else {
            int over = writeHead - loc;
            byte[] dataOver = new byte[over];
            System.arraycopy(this.data, loc, dataOver, 0, over);
            writeHead = loc;

            BufferPart part = new BufferPart(getRoot(), over + length - this.data.length + loc);
            insert(part);

            BufferPart writer = getRoot().currentWriter();
            write(data, offset, length);
            write(dataOver, 0, over);

            if (!writer.equals(this))
                getRoot().setCurrentWriter(writer);
        }
    }

    void remove(BufferPart bufferPart) {
        if (bufferPart.equals(this))
            remove();
        else if (hasNext())
            next.remove(bufferPart);
    }

    void remove() {
        if (hasNext())
            next.parent = parent;
        parent.next = next;

        parent = null;
        next = null;
    }

    boolean hasNext() {
        return next != null;
    }

    BufferPart next() {
        return next;
    }

    void setNext(BufferPart bufferPart) {
        this.next = bufferPart;
    }

    BufferRoot getRoot() {
        return root;
    }
}
