package imcode.server.document;

import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.imcms.api.*;
import com.imcode.imcms.util.l10n.LocalizedMessage;

/**
 * Parent of all imCMS document types.
 * <p/>
 * Holds document content and meta.
 */
public abstract class DocumentDomainObject implements Cloneable, Serializable {

    public static final int ID_NEW = 0;

    /**
     * Document's alias.
     * Can be set, changed and removed by an user which have rights to modify document information.
     * Must not be used as a hardcoded identity to access documents through API.
     *
     * @see com.imcode.imcms.mapping.DocumentMapper#getDocument(String)
     */
    public static final String DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS = "imcms.document.alias";

    /**
     * Document's internal id assigned by an application developer.
     * Intended to be used as a private identity to access documents through the API.
     */
    public static final String DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_INTERNAL_ID = "imcms.document.internal.id";

    /**
     * Legacy, property based modifier support.
     */
    public static final String DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_MODIFIED_BY = "imcms.document.modified_by";

    private static Logger log = Logger.getLogger(DocumentDomainObject.class);

    private Meta meta = new Meta();

    private I18nMeta i18nMeta = new I18nMeta();

    private DocumentVersion version = new DocumentVersion();


    @Override
    public DocumentDomainObject clone() {
        DocumentDomainObject clone;

        try {
            clone = (DocumentDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        if (clone.meta != null) {
            clone.meta = meta.clone();
        }

        if (clone.i18nMeta != null) {
            clone.i18nMeta = i18nMeta.clone();
        }

        if (version != null) {
            clone.version = version.clone();
        }

        return clone;
    }


    /**
     * Copies non-meta attributes from other doc.
     * @param doc
     */
//    public void copyAttributesFrom(DocumentDomainObject doc) {
//        seti18nMeta(doc.geti18nMeta().clone());
//        setLanguage(doc.getLanguage().clone());
//        setPermissionSets(doc.getPermissionSets().clone());
//        setPermissionSetsForNew(doc.getPermissionSetsForNewDocuments().clone());
//        setRoleIdsMappedToDocumentPermissionSetTypes(doc.getRolePermissionMappings().clone());
//    }

    /**
     * Returns this document's version.
     */
    public DocumentVersion getVersion() {
        return version;
    }

    /**
     * Returns this document's version no.
     */
    public Integer getVersionNo() {
        DocumentVersion version = getVersion();

        return version == null ? null : version.getNo();
    }


    /**
     * Returns this document's version.
     */
    public void setVersion(DocumentVersion version) {
        this.version = version;
    }


    /**
     * Factory method. Creates new document.
     *
     * @param documentTypeId document type id.
     * @return new document
     */
    public static DocumentDomainObject fromDocumentTypeId(int documentTypeId) {
        DocumentDomainObject document;

        switch (documentTypeId) {
            case DocumentTypeDomainObject.TEXT_ID:
                document = new TextDocumentDomainObject();
                break;
            case DocumentTypeDomainObject.URL_ID:
                document = new UrlDocumentDomainObject();
                break;
            case DocumentTypeDomainObject.FILE_ID:
                document = new FileDocumentDomainObject();
                break;
            case DocumentTypeDomainObject.HTML_ID:
                document = new HtmlDocumentDomainObject();
                break;
            default:
                String errorMessage = "Unknown document-type-id: " + documentTypeId;
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
        }

        document.setLanguage(Imcms.getI18nSupport().getDefaultLanguage());
        document.setVersion(new DocumentVersion(null, 0, null, new Date()));

        return document;
    }

    /**
     * Deprecated with no replacement.
     */
    @Deprecated
    public Date getArchivedDatetime() {
        return meta.getArchivedDatetime();
    }

    /**
     * Deprecated with no replacement.
     */
    @Deprecated
    public void setArchivedDatetime(Date v) {
        meta.setArchivedDatetime(v);
    }

    public Set<Integer> getCategoryIds() {
        return meta.getCategoryIds();
    }

    public Date getCreatedDatetime() {
        return meta.getCreatedDatetime();
    }

    public void setCreatedDatetime(Date v) {
        meta.setCreatedDatetime(v);
    }

    public int getCreatorId() {
        return meta.getCreatorId();
    }

    /**
     * Returns the last user who modified this document.
     * @return the last user who modified this document or null is there is no associated user.
     */
    public Integer getModifierId() {
        try {
            Integer modifierId = version == null ? null : version.getModifiedBy();

            // legacy property based modifier support
            return modifierId != null ? modifierId : Integer.valueOf(getProperty(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_MODIFIED_BY));
        } catch (Exception e) {
            return null;
        }
    }

    public void setCreatorId(int creatorId) {
        meta.setCreatorId(creatorId);
    }

    public void setCreator(UserDomainObject creator) {
        setCreatorId(creator.getId());
    }

    public String getHeadline() {
        return i18nMeta.getHeadline();
    }

    public void setHeadline(String v) {
        i18nMeta.setHeadline(v);
    }

    public int getId() {
        Integer id = getIdValue();
        return id == null ? ID_NEW : id;
    }

    public Integer getIdValue() {
        return meta.getId();
    }

    public void setId(int id) {
        meta.setId(id);
    }

    public String getMenuImage() {
        return i18nMeta.getMenuImageURL();
    }

    public void setMenuImage(String v) {
        i18nMeta.setMenuImageURL(v);
    }


    public Set<String> getKeywords() {
        return meta.getKeywords();
    }


    public void setKeywords(Set<String> keywords) {
        meta.setKeywords(keywords);
    }

    public void setProperties(Map properties) {
        meta.setProperties(properties);
    }

    public Map<String, String> getProperties() {
        return meta.getProperties();
    }

    public String getProperty(String key) {
        Map<String, String> properties = meta.getProperties();
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        Map<String, String> properties = meta.getProperties();
        properties.put(key, value);
    }

    public void removeProperty(String key) {
        Map<String, String> properties = meta.getProperties();
        properties.remove(key);
    }

    public String getMenuText() {
        return i18nMeta.getMenuText();
    }


    public void setMenuText(String v) {
        i18nMeta.setMenuText(v);
    }

    public Date getModifiedDatetime() {
        return meta.getModifiedDatetime();
    }

    public void setModifiedDatetime(Date v) {
        meta.setModifiedDatetime(v);
    }

    public void setActualModifiedDatetime(Date modifiedDatetime) {
        meta.setActualModifiedDatetime(modifiedDatetime);
    }

    public Date getActualModifiedDatetime() {
        return meta.getActualModifiedDatetime();
    }

    public Date getPublicationEndDatetime() {
        return meta.getPublicationEndDatetime();
    }

    public void setPublicationEndDatetime(Date datetime) {
        meta.setPublicationEndDatetime(datetime);
    }

    public Date getPublicationStartDatetime() {
        return meta.getPublicationStartDatetime();
    }

    public void setPublicationStartDatetime(Date v) {
        meta.setPublicationStartDatetime(v);
    }

    public Integer getPublisherId() {
        return meta.getPublisherId();
    }

    public void setPublisher(UserDomainObject user) {
        setPublisherId(user.getId());
    }

    public void setPublisherId(Integer publisherId) {
        meta.setPublisherId(publisherId);
    }

    public RoleIdToDocumentPermissionSetTypeMappings getRoleIdsMappedToDocumentPermissionSetTypes() {
        return getRolePermissionMappings().clone();
    }

    private RoleIdToDocumentPermissionSetTypeMappings getRolePermissionMappings() {
        return meta.getRoleIdToDocumentPermissionSetTypeMappings();
    }

    public void setRoleIdsMappedToDocumentPermissionSetTypes(RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings) {
        meta.setRoleIdsMappedToDocumentPermissionSetTypes(roleIdToDocumentPermissionSetTypeMappings);
    }

    public Document.PublicationStatus getPublicationStatus() {
        return meta.getPublicationStatus();
    }

    public void setPublicationStatus(Document.PublicationStatus status) {
        meta.setPublicationStatus(status);
    }

    public String getTarget() {
        return meta.getTarget();
    }

    public void setTarget(String v) {
        meta.setTarget(v);
    }

    public boolean isArchived() {
        return hasBeenArchivedAtTime(new Date());
    }

    public boolean isLinkableByOtherUsers() {
        return meta.getLinkableByOtherUsers();
    }

    public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
        meta.setLinkableByOtherUsers(linkableByOtherUsers);
    }

    public boolean isRestrictedOneMorePrivilegedThanRestrictedTwo() {
        return meta.getRestrictedOneMorePrivilegedThanRestrictedTwo();
    }

    public void setRestrictedOneMorePrivilegedThanRestrictedTwo(boolean b) {
        meta.setRestrictedOneMorePrivilegedThanRestrictedTwo(b);
    }

    public boolean isPublished() {
        return isPublishedAtTime(new Date());
    }

    public boolean isActive() {
        return isActiveAtTime(new Date());
    }

    private boolean isActiveAtTime(Date now) {
        return isPublishedAtTime(now) && !hasBeenArchivedAtTime(now);
    }

    public boolean isSearchDisabled() {
        return meta.getSearchDisabled();
    }

    public void setSearchDisabled(boolean searchDisabled) {
        meta.setSearchDisabled(searchDisabled);
    }

    public boolean isLinkedForUnauthorizedUsers() {
        return meta.getLinkedForUnauthorizedUsers();
    }

    public void setLinkedForUnauthorizedUsers(boolean linkedForUnauthorizedUsers) {
        meta.setLinkedForUnauthorizedUsers(linkedForUnauthorizedUsers);
    }

    public void addCategoryId(int categoryId) {
        Set categoryIds = (Set) meta.getCategoryIds();
        categoryIds.add(categoryId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentDomainObject)) {
            return false;
        }

        final DocumentDomainObject document = (DocumentDomainObject) o;

        return getId() == document.getId();

    }

