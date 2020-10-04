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

package com.cerberustek.data.impl.elements;

import com.cerberustek.CerberusData;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.util.logging.Level;

@SuppressWarnings("Duplicates")
public class MapElementBuilder implements MetaBuilder<MapElement> {

    @Override
    public MapElement build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        int length = inputStream.readInt();
        MapElement<MetaElement, MetaElement> output = new MapElement<>();

        for (int i = 0; i < length; i++) {
            long size = inputStream.readLong();
            long read = inputStream.getByteCount();
            try {
                MetaBuilder keyBuilder = inputStream.getDiscriminatorMap().getBuilder(inputStream.readShort());
                MetaBuilder valueBuilder = inputStream.getDiscriminatorMap().getBuilder(inputStream.readShort());
                MetaElement key = (MetaElement) keyBuilder.build(null, inputStream);
                MetaElement value;

                if (valueBuilder != null)
                    value = (MetaElement) valueBuilder.build(null, inputStream);
                else
                    value = null;
                output.put(key, value);
            } catch (UnknownDiscriminatorException e) {
                long currentByteCount = inputStream.getByteCount();
                inputStream.skipFully(size - (currentByteCount - read));
                CerberusData.getLogger().log(Level.WARNING, "Failed to read element of meta map; " + size + " bytes lost!");
            }
        }
        return output;
    }

    @Override
    public MapElement build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        int length = buffer.readInt();
        MapElement<MetaElement, MetaElement> output = new MapElement<>();

        for (int i = 0; i < length; i++) {
            long size = buffer.readLong();
            long read = buffer.remaining();
            try {
                MetaBuilder keyBuilder = buffer.getDiscriminatorMap().getBuilder(buffer.readShort());
                MetaBuilder valueBuilder = buffer.getDiscriminatorMap().getBuilder(buffer.readShort());
                MetaElement key = (MetaElement) keyBuilder.build(null, buffer);
                MetaElement value;

                if (valueBuilder != null)
                    value = (MetaElement) valueBuilder.build(null, buffer);
                else
                    value = null;
                output.put(key, value);
            } catch (UnknownDiscriminatorException e) {
                long currentByteCount = buffer.remaining();
                buffer.skipFully(size - (currentByteCount - read));
                CerberusData.getLogger().log(Level.WARNING, "Failed to read element of meta map; " + size + " bytes lost!");
            }
        }
        return output;
    }

    @Override
    public Class<MapElement> getDataClass() {
        return MapElement.class;
    }

    @Override
    public int getFinalSize() {
        return -1;
    }

    @Override
    public boolean isTag() {
        return false;
    }
}
