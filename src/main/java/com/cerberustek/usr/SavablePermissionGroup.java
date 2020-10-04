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
import com.cerberustek.data.impl.elements.UUIDElement;
import com.cerberustek.data.impl.tags.DocTag;
import com.cerberustek.data.impl.tags.MapTag;
import com.cerberustek.data.impl.tags.SetTag;
import com.cerberustek.data.impl.tags.UUIDTag;
import com.cerberustek.CerberusEvent;
import com.cerberustek.CerberusRegistry;
import com.cerberustek.exception.LoadFormatException;

import java.util.*;

public class SavablePermissionGroup implements PermissionGroup, MetaConvertible, MetaLoadable {

    private final HashSet<PermissionGroup> parents = new HashSet<>();
    private final MapTag<StringElement, BooleanElement> permissions = new MapTag<>("permission");
    private final UUID uuid;

    private String name;
    private CerberusEvent eventService;

    public SavablePermissionGroup(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public Collection<PermissionGroup> parents() {
        return new LinkedHashSet<>(parents);
    }

    @Override
    public void addParent(PermissionGroup permissionGroup) {
        parents.add(permissionGroup);
    }

    @Override
    public boolean hasParent(PermissionGroup permissionGroup) {
        return parents.contains(permissionGroup);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UUID getGroupId() {
        return uuid;
    }

    @Override
    public boolean hasPermission(String s) {
        if (!getEventService().executeShortEIF(new PermissionEvent(s, this, PermissionEventType.REQUEST)))
            return false;

        BooleanElement permission = permissions.get(new StringElement(s));
        if (permission != null)
            return permission.get();

        for (PermissionGroup parent : parents) {
            if (parent.hasPermission(s))
                return true;
        }
        return false;
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
        DocTag group = new DocTag(name);

        group.insert(new UUIDTag("group_id", uuid));
        group.insert(permissions);

        SetTag<UUIDElement> groups = new SetTag<>("groups");
        for (PermissionGroup parent : parents)
            groups.add(new UUIDElement(parent.getGroupId()));
        group.insert(groups);
        return group;
    }

    @Override
    public void load(MetaData data) throws LoadFormatException {
        if (data instanceof DocTag) {
            DocTag doc = (DocTag) data;

            UUIDTag groupId = doc.extractUUID("group_id");
            if (groupId == null)
                throw new LoadFormatException("Invalid data id");

            if (!uuid.equals(groupId.get()))
                throw new LoadFormatException("Data id does not match group id!");

            permissions.clear();
            //noinspection unchecked
            permissions.putAll((MapTag<StringElement, BooleanElement>) doc.extractMap("permission"));
        }
    }

    private CerberusEvent getEventService() {
        if (eventService == null)
            eventService = CerberusRegistry.getInstance().getService(CerberusEvent.class);
        return eventService;
    }
}
