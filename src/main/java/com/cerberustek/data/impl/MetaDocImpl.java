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

import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaDoc;
import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaTag;
import com.cerberustek.data.impl.tags.*;
import com.cerberustek.CerberusData;
import com.cerberustek.exception.ResourceUnavailableException;
import com.cerberustek.querry.QueryResult;
import com.cerberustek.querry.trace.QueryTrace;
import com.cerberustek.querry.trace.TraceTag;
import com.cerberustek.querry.trace.impl.pull.PullTraceDoc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class MetaDocImpl implements MetaDoc, MetaData {

    protected HashMap<String, MetaTag> tags = new HashMap<>();

    @Override
    public MetaDoc clear() {
        tags.clear();
        return this;
    }

    @Override
    public MetaDoc insert(MetaTag data) {
        tags.put(data.getTag(), data);
        return this;
    }

    @Override
    public MetaDoc remove(String tag) {
        this.tags.remove(tag);
        return this;
    }

    @Override
    public int size() {
        return tags.size();
    }

    @Override
    public MetaTag extract(String tag) {
        return this.tags.get(tag);
    }

    @Override
    public <T extends MetaTag> T extract(@NotNull String tag, Class<T> clazz) {
        MetaTag meta = extract(tag);
        if (meta == null)
            return null;

        if (clazz.isInstance(meta))
            return clazz.cast(meta);
        throw new IllegalStateException("Value with tag: \"" + tag + "\" is not an instance" +
                " of class: " + clazz + "!");
    }

    @Override
    @NotNull
    public Iterator<MetaTag> iterator() {
        return tags.values().iterator();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[').append(getClass().getName()).append(']');
        if (this instanceof MetaTag)
            stringBuilder.append("<").append(((MetaTag) this).getTag()).append(">");
        stringBuilder.append(':').append(' ').append('{');

        if (tags.isEmpty()) {
            stringBuilder.append('}');
            return stringBuilder.toString();
        }

        tags.values().forEach(value ->
            stringBuilder.append("\n\t").append(value.toString().replace("\n", "\n\t")));
        stringBuilder.append("\n}");
        return stringBuilder.toString();
    }

    @Override
    public QueryResult trace(QueryTrace request) throws ResourceUnavailableException {
        if (request instanceof TraceTag) {
            if (request instanceof PullTraceDoc)
                return CerberusData.pullResult(request, this, createDoc(((TraceTag) request).getTag()));
            return CerberusData.pullResult(request, this, extract(((TraceTag) request).getTag()));
        }
        return CerberusData.pullResult(request, this, this);
    }

    @Override
    public boolean contains(String tag) {
        return tags.containsKey(tag);
    }

    @Override
    public StringTag extractString(@NotNull String tag) {
        return extract(tag, StringTag.class);
    }

    @Override
    public ByteTag extractByte(@NotNull String tag) {
        return extract(tag, ByteTag.class);
    }

    @Override
    public ShortTag extractShort(@NotNull String tag) {
        return extract(tag, ShortTag.class);
    }

    @Override
    public CharTag extractChar(@NotNull String tag) {
        return extract(tag, CharTag.class);
    }

    @Override
    public IntTag extractInt(@NotNull String tag) {
        return extract(tag, IntTag.class);
    }

    @Override
    public BooleanTag extractBoolean(@NotNull String tag) {
        return extract(tag, BooleanTag.class);
    }

    @Override
    public LongTag extractLong(@NotNull String tag) {
        return extract(tag, LongTag.class);
    }

    @Override
    public FloatTag extractFloat(@NotNull String tag) {
        return extract(tag, FloatTag.class);
    }

    @Override
    public DoubleTag extractDouble(@NotNull String tag) {
        return extract(tag, DoubleTag.class);
    }

    @Override
    public ArrayTag extractArray(@NotNull String tag) {
        return extract(tag, ArrayTag.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends MetaData> ArrayTag<T> extractArray(@NotNull String tag, @NotNull Class<T> clazz) {
        ArrayTag<T> out;
        try {
            out = (ArrayTag<T>) extractArray(tag);
        } catch (ClassCastException e) {
            return null;
        }
        return out;
    }

    @Override
    public ListTag extractList(@NotNull String tag) {
        return extract(tag, ListTag.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends MetaData> ListTag<T> extractList(@NotNull String tag, @NotNull Class<T> clazz) {
        ListTag<T> out;
        try {
            out = (ListTag<T>) extractList(tag);
        } catch (ClassCastException e) {
            return null;
        }
        return out;
    }

    @Override
    public SetTag extractSet(@NotNull String tag) {
        return extract(tag, SetTag.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends MetaData> SetTag<T> extractSet(@NotNull String tag, @NotNull Class<T> clazz) {
        SetTag<T> out;
        try {
            out = (SetTag<T>) extractSet(tag);
        } catch (ClassCastException e) {
            return null;
        }
        return out;
    }

    @Override
    public MapTag extractMap(@NotNull String tag) {
        return extract(tag, MapTag.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <Key extends MetaElement, Value extends MetaElement> MapTag<Key, Value> extractMap(
            @NotNull String tag, @NotNull Class<Key> keyClass, @NotNull Class<Value> valueClass) {
        MapTag<Key, Value> map;
        try {
            map = extractMap(tag);
        } catch (ClassCastException e) {
            return null;
        }
        return map;
    }

    @Override
    public DocTag extractDoc(@NotNull String tag) {
        return extract(tag, DocTag.class);
    }

    @Override
    public UUIDTag extractUUID(@NotNull String tag) {
        return extract(tag, UUIDTag.class);
    }

    @Override
    public <T extends MetaTag> @NotNull T extract(@NotNull T def) {
        @SuppressWarnings("unchecked") Class<T> clazz = (Class<T>) def.getClass();
        MetaTag t = extract(def.getTag());

        if (!clazz.isInstance(t)) {
            insert(def);
            return def;
        }
        return clazz.cast(t);
    }

    @Override
    public @NotNull StringTag createString(@NotNull String tag) {
        StringTag value = extract(tag, StringTag.class);
        if (value != null)
            return value;

        value = new StringTag(tag, "");
        insert(value);
        return value;
    }

    @Override
    public @NotNull BooleanTag createBoolean(@NotNull String tag) {
        BooleanTag value = extract(tag, BooleanTag.class);
        if (value != null)
            return value;

        value = new BooleanTag(tag, false);
        insert(value);
        return value;
    }

    @Override
    public @NotNull ByteTag createByte(@NotNull String tag) {
        ByteTag value = extract(tag, ByteTag.class);
        if (value != null)
            return value;

        value = new ByteTag(tag, (byte) 0);
        insert(value);
        return value;
    }

    @Override
    public @NotNull ShortTag createShort(@NotNull String tag) {
        ShortTag value = extract(tag, ShortTag.class);
        if (value != null)
            return value;

        value = new ShortTag(tag, (short) 0);
        insert(value);
        return value;
    }

    @Override
    public @NotNull CharTag createChar(@NotNull String tag) {
        CharTag value = extract(tag, CharTag.class);
        if (value != null)
            return value;

        value = new CharTag(tag, ' ');
        insert(value);
        return value;
    }

    @Override
    public @NotNull IntTag createInt(@NotNull String tag) {
        IntTag value = extract(tag, IntTag.class);
        if (value != null)
            return value;

        value = new IntTag(tag, 0);
        insert(value);
        return value;
    }

    @Override
    public @NotNull LongTag createLong(@NotNull String tag) {
        LongTag value = extract(tag, LongTag.class);
        if (value != null)
            return value;

        value = new LongTag(tag, 0L);
        insert(value);
        return value;
    }

    @Override
    public @NotNull FloatTag createFloat(@NotNull String tag) {
        FloatTag value = extract(tag, FloatTag.class);
        if (value != null)
            return value;

        value = new FloatTag(tag, 0f);
        insert(value);
        return value;
    }

    @Override
    public @NotNull DoubleTag createDouble(@NotNull String tag) {
        DoubleTag value = extract(tag, DoubleTag.class);
        if (value != null)
            return value;

        value = new DoubleTag(tag, 0d);
        insert(value);
        return value;
    }

    @Override
    public @NotNull ArrayTag createArray(@NotNull String tag) {
        ArrayTag value = extract(tag, ArrayTag.class);
        if (value != null)
            return value;

        value = new ArrayTag<>(tag, new MetaData[0]);
        insert(value);
        return value;
    }

    @Override
    public @NotNull ListTag createList(@NotNull String tag) {
        ListTag value = extract(tag, ListTag.class);
        if (value != null)
            return value;

        value = new ListTag<>(tag);
        insert(value);
        return value;
    }

    @Override
    public @NotNull SetTag createSet(@NotNull String tag) {
        SetTag value = extract(tag, SetTag.class);
        if (value != null)
            return value;

        value = new SetTag(tag);
        insert(value);
        return value;
    }

    @Override
    public @NotNull MapTag createMap(@NotNull String tag) {
        MapTag value = extract(tag, MapTag.class);
        if (value != null)
            return value;

        value = new MapTag<>(tag);
        insert(value);
        return value;
    }

    @Override
    public @NotNull DocTag createDoc(@NotNull String tag) {
        DocTag value = extract(tag, DocTag.class);
        if (value != null)
            return value;

        value = new DocTag(tag);
        insert(value);
        return value;
    }

    @Override
    public @NotNull UUIDTag createUUID(@NotNull String tag) {
        UUIDTag value = extract(tag, UUIDTag.class);
        if (value != null)
            return value;

        value = new UUIDTag(tag, UUID.randomUUID());
        insert(value);
        return value;
    }

    @Override
    public @Nullable String valueString(@NotNull String tag) {
        StringTag value = extract(tag, StringTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Boolean valueBoolean(@NotNull String tag) {
        BooleanTag value = extract(tag, BooleanTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Byte valueByte(@NotNull String tag) {
        ByteTag value = extract(tag, ByteTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Short valueShort(@NotNull String tag) {
        ShortTag value = extract(tag, ShortTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Character valueChar(@NotNull String tag) {
        CharTag value = extract(tag, CharTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Integer valueInt(@NotNull String tag) {
        IntTag value = extract(tag, IntTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Long valueLong(@NotNull String tag) {
        LongTag value = extract(tag, LongTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Float valueFloat(@NotNull String tag) {
        FloatTag value = extract(tag, FloatTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Double valueDouble(@NotNull String tag) {
        DoubleTag value = extract(tag, DoubleTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable MetaData[] valueArray(@NotNull String tag) {
        ArrayTag value = extract(tag, ArrayTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public <T extends MetaData> @Nullable T[] valueArray(@NotNull String tag, @NotNull Class<T> clazz) {
        ArrayTag<T> value = extractArray(tag, clazz);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable List valueList(@NotNull String tag) {
        ListTag value = extract(tag, ListTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable <T extends MetaData> List<T> valueList(@NotNull String tag, @NotNull Class<T> clazz) {
        ListTag<T> value = extractList(tag, clazz);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Set valueSet(@NotNull String tag) {
        SetTag value = extract(tag, SetTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable <T extends MetaData> Set<T> valueSet(@NotNull String tag, @NotNull Class<T> clazz) {
        SetTag<T> value = extractSet(tag, clazz);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable Map valueMap(@NotNull String tag) {
        MapTag value = extract(tag, MapTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable <Key extends MetaElement, Value extends MetaElement> Map<Key, Value> valueMap(
            @NotNull String tag, @NotNull Class<Key> keyClass, @NotNull Class<Value> valueClass) {
        MapTag<Key, Value> value = extractMap(tag, keyClass, valueClass);
        return value != null ? value.get() : null;
    }

    @Override
    public @Nullable UUID valueUUID(@NotNull String tag) {
        UUIDTag value = extract(tag, UUIDTag.class);
        return value != null ? value.get() : null;
    }

    @Override
    public @NotNull String value(@NotNull String tag, @NotNull String def) {
        StringTag value = extract(tag, StringTag.class);
        if (value == null) {
            value = new StringTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Boolean value(@NotNull String tag, @NotNull Boolean def) {
        BooleanTag value = extract(tag, BooleanTag.class);
        if (value == null) {
            value = new BooleanTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Byte value(@NotNull String tag, @NotNull Byte def) {
        ByteTag value = extract(tag, ByteTag.class);
        if (value == null) {
            value = new ByteTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Short value(@NotNull String tag, @NotNull Short def) {
        ShortTag value = extract(tag, ShortTag.class);
        if (value == null) {
            value = new ShortTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Character value(@NotNull String tag, @NotNull Character def) {
        CharTag value = extract(tag, CharTag.class);
        if (value == null) {
            value = new CharTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Integer value(@NotNull String tag, @NotNull Integer def) {
        IntTag value = extract(tag, IntTag.class);
        if (value == null) {
            value = new IntTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Long value(@NotNull String tag, @NotNull Long def) {
        LongTag value = extract(tag, LongTag.class);
        if (value == null) {
            value = new LongTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Float value(@NotNull String tag, @NotNull Float def) {
        FloatTag value = extract(tag, FloatTag.class);
        if (value == null) {
            value = new FloatTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @Override
    public @NotNull Double value(@NotNull String tag, @NotNull Double def) {
        DoubleTag value = extract(tag, DoubleTag.class);
        if (value == null) {
            value = new DoubleTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T extends MetaData> T[] value(@NotNull String tag, @NotNull T[] def) {
        ArrayTag<T> value;
        try {
            value = (ArrayTag<T>) extract(tag, ArrayTag.class);
        } catch (ClassCastException e) {
            remove(tag);
            value = null;
        }

        if (value == null) {
            value = new ArrayTag<>(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T extends MetaData> List<T> value(@NotNull String tag, @NotNull List<T> def) {
        ListTag<T> value;
        try {
            value = (ListTag<T>) extract(tag, ListTag.class);
        } catch (ClassCastException e) {
            remove(tag);
            value = null;
        }

        if (value == null) {
            value = new ListTag<>(tag);
            value.addAll(def);
            insert(value);
        }
        return value.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T extends MetaData> Set<T> value(@NotNull String tag, @NotNull Set<T> def) {
        SetTag<T> value;
        try {
            value = (SetTag<T>) extract(tag, SetTag.class);
        } catch (ClassCastException e) {
            remove(tag);
            value = null;
        }

        if (value == null) {
            value = new SetTag<>(tag);
            value.addAll(def);
            insert(value);
        }
        return value.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <Key extends MetaElement, Value extends MetaElement> Map<Key, Value> value(
            @NotNull String tag, @NotNull Map<Key, Value> def) {
        MapTag<Key, Value> value;
        try {
            value = (MapTag<Key, Value>) extract(tag, MapTag.class);
        } catch (ClassCastException e) {
            remove(tag);
            value = null;
        }

        if (value == null) {
            value = new MapTag<>(tag);
            value.putAll(def);
            insert(value);
        }
        return value.get();
    }

    @Override
    public @NotNull UUID value(@NotNull String tag, @NotNull UUID def) {
        UUIDTag value = extract(tag, UUIDTag.class);
        if (value == null) {
            value = new UUIDTag(tag, def);
            insert(value);
            return def;
        }
        return value.get();
    }
}
