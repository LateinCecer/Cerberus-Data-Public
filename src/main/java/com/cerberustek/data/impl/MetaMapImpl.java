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
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaMap;
import com.cerberustek.data.MetaTag;
import com.cerberustek.exception.ResourceUnavailableException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.trace.TraceElement;
import com.cerberustek.querry.trace.QueryTrace;

import java.util.HashMap;

public abstract class MetaMapImpl<T extends MetaElement, D extends MetaElement> extends HashMap<T, D> implements MetaMap<T, D> {

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

        forEach((key, value) ->
                stringBuilder.append("\n\t\"").append(key.toString()).append("\": ").append(value.toString().replace("\n", "\n\t")));
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    @Override
    public QueryResult trace(QueryTrace request) throws ResourceUnavailableException {
        if (request instanceof TraceElement)
            return CerberusData.pullResult(request, this, get(((TraceElement) request).getElement()));
        return CerberusData.pullResult(request, this, this);
    }
}
