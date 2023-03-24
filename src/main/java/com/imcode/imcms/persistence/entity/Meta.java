package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Category;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Document's meta.
 *
 * Shared by all versions of the same document.
 */
@Entity
@Table(name = "meta")
@Data
@NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Meta implements Serializable {

	private static final long serialVersionUID = 9024338066876530277L;
	private static final int MAX_TARGET_LENGTH = 10;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "meta_id")
	private Integer id;

	@Column(name = "default_version_no", nullable = false)
	private int defaultVersionNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "disabled_language_show_rule", nullable = false)
	private DisabledLanguageShowMode disabledLanguageShowMode = DisabledLanguageShowMode.DO_NOT_SHOW;

	@Column(name = "default_language_alias_enabled")
	private boolean defaultLanguageAliasEnabled = false;

	@Column(name = "doc_type", nullable = false, updatable = false)
	@Enumerated(EnumType.ORDINAL) // todo: change to EnumType.STRING
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

    @Column(name = "cache_for_unauthorized", nullable = false, columnDefinition = "int")
    private boolean cacheForUnauthorizedUsers;

    @Column(name = "cache_for_authorized", nullable = false, columnDefinition = "int")
    private boolean cacheForAuthorizedUsers;

    @Column(name = "disable_search", nullable = false, columnDefinition = "int")
    private boolean searchDisabled;

    @Column(name = "visible", nullable = false, columnDefinition = "int")
    private Boolean visible;

    // todo: transform to enum because only few values applicable
    @Column(name = "target", nullable = false, length = MAX_TARGET_LENGTH)
    private String target;

	@Column(name = "imported", nullable = false, columnDefinition = "int")
	private boolean imported;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "document_properties",
            joinColumns = @JoinColumn(name = "meta_id")
    )
    @MapKeyColumn(name = "key_name")
    @Column(name = "value", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Map<String, String> properties = new HashMap<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "document_categories",
            joinColumns = @JoinColumn(name = "meta_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", nullable = false)
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<CategoryJPA> categories = new HashSet<>();

    /**
     * @see RoleJPA#id as key
     * @see Permission as value
     */
    @ElementCollection(fetch = FetchType.EAGER, targetClass = Permission.class)
    @CollectionTable(
            name = "roles_rights",
            joinColumns = @JoinColumn(name = "meta_id")
    )
    @MapKeyColumn(name = "role_id")
    @Column(name = "permission", columnDefinition = "VARCHAR(16)")
    @Enumerated(EnumType.STRING)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Map<Integer, Permission> roleIdToPermission = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_doc_keywords",
            joinColumns = @JoinColumn(name = "doc_id")
    )
    @Column(name = "value")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<String> keywords = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "imcms_doc_restricted_permissions",
            joinColumns = @JoinColumn(name = "meta_id")
    )
    @OrderColumn(name = "order_index")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<RestrictedPermissionJPA> restrictedPermissions = new HashSet<>();

    /**
     * DocumentType database column has int value. It takes from {@link Enum#ordinal}.
     * Do not change order of enum constants.
     */
    public enum DocumentType {
        FILE,
        HTML,
        TEXT,
        URL;

	    private static final Map<String, DocumentType> rb4NameToDocumentTypeMap = new HashMap<>();

		static {
			rb4NameToDocumentTypeMap.put("File", FILE);
			rb4NameToDocumentTypeMap.put("HTML", HTML);
			rb4NameToDocumentTypeMap.put("Textpage", TEXT);
			rb4NameToDocumentTypeMap.put("External link", URL);
		}

		public static DocumentType getByRB4Name(String rb4Name){
			return rb4NameToDocumentTypeMap.get(rb4Name);
		}
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
     * Permission set type for document access.
     *
     * Permission set is assigned per role per document.
     *
     * @see imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings
     *
     * Permission set with lower type id (EDIT) is most privileged.
     * Any new permission defined in the system is automatically included into that set.
     *
     * Permission set with higher type id (NONE) has no privileges at all.
     * This set is always empty.
     *
     * VIEW permission set defines permissions only for document viewing.
     *
     * EDIT, VIEW and NONE sets are sealed - i.e each of them contains predefined and unmodifiable permissions.
     * Those sets are shared by all documents in a system.
     *
     * RESTRICTED_1 and RESTRICTED_2 are sets customizable per document,
     * however, they also contain the fixed subset of permissions - VIEW.
     * Additionally any document may extend a restricted set of permissions with permissions from the EDIT set.
     *
     * Please note:
     * By definition RESTRICTED_2 is more restrictive than RESTRICTED_1 but this can be changed at a document level (why?).
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
            return toString().toLowerCase().replace('_', ' ');
        }

	    public boolean isMorePrivilegedThan(Permission type) {
		    return ordinal() < type.ordinal();
	    }

	    public boolean isAtLeastAsPrivilegedAs(Permission type) {
		    return ordinal() <= type.ordinal();
	    }
    }

	public Set<Category> getCategories() {
		return new HashSet<>(this.categories);
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories.stream().map(CategoryJPA::new).collect(Collectors.toSet());
	}


}
