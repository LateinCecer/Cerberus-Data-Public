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

import com.cerberustek.data.impl.elements.*;
import com.cerberustek.data.impl.tags.*;
import com.cerberustek.json.impl.*;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaTag;
import com.cerberustek.exception.JSONFormatException;

import java.io.*;
import java.util.HashSet;

public class JSONUtil {

    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private static final String NULL = "null";

    private static final HashSet<Character> numberCharset = createNumberCharset();

    public static JSONElement fromString(String raw) throws JSONFormatException {
        StringStream stream = new StringStream(raw);
        return readElement(stream);
    }

    public static JSONElement fromStream(InputStream inputStream) throws JSONFormatException, IOException {
        return fromString(readFully(inputStream));
    }

    public static JSONElement fromFile(File file) throws JSONFormatException, IOException {
        return fromStream(new FileInputStream(file));
    }

    public static JSONElement fromFile(String path) throws JSONFormatException, IOException {
        return fromString(readFully(new FileInputStream(path)));
    }

    public static JSONElement readElement(StringStream inputStream) throws JSONFormatException {
        String tag = null;

        char c;
        while (true) {
            c = inputStream.getChar();

            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                inputStream.advance();
                continue;
            }

            if (c == JSONReader.STRING) {
                // read string
                String string = readString(inputStream);
                // System.out.println("Read string: " + string);

                // skip ahead to the next relevant char
                traverseEmpty(inputStream);

                c = inputStream.getChar();
                if (c == JSONReader.VALUE) {
                    // The string that was just read is a tag
                    if (tag == null)
                        tag = string;
                    else
                        throw new JSONFormatException("Unexpected character: " + c
                                + " at position: " + inputStream.getIndex());

                    // read the rest of the value
                    inputStream.advance();
                } else {
                    // inputStream.advance();
                    // The string that was just read is a string value
                    if (tag != null) {
                        // create new JSON String object with a tag
                        return new JSONTagString(tag, string);
                    } else {
                        // create new JSON String object without a tag
                        return new JSONString(string);
                    }
                }

            } else if (c == JSONReader.BEGIN_LIST) {
                // read array
                JSONArray array;
                if (tag != null) {
                    // create array object with tag
                    array = new JSONTagArray(tag);
                } else {
                    // create array object without tag
                    array = new JSONArray();
                }
                array.readContents(inputStream);
                return array;

            } else if (c == JSONReader.BEGIN_OBJ) {
                // read object
                JSONObject object;
                if (tag != null) {
                    // create object with tag
                    object = new JSONTagObject(tag);
                } else {
                    // create object without tag
                    object = new JSONObject();
                }
                object.readContents(inputStream);
                return object;

            } else if (numberCharset.contains(c) || c == '-' || c == '+') {
                // read integer
                Number n = readNumber(inputStream);

                if (tag != null) {
                    if (n instanceof Integer)
                        return new JSONTagInteger(tag, (Integer) n);
                    if (n instanceof Long)
                        return new JSONTagLong(tag, (Long) n);
                    if (n instanceof Double)
                        return new JSONTagDouble(tag, (Double) n);
                } else {
                    if (n instanceof Integer)
                        return new JSONInteger((Integer) n);
                    if (n instanceof Long)
                        return new JSONLong((Long) n);
                    if (n instanceof Double)
                        return new JSONDouble((Double) n);
                }
            } else {

                inputStream.mark();
                try {
                    boolean b = readBoolean(inputStream);

                    if (tag != null)
                        return new JSONTagBoolean(tag, b);
                    else
                        return new JSONBoolean(b);
                } catch (JSONFormatException e) {
                    inputStream.reset();
                }

                if (checkSequenceIgnoreCase(inputStream, NULL)) {
                    // sequence is a null object

                    if (tag != null)
                        return new JSONTagObject(tag);
                    else
                        return new JSONObject();
                } else
                    inputStream.reset();

                throw new JSONFormatException("Unknown json data type, at " + inputStream.getIndex()
                        + ", line: " + inputStream.getLine(inputStream.getIndex()));
            }
        }
    }

    /**
     * Will read a formatted string from the input stream.
     *
     * Formatted in this sense means, that the string has
     * to be surrounded by quotation marks: "example".
     * This method will not move the marker of the
     * StringStream.
     *
     * @param inputStream input stream
     * @return formatted string
     * @throws JSONFormatException exception that is thrown,
     *          when no string with the expected format could
     *          be found
     */
    public static String readString(StringStream inputStream) throws JSONFormatException {
        boolean readMode = false;
        StringBuilder output = new StringBuilder();

        char prev;
        char c = ' ';
        while (true) {
            prev = c;
            c = inputStream.getChar();

            if (readMode) {

                inputStream.advance();

                if (c == JSONReader.STRING && prev != '\\')
                    return output.toString();
                else
                    output.append(c);

            } else {
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    inputStream.advance();
                    continue;
                }

                if (c == JSONReader.STRING) {
                    inputStream.advance();
                    readMode = true;
                } else
                    throw new JSONFormatException("Unexpected character: " + c + ", line: " + inputStream.getLine(inputStream.getIndex()));
            }
        }
    }

    /**
     * Will read a number from the string stream.
     *
     * For Integers, this method will choose if the
     * number that is read can fit inside a 32-bit
     * integer, or if it requires a 64-bit integer.
     * For floating point numbers, this will always
     * choose double precision as it is the norm
     * with the JSON-file format.
     * Allowed keywords are:
     * - "NaN": Not a number
     * - "Inf": positive infinity
     * - "nInf": negative infinity
     *
     * This method will not move the marker of the
     * StringStream.
     *
     * @param inputStream input String stream
     * @return number
     * @throws JSONFormatException Exception that gets
     *          thrown if no number of the expected
     *          format could be found.
     */
    public static Number readNumber(StringStream inputStream) throws JSONFormatException {
        boolean readMode = false;
        boolean flag = false;
        boolean floatingPoint = false;
        StringBuilder output = new StringBuilder();

        char c;
        while (true) {
            c = inputStream.getChar();

            if (readMode) {

                if (c == '.') {
                    if (floatingPoint)
                        throw new JSONFormatException("Invalid Number Format \"" + output.toString() + ".\"");
                    floatingPoint = true;
                }
                if (numberCharset.contains(c)) {
                    output.append(c);
                    inputStream.advance();
                } else if (flag) {
                    output.append(c);
                    inputStream.advance();
                    flag = false;
                } else
                    break;

            } else {

                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    inputStream.advance();
                    continue;
                }

                if (numberCharset.contains(c)) {
                    readMode = true;
                } else if (c == '-' || c == '+') {
                    readMode = true;
                    flag = true;
                } else {
                    int index = inputStream.getIndex();

                    if (checkSequence(inputStream, "NaN")) {
                        inputStream.advance();
                        return Double.NaN;
                    }
                    inputStream.setIndex(index);
                    if (checkSequence(inputStream, "nInf")) {
                        inputStream.advance();
                        return Double.NEGATIVE_INFINITY;
                    }
                    inputStream.setIndex(index);
                    if (checkSequence(inputStream, "Inf")) {
                        inputStream.advance();
                        return Double.POSITIVE_INFINITY;
                    }
                    inputStream.setIndex(index);

                    throw new JSONFormatException("Unexpected character: " + c);
                }
            }
        }

        String rawString = output.toString();
        if (floatingPoint) {
            try {
                return Double.parseDouble(rawString);
            } catch (NumberFormatException e) {
                throw new JSONFormatException("Invalid Number Format \"" + rawString + "\"");
            }
        } else {
            try {
                // Decide if the integer number should have 64- or 32-bits
                long l = Long.parseLong(rawString);
                Integer i = Integer.parseInt(rawString);

                if (l == (long) i)
                    return i;
                else
                    return l;
            } catch (NumberFormatException e) {
                throw new JSONFormatException("Invalid Number Format \"" + rawString + "\"");
            }
        }
    }

    /**
     * Will read a boolean value form the StringStream
     * input.
     *
     * Doing so, this method will specifically look for
     * the values "true" and "false" and is <bold>not</bold>
     * case sensitive!
     * This method will not move the marker of the
     * StringStream.
     *
     * @param stringStream input String stream
     * @return read boolean value
     * @throws JSONFormatException Exception that gets
     *          thrown, if the read value could not be
     *          converted into a boolean value
     *          (either "true" or "false", non case
     *          sensitive)
     */
    public static Boolean readBoolean(StringStream stringStream) throws JSONFormatException {
        // get rid of empty spaces
        traverseEmpty(stringStream);

        int index = stringStream.getIndex();
        if (checkSequenceIgnoreCase(stringStream, FALSE)) {
            stringStream.advance();
            return false;
        }
        stringStream.setIndex(index);

        if (checkSequenceIgnoreCase(stringStream, TRUE)) {
            stringStream.advance();
            return true;
        }
        stringStream.setIndex(index);
        throw new JSONFormatException("Invalid Boolean format!");
    }

    /**
     * Will advance all the way through "empty" chars.
     *
     * "Empty" chars are spaces ' ', line separations
     * '\n' and tabs '\t'.
     *
     * @param stringStream stream to advance
     */
    public static void traverseEmpty(StringStream stringStream) {
        while (stringStream.getChar() == ' ' || stringStream.getChar() == '\t' || stringStream.getChar() == '\n'
                    || stringStream.getChar() == '\r')
            stringStream.advance();
    }

    private static HashSet<Character> createNumberCharset() {
        HashSet<Character> out = new HashSet<>();
        out.add('0');
        out.add('1');
        out.add('2');
        out.add('3');
        out.add('4');
        out.add('5');
        out.add('6');
        out.add('7');
        out.add('8');
        out.add('9');
        out.add('.');
        return out;
    }

    public static boolean checkSequence(StringStream inputStream, String checkSequence) {
        char[] chars = checkSequence.toCharArray();
        for (char aChar : chars) {
            if (!inputStream.isValid())
                return false;

            if (inputStream.getChar() != aChar)
                return false;
            inputStream.advance();
        }
        return true;
    }

    public static boolean checkSequenceIgnoreCase(StringStream inputStream, String checkSequence) {
        char[] chars = checkSequence.toCharArray();
        for (char aChar : chars) {
            if (!inputStream.isValid())
                return false;

            if (Character.toLowerCase(inputStream.getChar()) != Character.toLowerCase(aChar))
                return false;
            inputStream.advance();
        }
        return true;
    }

    public static String readFully(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        byte[] buffer = new byte[4096];

        for (int i = inputStream.read(buffer); i != -1; i = inputStream.read(buffer))
            builder.append(new String(buffer, 0, i));

        return builder.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static JSONElement fromMeta(MetaData data) throws JSONFormatException {
        if (!(data instanceof MetaTag)) {

            if (data instanceof IntElement) {
                return new JSONInteger(((IntElement) data).get());
            } else if (data instanceof LongElement) {
                return new JSONLong(((LongElement) data).get());
            } else if (data instanceof ByteElement) {
                return new JSONInteger(((ByteElement) data).get());
            } else if (data instanceof ShortElement) {
                return new JSONInteger(((ShortElement) data).get());
            } else if (data instanceof CharElement) {
                return new JSONString(String.valueOf(((CharElement) data).get()));
            } else if (data instanceof FloatElement) {
                return new JSONDouble(((FloatElement) data).get());
            } else if (data instanceof DoubleElement) {
                return new JSONDouble(((DoubleElement) data).get());
            } else if (data instanceof StringElement) {
                return new JSONString(((StringElement) data).get());
            } else if (data instanceof DocElement) {
                JSONObject obj = new JSONObject();
                ((DocElement) data).get().values().forEach(value -> {
                    try {
                        JSONElement element = fromMeta(value);
                        obj.put(value.getTag(), element);
                    } catch (JSONFormatException e) {
                        e.printStackTrace();
                    }
                });
                return obj;

            } else if (data instanceof ArrayElement) {
                JSONElement[] elements = new JSONElement[((ArrayElement) data).get().length];

                for (int i = 0; i < elements.length; i++)
                    elements[i] = fromMeta(((ArrayElement) data).get(i));

                return new JSONArray(elements);

            } else if (data instanceof ListElement) {
                JSONElement[] elements = new JSONElement[((ListElement) data).size()];

                for (int i = 0; i < elements.length; i++)
                    elements[i] = fromMeta(((ListElement<MetaData>) data).get(i));

                return new JSONArray(elements);

            } else if (data instanceof SetElement) {
                JSONElement[] elements = new JSONElement[((SetElement) data).size()];

                int counter = 0;

                for (MetaData metaData : (SetElement<MetaData>) data) {
                    elements[counter] = fromMeta(metaData);
                    counter++;
                }
                return new JSONArray(elements);

            } else if (data instanceof BooleanElement) {
                return new JSONBoolean(((BooleanElement) data).get());
            } else
                throw new JSONFormatException("Meta data of type "
                        + data.getClass() + " cannot be converted to the JSON data!");
        } else {

            if (data instanceof IntTag) {
                return new JSONTagInteger(((IntTag) data).getTag(), ((IntTag) data).get());
            } else if (data instanceof LongTag) {
                return new JSONTagLong(((LongTag) data).getTag(), ((LongTag) data).get());
            } else if (data instanceof ByteTag) {
                return new JSONTagInteger(((ByteTag) data).getTag(), ((ByteTag) data).get());
            } else if (data instanceof ShortTag) {
                return new JSONTagInteger(((ShortTag) data).getTag(), ((ShortTag) data).get());
            } else if (data instanceof CharTag) {
                return new JSONTagString(((CharTag) data).getTag(), String.valueOf(((CharTag) data).get()));
            } else if (data instanceof FloatTag) {
                return new JSONTagDouble(((FloatTag) data).getTag(), ((FloatTag) data).get());
            } else if (data instanceof DoubleTag) {
                return new JSONTagDouble(((DoubleTag) data).getTag(), ((DoubleTag) data).get());
            } else if (data instanceof StringTag) {
                return new JSONTagString(((StringTag) data).getTag(), ((StringTag) data).get());
            } else if (data instanceof DocTag) {
                JSONTagObject obj = new JSONTagObject(((DocTag) data).getTag());
                ((DocTag) data).get().values().forEach(value -> {
                    try {
                        JSONElement element = fromMeta(value);
                        obj.put(value.getTag(), element);
                    } catch (JSONFormatException e) {
                        e.printStackTrace();
                    }
                });
                return obj;

            } else if (data instanceof ArrayTag) {
                JSONElement[] elements = new JSONElement[((ArrayTag) data).get().length];

                for (int i = 0; i < elements.length; i++)
                    elements[i] = fromMeta(((ArrayElement) data).get(i));

                return new JSONTagArray(((ArrayTag) data).getTag(), elements);

            } else if (data instanceof ListTag) {
                JSONElement[] elements = new JSONElement[((ListTag) data).size()];

                for (int i = 0; i < elements.length; i++)
                    elements[i] = fromMeta(((ListTag<MetaData>) data).get(i));

                return new JSONTagArray(((ListTag) data).getTag(), elements);

            } else if (data instanceof SetTag) {
                JSONElement[] elements = new JSONElement[((SetTag) data).size()];

                int counter = 0;

                for (MetaData metaData : (SetTag<MetaData>) data) {
                    elements[counter] = fromMeta(metaData);
                    counter++;
                }
                return new JSONTagArray(((SetTag<MetaData>) data).getTag(), elements);

            } else if (data instanceof BooleanTag) {
                return new JSONTagBoolean(((BooleanTag) data).getTag(), ((BooleanTag) data).get());
            } else
                throw new JSONFormatException("Meta data of type "
                        + data.getClass() + " cannot be converted to the JSON data!");
        }
    }

    public static JSONInteger from(byte b) {
        return new JSONInteger(b);
    }

    public static JSONInteger from(short s) {
        return new JSONInteger(s);
    }

    public static JSONInteger from(int i) {
        return new JSONInteger(i);
    }

    public static JSONLong from(long l) {
        return new JSONLong(l);
    }

    public static JSONDouble form(float f) {
        return new JSONDouble(f);
    }

    public static JSONDouble from(double d) {
        return new JSONDouble(d);
    }

    public static JSONString from(char c) {
        return new JSONString(String.valueOf(c));
    }

    public static JSONString from(String s) {
        return new JSONString(s);
    }

    public static JSONObject empty() {
        return new JSONObject();
    }

    public static JSONArray array(int length) {
        return new JSONArray(length);
    }

    public static JSONTagInteger from(String tag, byte b) {
        return new JSONTagInteger(tag, b);
    }

    public static JSONTagInteger from(String tag, short s) {
        return new JSONTagInteger(tag, s);
    }

    public static JSONTagInteger from(String tag, int i) {
        return new JSONTagInteger(tag, i);
    }

    public static JSONTagLong from(String tag, long l) {
        return new JSONTagLong(tag, l);
    }

    public static JSONTagDouble from(String tag, float f) {
        return new JSONTagDouble(tag, f);
    }

    public static JSONTagDouble from(String tag, double d) {
        return new JSONTagDouble(tag, d);
    }

    public static JSONTagString from(String tag, char c) {
        return new JSONTagString(tag, String.valueOf(c));
    }

    public static JSONTagString from(String tag, String s) {
        return new JSONTagString(tag, s);
    }

    public static JSONTagObject empty(String tag) {
        return new JSONTagObject(tag);
    }

    public static JSONTagArray array(String tag, int length) {
        return new JSONTagArray(tag, length);
    }
}
