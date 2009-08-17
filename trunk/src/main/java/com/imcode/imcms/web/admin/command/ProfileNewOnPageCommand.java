package com.imcode.imcms.web.admin.command;

import java.util.List;

public final class ProfileNewOnPageCommand {

	private String name;
	private String profileOnCreateNew;

	private ProfileRoleCommand newRoleOnPage;

	private List<ProfileRoleCommand> rolesInProfile;
	private List<ProfileRoleCommand> rolesOnPage;
	
	public ProfileNewOnPageCommand() {
		newRoleOnPage = new ProfileRoleCommand();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfileOnCreateNew() {
		return profileOnCreateNew;
	}

	public void setProfileOnCreateNew(String profileOnCreateNew) {
		this.profileOnCreateNew = profileOnCreateNew;
	}

	public ProfileRoleCommand getNewRoleOnPage() {
		return newRoleOnPage;
	}

	public void setNewRoleOnPage(ProfileRoleCommand newRoleOnPage) {
		this.newRoleOnPage = newRoleOnPage;
	}

	public List<ProfileRoleCommand> getRolesInProfile() {
		return rolesInProfile;
	}

	public void setRolesInProfile(List<ProfileRoleCommand> rolesInProfile) {
		this.rolesInProfile = rolesInProfile;
	}

	public List<ProfileRoleCommand> getRolesOnPage() {
		return rolesOnPage;
	}

	public void setRolesOnPage(List<ProfileRoleCommand> rolesOnPage) {
		this.rolesOnPage = rolesOnPage;
	}
}
