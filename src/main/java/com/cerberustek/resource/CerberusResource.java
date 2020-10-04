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

package com.cerberustek.resource;

import com.cerberustek.CerberusData;
import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.exception.UnknownDiscriminatorException;
import com.cerberustek.service.CerberusService;
import com.cerberustek.utils.DiscriminatorFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class CerberusResource implements CerberusService {

    private static final String DISCRIMINATOR_PATH = "discriminators.cdd";

    private DiscriminatorFile discriminatorFile;
    private DiscriminatorMap discriminatorMap;

    @Override
    public void start() {
        System.out.println("Reading discriminators...");
        try {
            File file = new File(DISCRIMINATOR_PATH);
            discriminatorFile = new DiscriminatorFile(file);
            if (file.exists()) {
                discriminatorMap = CerberusData.genEmptyDiscriminators();
                discriminatorFile.read(discriminatorMap);
            } else {
                discriminatorMap = CerberusData.genDefaultDiscriminators();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Writing discriminators...");
        try {
            discriminatorFile.write(discriminatorMap);
        } catch (IOException | UnknownDiscriminatorException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the discriminator map.
     * @return discriminator map
     */
    public DiscriminatorMap getDiscriminatorMap() {
        return discriminatorMap;
    }

    @Override
    public Class<? extends CerberusService> serviceClass() {
        return CerberusResource.class;
    }

    @Override
    public Collection<Thread> getThreads() {
        return new HashSet<>();
    }
}
