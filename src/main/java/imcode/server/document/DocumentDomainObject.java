package imcode.server.document;

import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.DocRef;
import com.imcode.imcms.mapping.DocVersionRef;
import com.imcode.imcms.mapping.DocumentCommonContent;
import com.imcode.imcms.mapping.Meta;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * Parent of all imCMS document types.
 * <p/>
 * Holds document meta and a content.
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

    private volatile Meta meta = new Meta();

    private volatile DocumentCommonContent appearance = DocumentCommonContent.builder().build();

    private volatile int versionNo = DocumentVersion.WORKING_VERSION_NO;

    /*
     * Stub.
     * Documents instances are created directly (using new) only in tests.
     * In production instances are created via factories that are responsible for injecting an appropriate language.
     */
    private volatile DocumentLanguage language = DocumentLanguage.builder().code("en").enabled(true).build();

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

        return clone;
    }

    /**
     * Returns this document's version no.
     */
    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int no) {
        versionNo = no;
    }

    public DocRef getRef() {
        return DocRef.of(getId(), getVersionNo(), getLanguage().getCode());
    }

    public DocVersionRef getVersionRef() {
        return DocVersionRef.of(getId(), getVersionNo());
    }

    /**
     * Factory method. Creates new document.
     *
     * @param documentTypeId document type id.
     * @return new document
     */
    public static <T extends DocumentDomainObject> T fromDocumentTypeId(int documentTypeId) {
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

        document.setLanguage(Imcms.getServices().getDocumentLanguageSupport().getDefaultLanguage());
        document.setVersionNo(DocumentVersion.WORKING_VERSION_NO);

        return (T) document;
    }

    public Date getArchivedDatetime() {
        return meta.getArchivedDatetime();
    }

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

    public void setCreatorId(int creatorId) {
        meta.setCreatorId(creatorId);
    }

    public void setCreator(UserDomainObject creator) {
        setCreatorId(creator.getId());
    }

    public String getHeadline() {
        return appearance.getHeadline();
    }

    public void setHeadline(String v) {
        setCommonContent(DocumentCommonContent.builder(getCommonContent()).headline(v).build());
    }

    public int getId() {
        Integer id = meta.getId();
        return id == null ? ID_NEW : id;
    }

    public void setId(int id) {
        meta.setId(id == ID_NEW ? null : id);
    }

    public String getMenuImage() {
        return appearance.getMenuImageURL();
    }

    public void setMenuImage(String v) {
        setCommonContent(DocumentCommonContent.builder(getCommonContent()).menuImageURL(v).build());
    }


    public Set<String> getKeywords() {
        return meta.getKeywords();
    }


    public void setKeywords(Set<String> keywords) {
        meta.setKeywords(keywords);
    }

    public void setProperties(Map<String, String> properties) {
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
        return appearance.getMenuText();
    }


    public void setMenuText(String v) {
        setCommonContent(DocumentCommonContent.builder(getCommonContent()).menuText(v).build());
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
        return hasBeenArchivedAtTime(meta, new Date());
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
        return isPublishedAtTime(meta, new Date());
    }

    public boolean isActive() {
        return isActiveAtTime(meta, new Date());
    }

    private static boolean isActiveAtTime(Meta meta, Date now) {
        return isPublishedAtTime(meta, now) && !hasBeenArchivedAtTime(meta, now);
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
        meta.getCategoryIds().add(categoryId);
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

    private static boolean hasBeenArchivedAtTime(Meta meta, Date time) {
        Date archivedDatetime = meta.getArchivedDatetime();
        return archivedDatetime != null && archivedDatetime.before(time);
    }

    public void removeAllCategories() {
        meta.setCategoryIds(new HashSet<Integer>());
    }

    public void removeCategoryId(int categoryId) {
        meta.getCategoryIds().remove(categoryId);
    }

    public void setDocumentPermissionSetTypeForRoleId(RoleId roleId,
                                                      DocumentPermissionSetTypeDomainObject permissionSetType) {
        getRolePermissionMappings().setPermissionSetTypeForRole(roleId, permissionSetType);
    }

    public DocumentPermissionSetTypeDomainObject getDocumentPermissionSetTypeForRoleId(RoleId roleId) {
        return getRolePermissionMappings().getPermissionSetTypeForRole(roleId);
    }

    private static boolean isPublishedAtTime(Meta meta, Date date) {
        boolean statusIsApproved = Document.PublicationStatus.APPROVED.equals(meta.getPublicationStatus());

        return statusIsApproved && publicationHasStartedAtTime(meta, date) && !publicationHasEndedAtTime(meta, date);
    }

    private static boolean publicationHasStartedAtTime(Meta meta, Date date) {
        Date publicationStartDatetime = meta.getPublicationStartDatetime();
        return publicationStartDatetime != null && publicationStartDatetime.before(date);
    }

    private static boolean publicationHasEndedAtTime(Meta meta, Date date) {
        Date publicationEndDatetime = meta.getPublicationEndDatetime();
        return publicationEndDatetime != null && publicationEndDatetime.before(date);
    }

    public DocumentPermissionSets getPermissionSets() {
        return meta.getPermissionSets();
    }

    public DocumentPermissionSets getPermissionSetsForNewDocuments() {
        return meta.getPermissionSetsForNewDocuments();
    }

    public abstract void accept(DocumentVisitor documentVisitor);

    public LifeCyclePhase getLifeCyclePhase() {
        return getLifeCyclePhaseAtTime(this, new Date());
    }

    public static LifeCyclePhase getLifeCyclePhaseAtTime(DocumentDomainObject doc, Date time) {
        Meta meta = doc.getMeta();
        LifeCyclePhase lifeCyclePhase;

        Document.PublicationStatus publicationStatus = meta.getPublicationStatus();
        if (publicationStatus == Document.PublicationStatus.NEW) {
            lifeCyclePhase = LifeCyclePhase.NEW;
        } else if (publicationStatus == Document.PublicationStatus.DISAPPROVED) {
            lifeCyclePhase = LifeCyclePhase.DISAPPROVED;
        } else {
            if (publicationHasEndedAtTime(meta, time)) {
                lifeCyclePhase = LifeCyclePhase.UNPUBLISHED;
            } else if (publicationHasStartedAtTime(meta, time)) {
                if (hasBeenArchivedAtTime(meta, time)) {
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

    public void setCategoryIds(Set<Integer> categoryIds) {
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
        Objects.requireNonNull(meta, "meta argument can not be null.");

        this.meta = meta.clone();
    }

    public DocumentLanguage getLanguage() {
        return language;
    }

    public void setLanguage(DocumentLanguage language) {
        Objects.requireNonNull(language, "language argument can not be null.");

        this.language = language;
    }


    public DocumentCommonContent getCommonContent() {
        return appearance;
    }

    public void setCommonContent(DocumentCommonContent appearance) {
        Objects.requireNonNull(appearance, "commonContent argument can not be null.");

        this.appearance = appearance;
    }

    public boolean isNew() {
        return getId() == ID_NEW;
    }
}