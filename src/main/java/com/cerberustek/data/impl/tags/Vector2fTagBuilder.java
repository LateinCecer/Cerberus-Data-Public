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

import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.exception.UnknownDiscriminatorException;
import com.cerberustek.logic.math.Vector2f;

import java.io.IOException;

public class Vector2fTagBuilder implements MetaBuilder<Vector2fTag> {

    @Override
    public Vector2fTag build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        return new Vector2fTag(tag, new Vector2f(inputStream.readFloat(), inputStream.readFloat()));
    }

    @Override
    public Vector2fTag build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        return new Vector2fTag(tag, new Vector2f(buffer.readFloat(), buffer.readFloat()));
    }

    @Override
    public Class<Vector2fTag> getDataClass() {
        return Vector2fTag.class;
    }

    @Override
    public int getFinalSize() {
        return 8;
    }

    @Override
    public boolean isTag() {
        return true;
    }
}
