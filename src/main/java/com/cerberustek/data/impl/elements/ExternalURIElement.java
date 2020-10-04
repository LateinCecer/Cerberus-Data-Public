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
import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.data.*;
import com.cerberustek.data.impl.tags.ExternalURITag;
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class ExternalURIElement implements MetaElement<MetaData> {

    private final URI uri;

    private DiscriminatorMap discriminatorMap;
    private MetaData data;

    public ExternalURIElement(URI uri, DiscriminatorMap discriminatorMap) {
        this.uri = uri;
        this.discriminatorMap = discriminatorMap;
    }

    /**
     * This method actually does very little in this
     * type of data element.
     *
     * Sadly, URI streams are read only by default,
     * this means that the external URI element can
     * only be read from. serialization will only
     * save the URI itself, not the data sorted at
     * the target destination, pointed at by the URI.
     * This method will set the internal data pointer
     * to the value provided by the caller, however
     * the data will not be saved.
     * @param value value to set to
     */
    public void set(MetaData value) {
        this.data = value;
    }

    @Override
    public MetaData get() {
        if (!isLoaded())
            reload();
        return data;
    }

    @Override
    public MetaTag toTag(String tag) {
        if (this instanceof ExternalURITag)
            return (MetaTag) this;
        ExternalURITag t = new ExternalURITag(tag, uri, discriminatorMap);
        t.set(data);
        return t;
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeUTF(uri.toASCIIString());
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeUTF(uri.toASCIIString());
    }

    @Override
    public long byteSize() {
        return uri.toASCIIString().getBytes().length + 2;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    public URI getURI() {
        return uri;
    }

    public boolean isLoaded() {
        return data != null;
    }

    public void unload() {
        data = null;
    }

    public boolean reload() {
        try (final MetaInputStream inputStream = CerberusData
                .createInputStream(uri.toURL().openStream(), discriminatorMap)) {

            data = inputStream.readData();
            return true;
        } catch (IOException | UnknownDiscriminatorException e) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusData.class, e));
            return false;
        }
    }

    public DiscriminatorMap getDiscriminatorMap() {
        return discriminatorMap;
    }

    public void setDiscriminatorMap(DiscriminatorMap discriminatorMap) {
        this.discriminatorMap = discriminatorMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalURIElement that = (ExternalURIElement) o;
        return Objects.equals(uri, that.uri) &&
                Objects.equals(discriminatorMap, that.discriminatorMap) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, discriminatorMap, data);
    }
}
