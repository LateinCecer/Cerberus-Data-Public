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

package com.cerberustek.buffers;

public interface DataBuffer {

    int write(byte[] bufferData);
    int write(byte[] bufferData, int length);
    int write(byte[] bufferData, int offset, int length);

    int read(byte[] bufferData);
    int read(byte[] bufferData, int length);
    int read(byte[] bufferData, int offset, int length);

    int readFully(int halt, byte[] bufferData) throws InterruptedException;
    int readFully(int halt, byte[] bufferData, int length) throws InterruptedException;
    int readFully(int halt, byte[] bufferData, int offset, int length) throws InterruptedException;

    int readFully(byte[] bufferData) throws InterruptedException;
    int readFully(byte[] bufferData, int length) throws InterruptedException;
    int readFully(byte[] bufferData, int offset, int length) throws InterruptedException;

    int skip(int length);
    int skipFully(int length) throws InterruptedException;
    int skipFully(int length, int halt) throws InterruptedException;

    int remaining();
    int free();
    int sizeof();

    void reset();
    void resetRead();
    void resetWrite();
}
