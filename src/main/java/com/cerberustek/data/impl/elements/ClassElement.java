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

import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.MetaTag;
import com.cerberustek.data.impl.MetaElementImpl;
import com.cerberustek.data.impl.tags.ClassTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;

import java.io.IOException;

public class ClassElement extends MetaElementImpl<String> {

    public ClassElement(String name) {
        super(name);
    }

    public ClassElement(Class<?> value) {
        super(value.getName());
    }

    @Override
    public MetaTag toTag(String tag) {
        return new ClassTag(tag, get());
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeUTF(get());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeUTF(get());
    }

    @Override
    public long byteSize() {
        return get().getBytes().length + 2;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    /**
     * Will load the internally specified class with the
     * specified class loader.
     * @param classLoader class loader
     * @return loaded class
     * @throws ClassNotFoundException class is not in class path
     */
    public Class<?> load(ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader.loadClass(get());
    }

    /**
     * Will load the internally specified class with the
     * class loader of this class.
     * @return loaded class
     * @throws ClassNotFoundException class is not in class path
     */
    public Class<?> load() throws ClassNotFoundException {
        return load(getClass().getClassLoader());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        return ((ClassElement) obj).get().equals(get());
    }
}
