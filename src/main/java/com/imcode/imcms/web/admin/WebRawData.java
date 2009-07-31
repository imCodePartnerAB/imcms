package com.imcode.imcms.web.admin;

import java.util.List;


/**
 *
 * Prototype class
 *
 */
public final class WebRawData {
	private List<NameValuePair> profiles;
	private List<NameValuePair> categoryTypes;
	private List<NameValuePair> categories;
	private List<NameValuePair> templates;
	private List<NameValuePair> roles;
	private List<NameValuePair> permissionGroups;
	private List<NameValuePair> profilesOnCreateNew;
	private List<NameValuePair> publicationStatuses;
	private List<NameValuePair> creators;
	private List<NameValuePair> publishers;
	private List<NameValuePair> textFields;

	public List<NameValuePair> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<NameValuePair> profiles) {
		this.profiles = profiles;
	}

	public List<NameValuePair> getCategoryTypes() {
		return categoryTypes;
	}

	public void setCategoryTypes(List<NameValuePair> categoryTypes) {
		this.categoryTypes = categoryTypes;
	}

	public List<NameValuePair> getCategories() {
		return categories;
	}

	public void setCategories(List<NameValuePair> categories) {
		this.categories = categories;
	}

	public List<NameValuePair> getTemplates() {
		return templates;
	}

	public void setTemplates(List<NameValuePair> templates) {
		this.templates = templates;
	}

	public List<NameValuePair> getRoles() {
		return roles;
	}

	public void setRoles(List<NameValuePair> roles) {
		this.roles = roles;
	}

	public List<NameValuePair> getPermissionGroups() {
		return permissionGroups;
	}

	public void setPermissionGroups(List<NameValuePair> permissionGroups) {
		this.permissionGroups = permissionGroups;
	}

	public List<NameValuePair> getProfilesOnCreateNew() {
		return profilesOnCreateNew;
	}

	public void setProfilesOnCreateNew(List<NameValuePair> profilesOnCreateNew) {
		this.profilesOnCreateNew = profilesOnCreateNew;
	}

	public List<NameValuePair> getPublicationStatuses() {
		return publicationStatuses;
	}

	public void setPublicationStatuses(List<NameValuePair> publicationStatuses) {
		this.publicationStatuses = publicationStatuses;
	}

	public List<NameValuePair> getCreators() {
		return creators;
	}

	public void setCreators(List<NameValuePair> creators) {
		this.creators = creators;
	}

	public List<NameValuePair> getPublishers() {
		return publishers;
	}

	public void setPublishers(List<NameValuePair> publishers) {
		this.publishers = publishers;
	}

	public List<NameValuePair> getTextFields() {
		return textFields;
	}

	public void setTextFields(List<NameValuePair> textFields) {
		this.textFields = textFields;
	}

}
