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

package com.cerberustek.querry.trace.impl;

import com.cerberustek.querry.trace.QueryTrace;

public abstract class QueryTraceImpl implements QueryTrace {

    private QueryTrace next;

    public QueryTraceImpl(QueryTrace next) {
        this.next = next;
    }

    public QueryTraceImpl() {
        this.next = null;
    }

    public void stack(QueryTrace next) {
        if (hasNext()) {
            if (this.next instanceof QueryTraceImpl)
                ((QueryTraceImpl) this.next).stack(next);
        } else
            this.next = next;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public QueryTrace next() {
        return next;
    }

    public void del() {
        if (hasNext()) {

            if (next instanceof QueryTraceImpl)
                ((QueryTraceImpl) next).del();
            next = null;
        }
    }
}
