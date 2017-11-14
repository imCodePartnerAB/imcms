package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.api.DocumentVersion;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import org.apache.commons.lang.NullArgumentException;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Document's meta.
 * <p/>
 * Shared by all versions of the same document.
 */
public class DocumentMeta implements Serializable, Cloneable {

    private volatile Integer id;
    private volatile int defaultVersionNo = DocumentVersion.WORKING_VERSION_NO;
    /**
     * Disabled language's content show option.
     */
    private volatile DisabledLanguageShowMode disabledLanguageShowMode = DisabledLanguageShowMode.DO_NOT_SHOW;
    // todo: rename to documentTypeId
    private volatile Integer documentType;
    private volatile Boolean restrictedOneMorePrivilegedThanRestrictedTwo;
    private volatile Boolean linkableByOtherUsers;
    private volatile Boolean linkedForUnauthorizedUsers;
    /**
     * (Saved) value of modified dt at the time this meta was actually loaded.
     * When loaded from the db its value is set to modifiedDatetime.
     * Used to test if modifiedDatetime was changed explicitly.
     *
     * @see com.imcode.imcms.mapping.DocumentSaver#updateDocument
     */
    private volatile Date actualModifiedDatetime;
    private volatile boolean searchDisabled;
    private volatile String target;
    private volatile Date createdDatetime;
    private volatile Date modifiedDatetime;
    private volatile Date archivedDatetime;
    private volatile Date publicationStartDatetime;
    private volatile Date publicationEndDatetime;
    private volatile Integer creatorId;
    // we haven't modifierId field
    private volatile Integer archiverId;
    private volatile Integer publisherId;
    private volatile Integer depublisherId;
    private volatile Map<String, String> properties = new ConcurrentHashMap<>();
    private volatile Set<Integer> categoryIds = new CopyOnWriteArraySet<>();
    private volatile Set<DocumentLanguage> enabledLanguages = new CopyOnWriteArraySet<>();
    private volatile Set<String> keywords = new CopyOnWriteArraySet<>();
    private volatile RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings = new RoleIdToDocumentPermissionSetTypeMappings();
    private volatile Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;

    @Override
    public DocumentMeta clone() {
        try {
            DocumentMeta clone = (DocumentMeta) super.clone();

            clone.disabledLanguageShowMode = disabledLanguageShowMode;
            clone.properties = new ConcurrentHashMap<>(properties);
            clone.categoryIds = new CopyOnWriteArraySet<>(categoryIds);

            clone.keywords = new CopyOnWriteArraySet<>(keywords);
            clone.enabledLanguages = new CopyOnWriteArraySet<>(enabledLanguages);

            if (roleIdToDocumentPermissionSetTypeMappings != null) {
                clone.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings.clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Attributes properties:
    public Integer getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Integer documentType) {
        this.documentType = documentType;
    }

    public Integer getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    public Boolean getRestrictedOneMorePrivilegedThanRestrictedTwo() {
        return restrictedOneMorePrivilegedThanRestrictedTwo;
    }

    public void setRestrictedOneMorePrivilegedThanRestrictedTwo(
            Boolean restrictedOneMorePrivilegedThanRestrictedTwo) {
        this.restrictedOneMorePrivilegedThanRestrictedTwo = restrictedOneMorePrivilegedThanRestrictedTwo;
    }

    public Boolean getLinkableByOtherUsers() {
        return linkableByOtherUsers;
    }

    public void setLinkableByOtherUsers(Boolean linkableByOtherUsers) {
        this.linkableByOtherUsers = linkableByOtherUsers;
    }

    public Boolean getLinkedForUnauthorizedUsers() {
        return linkedForUnauthorizedUsers;
    }

    public void setLinkedForUnauthorizedUsers(Boolean linkedForUnauthorizedUsers) {
        this.linkedForUnauthorizedUsers = linkedForUnauthorizedUsers;
    }

    public Date getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Date getModifiedDatetime() {
        return modifiedDatetime;
    }

    public void setModifiedDatetime(Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    public Date getActualModifiedDatetime() {
        return actualModifiedDatetime;
    }

    public void setActualModifiedDatetime(Date actualModifiedDatetime) {
        this.actualModifiedDatetime = actualModifiedDatetime;
    }

    public boolean getSearchDisabled() {
        return searchDisabled;
    }

    public void setSearchDisabled(boolean searchDisabled) {
        this.searchDisabled = searchDisabled;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Date getArchivedDatetime() {
        return archivedDatetime;
    }

    public void setArchivedDatetime(Date archivedDatetime) {
        this.archivedDatetime = archivedDatetime;
    }

    public Integer getArchiverId() {
        return archiverId;
    }

    public void setArchiverId(Integer archiverId) {
        this.archiverId = archiverId;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public Integer getDepublisherId() {
        return depublisherId;
    }

    public void setDepublisherId(Integer depublisherId) {
        this.depublisherId = depublisherId;
    }

    public Date getPublicationStartDatetime() {
        return publicationStartDatetime;
    }

    public void setPublicationStartDatetime(Date publicationStartDatetime) {
        this.publicationStartDatetime = publicationStartDatetime;
    }

    public Date getPublicationEndDatetime() {
        return publicationEndDatetime;
    }

    public void setPublicationEndDatetime(Date publicationEndDatetime) {
        this.publicationEndDatetime = publicationEndDatetime;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Set<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(Set<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = new CopyOnWriteArraySet<>(keywords != null ? keywords : Collections.emptySet());
    }

    public DisabledLanguageShowMode getDisabledLanguageShowMode() {
        return disabledLanguageShowMode;
    }

    public void setDisabledLanguageShowMode(DisabledLanguageShowMode disabledLanguageShowMode) {
        this.disabledLanguageShowMode = disabledLanguageShowMode;
    }

    public Set<DocumentLanguage> getEnabledLanguages() {
        return enabledLanguages;
    }

    public void setEnabledLanguages(Set<DocumentLanguage> languages) {
        this.enabledLanguages = new CopyOnWriteArraySet<>(
                languages != null ? languages : Collections.emptySet()
        );
    }

    public RoleIdToDocumentPermissionSetTypeMappings getRoleIdToDocumentPermissionSetTypeMappings() {
        return roleIdToDocumentPermissionSetTypeMappings;
    }

    public void setRoleIdToDocumentPermissionSetTypeMappings(RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings) {
        this.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings.clone();
    }

    public Document.PublicationStatus getPublicationStatus() {
        return publicationStatus;
    }

    public void setPublicationStatus(Document.PublicationStatus status) {
        if (null == status) {
            throw new NullArgumentException("status");
        }
        publicationStatus = status;
    }

    public String getAlias() {
        return properties.get(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
    }

    public void setAlias(String alias) {
        if (alias == null) {
            removeAlis();
        } else {
            properties.put(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, alias);
        }
    }

    public void removeAlis() {
        properties.remove(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
    }

    public Integer getDefaultVersionNo() {
        return defaultVersionNo;
    }

    public void setDefaultVersionNo(Integer defaultVersionNo) {
        this.defaultVersionNo = defaultVersionNo;
    }

    /**
     * Document show mode for disabled language.
     */
    public enum DisabledLanguageShowMode {
        SHOW_IN_DEFAULT_LANGUAGE,
        DO_NOT_SHOW,
    }
}
