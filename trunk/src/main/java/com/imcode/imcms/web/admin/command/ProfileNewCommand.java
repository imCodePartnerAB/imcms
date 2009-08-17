package com.imcode.imcms.web.admin.command;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ProfileNewCommand {

	public enum ProfileAction {
		CHANGE, CREATE_NEW_BASED_ON_PROFILE, CREATE_NEW;

		private static List<ProfileAction> allProfileActions = new ArrayList<ProfileAction>();

		static {
			for (ProfileAction p : EnumSet.allOf(ProfileAction.class)) {
				allProfileActions.add(p);
			}
		}

		private static List<ProfileAction> getAllProfileActions() {
			return allProfileActions;
		}

		public String getName() {
			return this.name();
		}
	}

	public enum ProfileSubAction {
		LOAD_CATEGORIES, UNLOAD_CATEGORIES, CREATE_ROLES, DELETE_ROLES, SAVE_AND_UPDATE_DOC, SAVE;
	}

	private ProfileAction profileAction;
	private ProfileSubAction profileSubActionp;
	
	private String name;
	private String profile;
	private String template;
	
	private ProfileRoleCommand newRoleInProfile;

	private List<String> unselectedCategoryTypes;
	private List<String> selectedCategoryTypes;
	private List<String> unselectedCategories;
	private List<String> selectedCategories;
	
	private List<ProfileRoleCommand> RolesInProfile = new ArrayList<ProfileRoleCommand>();
	
	public ProfileNewCommand() {
		newRoleInProfile = new ProfileRoleCommand();
	}

	public List<ProfileAction> getAllProfileActions() {
		return ProfileAction.getAllProfileActions();
	}

	public ProfileAction getProfileAction() {
		return profileAction;
	}

	public void setProfileAction(ProfileAction profileAction) {
		this.profileAction = profileAction;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getUnselectedCategoryTypes() {
		return unselectedCategoryTypes;
	}

	public void setUnselectedCategoryTypes(List<String> unselectedCategoryTypes) {
		this.unselectedCategoryTypes = unselectedCategoryTypes;
	}

	public List<String> getSelectedCategoryTypes() {
		return selectedCategoryTypes;
	}

	public void setSelectedCategoryTypes(List<String> selectedCategoryTypes) {
		this.selectedCategoryTypes = selectedCategoryTypes;
	}

	public List<String> getUnselectedCategories() {
		return unselectedCategories;
	}

	public void setUnselectedCategories(List<String> unselectedCategories) {
		this.unselectedCategories = unselectedCategories;
	}

	public List<String> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(List<String> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public ProfileRoleCommand getNewRoleInProfile() {
		return newRoleInProfile;
	}

	public void setNewRoleInProfile(ProfileRoleCommand newRoleInProfile) {
		this.newRoleInProfile = newRoleInProfile;
	}

	public List<ProfileRoleCommand> getRolesInProfile() {
		return RolesInProfile;
	}

	public void setRolesInProfile(List<ProfileRoleCommand> rolesInProfile) {
		RolesInProfile = rolesInProfile;
	}

	public ProfileSubAction getProfileSubActionp() {
		return profileSubActionp;
	}

	public void setProfileSubActionp(ProfileSubAction profileSubActionp) {
		this.profileSubActionp = profileSubActionp;
	}
}
