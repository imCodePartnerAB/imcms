package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentPermissionSets;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Document's meta.
 * 
 * Shared by all versions of a same document.
 */
@Entity
@Table(name="meta")
public class Meta implements Serializable, Cloneable {

    /**
	 * Create (create only!) permission for template or a document type.
	 * 
	 * set_id (actually it is a 'set type id') can be: restricted 1 or restricted 2   
	 * 
	 * Mapped to doc_permission_set and new_doc_permission_set
	 */
	@Embeddable
	static public class PermisionSetEx {
				
		@Column(name="set_id")
		private Integer setId;
		
		/**
		 * Document type (1 2 5 7 8) or template group id 
		 */
		@Column(name="permission_data")		
		private Integer permissionData;
		
		/** 
		 * For documents: DatabaseDocumentGetter.PERM_CREATE_DOCUMENT
		 * For templates: TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID
		 * ?Bit set value?
		 */
		@Column(name="permission_id")
		private Integer permissionId;
		
		@Override
		public boolean equals(Object o) {
			return (this == o) || 
				((o instanceof PermisionSetEx) && (hashCode() == o.hashCode()));
		}
		
		@Override
		public int hashCode() {
			return new HashCodeBuilder(17, 31)
				.append(setId)
				.append(permissionId)
				.append(permissionData)
				.toHashCode();
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
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="meta_id")
	private Integer id;

	
	/**
	 * Disabled language's content show rule.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name="disabled_language_show_rule", nullable=false)
	private DisabledLanguageShowSetting disabledLanguageShowSetting =
		DisabledLanguageShowSetting.DO_NOT_SHOW;
	
	// CHECKED	
	@Column(name="activate", nullable=false, updatable=false)
	private Integer activate;
	
	// The following fields are mapped to document attributes:
	// CHECKED
	@Column(name="doc_type", nullable=false, updatable=false)
	// For processing after load:
	// Discrimination column - from old code:
	// DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(permissionData);
    // todo: rename to documentTypeId
	private Integer documentType;
	
	// CHECKED	
	@Column(name="owner_id", nullable=false)
	private Integer creatorId;
	
	// CHECKED	
	@Column(name="permissions", nullable=false)
	private Boolean restrictedOneMorePrivilegedThanRestrictedTwo;
	
	// CHECKED	
    @Column(name="shared", nullable=false)
    private Boolean linkableByOtherUsers;
    
    // CHECKED	
    @Column(name="show_meta", nullable=false)
    private Boolean linkedForUnauthorizedUsers;
    
    // Not a property. 
    // TODO: Delete this field from the database table
    @Column(name="lang_prefix", nullable=false)
    @SuppressWarnings("unused")
    private String lang_prefix = "";
    
    // CHECKED	
    @Column(name="date_created", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDatetime;
    
    // CHECKED	
    @Column(name="date_modified", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDatetime;
    
    // NB! Same as modifiedDatetime
    
    //For processing after load:
    //@Column(name="date_modified")
    //@Temporal(TemporalType.TIMESTAMP)
    @Transient
    private Date actualModifiedDatetime;
        
    // CHECKED	
    @Column(name="disable_search", nullable=false)
    private Boolean searchDisabled;
           
    // CHECKED	
    @Column(name="target", nullable=false)
	private String target;
	         
    // CHECKED	
    @Column(name="archived_datetime", nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedDatetime;
           
    // CHECKED	
    @Column(name="publisher_id", nullable=true)
    private Integer publisherId;
           
    // CHECKED
    
    // For processing after load:
    // Should be converted after set - old code: 
    // Document.PublicationStatus publicationStatus = publicationStatusFromInt(publicationStatusInt);
    // document.setPublicationStatus(publicationStatus); 
    @Column(name="status", nullable=true)
    private Integer publicationStatusInt;
        
    // CHECKED
    @Column(name="publication_start_datetime", nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationStartDatetime;
        
    // CHECKED
    @Column(name="publication_end_datetime", nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationEndDatetime;

    // These fields were lazy loaded in previous version:
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(
    	name = "document_properties",
    	joinColumns = @JoinColumn(name = "meta_id", referencedColumnName="meta_id"))    		
    @org.hibernate.annotations.MapKey(columns = @Column(name="key_name"))    		
    @Column(name = "value", nullable = false)
    private Map<String, String> properties = new HashMap<String, String>();
    
    @ElementCollection(fetch=FetchType.EAGER)
   	@JoinTable(
    	name = "document_categories",
    	joinColumns = @JoinColumn(name = "meta_id", referencedColumnName="meta_id"))
    @Column(name = "category_id", nullable = false)
    private Set<Integer> categoryIds = new HashSet<Integer>();
    
    // Set id is either restricted 1 or restricted 2
    // Roles are user defined or system predefined roles
    // RoleId to permission-set id mapping.  
    // For processing after load:
    @ElementCollection(fetch=FetchType.EAGER)
    @JoinTable(
    	name = "roles_rights",
    	joinColumns = @JoinColumn(name = "meta_id", referencedColumnName="meta_id"))    		
    @org.hibernate.annotations.MapKey(columns = @Column(name="role_id"))    		
    @Column(name = "set_id")
    private Map<Integer, Integer> roleIdToPermissionSetIdMap = new HashMap<Integer, Integer>();
    
    
    /**
     * Limited 1 permission set's bits for new document.
     * Permission set id mapped to bits. 
     */    
    // For processing after load:
    // permisionId in the table actually is not an 'id' but a bit set value.
    @ElementCollection(fetch=FetchType.EAGER)
    @JoinTable(
    	name = "doc_permission_sets",
    	joinColumns = @JoinColumn(name = "meta_id", referencedColumnName="meta_id"))    		
    @org.hibernate.annotations.MapKey(columns = @Column(name="set_id"))    		
    @Column(name = "permission_id")
    private Map<Integer, Integer> permissionSetBitsMap = new HashMap<Integer, Integer>();

    
    /**
     * Limited 2 permission set's bits for new document.
     * Permission set id mapped to bits.
     */
    // For processing after load:
    // permisionId in the table actually is not an 'id' but a bit set value.
    @ElementCollection(fetch=FetchType.EAGER)
    @JoinTable(
    	name = "new_doc_permission_sets",
    	joinColumns = @JoinColumn(name = "meta_id", referencedColumnName="meta_id"))    		
    @org.hibernate.annotations.MapKey(columns = @Column(name="set_id"))    		
    @Column(name = "permission_id")
    private Map<Integer, Integer> permissionSetBitsForNewMap = new HashMap<Integer, Integer>();

