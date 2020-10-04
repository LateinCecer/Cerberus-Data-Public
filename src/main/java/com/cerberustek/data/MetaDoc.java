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

package com.cerberustek.data;

import com.cerberustek.data.impl.tags.*;
import com.cerberustek.querry.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface MetaDoc extends MetaData, Iterable<MetaTag>, ResourceLocation {

    MetaDoc clear();

    boolean contains(String tag);

    int size();

    MetaDoc insert(MetaTag data);
    MetaDoc remove(String tag);

    MetaTag extract(String tag);
    @Nullable <T extends MetaTag> T extract(@NotNull String tag, @NotNull Class<T> clazz);
    @NotNull <T extends MetaTag> T extract(@NotNull T def);

    @Nullable StringTag extractString(@NotNull String tag);
    @Nullable BooleanTag extractBoolean(@NotNull String tag);
    @Nullable ByteTag extractByte(@NotNull String tag);
    @Nullable ShortTag extractShort(@NotNull String tag);
    @Nullable CharTag extractChar(@NotNull String tag);
    @Nullable IntTag extractInt(@NotNull String tag);
    @Nullable LongTag extractLong(@NotNull String tag);
    @Nullable FloatTag extractFloat(@NotNull String tag);
    @Nullable DoubleTag extractDouble(@NotNull String tag);
    @Nullable ArrayTag extractArray(@NotNull String tag);
    @Nullable <T extends MetaData> ArrayTag<T> extractArray(@NotNull String tag, @NotNull Class<T> clazz);
    @Nullable ListTag extractList(@NotNull String tag);
    @Nullable <T extends MetaData> ListTag<T> extractList(@NotNull String tag, @NotNull Class<T> clazz);
    @Nullable SetTag extractSet(@NotNull String tag);
    @Nullable <T extends MetaData> SetTag<T> extractSet(@NotNull String tag, @NotNull Class<T> clazz);
    @Nullable MapTag extractMap(@NotNull String tag);
    @Nullable <Key extends MetaElement, Value extends MetaElement> MapTag<Key, Value> extractMap(@NotNull String tag,
                                                                                                 @NotNull Class<Key> keyClass,
                                                                                                 @NotNull Class<Value> valueClass);
    @Nullable DocTag extractDoc(@NotNull String tag);
    @Nullable UUIDTag extractUUID(@NotNull String tag);

    @NotNull StringTag createString(@NotNull String tag);
    @NotNull BooleanTag createBoolean(@NotNull String tag);
    @NotNull ByteTag createByte(@NotNull String tag);
    @NotNull ShortTag createShort(@NotNull String tag);
    @NotNull CharTag createChar(@NotNull String tag);
    @NotNull IntTag createInt(@NotNull String tag);
    @NotNull LongTag createLong(@NotNull String tag);
    @NotNull FloatTag createFloat(@NotNull String tag);
    @NotNull DoubleTag createDouble(@NotNull String tag);
    @NotNull ArrayTag createArray(@NotNull String tag);
    @NotNull ListTag createList(@NotNull String tag);
    @NotNull SetTag createSet(@NotNull String tag);
    @NotNull MapTag createMap(@NotNull String tag);
    @NotNull DocTag createDoc(@NotNull String tag);
    @NotNull UUIDTag createUUID(@NotNull String tag);

    @Nullable String valueString(@NotNull String tag);
    @Nullable Boolean valueBoolean(@NotNull String tag);
    @Nullable Byte valueByte(@NotNull String tag);
    @Nullable Short valueShort(@NotNull String tag);
    @Nullable Character valueChar(@NotNull String tag);
    @Nullable Integer valueInt(@NotNull String tag);
    @Nullable Long valueLong(@NotNull String tag);
    @Nullable Float valueFloat(@NotNull String tag);
    @Nullable Double valueDouble(@NotNull String tag);
    @Nullable MetaData[] valueArray(@NotNull String tag);
    @Nullable <T extends MetaData> T[] valueArray(@NotNull String tag, @NotNull Class<T> clazz);
    @Nullable List valueList(@NotNull String tag);
    @Nullable <T extends MetaData> List<T> valueList(@NotNull String tag, @NotNull Class<T> clazz);
    @Nullable Set valueSet(@NotNull String tag);
    @Nullable <T extends MetaData> Set<T> valueSet(@NotNull String tag, @NotNull Class<T> clazz);
    @Nullable Map valueMap(@NotNull String tag);
    @Nullable <Key extends MetaElement, Value extends MetaElement> Map<Key, Value> valueMap(@NotNull String tag,
                                                                                            @NotNull Class<Key> keyClass,
                                                                                            @NotNull Class<Value> valueClass);
    @Nullable UUID valueUUID(@NotNull String tag);

    @NotNull String value(@NotNull String tag, @NotNull String def);
    @NotNull Boolean value(@NotNull String tag, @NotNull Boolean def);
    @NotNull Byte value(@NotNull String tag, @NotNull Byte def);
    @NotNull Short value(@NotNull String tag, @NotNull Short def);
    @NotNull Character value(@NotNull String tag, @NotNull Character def);
    @NotNull Integer value(@NotNull String tag, @NotNull Integer def);
    @NotNull Long value(@NotNull String tag, @NotNull Long def);
    @NotNull Float value(@NotNull String tag, @NotNull Float def);
    @NotNull Double value(@NotNull String tag, @NotNull Double def);
    @NotNull <T extends MetaData> T[] value(@NotNull String tag, @NotNull T[] def);
    @NotNull <T extends MetaData> List<T> value(@NotNull String tag, @NotNull List<T> def);
    @NotNull <T extends MetaData> Set<T> value(@NotNull String tag, @NotNull Set<T> def);
    @NotNull <Key extends MetaElement, Value extends MetaElement> Map<Key, Value> value(@NotNull String tag,
                                                                                        @NotNull Map<Key, Value> def);
    @NotNull UUID value(@NotNull String tag, @NotNull UUID def);
}