    public abstract DocumentTypeDomainObject getDocumentType();

    public final int getDocumentTypeId() {
        return getDocumentType().getId();
    }

    public final LocalizedMessage getDocumentTypeName() {
        return getDocumentType().getName();
    }

    public int hashCode() {
        return getId();
    }

    private boolean hasBeenArchivedAtTime(Date time) {
        Date archivedDatetime = meta.getArchivedDatetime();
        return archivedDatetime != null && archivedDatetime.before(time);
    }

    public void removeAllCategories() {
        meta.setCategoryIds(new HashSet<Integer>());
    }

    public void removeCategoryId(int categoryId) {
        Set categoryIds = (Set) meta.getCategoryIds();
        categoryIds.remove(categoryId);
    }

    public void setDocumentPermissionSetTypeForRoleId(RoleId roleId,
                                                      DocumentPermissionSetTypeDomainObject permissionSetType) {
        getRolePermissionMappings().setPermissionSetTypeForRole(roleId,
                permissionSetType);
    }

    public DocumentPermissionSetTypeDomainObject getDocumentPermissionSetTypeForRoleId(
            RoleId roleId) {
        return getRolePermissionMappings().getPermissionSetTypeForRole(roleId);
    }

    private boolean isPublishedAtTime(Date date) {
        boolean statusIsApproved = Document.PublicationStatus.APPROVED
                .equals(meta.getPublicationStatus());
        return statusIsApproved && publicationHasStartedAtTime(date)
                && !publicationHasEndedAtTime(date);
    }

