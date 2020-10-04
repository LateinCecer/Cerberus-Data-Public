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

import com.cerberustek.data.MetaConvertible;
import com.cerberustek.data.MetaData;
import com.cerberustek.data.MetaLoadable;
import com.cerberustek.data.impl.elements.BooleanElement;
import com.cerberustek.data.impl.elements.StringElement;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.data.impl.tags.MapTag;
import com.cerberustek.data.impl.tags.StringTag;
import com.cerberustek.data.impl.tags.UUIDTag;
import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.exception.LoadFormatException;

import java.util.Locale;
import java.util.UUID;

public class SavableUser implements User, MetaConvertible, MetaLoadable {

    private final UUID uuid;
    private final MapTag<StringElement, BooleanElement> permissions = new MapTag<>("permission");

    private DocTag payload;
    private String name;
    private Locale locale;
    private PermissionGroup group;
    private CerberusEvent eventService;

    public SavableUser(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Locale getLocal() {
        return locale;
    }

    public void setLocal(Locale locale) {
        this.locale = locale;
    }

    public DocTag getPayload() {
        return payload;
    }

    public void setPayload(DocTag payload) {
        this.payload = payload;
    }

    @Override
    public PermissionGroup getGroup() {
        return group;
    }

    @Override
    public void setPermissionGroup(PermissionGroup permissionGroup) {
        group = permissionGroup;
    }

    @Override
    public boolean hasPermission(String s) {
        if (!getEventService().executeShortEIF(new PermissionEvent(s, this, PermissionEventType.REQUEST)))
            return false;

        BooleanElement permission = permissions.get(new StringElement(s));
        if (permission != null)
            return permission.get();

        return group.hasPermission(s);
    }

    @Override
    public void grad(String s) {
        if (getEventService().executeShortEIF(new PermissionEvent(s, this, PermissionEventType.GRAD))) {

            if (permissions.containsKey(new StringElement(s)))
                permissions.replace(new StringElement(s), new BooleanElement(true));
            else
                permissions.put(new StringElement(s), new BooleanElement(true));
        }
    }

    @Override
    public void deny(String s) {
        if (getEventService().executeShortEIF(new PermissionEvent(s, this, PermissionEventType.DENY))) {

            if (permissions.containsKey(new StringElement(s)))
                permissions.replace(new StringElement(s), new BooleanElement(false));
            else
                permissions.put(new StringElement(s), new BooleanElement(false));
        }
    }

    @Override
    public void reset(String s) {
        if (getEventService().executeShortEIF(new PermissionEvent(s, this, PermissionEventType.RESET)))
            permissions.remove(new StringElement(s));
    }

    @Override
    public MetaData convert() {
        DocTag data = new DocTag(uuid.toString());

        data.insert(new UUIDTag("uuid", uuid));
        data.insert(new StringTag("locale", locale.toLanguageTag()));
        data.insert(permissions);

        if (payload != null) {
            payload.setTag("payload");
            data.insert(payload);
        }

        if (name != null)
            data.insert(new StringTag("name", name));

        if (group != null)
            data.insert(new UUIDTag("group_id", group.getGroupId()));
        return data;
    }

    @Override
    public void load(MetaData data) throws LoadFormatException {
        try {
            if (!(data instanceof DocTag))
                throw new LoadFormatException("Invalid data format for user data");

            UUIDTag uuidTag = ((DocTag) data).extractUUID("uuid");
            if (uuidTag == null)
                throw new LoadFormatException("User data with invalid id");
            if (!uuidTag.get().equals(uuid))
                throw new LoadFormatException("User id does not match data id!");

            StringTag localeTag = ((DocTag) data).extractString("locale");
            if (localeTag == null)
                throw new LoadFormatException("User data with invalid local tag");
            locale = Locale.forLanguageTag(localeTag.get());

            permissions.clear();
            @SuppressWarnings("unchecked") MapTag<StringElement, BooleanElement> actualPermissions
                    = ((DocTag) data).extractMap("permission");
            if (actualPermissions != null)
                permissions.putAll(actualPermissions);

            StringTag nameTag = ((DocTag) data).extractString("name");
            if (nameTag != null)
                name = nameTag.get();
            UUIDTag groupTag = ((DocTag) data).extractUUID("group_id");
            if (groupTag != null) {
                SavablePermissionGroup group = CerberusRegistry.getInstance()
                        .getService(UserService.class).getGroup(groupTag.get());

                if (group == null)
                    CerberusRegistry.getInstance().warning("User data contains invalid group id");
                this.group = group;
            }

            payload = ((DocTag) data).extractDoc("payload");
        } catch (ClassCastException e) {
            throw new LoadFormatException("Invalid data class for user data");
        }
    }

    private CerberusEvent getEventService() {
        if (eventService == null)
            eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);
        return eventService;
    }
}
