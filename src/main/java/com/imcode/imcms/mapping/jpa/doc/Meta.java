package com.imcode.imcms.mapping.jpa.doc;

import com.imcode.imcms.persistence.entity.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Document's meta.
 * <p/>
 * Shared by all versions of the same document.
 */
@Entity
@Table(name = "meta")
@Data
@NoArgsConstructor
public class Meta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meta_id")
    private Integer id;

    @Column(name = "default_version_no", nullable = false)
    private int defaultVersionNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "disabled_language_show_rule", nullable = false)
    private DisabledLanguageShowMode disabledLanguageShowMode = DisabledLanguageShowMode.DO_NOT_SHOW;

    /**
     * DEPRECATED?
     * RB4: this field is never used or referenced, it is merely set to '1' at the insert.
     */
    @Column(name = "activate", nullable = false, updatable = false)
    private Integer activate = 1;

    @Column(name = "doc_type", nullable = false, updatable = false)
    @Enumerated(EnumType.ORDINAL)
    private DocumentType documentType;

    @Column(name = "owner_id", nullable = false)
    private Integer creatorId;

    @Column(name = "permissions", nullable = false, columnDefinition = "int")
    private Boolean restrictedOneMorePrivilegedThanRestrictedTwo;

    @Column(name = "shared", nullable = false, columnDefinition = "int")
    private Boolean linkableByOtherUsers;

    @Column(name = "show_meta", nullable = false, columnDefinition = "int")
    private Boolean linkedForUnauthorizedUsers;

    /**
     * Deprecated with no replacement.
     */
    @Deprecated
    @Column(name = "lang_prefix", nullable = false)
    @SuppressWarnings("unused")
    private String lang_prefix = "";

    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDatetime;

    @Column(name = "date_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDatetime;

    @Column(name = "disable_search", nullable = false, columnDefinition = "int")
    private boolean searchDisabled;

    @Column(name = "target", nullable = false)
    private String target;

    @Column(name = "archived_datetime", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedDatetime;

    @Column(name = "archiver_id")
    private Integer archiverId;

    @Column(name = "depublisher_id")
    private Integer depublisherId;

    @Column(name = "publisher_id", nullable = true)
    private Integer publisherId;

    @Column(name = "status", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private PublicationStatus publicationStatus;

    @Column(name = "publication_start_datetime", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationStartDatetime;

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

    /**
     * @see com.imcode.imcms.persistence.entity.Role#id as key
     * @see Permission#ordinal() as value
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "roles_rights", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "role_id")
    @Column(name = "set_id", columnDefinition = "smallint")
    private Map<Integer, Integer> roleIdToPermissionSetIdMap = new HashMap<>();

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

    // TODO: 20.10.17 Delete field mapping and table
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doc_permission_sets", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "set_id")
    @Column(name = "permission_id")
    private Map<Integer, Integer> permissionSetBitsMap = new HashMap<>();

    // TODO: 20.10.17 Delete field mapping and table
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "new_doc_permission_sets", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "set_id")
    @Column(name = "permission_id")
    private Map<Integer, Integer> permissionSetBitsForNewMap = new HashMap<>();

    // TODO: 20.10.17 Delete mapping and table
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doc_permission_sets_ex", joinColumns = @JoinColumn(name = "meta_id"))
    private Set<PermissionSetEx> permissionSetEx = new HashSet<>();

    // TODO: 20.10.17 Delete mapping and table
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "new_doc_permission_sets_ex", joinColumns = @JoinColumn(name = "meta_id"))
    private Set<PermissionSetEx> permissionSetExForNew = new HashSet<>();

    /**
     * DocumentType database column has int value. It takes from {@link Enum#ordinal}.
     * Do not change order of enum constants.
     */
    public enum DocumentType {
        FILE,
        HTML,
        TEXT,
        URL
    }

    /**
     * PublicationStatus database column has int value. It takes from {@link Enum#ordinal}.
     * Do not change order of enum constants.
     */
    public enum PublicationStatus {
        NEW,
        DISAPPROVED,
        APPROVED
    }

    /**
     * Document show mode for disabled language.
     */
    public enum DisabledLanguageShowMode {
        SHOW_IN_DEFAULT_LANGUAGE,
        DO_NOT_SHOW,
    }

    /**
     * Permissions for document access.
     * Permission strictness defined by descending {@link Permission#ordinal()}.
     * Do not change order of enum constants.
     */
    public enum Permission {
        EDIT,
        RESTRICTED_1,
        RESTRICTED_2,
        VIEW;

        public static Permission fromOrdinal(int ordinal) {
            return values()[ordinal];
        }

        public boolean isMorePrivilegedThan(Permission permission) {
            return ordinal() < permission.ordinal();
        }

        public boolean isAtLeastAsPrivilegedAs(Permission permission) {
            return ordinal() <= permission.ordinal();
        }
    }

    /**
     * Create (create only!) permission for template or a document type.
     * <p/>
     * set_id (actually it is a 'set *type* id') can be: restricted 1 or restricted 2
     * <p/>
     * Mapped to doc_permission_set and new_doc_permission_set
     */
    @Embeddable
    @Data
    public static class PermissionSetEx {

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

    }
}