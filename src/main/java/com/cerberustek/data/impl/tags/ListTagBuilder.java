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

package com.cerberustek.data.impl.tags;

import com.cerberustek.CerberusData;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.util.logging.Level;

public class ListTagBuilder implements MetaBuilder<ListTag> {

    @Override
    public ListTag build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        int length = inputStream.readInt();
        ListTag<MetaData> output = new ListTag<>(tag);

        for (int i = 0; i < length; i++) {
            try {
                output.add(inputStream.readData());
            } catch (UnknownDiscriminatorException e) {
                // This is more or less a generic error, nothing to major, just some data loss
                CerberusData.getLogger().log(Level.WARNING, "Failed to read element of meta list with index: " + i + "!");
            }
        }
        return output;
    }

    @Override
    public ListTag build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        int length = buffer.readInt();
        ListTag<MetaData> output = new ListTag<>(tag);

        for (int i = 0; i < length; i++) {
            try {
                output.add(buffer.readData());
            } catch (UnknownDiscriminatorException e) {
                // This is more or less a generic error, nothing to major, just some data loss
                CerberusData.getLogger().log(Level.WARNING, "Failed to read element of meta list with index: " + i + "!");
            }
        }
        return output;
    }

    @Override
    public Class<ListTag> getDataClass() {
        return ListTag.class;
    }

    @Override
    public int getFinalSize() {
        return -1;
    }

    @Override
    public boolean isTag() {
        return true;
    }
}
