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
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaSet;
import com.cerberustek.data.MetaTag;
import com.cerberustek.exception.ResourceUnavailableException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.trace.QueryTrace;
import com.cerberustek.querry.trace.TraceTag;

import java.util.HashSet;

public abstract class MetaSetImpl<T extends MetaData> extends HashSet<T> implements MetaSet<T> {

    @SuppressWarnings("DuplicatedCode")
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[').append(getClass().getName()).append(']');
        if (this instanceof MetaTag)
            stringBuilder.append("<").append(((MetaTag) this).getTag()).append(">");
        stringBuilder.append(':').append(' ').append('{');

        if (isEmpty()) {
            stringBuilder.append('}');
            return stringBuilder.toString();
        }

        forEach(value ->
                stringBuilder.append("\n\t").append(value.toString().replace("\n", "\n\t")));
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    @Override
    public QueryResult trace(QueryTrace request) throws ResourceUnavailableException {
        if (request instanceof TraceTag) {
            TraceTag tagTrace = (TraceTag) request;
            T metaTag = null;
            for (T t : this) {
                if (t instanceof MetaTag && ((MetaTag) t).getTag().equals(tagTrace.getTag())) {
                    metaTag = t;
                    break;
                }
            }
            return CerberusData.pullResult(request, this, metaTag);
        }
        return CerberusData.pullResult(request, this, this);
    }
}
