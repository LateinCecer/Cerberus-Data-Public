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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class PrimeUtil {

    private final ArrayList<BigInteger> primes = new ArrayList<>();

    @SuppressWarnings("DuplicatedCode")
    public BigInteger nextPrime(BigInteger number) {
        final BigInteger two = BigInteger.valueOf(2);
        // Filter 2 as the first prime to avoid having to
        // add it to the prime list, thus cutting processing
        // time in half
        if (number.equals(two))
            return two;
        if (number.mod(two).equals(BigInteger.ZERO))
            number = number.add(BigInteger.ONE);
        if (number.compareTo(two) < 0)
            return two;

        Find : for (;; number = number.add(two)) {

            // Add primes to prime list if necessary
            BigInteger S = sqrt(number);
            BigInteger lastCheck = primes.isEmpty() ? BigInteger.valueOf(3) : primes.get(primes.size() - 1).add(two);
            PrimeLoop : for (; lastCheck.compareTo(S) <= 0; lastCheck = lastCheck.add(two)) {

                // Use sieve of Eratosthenes
                for (BigInteger e : primes) {
                    if (e.multiply(e).compareTo(lastCheck) > 0) {
                        // lastChecked is not excluded by any following primes
                        // in the prime list -> continue with the next value
                        break;
                    }

                    if (lastCheck.mod(e).equals(BigInteger.ZERO))
                        continue PrimeLoop;
                }
                // System.out.println("Adding prime: " + lastCheck);
                primes.add(lastCheck);

                /*System.out.println("sieve construction: " + ((float) (BigInteger.valueOf(primes.size())
                        .multiply(BigInteger.valueOf(1000000)).divide(S)).intValue() / 10000f) + "%");*/
            }

            // Use sieve of Eratosthenes
            for (BigInteger prime : primes) {
                if (prime.multiply(prime).compareTo(number) > 0) // Can break here, because all primes
                    return number; // to come from this point on will not exclude number as a prime

                if (number.mod(prime).equals(BigInteger.ZERO))
                    continue Find; // number is not a prime. Continue the search
            }
            return number;
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public boolean isPrime(BigInteger number) {
        BigInteger two = BigInteger.valueOf(2);

        if (number.equals(two))
            return true;
        if (number.mod(two).equals(BigInteger.ZERO))
            return false;
        if (number.compareTo(two) < 0)
            return false;

        // Add primes to prime list if necessary
        BigInteger S = sqrt(number);
        BigInteger lastCheck = primes.isEmpty() ? BigInteger.valueOf(3) : primes.get(primes.size() - 1).add(two);
        PrimeLoop : for (; lastCheck.compareTo(S) <= 0; lastCheck = lastCheck.add(two)) {

            // Use sieve of Eratosthenes
            for (BigInteger e : primes) {
                if (e.multiply(e).compareTo(lastCheck) > 0)
                    break;

                if (lastCheck.mod(e).equals(BigInteger.ZERO)) {
                    continue PrimeLoop;

                }
            }
            // System.out.println("Adding prime: " + lastCheck);
            primes.add(lastCheck);
        }

        // Use sieve of Eratosthenes
        for (BigInteger prime : primes) {
            if (prime.multiply(prime).compareTo(number) > 0)
                return true;

            if (number.mod(prime).equals(BigInteger.ZERO))
                return false;
        }
        return true;
    }

    /**
     * Generates a random number generator for big integer numbers.
     *
     * The minimal length of the random number is <code>minLength</code>.
     * Parameters a, b and start are free to choose from, all be it,
     * that a must not be zero and minLength must be greater than
     * one!
     * @param start start parameter
     * @param a random parameter a
     * @param b random parameter b
     * @param minLength minimal length
     * @return random number generator for big numbers
     */
    public BigRandomGenerator genRandom(BigInteger start, BigInteger a, BigInteger b, BigInteger minLength) {
        if (a.equals(BigInteger.ZERO))
            throw new IllegalArgumentException("Parameter a must be different from zero!");
        if (minLength.compareTo(BigInteger.ONE) < 0)
            throw new IllegalArgumentException("Minimal Length must be greater than one!");

        minLength = nextPrime(minLength);
        return new BigRandomGenerator(start, a.abs().multiply(minLength).subtract(BigInteger.ONE),
                b.abs().multiply(minLength).subtract(BigInteger.ONE), minLength);
    }

    public List<BigInteger> getSieve() {
        return primes;
    }

    public static BigInteger createBigInteger(int smallValue) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.rewind();
        buffer.putInt(smallValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return new BigInteger(buffer.array());
    }

    public static BigInteger createBigInteger(long smallValue) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.rewind();
        buffer.putLong(smallValue);
        buffer.order(ByteOrder.BIG_ENDIAN);
        return new BigInteger(buffer.array());
    }

    /**
     * Big integer square root function for java versions
     * below 9.0:
     * @param x value
     * @return sqrt
     */
    public static BigInteger sqrt(BigInteger x) {
        /*
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;

        for (;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2))
                return y;
            div2 = div;
            div = y;
        }
        */

        if (x.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalArgumentException("The square-root of a negative number is complex!");
        if (x.equals(BigInteger.ZERO) || x.equals(BigInteger.ONE))
            return x;

        BigInteger two = BigInteger.valueOf(2);
        BigInteger y;

        //noinspection StatementWithEmptyBody
        for (y = x.divide(two);
                y.compareTo(x.divide(y)) > 0;
            y = ((x.divide(y)).add(y)).divide(two));
        return y;
    }
}
