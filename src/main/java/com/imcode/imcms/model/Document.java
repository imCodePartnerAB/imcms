package com.imcode.imcms.model;

import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.persistence.entity.Meta.DisabledLanguageShowMode;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.Permission;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import imcode.util.Utility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 11.01.18.
 */
@Data
@NoArgsConstructor
public abstract class Document implements Serializable {

    private static final long serialVersionUID = 9097330155648052626L;

    protected Document(Document from) {
        setId(from.getId());
	    setType(from.getType());
	    setTarget(from.getTarget());
	    setName(from.getName());
	    setCommonContents(from.getCommonContents());
	    setPublicationStatus(from.getPublicationStatus());
	    setPublished(from.getPublished());
	    setArchived(from.getArchived());
	    setPublicationEnd(from.getPublicationEnd());
	    setModified(from.getModified());
	    setCreated(from.getCreated());
	    setDefaultLanguageAliasEnabled(from.isDefaultLanguageAliasEnabled());
	    setDisabledLanguageShowMode(from.getDisabledLanguageShowMode());
	    setCurrentVersion(from.getCurrentVersion());
	    setLatestVersion(from.getLatestVersion());
	    setKeywords(from.getKeywords());
	    setSearchDisabled(from.isSearchDisabled());
	    setCategories(from.getCategories());
	    setRestrictedPermissions(from.getRestrictedPermissions());
	    setRoleIdToPermission(from.getRoleIdToPermission());
	    setLinkableByOtherUsers(from.isLinkableByOtherUsers());
	    setLinkableForUnauthorizedUsers(from.isLinkableForUnauthorizedUsers());
		setCacheForUnauthorizedUsers(from.isCacheForUnauthorizedUsers());
		setCacheForAuthorizedUsers(from.isCacheForAuthorizedUsers());
	    setVisible(from.isVisible());
        setProperties(from.getProperties());
		setImported(from.isImported());
		setDocumentWasteBasket(from.getDocumentWasteBasket());
    }

    public abstract Integer getId();

	public abstract void setId(Integer id);

	public abstract DocumentType getType();

	public abstract void setType(DocumentType type);

	public abstract String getTarget();

	public abstract void setTarget(String target);

	/**
	 * document alias/name related to rendered web page content
	 */
	public abstract String getName();

	public abstract void setName(String name);

	public abstract List<CommonContent> getCommonContents();

	public abstract void setCommonContents(List<CommonContent> commonContents);

	public abstract PublicationStatus getPublicationStatus();

	public abstract void setPublicationStatus(PublicationStatus publicationStatus);

    public abstract AuditDTO getPublished();

    public abstract void setPublished(AuditDTO published);

    public abstract AuditDTO getArchived();

    public abstract void setArchived(AuditDTO archived);

    public abstract AuditDTO getPublicationEnd();

	public abstract void setPublicationEnd(AuditDTO publicationEnd);

	public abstract AuditDTO getModified();

	public abstract void setModified(AuditDTO modified);

	public abstract AuditDTO getCreated();

	public abstract void setCreated(AuditDTO created);

	public abstract boolean isDefaultLanguageAliasEnabled();

	public abstract void setDefaultLanguageAliasEnabled(boolean defaultLanguageAliasEnabled);

	public abstract DisabledLanguageShowMode getDisabledLanguageShowMode();

	public abstract void setDisabledLanguageShowMode(DisabledLanguageShowMode disabledLanguageShowMode);

	public abstract AuditDTO getCurrentVersion();

	public abstract void setCurrentVersion(AuditDTO currentVersion);

	public abstract AuditDTO getLatestVersion();

	public abstract void setLatestVersion(AuditDTO latestVersion);

    public abstract Set<String> getKeywords();

    public abstract void setKeywords(Set<String> keywords);

    public abstract boolean isSearchDisabled();

    public abstract void setSearchDisabled(boolean searchDisabled);

    public abstract Set<Category> getCategories();

