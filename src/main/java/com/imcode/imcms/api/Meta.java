package com.imcode.imcms.api;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSets;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import org.apache.commons.lang.NullArgumentException;

import java.io.Serializable;
import java.util.*;

/**
 * Document's meta.
 * <p/>
 * Shared by all versions of the same document.
 */
public class Meta implements Serializable, Cloneable {

    /**
     * Create (create only!) permission for template or a document type.
     * <p/>
     * set_id (actually it is a 'set *type* id') can be: restricted 1 or restricted 2
     * <p/>
     * Mapped to doc_permission_set and new_doc_permission_set
     */
//    static public class PermisionSetEx {
//        private Integer setId;
//
//        /**
//         * Document type (1 2 5 7 8) or template group id
//         */
//        private Integer permissionData;
//
//        /**
//         * For documents: DatabaseDocumentGetter.PERM_CREATE_DOCUMENT
//         * For templates: TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID
//         * ?Bit set value?
//         */
//        private Integer permissionId;
//
//        public boolean equals(Object o) {
//            return (this == o) ||
//                    ((o instanceof PermisionSetEx) && (hashCode() == o.hashCode()));
//        }
//
//        public int hashCode() {
//            return Objects.hash(setId, permissionId, permissionData);
//        }
//
//        public Integer getPermissionData() {
//            return permissionData;
//        }
//
//        public void setPermissionData(Integer documentTypeId) {
//            this.permissionData = documentTypeId;
//        }
//
//        public Integer getPermissionId() {
//            return permissionId;
//        }
//
//        public void setPermissionId(Integer permissionId) {
//            this.permissionId = permissionId;
//        }
//
//        public Integer getSetId() {
//            return setId;
//        }
//
//        public void setSetId(Integer setId) {
//            this.setId = setId;
//        }
//    }

    /**
     * Document show setting for disabled language.
     */
    public static enum DisabledLanguageShowSetting {
        SHOW_IN_DEFAULT_LANGUAGE,
        DO_NOT_SHOW,
    }

    private volatile Integer id;

    private volatile int defaultVersionNo = DocumentVersion.WORKING_VERSION_NO;


    /**
     * Disabled language's content show rule.
     */
    private volatile DisabledLanguageShowSetting disabledLanguageShowSetting = DisabledLanguageShowSetting.DO_NOT_SHOW;

    private volatile Integer activate;

    // For processing after load:
    // Discrimination column - from old code:
    // DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(permissionData);
    // todo: rename to documentTypeId
    private volatile Integer documentType;

    private volatile Integer creatorId;

    private volatile Boolean restrictedOneMorePrivilegedThanRestrictedTwo;

    private volatile Boolean linkableByOtherUsers;

    private volatile Boolean linkedForUnauthorizedUsers;

    /**
     * Deprecated with no replacement.
     */
    @Deprecated
    @SuppressWarnings("unused")
    private volatile String lang_prefix = "";

    private volatile Date createdDatetime;

    private volatile Date modifiedDatetime;

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
    private volatile Date archivedDatetime;
    private volatile Integer publisherId;

    // For processing after load:
    // Should be converted after set - old code:
    // Document.PublicationStatus publicationStatus = publicationStatusFromInt(publicationStatusInt);
    // document.setPublicationStatus(publicationStatus);
    //private volatile Integer publicationStatusInt;
    private volatile Date publicationStartDatetime;
    private volatile Date publicationEndDatetime;

    // These fields were lazy loaded in previous version:
    private volatile Map<String, String> properties = new HashMap<>();

    private volatile Set<Integer> categoryIds = new HashSet<>();

    // Set id is either restricted 1 or restricted 2
    // Roles are user defined or system predefined roles
    // RoleId to permission-set id mapping.
    // For processing after load:
    //private volatile Map<Integer, Integer> roleIdToPermissionSetIdMap = new HashMap<>();


    /**
     * Limited 1 permission set's bits for new document.
     * Permission set id mapped to bits.
     */
    // For processing after load:
    // permisionId in the table actually is not an 'id' but a bit set value.
    //private volatile Map<Integer, Integer> permissionSetBitsMap = new HashMap<>();


    /**
     * Limited 1 permission set's bits for a new (inherited) document.
     * Permission set id mapped to bits.
     */
    // For processing after load:
    // permisionId in the table actually is not an 'id' but a bit set value.
    //private volatile Map<Integer, Integer> permissionSetBitsForNewMap = new HashMap<>();

    // For processing after load:
    //private volatile Set<PermisionSetEx> permisionSetEx = new HashSet<>();

    // For processing after load:
//    private volatile Set<PermisionSetEx> permisionSetExForNew = new HashSet<>();
    private volatile Set<DocumentLanguage> enabledLanguages = new HashSet<>();
    private volatile Set<String> keywords = new HashSet<>();


    //
    // Transients are moved from DocumentDomainObject.
    //

    private volatile DocumentPermissionSets permissionSets = new DocumentPermissionSets();

    private volatile DocumentPermissionSets permissionSetsForNewDocuments = new DocumentPermissionSets();

    private volatile RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings = new RoleIdToDocumentPermissionSetTypeMappings();

    private volatile Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;


