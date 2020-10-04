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

package com.cerberustek.usr;

import com.cerberustek.data.DiscriminatorMap;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaInputStream;
import com.cerberustek.data.MetaOutputStream;
import com.cerberustek.data.impl.elements.SetElement;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.CerberusData;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.exception.LoadFormatException;
import com.cerberustek.exception.NoMatchingDiscriminatorException;
import com.cerberustek.exception.UnknownDiscriminatorException;
import com.cerberustek.service.CerberusService;
import com.cerberustek.settings.Settings;
import com.cerberustek.settings.impl.SettingsImpl;
import com.cerberustek.utils.DiscriminatorFile;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class UserService implements CerberusService {

    private final static String SETTINGS_PATH = "users/settings.xml";

    private final HashMap<UUID, SavablePermissionGroup> groups = new HashMap<>();
    private final HashMap<UUID, SavableUser> users = new HashMap<>();

    private Settings settings;
    private DiscriminatorMap map;

    @Override
    public void start() {
        settings = new SettingsImpl(new File(SETTINGS_PATH));
        settings.init();
        loadGroups();
    }

    @Override
    public void stop() {
        users.keySet().forEach(this::unloadUser);
        unloadGroups();
        settings.destroy();
    }

    public void loadDiscriminators() {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        DiscriminatorFile file = new DiscriminatorFile(
                new File(settings.getString("discriminators", "Encoder.cdf")));
        if (file.getFile().exists()) {
            try {
                map = file.read();
            } catch (FileNotFoundException e) {
                registry.warning("Unable to read discriminators.");
                registry.warning("Loading default discriminators.");
                map = CerberusData.genDefaultDiscriminators();
            }
        } else {
            registry.warning("Discriminator file does not exit.");
            registry.warning("Continue with default discriminators.");
            map = CerberusData.genDefaultDiscriminators();
            try {
                file.write(map);
            } catch (IOException | UnknownDiscriminatorException ignored) {}
        }
    }

    public void unloadDiscriminators() {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        DiscriminatorFile file = new DiscriminatorFile(
                new File(settings.getString("discriminators", "Encoder.cdf")));
        try {
            file.write(map);
        } catch (UnknownDiscriminatorException | IOException e) {
            registry.warning("Unable to write discriminators.");
        }
    }

    @Override
    public Class<? extends CerberusService> serviceClass() {
        return UserService.class;
    }

    @Override
    public Collection<Thread> getThreads() {
        return null;
    }

    public boolean containsGroup(UUID uuid) {
        return groups.containsKey(uuid);
    }

    public SavablePermissionGroup getGroup(UUID uuid) {
        return groups.get(uuid);
    }

    public void addGroup(SavablePermissionGroup group) {
        if (!groups.containsKey(group.getGroupId()))
            groups.put(group.getGroupId(), group);
    }

    public void removeGroup(UUID uuid) {
        groups.remove(uuid);
    }

    public boolean containsUser(UUID uuid) {
        return users.containsKey(uuid);
    }

    public SavableUser getUser(UUID uuid) {
        return users.get(uuid);
    }

    public void addUser(SavableUser user) {
        if (!users.containsKey(user.getUUID()))
            users.put(user.getUUID(), user);
    }

    public void removeUser(UUID uuid) {
        users.remove(uuid);
    }

    public void deleteUserData(UUID uuid) {
        removeUser(uuid);

        File userDir = userDir();
        if (userDir == null)
            return;

        File[] files = userDir.listFiles((dir, name) -> name.equals(uuid.toString() + ".cdf"));
        if (files == null || files.length == 0)
            return;

        for (File f : files)
            f.deleteOnExit();
    }

    public SavableUser loadUser(UUID uuid) {
        File userDir = userDir();
        if (userDir == null)
            return null;

        File[] files = userDir.listFiles((dir, name) -> name.equals(uuid.toString() + ".cdf"));
        if (files == null || files.length == 0)
            return null;

        CerberusRegistry registry = CerberusRegistry.getInstance();
        for (File file : files) {

            try (MetaInputStream inputStream = CerberusData.createInputStream(new FileInputStream(file), map)) {

                MetaData data = inputStream.readData();
                SavableUser user = new SavableUser(uuid);
                user.load(data);
                return user;

            } catch (IOException | UnknownDiscriminatorException | LoadFormatException e) {
                registry.warning("Failed to load user from file " + file + "!");
                registry.warning("Deleting invalid file...");
                file.deleteOnExit();
            }
        }
        return null;
    }

    public void unloadUser(UUID uuid) {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        SavableUser user = getUser(uuid);
        if (user == null)
            return;

        File userDir = userDir();
        if (userDir == null) {
            registry.warning("Failed to save user!");
            return;
        }

        File file = new File(userDir.getPath() + "/" + uuid.toString() + ".cdf");
        try (MetaOutputStream outputStream = CerberusData.createOutputStream(new FileOutputStream(file), map)) {

            MetaData data = user.convert();
            outputStream.writeData(data);

        } catch (IOException | NoMatchingDiscriminatorException e) {
            registry.warning("Failed to save user data for user with id: " + uuid + "!");
        }
    }

    private File userDir() {
        String path = settings.getString("user_dir", "users/usr");
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            if (!file.mkdirs()) {
                CerberusRegistry.getInstance().warning("Could not find or generate user dir!");
                return null;
            }
        }
        return file;
    }

    public void loadGroups() {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        String groupFile = settings.getString("group_file", "groups.cdf");
        try (MetaInputStream inputStream = CerberusData.createInputStream(new FileInputStream(groupFile), map)) {

            MetaData data = inputStream.readData();
            if (data instanceof SetElement) {
                @SuppressWarnings("unchecked") SetElement<DocTag> groupMeta = (SetElement<DocTag>) data;

                for (DocTag groupInfo : groupMeta) {
                    String name = groupInfo.getTag();
                    if (!groupInfo.contains("group_id")) {
                        registry.warning("Info for group " + name + " does not contain group identifier!");
                        continue;
                    }

                    UUID uuid = groupInfo.extractUUID("group_id").get();

                    SavablePermissionGroup group = new SavablePermissionGroup(uuid, name);
                    groups.put(uuid, group);
                }

                for (DocTag groupInfo : groupMeta) {
                    UUID uuid = groupInfo.extractUUID("group_id").get();

                    SavablePermissionGroup group = groups.get(uuid);
                    try {
                        group.load(groupInfo);
                    } catch (LoadFormatException e) {
                        registry.warning("Skipped group " + uuid + " during loading: " + e);
                    }
                }
                registry.debug("Loaded permission groups");
            } else
                registry.warning("Group file contains invalid data!");

        } catch (IOException e) {
            registry.warning("Unable to load group file!");
        } catch (UnknownDiscriminatorException e) {
            registry.warning("Unable to load group even tough file exists!");
        }
    }

    public void unloadGroups() {
        CerberusRegistry registry = CerberusRegistry.getInstance();

        String groupFile = settings.getString("group_file", "users/groups.cdf");
        try (MetaOutputStream outputStream = CerberusData.createOutputStream(new FileOutputStream(groupFile), map)) {

            SetElement<DocTag> dataSet = new SetElement<>();
            for (SavablePermissionGroup group : groups.values()) {

                DocTag groupData = (DocTag) group.convert();
                dataSet.add(groupData);
            }
            outputStream.writeData(dataSet);
            registry.debug("Saved permission groups");

        } catch (IOException | NoMatchingDiscriminatorException e) {
            registry.warning("Could not save groups: " + e);
        }
    }
}
