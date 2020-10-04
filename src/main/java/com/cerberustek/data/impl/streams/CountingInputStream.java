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

import com.cerberustek.data.CountingStream;

import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends InputStream implements CountingStream {

    private final InputStream in;

    private long count;

    public CountingInputStream(InputStream in) {
        this.in = in;
        count = 0;
    }

    @Override
    public int read() throws IOException {
        count++;
        return in.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = in.read(b);
        count += read;
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = in.read(b, off, len);
        count += read;
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = in.skip(n);
        count += skipped;
        return skipped;
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    @Override
    public void resetByteCount() {
        count = 0;
    }

    @Override
    public long getByteCount() {
        return count;
    }
}
