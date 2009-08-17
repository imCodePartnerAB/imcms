package com.imcode.imcms.web.admin.command;

import java.io.Serializable;

public class SearchDocumentsFoldCommand implements Serializable {
	private static final long serialVersionUID = 7876754600022648854L;
	
	private boolean personsCollapsed;
	private boolean profileCollapsed;
	private boolean categoriesCollapsed;
	private boolean roleCollapsed;
	private boolean statusCollapsed;
	private boolean datesCollapsed;
	private boolean miscCollapsed;
	
	
	public SearchDocumentsFoldCommand() {
	}

	
	public boolean isPersonsCollapsed() {
		return personsCollapsed;
	}

	public void setPersonsCollapsed(boolean personsCollapsed) {
		this.personsCollapsed = personsCollapsed;
	}

	public boolean isProfileCollapsed() {
		return profileCollapsed;
	}

	public void setProfileCollapsed(boolean profileCollapsed) {
		this.profileCollapsed = profileCollapsed;
	}

	public boolean isCategoriesCollapsed() {
		return categoriesCollapsed;
	}

	public void setCategoriesCollapsed(boolean categoriesCollapsed) {
		this.categoriesCollapsed = categoriesCollapsed;
	}

	public boolean isRoleCollapsed() {
		return roleCollapsed;
	}

	public void setRoleCollapsed(boolean roleCollapsed) {
		this.roleCollapsed = roleCollapsed;
	}

	public boolean isStatusCollapsed() {
		return statusCollapsed;
	}

	public void setStatusCollapsed(boolean statusCollapsed) {
		this.statusCollapsed = statusCollapsed;
	}

	public boolean isDatesCollapsed() {
		return datesCollapsed;
	}

	public void setDatesCollapsed(boolean datesCollapsed) {
		this.datesCollapsed = datesCollapsed;
	}

	public boolean isMiscCollapsed() {
		return miscCollapsed;
	}

	public void setMiscCollapsed(boolean miscCollapsed) {
		this.miscCollapsed = miscCollapsed;
	}
}
