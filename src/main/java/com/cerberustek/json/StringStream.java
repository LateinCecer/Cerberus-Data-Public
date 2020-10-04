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

package com.cerberustek.json;

public class StringStream {

    private final String src;

    private int index;
    private int mark;

    public StringStream(String src) {
        this.src = src;
        mark = index = 0;
    }

    /**
     * Returns the char at the current index position of the
     * string stream.
     *
     * If the current read index does not contain a valid char,
     * this method will throw an IllegalStateException.
     *
     * @return char at index
     * @throws IllegalStateException An Exception throws, if the
     *              position of the current read index does not
     *              contain a valid character
     */
    public char getChar() {
        if (isValid())
            return src.charAt(index);
        else
            throw new IllegalStateException("End of string!");
    }

    public int getLine(int position) {
        int count = 0;
        for (int i = 0; i < position; i++) {
            if (src.charAt(i) == '\n')
                count++;
        }
        return count;
    }

    /**
     * Will advance the read index of the string stream by one
     */
    public void advance() {
        index++;
    }

    /**
     * Will reverse the read index of the string stream by one
     */
    public void reverse() {
        index--;
    }

    /**
     * Will reset the read index to zero
     */
    public void rewind() {
        index = 0;
    }

    /**
     * Returns true, if the current read position contains a
     * valid char
     *
     * @return is position valid
     */
    public boolean isValid() {
        return index >= 0 && index < src.length();
    }

    /**
     * Will set the mark position to the current read index
     */
    public void mark() {
        mark = index;
    }

    /**
     * Will reset the current read index to the last marked
     * position
     */
    public void reset() {
        index = mark;
    }

    /**
     * Returns the current read index
     * @return read index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the currently marked index
     * @return marked index
     */
    public int getMark() {
        return mark;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}
