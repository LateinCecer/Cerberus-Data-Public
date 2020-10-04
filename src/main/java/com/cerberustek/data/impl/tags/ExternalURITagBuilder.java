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
import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaByteBuffer;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("Duplicates")
public class ExternalURITagBuilder implements MetaBuilder<ExternalURITag> {

    private DiscriminatorMap discriminatorMap;

    public ExternalURITagBuilder(DiscriminatorMap discriminatorMap) {
        this.discriminatorMap = discriminatorMap;
    }

    public ExternalURITagBuilder() {
        this(null);
    }

    @Override
    public ExternalURITag build(String tag, MetaInputStream inputStream) throws IOException, UnknownDiscriminatorException {
        try {
            return new ExternalURITag(tag, new URI(inputStream.readUTF()),
                    discriminatorMap != null ? discriminatorMap : inputStream.getDiscriminatorMap());
        } catch (URISyntaxException e) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusData.class, e));
            return null;
        }
    }

    @Override
    public ExternalURITag build(String tag, MetaByteBuffer buffer) throws UnknownDiscriminatorException {
        try {
            return new ExternalURITag(tag, new URI(buffer.readUTF()),
                    discriminatorMap != null ? discriminatorMap : buffer.getDiscriminatorMap());
        } catch (URISyntaxException e) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusData.class, e));
            return null;
        }
    }

    @Override
    public Class<ExternalURITag> getDataClass() {
        return ExternalURITag.class;
    }

    @Override
    public int getFinalSize() {
        return -1;
    }

    @Override
    public boolean isTag() {
        return true;
    }

    public DiscriminatorMap getDiscriminatorMap() {
        return discriminatorMap;
    }

    public void setDiscriminatorMap(DiscriminatorMap discriminatorMap) {
        this.discriminatorMap = discriminatorMap;
    }
}
