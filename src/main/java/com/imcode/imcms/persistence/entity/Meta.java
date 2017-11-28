package com.imcode.imcms.persistence.entity;

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

    private static final long serialVersionUID = 9024338066876530277L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meta_id")
    private Integer id;

    @Column(name = "default_version_no", nullable = false)
    private int defaultVersionNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "disabled_language_show_rule", nullable = false)
    private DisabledLanguageShowMode disabledLanguageShowMode = DisabledLanguageShowMode.DO_NOT_SHOW;

    @Column(name = "doc_type", nullable = false, updatable = false)
    @Enumerated(EnumType.ORDINAL)
    private DocumentType documentType;

    @Column(name = "owner_id", nullable = false)
    private Integer creatorId;

    @Column(name = "date_created", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDatetime;

    @Column(name = "modifier_id", nullable = false)
    private Integer modifierId;

    @Column(name = "date_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDatetime;

    @Column(name = "archiver_id")
    private Integer archiverId;

    @Column(name = "archived_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedDatetime;

    @Column(name = "publisher_id")
    private Integer publisherId;

    @Column(name = "publication_start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationStartDatetime;

    @Column(name = "depublisher_id")
    private Integer depublisherId;

    @Column(name = "publication_end_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationEndDatetime;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private PublicationStatus publicationStatus;

    @Column(name = "shared", nullable = false, columnDefinition = "int")
    private Boolean linkableByOtherUsers;

    @Column(name = "show_meta", nullable = false, columnDefinition = "int")
    private Boolean linkedForUnauthorizedUsers;

    @Column(name = "disable_search", nullable = false, columnDefinition = "int")
    private boolean searchDisabled;

    @Column(name = "target", nullable = false)
    private String target;

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
     * @see RoleJPA#id as key
     * @see Permission as value
     */
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Permission.class)
    @CollectionTable(name = "roles_rights", joinColumns = @JoinColumn(name = "meta_id"))
    @MapKeyColumn(name = "role_id")
    @Column(name = "permission", columnDefinition = "VARCHAR(16)")
    @Enumerated(EnumType.STRING)
    private Map<Integer, Permission> roleIdToPermission = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "imcms_doc_keywords", joinColumns = @JoinColumn(name = "doc_id"))
    @Column(name = "value")
    private Set<String> keywords = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_doc_restricted_permissions",
            joinColumns = @JoinColumn(name = "meta_id")
    )
    @OrderColumn(name = "order_index")
    private Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();

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
     */
    public enum Permission {
        EDIT,
        RESTRICTED_1,
        RESTRICTED_2,
        VIEW,
        NONE;

        public int getId() {
            return ordinal();
        }

        public String getName() {
            return toString().toLowerCase();
        }

        public boolean isMorePrivilegedThan(Permission type) {
            return ordinal() < type.ordinal();
        }

        public boolean isAtLeastAsPrivilegedAs(Permission type) {
            return ordinal() <= type.ordinal();
        }
    }
}