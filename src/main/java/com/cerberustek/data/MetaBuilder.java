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

package com.cerberustek.data;

import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;

public interface MetaBuilder<T extends MetaData> {

    T build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException;
    T build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException;

    Class<T> getDataClass();

    /**
     * Returns the size of the element. This method can only be used for
     * elements which always contain the same amount of bytes. For every
     * other data type, this method will return -1.
     * @return finalized size if possible
     */
    int getFinalSize();

    /**
     * This method returns rather the data type in question has a tag or
     * not.
     * @return tag
     */
    boolean isTag();
}
