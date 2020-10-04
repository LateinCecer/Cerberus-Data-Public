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

import com.cerberustek.data.MetaElement;
import com.cerberustek.data.MetaTag;

public abstract class MetaElementImpl<T> implements MetaElement<T> {

    private T value;

    public MetaElementImpl() {

    }

    public MetaElementImpl(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public String toString() {
        if (this instanceof MetaTag)
            return "[" + getClass().getName() + "]<" + ((MetaTag) this).getTag() + ">: " + get();
        return "[" + getClass().getName() + "]: " + get();
    }

    @Override
    public int hashCode() {
        if (value != null)
            return value.hashCode();
        return super.hashCode();
    }
}
