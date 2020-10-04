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

import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaTag;
import com.cerberustek.querry.trace.impl.append.ApTraceMap;
import com.cerberustek.querry.trace.impl.append.ApTraceResource;
import com.cerberustek.querry.trace.impl.insert.InsTraceIndex;
import com.cerberustek.querry.trace.impl.pull.PullTraceDoc;
import com.cerberustek.querry.trace.impl.pull.PullTraceElement;
import com.cerberustek.querry.trace.impl.pull.PullTraceIndex;
import com.cerberustek.querry.trace.impl.pull.PullTraceTag;
import com.cerberustek.querry.trace.impl.remove.RemTraceElement;
import com.cerberustek.querry.trace.impl.remove.RemTraceIndex;
import com.cerberustek.querry.trace.impl.remove.RemTraceTag;
import com.cerberustek.querry.trace.impl.replace.ReplTraceElement;
import com.cerberustek.querry.trace.impl.replace.ReplTraceIndex;
import com.cerberustek.querry.trace.impl.replace.ReplTraceTag;
import com.cerberustek.querry.trace.QueryTrace;
import com.cerberustek.querry.trace.TraceBuilder;

public class SimpleTraceBuilder implements TraceBuilder {

    private QueryTrace trace;

    @Override
    public TraceBuilder stack(QueryTrace trace) {
        if (this.trace == null)
            this.trace = trace;
        else {

            if (this.trace instanceof QueryTraceImpl)
                ((QueryTraceImpl) this.trace).stack(trace);
            else
                throw new IllegalArgumentException("End of stack trace!");
        }
        return this;
    }

    @Override
    public TraceBuilder stackIndexLoc(int index) {
        return stack(new PullTraceIndex(index));
    }

    @Override
    public TraceBuilder stackIndexRemove(int index) {
        return stack(new RemTraceIndex(index));
    }

    @Override
    public TraceBuilder stackIndexInsert(int index, MetaElement value) {
        return stack(new InsTraceIndex(index, value));
    }

    @Override
    public TraceBuilder stackIndexReplace(int index, MetaElement value) {
        return stack(new ReplTraceIndex(index, value));
    }

    @Override
    public TraceBuilder stackElementLoc(MetaElement key) {
        return stack(new PullTraceElement(key));
    }

    @Override
    public TraceBuilder stackElementRemove(MetaElement key) {
        return stack(new RemTraceElement(key));
    }

    @Override
    public TraceBuilder stackElementReplace(MetaElement key, MetaElement value) {
        return stack(new ReplTraceElement(key, value));
    }

    @Override
    public TraceBuilder stackDocLoc(String tag) {
        return stack(new PullTraceDoc(tag));
    }

    @Override
    public TraceBuilder stackTagLoc(String tag) {
        return stack(new PullTraceTag(tag));
    }

    @Override
    public TraceBuilder stackTagRemove(String tag) {
        return stack(new RemTraceTag(tag));
    }

    @Override
    public TraceBuilder stackTagReplace(MetaTag value) {
        return stack(new ReplTraceTag(value));
    }

    @Override
    public TraceBuilder stackAppend(MetaElement value) {
        return stack(new ApTraceResource(value));
    }

    @Override
    public TraceBuilder stackAppend(MetaTag value) {
        return stack(new ApTraceResource(value));
    }

    @Override
    public TraceBuilder stackAppend(MetaElement key, MetaElement value) {
        return stack(new ApTraceMap(key, value));
    }

    @Override
    public QueryTrace build() {
        return trace;
    }

    @Override
    public void reset() {
        if (trace instanceof QueryTraceImpl)
            ((QueryTraceImpl) trace).del();
        trace = null;
    }
}
