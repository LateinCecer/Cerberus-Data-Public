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

package com.cerberustek.cipher;

import java.math.BigInteger;

public class BigRandomGenerator {

    private BigInteger last;
    private BigInteger mul;
    private BigInteger add;
    private BigInteger mod;

    public BigRandomGenerator(BigInteger start, BigInteger mul, BigInteger add, BigInteger mod) {
        if (mul.equals(BigInteger.ZERO) || mod.equals(BigInteger.ZERO))
            throw new IllegalArgumentException("Invalid random generator settings");

        this.last = start;
        this.mul = mul;
        this.add = add;
        this.mod = mod;
    }

    public BigInteger next() {
        return last = (mul.multiply(last).add(add)).mod(mod);
    }
}
