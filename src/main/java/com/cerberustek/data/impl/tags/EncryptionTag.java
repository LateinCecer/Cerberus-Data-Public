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

package com.cerberustek.data.impl.tags;

import com.cerberustek.data.impl.elements.EncryptionElement;
import com.cerberustek.cipher.CerberusCipher;
import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaTag;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EncryptionTag extends EncryptionElement implements MetaTag {

    private String tag;

    public EncryptionTag(String tag, @NotNull MetaData value, @NotNull CerberusCipher cipher,
                         @NotNull DiscriminatorMap map) throws NoMatchingDiscriminatorException {
        super(value, cipher, map);
        this.tag = tag;
    }

    public EncryptionTag(String tag, byte[] raw) {
        super(raw);
        this.tag = tag;
    }

    public EncryptionTag(String tag) {
        this.tag = tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public EncryptionElement toElement() {
        return new EncryptionElement(get());
    }

    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            assert o instanceof MetaTag;
            return Objects.equals(tag, ((MetaTag) o).getTag());
        }
        return false;
    }
}
