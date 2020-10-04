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
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.util.logging.Level;

public class SetElementBuilder implements MetaBuilder<SetElement> {

    @Override
    public SetElement build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        int length = inputStream.readInt();
        SetElement<MetaData> set = new SetElement<>();

        for (int i = 0; i < length; i++) {
            try {
                set.add(inputStream.readData());
            } catch (UnknownDiscriminatorException e) {
                // Data loss
                CerberusData.getLogger().log(Level.WARNING, "Failed to read element of meta set!");
            }
        }
        return set;
    }

    @Override
    public SetElement build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        int length = buffer.readInt();
        SetElement<MetaData> set = new SetElement<>();

        for (int i = 0; i < length; i++) {
            try {
                set.add(buffer.readData());
            } catch (UnknownDiscriminatorException e) {
                // Data loss
                CerberusData.getLogger().log(Level.WARNING, "Failed to read element of meta set!");
            }
        }
        return set;
    }

    @Override
    public Class<SetElement> getDataClass() {
        return SetElement.class;
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
