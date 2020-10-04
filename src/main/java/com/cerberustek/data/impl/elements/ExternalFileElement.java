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
import com.cerberustek.events.ExceptionEvent;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ExternalFileElement implements MetaElement<MetaData> {

    private final String path;

    private DiscriminatorMap discriminatorMap;
    private MetaData data;

    public ExternalFileElement(String path, DiscriminatorMap discriminatorMap) {
        this.path = path;
        this.discriminatorMap = discriminatorMap;
    }

    @Override
    public void set(MetaData value) {
        this.data = value;
    }

    @Override
    public MetaData get() {
        return data;
    }

    @Override
    public MetaTag toTag(String tag) {
        return null;
    }

    @Override
    public void serialize(MetaOutputStream metaOutputStream) throws IOException, NoMatchingDiscriminatorException {
        metaOutputStream.writeUTF(path);
        save();
    }

    @Override
    public void serialize(MetaByteBuffer metaBuffer) throws NoMatchingDiscriminatorException {
        metaBuffer.writeUTF(path);
        save();
    }

    @Override
    public long byteSize() {
        return path.getBytes().length + 2;
    }

    @Override
    public long finalSize() {
        return -1;
    }

    public String getPath() {
        return path;
    }

    public void setDiscriminatorMap(DiscriminatorMap discriminatorMap) {
        this.discriminatorMap = discriminatorMap;
    }

    public DiscriminatorMap getDiscriminatorMap() {
        return discriminatorMap;
    }

    /**
     * Will load the data form the path.
     *
     * The data that is currently stored in this
     * object will be overwritten.
     * @return success
     */
    public boolean load() {
        try (final MetaInputStream inputStream = CerberusData
                .createInputStream(new FileInputStream(path), discriminatorMap)) {

            data = inputStream.readData();
            return true;
        } catch (IOException | UnknownDiscriminatorException e) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusData.class, e));
            return false;
        }
    }

    /**
     * Returns if there is currently data
     * stored in this object.
     * @return is data loaded
     */
    public boolean isloaded() {
        return data != null;
    }

    /**
     * Will unload the data stored in this
     * object to the destination.
     *
     * All data that is currently saved at
     * the destination path will be overwritten.
     * @return unload-/saving success
     */
    public boolean unload() {
        boolean outcome = save();
        if (outcome)
            data = null;
        return outcome;
    }

    /**
     * Will save the data currently stored
     * in this object to the destination
     * without unloaded it.
     * @return success
     */
    public boolean save() {
        try (final MetaOutputStream outputStream = CerberusData
                .createOutputStream(new FileOutputStream(path), discriminatorMap)) {

            outputStream.writeData(data);
            outputStream.flush();
            return true;
        } catch (IOException | NoMatchingDiscriminatorException e) {
            CerberusRegistry.getInstance().getService(CerberusEvent.class)
                    .executeFullEIF(new ExceptionEvent(CerberusData.class, e));
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalFileElement that = (ExternalFileElement) o;
        return Objects.equals(path, that.path) &&
                Objects.equals(discriminatorMap, that.discriminatorMap) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, discriminatorMap, data);
    }
}
