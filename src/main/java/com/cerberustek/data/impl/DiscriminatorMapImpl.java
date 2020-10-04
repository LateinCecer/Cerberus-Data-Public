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
import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaData;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.util.Collection;
import java.util.HashMap;

public class DiscriminatorMapImpl implements DiscriminatorMap {

    private final HashMap<Short, MetaBuilder> builders = new HashMap<>();
    private final HashMap<Class<? extends MetaData>, Short> clazzes = new HashMap<>();

    @Override
    public <T extends MetaData> void registerData(Class<T> clazz, MetaBuilder<T> builder, short discriminator) {
        if (!clazzes.containsKey(clazz) && discriminator != CerberusData.CERBERUS_NULL) {
            clazzes.put(clazz, discriminator);
            builders.put(discriminator, builder);
        }
    }

    @Override
    public void unregisterData(Class<? extends MetaData> clazz) {
        short discriminator = getDiscriminator(clazz);
        if (discriminator != CerberusData.CERBERUS_NULL) {
            clazzes.remove(clazz);
            builders.remove(discriminator);
        }
    }

    @Override
    public short getDiscriminator(Class<? extends MetaData> clazz) {
        if (clazzes.containsKey(clazz))
            return clazzes.get(clazz);
        return CerberusData.CERBERUS_NULL;
    }

    @Override
    public MetaBuilder getBuilder(short discriminator) throws UnknownDiscriminatorException {
        MetaBuilder builder = builders.get(discriminator);
        if (builder != null)
            return builder;
        throw new UnknownDiscriminatorException(discriminator);
    }

    @Override
    public Collection<Class<? extends MetaData>> getRegisteredData() {
        return clazzes.keySet();
    }

    @Override
    public String toString() {
        return "DiscriminatorMapImpl{" +
                "builders=" + builders +
                ", clazzes=" + clazzes +
                '}';
    }
}
