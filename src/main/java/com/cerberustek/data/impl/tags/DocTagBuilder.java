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

import com.cerberustek.data.*;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;

public class DocTagBuilder implements MetaBuilder<DocTag> {

    @Override
    public DocTag build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        DocTag doc = new DocTag(tag);

        int length = inputStream.readInt();
        for (int i = 0; i < length; i++) {
            MetaData data = inputStream.readData();
            if (data instanceof MetaTag)
                doc.insert((MetaTag) data);
        }
        return doc;
    }

    @Override
    public DocTag build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        DocTag doc = new DocTag(tag);

        int length = buffer.readInt();
        for (int i = 0; i < length; i++) {
            MetaData data = buffer.readData();
            if (data instanceof MetaTag)
                doc.insert((MetaTag) data);
        }
        return doc;
    }

    @Override
    public Class<DocTag> getDataClass() {
        return DocTag.class;
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
