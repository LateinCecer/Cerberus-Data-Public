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
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.MetaTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MetaOutputStreamImpl extends DataOutputStream implements MetaOutputStream, AutoCloseable {

    private final DiscriminatorMap discriminatorMap;

    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter <code>written</code> is
     * set to zero.
     *
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @param discriminatorMap map of discriminators this stream uses to
     *                         format data.
     * @see FilterOutputStream#out
     */
    public MetaOutputStreamImpl(OutputStream out, DiscriminatorMap discriminatorMap) {
        super(new CountingOutputStream(out));
        this.discriminatorMap = discriminatorMap;
    }

    @Override
    public void write(byte[] buffer, int length) throws IOException {
        write(buffer, 0, length);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void writeData(MetaData data) throws IOException, NoMatchingDiscriminatorException {
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
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public synchronized void write(int b) throws IOException {
        super.write(b);
    }

    @Override
    public long getByteCount() {
        return ((CountingOutputStream) super.out).getByteCount();
    }

    @Override
    public void resetByteCount() {
        ((CountingOutputStream) super.out).resetByteCount();
    }

    @Override
    public DiscriminatorMap getDiscriminatorMap() {
        return discriminatorMap;
    }
}
