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

public class BufferPartStart extends BufferPart implements BufferRoot {

    private BufferPart currentRead;
    private BufferPart currentWrite;

    BufferPartStart(int size) {
        super(null, size);
        currentRead = currentWrite = this;
        setNext(this);
    }

    void insertPart(BufferPart parent, BufferPart part) {
        super.insert(parent, part);
    }

    @Override
    void insert(BufferPart parent, BufferPart part) {}

    void removePart(BufferPart part) {
        super.remove(part);
    }

    @Override
    void remove(BufferPart bufferPart) {}

    @Override
    void remove() {}

    @Override
    boolean hasNext() {
        return super.hasNext();
    }

    @Override
    BufferPart next() {
        return super.next();
    }

    int sizeWhole() {
        return next().sizeofAll(this);
    }

    int remainingWhole() {
        return currentRead.remainingAll(currentWrite);
    }

    int freeWhole() {
        if (currentRead.equals(currentWrite) && currentWrite.hasNext())
            return currentWrite.next().freeAll(currentRead);
        return currentWrite.freeAll(currentRead);
    }

    void resetReadWhole() {
        currentRead = this;
        if (hasNext())
            next().resetReadAll(this);
    }

    void resetWriteWhole() {
        currentWrite = this;
        if (hasNext())
            next().resetWriteAll(this);
    }

    BufferPart get(int location) {
        return super.getByLocation(location);
    }

    @Override
    BufferPart getByLocation(int location) {
        return null;
    }

    void insertData(int loc, byte[] data, int offset, int length) {
        super.insertAll(loc, data, offset, length);
    }

    void insertData(BufferPart parent, int loc, byte[] data, int offset, int length) {
        super.insert(parent, loc, data, offset, length);
    }

    @Override
    void insert(BufferPart parent, int loc, byte[] data, int offset, int length) {}

    public BufferPart currentReader() {
        return currentRead;
    }

    public BufferPart currentWriter() {
        return currentWrite;
    }

    public void setCurrentReader(BufferPart value) {
        this.currentRead = value;
    }

    public void setCurrentWriter(BufferPart value) {
        this.currentWrite = value;
    }

    BufferPartStart getRoot() {
        return this;
    }
}
