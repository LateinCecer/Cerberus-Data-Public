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

import com.cerberustek.exception.UnknownDiscriminatorException;

import java.util.Collection;

public interface DiscriminatorMap {

    <T extends MetaData> void registerData(Class<T> clazz, MetaBuilder<T> builder, short discriminator);
    void unregisterData(Class<? extends MetaData> clazz);

    short getDiscriminator(Class<? extends MetaData> clazz);

    MetaBuilder getBuilder(short discriminator) throws UnknownDiscriminatorException;

    Collection<Class<? extends MetaData>> getRegisteredData();
}
