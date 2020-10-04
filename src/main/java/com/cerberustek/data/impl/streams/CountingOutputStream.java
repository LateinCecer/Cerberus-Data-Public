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
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream implements CountingStream {

    private final OutputStream out;

    private long count;

    public CountingOutputStream(OutputStream out) {
        this.out = out;
        count = 0;
    }

    @Override
    public void write(int b) throws IOException {
        count++;
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        count += b.length;
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        count += len;
        out.write(b, off, len);
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
