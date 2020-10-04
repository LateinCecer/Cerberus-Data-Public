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

import com.cerberustek.data.MetaData;
import com.cerberustek.exception.JSONFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public interface JSONReader {

    int READ_BUFFER_SIZE = 4096;
    char BEGIN_OBJ = '{';
    char END_OBJ = '}';
    char BEGIN_LIST = '[';
    char END_LIST = ']';
    char VALUE = ':';
    char STRING = '\"';
    char SEPERATOR = ',';

    int read(InputStream inputStream) throws IOException, JSONFormatException;
    int read(Scanner scanner) throws JSONFormatException;
    int read(String string) throws JSONFormatException;
    int read(char[] data, int off, int len) throws  JSONFormatException;
    boolean read(char c) throws JSONFormatException;

    MetaData build() throws JSONFormatException;

    void reset();
}
