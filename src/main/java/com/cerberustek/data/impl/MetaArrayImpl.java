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

package com.cerberustek.data.impl;

import com.cerberustek.CerberusData;
import com.cerberustek.data.MetaArray;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaTag;
import com.cerberustek.exception.ResourceUnavailableException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.trace.TraceIndex;
import com.cerberustek.querry.trace.QueryTrace;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

public abstract class MetaArrayImpl<T extends MetaData> implements MetaArray<T> {

    protected T[] values;

    public MetaArrayImpl(T[] values) {
        this.values = values;
    }

    @Override
    public void set(int index, T value) {
        values[index] = value;
    }

    @Override
    public int length() {
        return values != null ? values.length : 0;
    }

    @Override
    public T get(int index) {
        return values[index];
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator<>(this);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        if (values == null)
            return;
        for (T value : values) action.accept(value);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[').append(getClass().getName()).append(']');
        if (this instanceof MetaTag)
            stringBuilder.append("<").append(((MetaTag) this).getTag()).append(">");
        stringBuilder.append(':').append(' ').append('{');

        if (values == null || values.length == 0) {
            stringBuilder.append('}');
            return stringBuilder.toString();
        }

        for (T t : values)
            stringBuilder.append("\n\t").append(t.toString().replace("\n", "\n\t"));
        stringBuilder.append("\n").append('}');
        return stringBuilder.toString();
    }

    @Override
    public QueryResult trace(QueryTrace request) throws ResourceUnavailableException {
        if (request instanceof TraceIndex)
            return CerberusData.pullResult(request, this, get(((TraceIndex) request).getIndex()));
        return CerberusData.pullResult(request, this, this);
    }

    private static class ArrayIterator<T extends MetaData> implements Iterator<T> {

        private final MetaArray<T> array;

        private int index;

        private ArrayIterator(MetaArray<T> array) {
            this.array = array;
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return array.length() > index;
        }

        @Override
        public T next() {
            return array.get(index++);
        }
    }
}