    private boolean publicationHasStartedAtTime(Date date) {
        Date publicationStartDatetime = meta.getPublicationStartDatetime();
        return publicationStartDatetime != null
                && publicationStartDatetime.before(date);
    }

    private boolean publicationHasEndedAtTime(Date date) {
        Date publicationEndDatetime = meta.getPublicationEndDatetime();
        return publicationEndDatetime != null
                && publicationEndDatetime.before(date);
    }

    public DocumentPermissionSets getPermissionSets() {
        return meta.getPermissionSets();
    }

    public DocumentPermissionSets getPermissionSetsForNewDocuments() {
        return meta.getPermissionSetsForNewDocuments();
    }

    public abstract void accept(DocumentVisitor documentVisitor);

    public LifeCyclePhase getLifeCyclePhase() {
        return getLifeCyclePhaseAtTime(new Date());
    }

    LifeCyclePhase getLifeCyclePhaseAtTime(Date time) {
        LifeCyclePhase lifeCyclePhase;
        Document.PublicationStatus publicationStatus = meta.getPublicationStatus();
        if (Document.PublicationStatus.NEW.equals(publicationStatus)) {
            lifeCyclePhase = LifeCyclePhase.NEW;
        } else if (Document.PublicationStatus.DISAPPROVED
                .equals(publicationStatus)) {
            lifeCyclePhase = LifeCyclePhase.DISAPPROVED;
        } else {
            if (publicationHasEndedAtTime(time)) {
                lifeCyclePhase = LifeCyclePhase.UNPUBLISHED;
            } else if (publicationHasStartedAtTime(time)) {
                if (hasBeenArchivedAtTime(time)) {
                    lifeCyclePhase = LifeCyclePhase.ARCHIVED;
                } else {
                    lifeCyclePhase = LifeCyclePhase.PUBLISHED;
                }
            } else {
                lifeCyclePhase = LifeCyclePhase.APPROVED;
            }
        }
        return lifeCyclePhase;
    }

    public void setCategoryIds(Set categoryIds) {
        meta.setCategoryIds(categoryIds);
    }

    public void setPermissionSets(DocumentPermissionSets permissionSets) {
        meta.setPermissionSets(permissionSets);
    }

    public void setPermissionSetsForNew(DocumentPermissionSets permissionSetsForNew) {
        meta.setPermissionSetsForNew(permissionSetsForNew);
    }

    public String getAlias() {
        return meta.getAlias();
    }

    public void setAlias(String alias) {
        meta.setAlias(alias);
    }

    public String getName() {
        return StringUtils.defaultString(getAlias(), getId() + "");
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        if (meta == null) {
            throw new IllegalArgumentException("Meta argument can not be null.");
        }

        this.meta = meta.clone();
    }

    public I18nLanguage getLanguage() {
        return i18nMeta.getLanguage();
    }

    public void setLanguage(I18nLanguage language) {
        this.i18nMeta.setLanguage(language);
    }


    public I18nMeta getI18nMeta() {
        return i18nMeta;
    }

    public void setI18nMeta(I18nMeta i18nMeta) {
        if (i18nMeta == null) {
            throw new IllegalArgumentException("i18nMeta argument can not be null.");
        }

        this.i18nMeta = i18nMeta;
    }
}
