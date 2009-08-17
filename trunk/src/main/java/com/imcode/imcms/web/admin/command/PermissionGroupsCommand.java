package com.imcode.imcms.web.admin.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
//import org.apache.commons.collections.list.GrowthList;
import org.apache.commons.collections.list.LazyList;

import com.imcode.imcms.web.admin.PermissionGroup;

public final class PermissionGroupsCommand {
	private String name;
	private String selectedGroup;
	private List<PermissionGroup> groups;

	@SuppressWarnings("unchecked")
	public PermissionGroupsCommand() {	
		groups = LazyList.decorate(new ArrayList<PermissionGroup>(), new Factory() {
			public Object create() {
				return new PermissionGroup();
			}
		});
	}

	public List<PermissionGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<PermissionGroup> groups) {
		this.groups = groups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSelectedGroup() {
		return selectedGroup;
	}

	public void setSelectedGroup(String selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("PermissionGroup\n");
		sb.append(String.format("HashCode: %s\n;", hashCode()));
		sb.append(String.format("Name: %s\n;", getName()));
		sb.append(String.format("Selected group: %s\n;", getSelectedGroup()));

		for (PermissionGroup p : groups) {
			sb.append(p.toString());
		}

		return sb.toString();
	}
}
