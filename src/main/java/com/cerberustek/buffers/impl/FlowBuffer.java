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

import com.cerberustek.buffers.InsertionBuffer;

public class FlowBuffer implements InsertionBuffer {

    private final BufferPartStart start;

    public FlowBuffer(int initialSize) {
        start = new BufferPartStart(initialSize);
    }

    @Override
    public void insert(int location, byte[] data) {
        insert(location, data, 0, data.length);
    }

    @Override
    public void insert(int location, byte[] data, int length) {
        insert(location, data, 0, length);
    }

    @Override
    public void insert(int location, byte[] data, int offset, int length) {
        start.insertData(location, data, offset, length);
    }

    @Override
    public void increase(int location, int size) {
        start.insertData(location, new byte[size], 0, size);
    }

    @Override
    public int write(byte[] bufferData) {
        return start.currentWriter().write(bufferData);
    }

    @Override
    public int write(byte[] bufferData, int length) {
        return start.currentWriter().write(bufferData, length);
    }

    @Override
    public int write(byte[] bufferData, int offset, int length) {
        return start.currentWriter().write(bufferData, offset, length);
    }

    @Override
    public int read(byte[] bufferData) {
        return start.currentReader().read(bufferData);
    }

    @Override
    public int read(byte[] bufferData, int length) {
        return start.currentReader().read(bufferData, length);
    }

    @Override
    public int read(byte[] bufferData, int offset, int length) {
        return start.currentReader().read(bufferData, offset, length);
    }

    @Override
    public int readFully(int halt, byte[] bufferData) throws InterruptedException {
        return start.currentReader().readFully(halt, bufferData);
    }

    @Override
    public int readFully(int halt, byte[] bufferData, int length) throws InterruptedException {
        return start.currentReader().readFully(halt, bufferData, length);
    }

    @Override
    public int readFully(int halt, byte[] bufferData, int offset, int length) throws InterruptedException {
        return start.currentReader().readFully(halt, bufferData, offset, length);
    }

    @Override
    public int readFully(byte[] bufferData) throws InterruptedException {
        return start.currentReader().readFully(bufferData);
    }

    @Override
    public int readFully(byte[] bufferData, int length) throws InterruptedException {
        return start.currentReader().readFully(bufferData, length);
    }

    @Override
    public int readFully(byte[] bufferData, int offset, int length) throws InterruptedException {
        return start.currentReader().readFully(bufferData, offset, length);
    }

    @Override
    public int skip(int length) {
        return start.currentReader().skip(length);
    }

    @Override
    public int skipFully(int length) throws InterruptedException {
        return start.currentReader().skipFully(length);
    }

    @Override
    public int skipFully(int length, int halt) throws InterruptedException {
        return start.currentReader().skipFully(length, halt);
    }

    @Override
    public int remaining() {
        return start.remainingWhole();
    }

    @Override
    public int free() {
        return start.freeWhole();
    }

    @Override
    public int sizeof() {
        return start.sizeWhole();
    }

    @Override
    public void reset() {
        resetRead();
        resetWrite();
    }

    @Override
    public void resetRead() {
        start.resetReadWhole();
    }

    @Override
    public void resetWrite() {
        start.resetWriteWhole();
    }
}
