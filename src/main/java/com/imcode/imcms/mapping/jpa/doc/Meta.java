package com.imcode.imcms.mapping.jpa.doc;

import javax.persistence.*;
import java.util.*;

/**
 * Document's meta.
 * <p/>
 * Shared by all versions of the same document.
 */
@Entity
@Table
public class Meta {

    /**
     * Create (create only!) permission for template or a document type.
     * <p/>
     * set_id (actually it is a 'set *type* id') can be: restricted 1 or restricted 2
     * <p/>
     * Mapped to doc_permission_set and new_doc_permission_set
     */
    @Embeddable
    static public class PermissionSetEx {

        @Column(name = "set_id")
        private Integer setId;

        /**
         * Document type (1 2 5 7 8) or template group id
         */
        @Column(name = "permission_data")
        private Integer permissionData;

        /**
         * For documents: DatabaseDocumentGetter.PERM_CREATE_DOCUMENT
         * For templates: TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID
         * ?Bit set value?
         */
        @Column(name = "permission_id")
        private Integer permissionId;

        @Override
        public boolean equals(Object o) {
            return (this == o) ||
                    ((o instanceof PermissionSetEx) && (hashCode() == o.hashCode()));
        }

        @Override
        public int hashCode() {
            return Objects.hash(setId, permissionId, permissionData);
        }

        public Integer getPermissionData() {
            return permissionData;
        }

        public void setPermissionData(Integer documentTypeId) {
            this.permissionData = documentTypeId;
        }

        public Integer getPermissionId() {
            return permissionId;
        }

        public void setPermissionId(Integer permissionId) {
            this.permissionId = permissionId;
        }

        public Integer getSetId() {
            return setId;
        }

        public void setSetId(Integer setId) {
            this.setId = setId;
        }
    }

    /**
     * Document show setting for disabled language.
     */
    public static enum DisabledLanguageShowSetting {
        SHOW_IN_DEFAULT_LANGUAGE,
        DO_NOT_SHOW,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meta_id")
    private Integer id;

    @Column(name = "default_version_no", nullable = false)
    private int defaultVersionNo;


    /**
     * Disabled language's content show rule.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "disabled_language_show_rule", nullable = false)
    private DisabledLanguageShowSetting disabledLanguageShowSetting = DisabledLanguageShowSetting.DO_NOT_SHOW;

    // CHECKED
    // DEPRECATED?
    // RB4: this field is never used or referenced, it is merely set to '1' at the insert.
    @Column(name = "activate", nullable = false, updatable = false)
    private Integer activate = 1;

    // The following fields are mapped to document attributes:
    // CHECKED
    @Column(name = "doc_type", nullable = false, updatable = false)
    // For processing after load:
    // Discrimination column - from old code:
    // DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(permissionData);
    // todo: rename to documentTypeId
    private Integer documentType;

    // CHECKED
    @Column(name = "owner_id", nullable = false)
    private Integer creatorId;

    // CHECKED
    @Column(name = "permissions", nullable = false)
    private Boolean restrictedOneMorePrivilegedThanRestrictedTwo;

    // CHECKED
    @Column(name = "shared", nullable = false)
    private Boolean linkableByOtherUsers;

    // CHECKED	
    @Column(name = "show_meta", nullable = false)
    private Boolean linkedForUnauthorizedUsers;

    /**
     * Deprecated with no replacement.
     */
    @Deprecated
    @Column(name = "lang_prefix", nullable = false)
    @SuppressWarnings("unused")
    private String lang_prefix = "";

    // CHECKED	
    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDatetime;