    @Override
    public Meta clone() {
        try {
            Meta clone = (Meta) super.clone();

            clone.disabledLanguageShowSetting = disabledLanguageShowSetting;

            //clone.permisionSetEx = new HashSet<>(permisionSetEx);
//            clone.permisionSetExForNew = new HashSet<>(permisionSetExForNew);
            //clone.permissionSetBitsMap = new HashMap<>(permissionSetBitsMap);
            //clone.permissionSetBitsForNewMap = new HashMap<>(permissionSetBitsForNewMap);

            //clone.roleIdToPermissionSetIdMap = new HashMap<>(roleIdToPermissionSetIdMap);

            clone.properties = new HashMap<>(properties);
            clone.categoryIds = new HashSet<>(categoryIds);

            clone.keywords = new HashSet<>(keywords);
            clone.enabledLanguages = new HashSet<>(enabledLanguages);

            if (permissionSets != null) {
                clone.permissionSets = permissionSets.clone();
            }

            if (permissionSetsForNewDocuments != null) {
                clone.permissionSetsForNewDocuments = permissionSetsForNewDocuments.clone();
            }

            if (roleIdToDocumentPermissionSetTypeMappings != null) {
                clone.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings.clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public DisabledLanguageShowSetting getI18nShowSetting() {
        return disabledLanguageShowSetting;
    }

    public void setI18nShowSettings(DisabledLanguageShowSetting disabledLanguageShowSetting) {
        this.disabledLanguageShowSetting = disabledLanguageShowSetting;
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

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

//    public Integer getPublicationStatusInt() {
//        return publicationStatusInt;
//    }
//
//    public void setPublicationStatusInt(Integer publicationStatusInt) {
//        this.publicationStatusInt = publicationStatusInt;
//    }

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


    // Lazy loaded properties
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

//    // For processing after load:
//    public Map<Integer, Integer> getRoleIdToPermissionSetIdMap() {
//        return roleIdToPermissionSetIdMap;
//    }
//
//    public void setRoleIdToPermissionSetIdMap(Map<Integer, Integer> roleRights) {
//        this.roleIdToPermissionSetIdMap = roleRights;
//    }

//    public Map<Integer, Integer> getPermissionSetBitsMap() {
//        return permissionSetBitsMap;
//    }

//    public void setPermissionSetBitsMap(Map<Integer, Integer> permissionSetBits) {
//        this.permissionSetBitsMap = permissionSetBits;
//    }

//    public Map<Integer, Integer> getPermissionSetBitsForNewMap() {
//        return permissionSetBitsForNewMap;
//    }

//    public void setPermissionSetBitsForNewMap(Map<Integer, Integer> permissionSetBitsForNew) {
//        this.permissionSetBitsForNewMap = permissionSetBitsForNew;
//    }
//
//    public Set<PermisionSetEx> getPermisionSetEx() {
//        return permisionSetEx;
//    }
//
//    public void setPermisionSetEx(Set<PermisionSetEx> permisionSetEx) {
//        this.permisionSetEx = permisionSetEx;
//    }

//    public Set<PermisionSetEx> getPermisionSetExForNew() {
//        return permisionSetExForNew;
//    }

//    public void setPermisionSetExForNew(Set<PermisionSetEx> docPermisionSetExForNew) {
//        this.permisionSetExForNew = docPermisionSetExForNew;
//    }

    public Integer getActivate() {
        return activate;
    }

    public void setActivate(Integer activate) {
        this.activate = activate;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords != null ? keywords : new HashSet<String>();
    }

    public DisabledLanguageShowSetting getDisabledLanguageShowSetting() {
        return disabledLanguageShowSetting;
    }

    public void setDisabledLanguageShowSetting(DisabledLanguageShowSetting disabledLanguageShowSetting) {
        this.disabledLanguageShowSetting = disabledLanguageShowSetting;
    }

    public Set<DocumentLanguage> getEnabledLanguages() {
        return enabledLanguages;
    }

    public void setEnabledLanguages(Set<DocumentLanguage> languages) {
        this.enabledLanguages = languages != null ? languages : new HashSet<DocumentLanguage>();
    }

    // Transient properties
    public DocumentPermissionSets getPermissionSets() {
        return permissionSets;
    }

    public void setPermissionSets(DocumentPermissionSets permissionSets) {
        this.permissionSets = permissionSets;
    }

    public DocumentPermissionSets getPermissionSetsForNewDocuments() {
        return permissionSetsForNewDocuments;
    }

    public void setPermissionSetsForNew(DocumentPermissionSets permissionSetsForNew) {
        this.permissionSetsForNewDocuments = permissionSetsForNew;
    }

    public void setPermissionSetsForNewDocuments(DocumentPermissionSets permissionSetsForNewDocuments) {
        this.permissionSetsForNewDocuments = permissionSetsForNewDocuments;
    }

    public RoleIdToDocumentPermissionSetTypeMappings getRoleIdToDocumentPermissionSetTypeMappings() {
        return roleIdToDocumentPermissionSetTypeMappings;
    }

    public void setRoleIdToDocumentPermissionSetTypeMappings(RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings) {
        this.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings;
    }

    // transient
    public void setRoleIdsMappedToDocumentPermissionSetTypes(RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings) {
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
}
