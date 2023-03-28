package imcode.server.document;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.document.textdocument.CopyableHashMap;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;
import imcode.util.LazilyLoadedObject;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;

public abstract class DocumentDomainObject implements Cloneable, Serializable {

    public static final int ID_NEW = 0;
    public static final String DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS = "imcms.document.alias";
    public static final String DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_MODIFIED_BY = "imcms.document.modified_by";
    private static Logger log = LogManager.getLogger(DocumentDomainObject.class);
    protected Attributes attributes = new Attributes();

    public static DocumentDomainObject fromDocumentTypeId(int documentTypeId) {
        DocumentDomainObject document;

        switch (documentTypeId) {
            case DocumentTypeDomainObject.TEXT_ID:
                document = new TextDocumentDomainObject();
                break;
            case DocumentTypeDomainObject.URL_ID:
                document = new UrlDocumentDomainObject();
                break;
            case DocumentTypeDomainObject.BROWSER_ID:
                document = new BrowserDocumentDomainObject();
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

        return document;
    }

    public Object clone() throws CloneNotSupportedException {
        DocumentDomainObject clone = (DocumentDomainObject) super.clone();
        if (null != attributes) {
            clone.attributes = (Attributes) attributes.clone();
        }
        return clone;
    }

    public Date getArchivedDatetime() {
        return attributes.archivedDatetime;
    }

    public void setArchivedDatetime(Date v) {
        attributes.archivedDatetime = v;
    }

    public Set<Integer> getCategoryIds() {
        return Collections.unmodifiableSet(attributes.categoryIds.get());
    }

    public Date getCreatedDatetime() {
        return attributes.createdDatetime;
    }

    public void setCreatedDatetime(Date v) {
        attributes.createdDatetime = v;
    }

    public int getCreatorId() {
        return attributes.creatorId;
    }

    public void setCreatorId(int creatorId) {
        attributes.creatorId = creatorId;
    }

    /**
     * @return a user's id who modified document or null if there is no such data.
     */
    public Integer getModifierId() {
        try {
            return Integer.valueOf(getProperty(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_MODIFIED_BY));
        } catch (Exception e) {
            return null;
        }
    }

    public void setCreator(UserDomainObject creator) {
        setCreatorId(creator.getId());
    }

    public String getHeadline() {
        return attributes.headline;
    }

    public void setHeadline(String v) {
        attributes.headline = v;
    }

    public int getId() {
        return attributes.id;
    }

    public void setId(int v) {
        attributes.id = v;
    }

    public String getMenuImage() {
        return attributes.image;
    }

    public void setMenuImage(String v) {
        attributes.image = v;
    }

    public Set<String> getKeywords() {
        return Collections.unmodifiableSet(attributes.keywords.get());
    }

    public void setKeywords(Set<String> keywords) {
        attributes.keywords.set(new CopyableHashSet<>(keywords));
    }

    public Map<String, String> getProperties() {
        return new HashMap<>(attributes.properties.get());
    }

    public void setProperties(Map<String, String> properties) {
        attributes.properties.set(new CopyableHashMap<>(properties));
    }

    public String getProperty(String key) {
        Map<String, String> properties = attributes.properties.get();
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        Map<String, String> properties = attributes.properties.get();
        properties.put(key, value);
    }

    public void removeProperty(String key) {
        Map<String, String> properties = attributes.properties.get();
        properties.remove(key);
    }

    public String getLanguageIso639_2() {
        return attributes.languageIso639_2;
    }

    public void setLanguageIso639_2(String languageIso639_2) {
        attributes.languageIso639_2 = languageIso639_2;
    }

    public String getMenuText() {
        return attributes.menuText;
    }

    public void setMenuText(String v) {
        attributes.menuText = v;
    }

    public Date getModifiedDatetime() {
        return attributes.modifiedDatetime;
    }

    public void setModifiedDatetime(Date v) {
        attributes.modifiedDatetime = v;
    }

    public Date getActualModifiedDatetime() {
        return attributes.actualModifiedDatetime;
    }

    public void setActualModifiedDatetime(Date modifiedDatetime) {
        this.attributes.actualModifiedDatetime = modifiedDatetime;
    }

    public Date getPublicationEndDatetime() {
        return attributes.publicationEndDatetime;
    }

    public void setPublicationEndDatetime(Date datetime) {
        attributes.publicationEndDatetime = datetime;
    }

	public boolean isExportAllowed() {
		return attributes.exportAllowed;
	}

	public void setExportAllowed(boolean allowedToExport) {
		attributes.exportAllowed = allowedToExport;
	}

	public boolean isExported() {
		return attributes.exported;
	}

	public void setExported(boolean exported) {
		attributes.exported = exported;
	}

    public Date getPublicationStartDatetime() {
        return attributes.publicationStartDatetime;
    }

    public void setPublicationStartDatetime(Date v) {
        attributes.publicationStartDatetime = v;
    }

    public Integer getPublisherId() {
        return attributes.publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        attributes.publisherId = publisherId;
    }

    public void setPublisher(UserDomainObject user) {
        setPublisherId(user.getId());
    }

    public RoleIdToDocumentPermissionSetTypeMappings getRoleIdsMappedToDocumentPermissionSetTypes() {
        return getRolePermissionMappings().clone();
    }

    public void setRoleIdsMappedToDocumentPermissionSetTypes(RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings) {
        attributes.roleIdToDocumentPermissionSetTypeMappings.set(roleIdToDocumentPermissionSetTypeMappings.clone());
    }

    private RoleIdToDocumentPermissionSetTypeMappings getRolePermissionMappings() {
        return attributes.roleIdToDocumentPermissionSetTypeMappings.get();
    }

    public Set<Integer> getSectionIds() {
        return Collections.unmodifiableSet(attributes.sectionIds.get());
    }

    public void setSectionIds(Set<Integer> sectionIds) {
        attributes.sectionIds.set(new CopyableHashSet<>(sectionIds));
    }

    public Document.PublicationStatus getPublicationStatus() {
        return attributes.publicationStatus;
    }

    public void setPublicationStatus(Document.PublicationStatus status) {
        if (null == status) {
            throw new NullArgumentException("status");
        }
        attributes.publicationStatus = status;
    }

    public String getTarget() {
        return attributes.target;
    }

    public void setTarget(String v) {
        attributes.target = v;
    }

    public boolean isArchived() {
        return hasBeenArchivedAtTime(new Date());
    }

    public boolean isLinkableByOtherUsers() {
        return attributes.linkableByOtherUsers;
    }

    public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
        attributes.linkableByOtherUsers = linkableByOtherUsers;
    }