    public abstract void setCategories(Set<Category> categories);

    public abstract Set<RestrictedPermission> getRestrictedPermissions();

    public abstract void setRestrictedPermissions(Set<RestrictedPermission> restrictedPermissions);

    public abstract Map<String, String> getProperties();

    public abstract void setProperties(Map<String, String> properties);

    public abstract Map<Integer, Permission> getRoleIdToPermission();

    public abstract void setRoleIdToPermission(Map<Integer, Permission> roleIdToPermission);

    public abstract boolean isLinkableByOtherUsers();

    public abstract void setLinkableByOtherUsers(boolean isLinkableByOtherUsers);

    public abstract boolean isLinkableForUnauthorizedUsers();

	public abstract void setCacheForUnauthorizedUsers(boolean cacheForUnauthorizedUsers);

	public abstract boolean isCacheForUnauthorizedUsers();

	public abstract void setCacheForAuthorizedUsers(boolean cacheForAuthorizedUsers);

	public abstract boolean isCacheForAuthorizedUsers();

    public abstract void setLinkableForUnauthorizedUsers(boolean isLinkableForUnauthorizedUsers);

    public abstract boolean isVisible();

    public abstract void setVisible(boolean isVisible);

	public abstract boolean isImported();

	public abstract void setImported(boolean imported);

	public abstract DocumentWasteBasket getDocumentWasteBasket();

	public abstract void setDocumentWasteBasket(DocumentWasteBasket documentWasteBasket);

	public boolean isInWasteBasket(){
		return getDocumentWasteBasket() != null;
	}

    @SuppressWarnings("unused") // used on client side
    public DocumentStatus getDocumentStatus() {
        final PublicationStatus publicationStatus = getPublicationStatus();

		if (getDocumentWasteBasket() != null){
			return DocumentStatus.WASTE_BASKET;
		}

	    if (PublicationStatus.NEW.equals(publicationStatus)) {
		    return DocumentStatus.IN_PROCESS;
	    }
	    if (PublicationStatus.DISAPPROVED.equals(publicationStatus)) {
		    return DocumentStatus.DISAPPROVED;
	    }

	    if (isNullAuditDate(getPublished())) {
		    return DocumentStatus.IN_PROCESS;
	    }
        if ((isAuditDateInPast(getPublicationEnd()) && isAuditBefore(getPublished(), getPublicationEnd()))
                || isAuditDateEquals(getPublished(), getPublicationEnd())) {
            return DocumentStatus.PASSED;
        }
	    if (isAuditDateInPast(getArchived()) && !isAuditDateInFuture(getPublished())) {
		    return DocumentStatus.ARCHIVED;
	    }
	    if (isAuditDateInFuture(getPublished())) {
		    return DocumentStatus.PUBLISHED_WAITING;
	    }

        return DocumentStatus.PUBLISHED;
    }

    private boolean isNullAuditDate(AuditDTO auditToCheck) {
        return (auditToCheck == null) || (auditToCheck.getFormattedDate() == null);
    }

    private boolean isAuditDateInPast(AuditDTO auditToCheck) {
        return !isNullAuditDate(auditToCheck) && Utility.isDateInPast.test(auditToCheck.getFormattedDate());
    }

    private boolean isAuditDateInFuture(AuditDTO auditToCheck) {
        return !isNullAuditDate(auditToCheck) && Utility.isDateInFuture.test(auditToCheck.getFormattedDate());
    }

    private boolean isAuditBefore(AuditDTO audit, AuditDTO other){
        return !isNullAuditDate(audit) && !isNullAuditDate(other) &&
                Utility.isDateBefore.test(audit.getFormattedDate(), other.getFormattedDate());
    }

    private boolean isAuditDateEquals(AuditDTO audit, AuditDTO other){
        return !isNullAuditDate(audit) && !isNullAuditDate(other) &&
                audit.getFormattedDate().equals(other.getFormattedDate());
    }
}
