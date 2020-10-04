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

package com.cerberustek.utils;

import com.cerberustek.CerberusData;
import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaBuilder;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.impl.DiscriminatorMapImpl;
import com.cerberustek.exception.UnknownDiscriminatorException;

import java.io.*;

public class DiscriminatorFile {

    private final File file;

    public DiscriminatorFile(File file) {
        this.file = file;
    }

    public void writeDefault() throws IOException, UnknownDiscriminatorException {
        write(CerberusData.genDefaultDiscriminators());
    }

    public DiscriminatorMap write(DiscriminatorMap map) throws IOException, UnknownDiscriminatorException {
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));
        for (Class<? extends MetaData> data : map.getRegisteredData()) {
            short discriminator = map.getDiscriminator(data);
            MetaBuilder builder = map.getBuilder(discriminator);
            outputStream.writeUTF(data.getName());
            outputStream.writeUTF(builder.getClass().getName());
            outputStream.writeShort(discriminator);
        }
        outputStream.close();
        return map;
    }

    public DiscriminatorMap read(DiscriminatorMap map) throws FileNotFoundException {
        DataInputStream inputStream = new DataInputStream(new FileInputStream(file));
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        boolean reading = true;

        while (reading) {
            try {
                String className = inputStream.readUTF();
                String classBuilder = inputStream.readUTF();
                short discriminator = inputStream.readShort();

                Class dataClass = classLoader.loadClass(className);
                Class builderClass = classLoader.loadClass(classBuilder);
                MetaBuilder builder = (MetaBuilder) builderClass.newInstance();
                //noinspection unchecked
                map.registerData(dataClass, builder, discriminator);
            } catch (IOException e) {
                reading = false;
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                System.err.println("Could not read discriminator map entry!");
            }
        }
        return map;
    }

    public DiscriminatorMap read() throws FileNotFoundException {
        return read(new DiscriminatorMapImpl());
    }

    public File getFile() {
        return file;
    }
}