    public boolean isRestrictedOneMorePrivilegedThanRestrictedTwo() {
        return attributes.restrictedOneMorePrivilegedThanRestrictedTwo;
    }

    public void setRestrictedOneMorePrivilegedThanRestrictedTwo(boolean b) {
        attributes.restrictedOneMorePrivilegedThanRestrictedTwo = b;
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
        return attributes.searchDisabled;
    }

    public void setSearchDisabled(boolean searchDisabled) {
        attributes.searchDisabled = searchDisabled;
    }

    public boolean isLinkedForUnauthorizedUsers() {
        return attributes.linkedForUnauthorizedUsers;
    }

    public void setLinkedForUnauthorizedUsers(boolean linkedForUnauthorizedUsers) {
        attributes.linkedForUnauthorizedUsers = linkedForUnauthorizedUsers;
    }

    public void addCategoryId(int categoryId) {
        Set<Integer> categoryIds = attributes.categoryIds.get();
        categoryIds.add(categoryId);
    }

    public void addSectionId(int sectionId) {
        Set<Integer> sectionIds = attributes.sectionIds.get();
        sectionIds.add(sectionId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentDomainObject)) {
            return false;
        }

        final DocumentDomainObject document = (DocumentDomainObject) o;

        return attributes.id == document.attributes.id;

    }

    public abstract DocumentTypeDomainObject getDocumentType();

    public final int getDocumentTypeId() {
        return getDocumentType().getId();
    }

    public final LocalizedMessage getDocumentTypeName() {
        return getDocumentType().getName();
    }

    public int hashCode() {
        return attributes.id;
    }

    private boolean hasBeenArchivedAtTime(Date time) {
        Date archivedDatetime = this.attributes.archivedDatetime;
        return archivedDatetime != null && archivedDatetime.before(time);
    }

    public void removeAllCategories() {
        attributes.categoryIds.set(new CopyableHashSet<>());
    }

    public void removeAllSections() {
        attributes.sectionIds.set(new CopyableHashSet<>());
    }

    public void removeCategoryId(int categoryId) {
        Set<Integer> categoryIds = attributes.categoryIds.get();
        categoryIds.remove(categoryId);
    }

    public void setDocumentPermissionSetTypeForRoleId(RoleId roleId, DocumentPermissionSetTypeDomainObject permissionSetType) {
        getRolePermissionMappings().setPermissionSetTypeForRole(roleId, permissionSetType);
    }

    public DocumentPermissionSetTypeDomainObject getDocumentPermissionSetTypeForRoleId(RoleId roleId) {
        return getRolePermissionMappings().getPermissionSetTypeForRole(roleId);
    }

    private boolean isPublishedAtTime(Date date) {
        boolean statusIsApproved = Document.PublicationStatus.APPROVED.equals(getPublicationStatus());
        return statusIsApproved && publicationHasStartedAtTime(date)
                && !publicationHasEndedAtTime(date);
    }

    private boolean publicationHasStartedAtTime(Date date) {
        Date publicationStartDatetime = attributes.publicationStartDatetime;
        return publicationStartDatetime != null && publicationStartDatetime.before(date);
    }

    private boolean publicationHasEndedAtTime(Date date) {
        Date publicationEndDatetime = attributes.publicationEndDatetime;
        return publicationEndDatetime != null && publicationEndDatetime.before(date);
    }

    public DocumentPermissionSets getPermissionSets() {
        return this.attributes.permissionSets.get();
    }

    public DocumentPermissionSets getPermissionSetsForNewDocuments() {
        return this.attributes.permissionSetsForNewDocuments.get();
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public abstract void accept(DocumentVisitor documentVisitor);

    public LifeCyclePhase getLifeCyclePhase() {
        return getLifeCyclePhaseAtTime(new Date());
    }

    LifeCyclePhase getLifeCyclePhaseAtTime(Date time) {
        LifeCyclePhase lifeCyclePhase;
        Document.PublicationStatus publicationStatus = getPublicationStatus();
        if (Document.PublicationStatus.NEW.equals(publicationStatus)) {
            lifeCyclePhase = LifeCyclePhase.NEW;
        } else if (Document.PublicationStatus.DISAPPROVED.equals(publicationStatus)) {
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

    public void setLazilyLoadedSectionIds(LazilyLoadedObject<CopyableHashSet<Integer>> sectionIds) {
        attributes.sectionIds = sectionIds;
    }

    public void setLazilyLoadedKeywords(LazilyLoadedObject<CopyableHashSet<String>> keywords) {
        attributes.keywords = keywords;
    }

    public void setLazilyLoadedProperties(LazilyLoadedObject<CopyableHashMap<String, String>> properties) {
        attributes.properties = properties;
    }

    public void setLazilyLoadedCategoryIds(LazilyLoadedObject<CopyableHashSet<Integer>> categoryIds) {
        attributes.categoryIds = categoryIds;
    }

    public void setLazilyLoadedRoleIdsMappedToDocumentPermissionSetTypes(LazilyLoadedObject<RoleIdToDocumentPermissionSetTypeMappings> rolePermissionMappings) {
        attributes.roleIdToDocumentPermissionSetTypeMappings = rolePermissionMappings;
    }

    public void setLazilyLoadedPermissionSets(LazilyLoadedObject<DocumentPermissionSets> permissionSets) {
        attributes.permissionSets = permissionSets;
    }

    public void setLazilyLoadedPermissionSetsForNew(LazilyLoadedObject<DocumentPermissionSets> permissionSetsForNew) {
        attributes.permissionSetsForNewDocuments = permissionSetsForNew;
    }

    public void loadAllLazilyLoaded() {
        attributes.categoryIds.load();
        attributes.sectionIds.load();
        attributes.keywords.load();
        attributes.permissionSets.load();
        attributes.permissionSetsForNewDocuments.load();
        attributes.roleIdToDocumentPermissionSetTypeMappings.load();
        attributes.properties.load();
    }

    public String getAlias() {
        return getProperty(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
    }

    public void setAlias(String alias) {
        if (alias == null) {
            removeProperty(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
        } else {
            setProperty(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, alias);
        }
    }

    public String getName() {
        return StringUtils.defaultString(getAlias(), getId() + "");
    }

    public static class Attributes implements Cloneable, Serializable {

        private Date archivedDatetime;

        private Date createdDatetime;

        private int creatorId;

        private String headline = "";

        private String image;
        private String languageIso639_2;
        private boolean linkableByOtherUsers;
        private String menuText = "";
        private int id;
        private Date modifiedDatetime;
        private Date actualModifiedDatetime;
        private boolean restrictedOneMorePrivilegedThanRestrictedTwo;
        private Date publicationStartDatetime;
        private Date publicationEndDatetime;
		private boolean exportAllowed;
		private boolean exported;
        private Integer publisherId;
        private boolean searchDisabled;
        private Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;
        private String target;
        private boolean linkedForUnauthorizedUsers;

        private LazilyLoadedObject<CopyableHashSet<Integer>> categoryIds = new LazilyLoadedObject<>(new CopyableHashSetLoader<>());

        private LazilyLoadedObject<CopyableHashSet<String>> keywords = new LazilyLoadedObject<>(new CopyableHashSetLoader<>());
        private LazilyLoadedObject<CopyableHashSet<Integer>> sectionIds = new LazilyLoadedObject<>(new CopyableHashSetLoader<>());
        private LazilyLoadedObject<DocumentPermissionSets> permissionSets = new LazilyLoadedObject<>(new DocumentPermissionSetsLoader());
        private LazilyLoadedObject<DocumentPermissionSets> permissionSetsForNewDocuments = new LazilyLoadedObject<>(new DocumentPermissionSetsLoader());
        private LazilyLoadedObject<CopyableHashMap<String, String>> properties = new LazilyLoadedObject<>(new CopyableHashMapLoader<>());

        private LazilyLoadedObject<RoleIdToDocumentPermissionSetTypeMappings> roleIdToDocumentPermissionSetTypeMappings = new LazilyLoadedObject<>(RoleIdToDocumentPermissionSetTypeMappings::new);

        public Object clone() throws CloneNotSupportedException {
            Attributes clone = (Attributes) super.clone();
            clone.keywords = keywords.clone();
            clone.properties = properties.clone();
            clone.sectionIds = sectionIds.clone();
            clone.categoryIds = categoryIds.clone();
            clone.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings.clone();
            clone.permissionSets = permissionSets.clone();
            clone.permissionSetsForNewDocuments = permissionSetsForNewDocuments.clone();
            return clone;
        }

        private static class CopyableHashSetLoader<K> implements LazilyLoadedObject.Loader<CopyableHashSet<K>> {

            public CopyableHashSet<K> load() {
                return new CopyableHashSet<>();
            }
        }

        public static class CopyableHashMapLoader<K, V> implements LazilyLoadedObject.Loader<CopyableHashMap<K, V>> {

            public CopyableHashMap<K, V> load() {
                return new CopyableHashMap<>();
            }
        }

        private static class DocumentPermissionSetsLoader implements LazilyLoadedObject.Loader<DocumentPermissionSets> {

            public DocumentPermissionSets load() {
                return new DocumentPermissionSets();
            }
        }
    }

}
