package com.imcode.imcms.web.admin.command;

import imcode.server.document.index.DocumentIndex;

import java.io.Serializable;

import com.imcode.imcms.web.admin.FieldDateRange;
import com.imcode.imcms.web.admin.IntegerRange;

public class SearchDocumentsCommand implements Serializable {
	private static final long serialVersionUID = 1362277403354022187L;
	
	public static final int NOT_SELECTED = -1;
	
	public enum SearchType {
		SEARCH, 
		ADD_TO_SELECTION, 
		SEARCH_IN_SELECTION;
	}
	
	private int[] creators;
	private int[] publishers;
	private int[] categories;
	
	private String template;
	
	private int role;
	private boolean roleNotOnPage;
	
	private boolean statusNew;
	private boolean statusApproved;
	private boolean statusPublished;
	private boolean statusArchived;
	private boolean statusExpired;
	private boolean statusPublishingDenied;
	
	private FieldDateRange creationRange;
	private FieldDateRange changeRange;
	private FieldDateRange publishingRange;
	private FieldDateRange archivedRange;
	private FieldDateRange expiredRange;
	
	private boolean parents;
	private boolean noParents;
	private boolean children;
	private boolean noChildren;
	
	private IntegerRange metaRange;
	private String text;
	
	private String alias;
	
	private SearchType searchType;
	private String clearAction;
	private String searchAction;
	
	
	public SearchDocumentsCommand() {
		clear();
	}
	
	
	public void clear() {
		creators = null;
		publishers = null;
		categories = null;
		
		template = null;
		
		role = NOT_SELECTED;
		roleNotOnPage = false;
		
		statusNew = false;
		statusApproved = false;
		statusPublished = false;
		statusArchived = false;
		statusExpired = false;
		statusPublishingDenied = false;
		
		creationRange = new FieldDateRange(DocumentIndex.FIELD__CREATED_DATETIME);
		changeRange = new FieldDateRange(DocumentIndex.FIELD__MODIFIED_DATETIME);
		publishingRange = new FieldDateRange(DocumentIndex.FIELD__PUBLICATION_START_DATETIME);
		archivedRange = new FieldDateRange(DocumentIndex.FIELD__ARCHIVED_DATETIME);
		expiredRange = new FieldDateRange(DocumentIndex.FIELD__PUBLICATION_END_DATETIME);
		
		parents = false;
		noParents = false;
		children = false;
		noChildren = false;
		
		metaRange = new IntegerRange();
		text = null;
		alias = null;
		
		searchType = SearchType.SEARCH;
	}
	
	public boolean isAction() {
		return isSearch() || isClear();
	}
	
	public boolean isSearch() {
		return searchAction != null;
	}
	
	public boolean isClear() {
		return clearAction != null;
	}
	
	public int[] getCategories() {
		return categories;
	}

	public void setCategories(int[] categories) {
		this.categories = categories;
	}

	public boolean isStatusNew() {
		return statusNew;
	}

	public void setStatusNew(boolean statusNew) {
		this.statusNew = statusNew;
	}

	public boolean isStatusApproved() {
		return statusApproved;
	}

	public void setStatusApproved(boolean statusApproved) {
		this.statusApproved = statusApproved;
	}

	public boolean isStatusPublished() {
		return statusPublished;
	}

	public void setStatusPublished(boolean statusPublished) {
		this.statusPublished = statusPublished;
	}

	public boolean isStatusExpired() {
		return statusExpired;
	}

	public void setStatusExpired(boolean statusExpired) {
		this.statusExpired = statusExpired;
	}

	public boolean isStatusPublishingDenied() {
		return statusPublishingDenied;
	}

	public void setStatusPublishingDenied(boolean statusPublishingDenied) {
		this.statusPublishingDenied = statusPublishingDenied;
	}

	public boolean isParents() {
		return parents;
	}

	public void setParents(boolean parents) {
		this.parents = parents;
	}

	public boolean isNoParents() {
		return noParents;
	}

	public void setNoParents(boolean noParents) {
		this.noParents = noParents;
	}

	public boolean isChildren() {
		return children;
	}

	public void setChildren(boolean children) {
		this.children = children;
	}

	public boolean isNoChildren() {
		return noChildren;
	}

	public void setNoChildren(boolean noChildren) {
		this.noChildren = noChildren;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getClearAction() {
		return clearAction;
	}

	public void setClearAction(String clearAction) {
		this.clearAction = clearAction;
	}

	public String getSearchAction() {
		return searchAction;
	}

	public void setSearchAction(String searchAction) {
		this.searchAction = searchAction;
	}
	
	public int[] getCreators() {
		return creators;
	}

	public void setCreators(int[] creators) {
		this.creators = creators;
	}

	public int[] getPublishers() {
		return publishers;
	}

	public void setPublishers(int[] publishers) {
		this.publishers = publishers;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	public boolean isStatusArchived() {
		return statusArchived;
	}

	public void setStatusArchived(boolean statusArchived) {
		this.statusArchived = statusArchived;
	}

	public FieldDateRange getCreationRange() {
		return creationRange;
	}

	public void setCreationRange(FieldDateRange creationRange) {
		this.creationRange = creationRange;
	}

	public FieldDateRange getChangeRange() {
		return changeRange;
	}

	public void setChangeRange(FieldDateRange changeRange) {
		this.changeRange = changeRange;
	}

	public FieldDateRange getPublishingRange() {
		return publishingRange;
	}

	public void setPublishingRange(FieldDateRange publishingRange) {
		this.publishingRange = publishingRange;
	}

	public FieldDateRange getArchivedRange() {
		return archivedRange;
	}

	public void setArchivedRange(FieldDateRange archivedRange) {
		this.archivedRange = archivedRange;
	}

	public FieldDateRange getExpiredRange() {
		return expiredRange;
	}

	public void setExpiredRange(FieldDateRange expiredRange) {
		this.expiredRange = expiredRange;
	}

	public IntegerRange getMetaRange() {
		return metaRange;
	}

	public void setMetaRange(IntegerRange metaRange) {
		this.metaRange = metaRange;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public boolean isRoleNotOnPage() {
		return roleNotOnPage;
	}

	public void setRoleNotOnPage(boolean roleNotOnPage) {
		this.roleNotOnPage = roleNotOnPage;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
}