    /**
     * 
     */
    // For processing after load:
	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "doc_permission_sets_ex",
	    joinColumns = @JoinColumn(name="meta_id", referencedColumnName="meta_id"))    
	private Set<PermisionSetEx> permisionSetEx = new HashSet<PermisionSetEx>();
	
	
    // For processing after load:
	@ElementCollection(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "new_doc_permission_sets_ex",
	    joinColumns = @JoinColumn(name="meta_id", referencedColumnName="meta_id"))    
	private Set<PermisionSetEx> permisionSetExForNew = new HashSet<PermisionSetEx>();


    /**
     * Might be empty.
     */
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinTable(
	    name = "imcms_doc_languages",
	    joinColumns = @JoinColumn(name="doc_id"),
            inverseJoinColumns = @JoinColumn(name="language_id")
        )
	private Set<I18nLanguage> languages = new HashSet<I18nLanguage>();


	@org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
	@javax.persistence.JoinTable(
		name = "imcms_doc_keywords",
		joinColumns={
		    @JoinColumn(name="doc_id", referencedColumnName="meta_id")
		}
	)
	@Column(name = "value")
	private Set<String> keywords = new HashSet<String>();
    

    /**
     * Moved from DocumentDomainObject. 
     */
    @Transient
    private DocumentPermissionSets permissionSets = new DocumentPermissionSets();

    @Transient
    private DocumentPermissionSets permissionSetsForNewDocuments = new DocumentPermissionSets();

    @Transient
    private RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings = new RoleIdToDocumentPermissionSetTypeMappings();

    @Transient
    private Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;

	
	@Override
	public Meta clone() {
		try {
			Meta clone = (Meta)super.clone();
			
			clone.disabledLanguageShowSetting = disabledLanguageShowSetting;

			clone.permisionSetEx = new HashSet<PermisionSetEx>(permisionSetEx);
			clone.permisionSetExForNew = new HashSet<PermisionSetEx>(permisionSetExForNew);
			clone.permissionSetBitsMap = new HashMap<Integer, Integer>(permissionSetBitsMap);
			clone.permissionSetBitsForNewMap = new HashMap<Integer, Integer>(permissionSetBitsForNewMap);
			
			clone.roleIdToPermissionSetIdMap = new HashMap<Integer, Integer>(roleIdToPermissionSetIdMap);		
			
			clone.properties = new HashMap<String, String>(properties);
			clone.categoryIds = new HashSet<Integer>(categoryIds);

            // todo: remove null check, add protection from null in setters.
			if (keywords != null) {
				clone.keywords = new HashSet<String>(keywords);
			}

			if (languages != null) {
				clone.languages = new HashSet<I18nLanguage>(languages);
			}

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
	
	public void setI18nShowMode(DisabledLanguageShowSetting disabledLanguageShowSetting) {
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

	public Boolean getSearchDisabled() {
		return searchDisabled;
	}

	public void setSearchDisabled(Boolean searchDisabled) {
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

	public Set<PermisionSetEx> getPermisionSetEx() {
		return permisionSetEx;
	}

	public void setPermisionSetEx(Set<PermisionSetEx> permisionSetEx) {
		this.permisionSetEx = permisionSetEx;
	}

	public Set<PermisionSetEx> getPermisionSetExForNew() {
		return permisionSetExForNew;
	}

	public void setPermisionSetExForNew(Set<PermisionSetEx> docPermisionSetExForNew) {
		this.permisionSetExForNew = docPermisionSetExForNew;
	}

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
		this.keywords = keywords;
	}

    public DisabledLanguageShowSetting getDisabledLanguageShowSetting() {
        return disabledLanguageShowSetting;
    }

    public void setDisabledLanguageShowSetting(DisabledLanguageShowSetting disabledLanguageShowSetting) {
        this.disabledLanguageShowSetting = disabledLanguageShowSetting;
    }

    public Set<I18nLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<I18nLanguage> languages) {
        this.languages = languages;
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
}