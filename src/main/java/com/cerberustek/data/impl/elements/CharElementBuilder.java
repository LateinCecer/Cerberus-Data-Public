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

import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;

public class CharElementBuilder implements MetaBuilder<CharElement> {

    @Override
    public CharElement build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        return new CharElement(inputStream.readChar());
    }

    @Override
    public CharElement build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        return new CharElement(buffer.readChar());
    }

    @Override
    public Class<CharElement> getDataClass() {
        return CharElement.class;
    }

    @Override
    public int getFinalSize() {
        return 2;
    }

    @Override
    public boolean isTag() {
        return false;
    }
}
