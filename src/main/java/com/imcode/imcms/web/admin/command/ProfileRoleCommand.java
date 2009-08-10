package com.imcode.imcms.web.admin.command;

public class ProfileRoleCommand {
	private String role;
	private String permissionGroup;
	private String profileOnCreateNew;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPermissionGroup() {
		return permissionGroup;
	}

	public void setPermissionGroup(String permissionGroup) {
		this.permissionGroup = permissionGroup;
	}

	public String getProfileOnCreateNew() {
		return profileOnCreateNew;
	}

	public void setProfileOnCreateNew(String profileOnCreateNew) {
		this.profileOnCreateNew = profileOnCreateNew;
	}
}
