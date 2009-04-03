package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Document meta.
 * 
 * Every document in a system have a meta.   
 */
@Entity
@Table(name="meta")
@NamedQueries({
})
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
			int iSetId = setId;
			int iPermissionData = permissionData;
			int iPermissionId = permissionId;
			
			int result = 5;
			
			return (((31 * result + iSetId) 
					* 31 + iPermissionId) 
						* 31 + iPermissionData);
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
	 * Document content show mode for disabled translation.
	 * 
	 * The name 'DiabledI18nContentShowMode' would be more descriptive.
	 * 
	 * @see I18nMeta.getEnabled
	 */
	public static enum UnavailableI18nDataSubstitution {
		SHOW_IN_DEFAULT_LANGUAGE,
		DO_NOT_SHOW,		
	}
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="meta_id")
	private Integer id;
					
	/**
	 * I18n-ized parts of the meta.
	 */
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinColumn(name="meta_id", referencedColumnName="meta_id")		
	private Set<I18nMeta> i18nMetas = new HashSet<I18nMeta>();
	
	@Enumerated(EnumType.STRING)
	@Column(name="missing_i18n_show_rule", nullable=false)
	private UnavailableI18nDataSubstitution unavailableI18nDataSubstitution =
		UnavailableI18nDataSubstitution.DO_NOT_SHOW;

	/**
	 * In the current imcms release, meta does not have version - e.g.
	 * all document's versions have the same meta. But technically it 
	 * is convinient to have reference to version in meta. It is possible
	 * because cached documents do not share single meta object.   
	 * 
	 * NB! In the future meta fields will be also versioned. 
	 */
	@Transient
	private DocumentVersion version;	
	
	// CHECKED	
	@Column(name="activate", nullable=false, updatable=false)
	private Integer activate;
	
	// The following fields are mapped to document attributes:
	// CHECKED
	@Column(name="doc_type", nullable=false, updatable=false)
	// For processing after load:
	// Discrimination column - from old code:
	// DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(permissionData);
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
    
    // CHECKED	
    @Column(name="lang_prefix", nullable=false)
    private String languageIso639_2;
    
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
    
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
   	@JoinTable(
    	name = "document_categories",
    	joinColumns = @JoinColumn(name = "meta_id", referencedColumnName="meta_id"))
    @Column(name = "category_id", nullable = false)
    private Set<Integer> categoryIds = new HashSet<Integer>();
    
    
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
   	@JoinTable(
    	name = "meta_section",
    	joinColumns = @JoinColumn(name = "meta_id", referencedColumnName="meta_id"))
    @Column(name = "section_id", nullable = false)
    private Set<Integer> sectionIds = new HashSet<Integer>();
    
    // Set id is either restricted 1 or restricted 2
    // Roles are user defined or system predefined roles
    // RoleId to permission-set id mapping.  
    // For processing after load:
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
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
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
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
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
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
	@org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "doc_permission_sets_ex",
	    joinColumns = @JoinColumn(name="meta_id", referencedColumnName="meta_id"))    
	private Set<PermisionSetEx> permisionSetEx = new HashSet<PermisionSetEx>();
	
	
    // For processing after load:
	@org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(
	    name = "new_doc_permission_sets_ex",
	    joinColumns = @JoinColumn(name="meta_id", referencedColumnName="meta_id"))    
	private Set<PermisionSetEx> permisionSetExForNew = new HashSet<PermisionSetEx>();
	
	/**
	 * Workaround.
	 * Map provides quicker access to I18n metas than list.
	 * Initilized by MetaDAO API, when document is requested.
	 * TODO: Rewrite initializing logic.
	 */
	@Transient
	private Map<I18nLanguage, I18nMeta> metaMap;
	
	@Override
	public Meta clone() {
		try {
			Meta clone = (Meta)super.clone();
			
			clone.unavailableI18nDataSubstitution = unavailableI18nDataSubstitution;
			clone.version = version.clone();

			clone.permisionSetEx = new HashSet<PermisionSetEx>(permisionSetEx);
			clone.permisionSetExForNew = new HashSet<PermisionSetEx>(permisionSetExForNew);
			clone.permissionSetBitsMap = new HashMap<Integer, Integer>(permissionSetBitsMap);
			clone.permissionSetBitsForNewMap = new HashMap<Integer, Integer>(permissionSetBitsForNewMap);
			
			clone.roleIdToPermissionSetIdMap = new HashMap<Integer, Integer>(roleIdToPermissionSetIdMap);		
			
			clone.sectionIds = new HashSet<Integer>(sectionIds);
			clone.properties = new HashMap<String, String>(properties);
			clone.categoryIds = new HashSet<Integer>(categoryIds);
			
			if (i18nMetas != null) {
				clone.i18nMetas = new HashSet<I18nMeta>();
			
				for (I18nMeta i18nMeta: i18nMetas) {
					clone.i18nMetas.add(i18nMeta.clone());	
				}
			}
			
			clone.initI18nMetaMapping();
			
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

	public Set<I18nMeta> getI18nMetas() {
		return i18nMetas;
	}
	
	public void setI18nMetas(Set<I18nMeta> i18nMetas) {
		this.i18nMetas = i18nMetas;		
	}

	
	/**
	 * Use getI18nShowMode instead.
	 */
	@Deprecated	
	public UnavailableI18nDataSubstitution getUnavailableI18nDataSubstitution() {
		return unavailableI18nDataSubstitution;
	}
	
	
	/**
	 * Use setI18nShowMode instead.
	 */
	@Deprecated
	public void setUnavailableI18nDataSubstitution(UnavailableI18nDataSubstitution unavailableI18nDataSubstitution) {
		this.unavailableI18nDataSubstitution = unavailableI18nDataSubstitution;
	}
	
	public UnavailableI18nDataSubstitution getI18nShowMode() {
		return getUnavailableI18nDataSubstitution();
	}
	
	public void setI18nShowMode(UnavailableI18nDataSubstitution unavailableI18nDataSubstitution) {
		setUnavailableI18nDataSubstitution(unavailableI18nDataSubstitution);
	}	
	
	/**
	 * Initializes language to i18n meta mapping. 
	 */
	public void initI18nMetaMapping() {
		Map<I18nLanguage, I18nMeta> map = new HashMap<I18nLanguage, I18nMeta>();
		
		if (i18nMetas != null) {
			for (I18nMeta i18nMeta: i18nMetas) {
				map.put(i18nMeta.getLanguage(), i18nMeta);
			}
		}		
		
		metaMap = Collections.unmodifiableMap(map);
	}
	
	
	public I18nMeta getI18nMeta(I18nLanguage language) {
		return metaMap.get(language);
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

	public String getLanguageIso639_2() {
		return languageIso639_2;
	}

	public void setLanguageIso639_2(String languageIso639_2) {
		this.languageIso639_2 = languageIso639_2;
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

	public Set<Integer> getSectionIds() {
		return sectionIds;
	}

	public void setSectionIds(Set<Integer> sectionIds) {
		this.sectionIds = sectionIds;
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

	public void setPermissionSetBitsForNewMap(
			Map<Integer, Integer> permissionSetBitsForNew) {
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

	public void setPermisionSetExForNew(
			Set<PermisionSetEx> docPermisionSetExForNew) {
		this.permisionSetExForNew = docPermisionSetExForNew;
	}

	public Integer getActivate() {
		return activate;
	}

	public void setActivate(Integer activate) {
		this.activate = activate;
	}
	
	@Deprecated
	public boolean getShowDisabledI18nDataInDefaultLanguage() {
		return unavailableI18nDataSubstitution == 
			UnavailableI18nDataSubstitution.SHOW_IN_DEFAULT_LANGUAGE;
	}
	
	public boolean isShowDisabledI18nContentInDefaultLanguage() {
		return unavailableI18nDataSubstitution == 
			UnavailableI18nDataSubstitution.SHOW_IN_DEFAULT_LANGUAGE;
	}

	public DocumentVersion getVersion() {
		return version;
	}

	public void setVersion(DocumentVersion documentVersion) {
		this.version = documentVersion;
	}
}