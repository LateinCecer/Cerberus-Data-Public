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
import com.cerberustek.logic.math.Matrix4f;

import java.io.IOException;

public class Matrix4fTagBuilder implements MetaBuilder<Matrix4fTag> {

    @Override
    public Matrix4fTag build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        Matrix4f mat = new Matrix4f();

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                mat.set(x, y, inputStream.readFloat());
            }
        }
        return new Matrix4fTag(tag, mat);
    }

    @Override
    public Matrix4fTag build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        Matrix4f mat = new Matrix4f();

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                mat.set(x, y, buffer.readFloat());
            }
        }
        return new Matrix4fTag(tag, mat);
    }

    @Override
    public Class<Matrix4fTag> getDataClass() {
        return Matrix4fTag.class;
    }

    @Override
    public int getFinalSize() {
        return 64;
    }

    @Override
    public boolean isTag() {
        return true;
    }
}