    // CHECKED	
    @Column(name = "date_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDatetime;

    // CHECKED	
    @Column(name = "disable_search", nullable = false)
    private boolean searchDisabled;

    // CHECKED	
    @Column(name = "target", nullable = false)
    private String target;

    @Column(name = "archived_datetime", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedDatetime;

    // CHECKED	
    @Column(name = "publisher_id", nullable = true)
    private Integer publisherId;

    // CHECKED

    // For processing after load:
    // Should be converted after set - old code: 
    // Document.PublicationStatus publicationStatus = publicationStatusFromInt(publicationStatusInt);
    // document.setPublicationStatus(publicationStatus); 
    @Column(name = "status", nullable = true)
    private Integer publicationStatusInt;

    // CHECKED
    @Column(name = "publication_start_datetime", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationStartDatetime;

    // CHECKED
    @Column(name = "publication_end_datetime", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationEndDatetime;

    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "document_properties", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "key_name")
    @Column(name = "value", nullable = false)
    private Map<String, String> properties = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "document_categories", joinColumns = @JoinColumn(name = "meta_id"))
    @Column(name = "category_id", nullable = false)
    private Set<Integer> categoryIds = new HashSet<>();

    // Set id is either restricted 1 or restricted 2
    // Roles are user defined or system predefined roles
    // RoleId to permission-set id mapping.  
    // For processing after load:
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "roles_rights", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "role_id")
    @Column(name = "set_id")
    private Map<Integer, Integer> roleIdToPermissionSetIdMap = new HashMap<>();


    /**
     * Limited 1 permission set's bits for new document.
     * Permission set id mapped to bits.
     */
    // For processing after load:
    // permisionId in the table actually is not an item 'id' but a bit set value.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doc_permission_sets", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "set_id")
    @Column(name = "permission_id")
    private Map<Integer, Integer> permissionSetBitsMap = new HashMap<>();


    /**
     * Limited 1 permission set's bits for a new (inherited) document.
     * Permission set id mapped to bits.
     */
    // For processing after load:
    // permisionId in the table actually is not an 'id' but a bit set value.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "new_doc_permission_sets", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "set_id")
    @Column(name = "permission_id")
    private Map<Integer, Integer> permissionSetBitsForNewMap = new HashMap<>();

    /**
     *
     */
    // For processing after load:
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doc_permission_sets_ex", joinColumns = @JoinColumn(name = "meta_id"))
    private Set<PermissionSetEx> permissionSetEx = new HashSet<>();


    // For processing after load:
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "new_doc_permission_sets_ex", joinColumns = @JoinColumn(name = "meta_id"))
    private Set<PermissionSetEx> permissionSetExForNew = new HashSet<>();


    /**
     * Enabled languages - might be empty.
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "imcms_doc_languages",
            joinColumns = @JoinColumn(name = "doc_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> enabledLanguages = new HashSet<>();


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "imcms_doc_keywords", joinColumns = @JoinColumn(name = "doc_id"))
    @Column(name = "value")
    private Set<String> keywords = new HashSet<>();

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

    public Integer getPublicationStatusInt() {
        return publicationStatusInt;
    }

    public void setPublicationStatusInt(Integer publicationStatusInt) {
        this.publicationStatusInt = publicationStatusInt;
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

    // For processing after load:
    public Map<Integer, Integer> getRoleIdToPermissionSetIdMap() {
        return roleIdToPermissionSetIdMap;
    }

    public void setRoleIdToPermissionSetIdMap(Map<Integer, Integer> roleRights) {
        this.roleIdToPermissionSetIdMap = roleRights;
    }

    public Map<Integer, Integer> getPermissionSetBitsMap() {
        return permissionSetBitsMap;
    }

    public void setPermissionSetBitsMap(Map<Integer, Integer> permissionSetBits) {
        this.permissionSetBitsMap = permissionSetBits;
    }

    public Map<Integer, Integer> getPermissionSetBitsForNewMap() {
        return permissionSetBitsForNewMap;
    }

    public void setPermissionSetBitsForNewMap(Map<Integer, Integer> permissionSetBitsForNew) {
        this.permissionSetBitsForNewMap = permissionSetBitsForNew;
    }

    public Set<PermissionSetEx> getPermissionSetEx() {
        return permissionSetEx;
    }

    public void setPermissionSetEx(Set<PermissionSetEx> permissionSetEx) {
        this.permissionSetEx = permissionSetEx;
    }

    public Set<PermissionSetEx> getPermissionSetExForNew() {
        return permissionSetExForNew;
    }

    public void setPermissionSetExForNew(Set<PermissionSetEx> docPermissionSetExForNew) {
        this.permissionSetExForNew = docPermissionSetExForNew;
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

    public Set<Language> getEnabledLanguages() {
        return enabledLanguages;
    }

    public void setEnabledLanguages(Set<Language> languages) {
        this.enabledLanguages = languages != null ? languages : new HashSet<Language>();
    }

    public Integer getDefaultVersionNo() {
        return defaultVersionNo;
    }

    public void setDefaultVersionNo(Integer defaultVersionNo) {
        this.defaultVersionNo = defaultVersionNo;
    }
}