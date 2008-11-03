package com.imcode.imcms.api;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="meta")
/**
 * Document meta.
 */
public class Meta implements Serializable, Cloneable {
	
	// TODO i18n: refactor
	// Disabled i18n_meta show mode
    // public static enum DisabledI18nDataShowMode {
	//     SHOW_IN_DEFAULT_LANGUAGE,
	//     DO_NOT_SHOW,
	// }
	
	/**
	 * Substitution modes for disabled or missing i18n-ed data.
	 * 
	 * Data considered unavailable if inner accessor returns null.
	 * Note: 
	 *   ImageDomainObject instance also considered unavailable if its URL property is not set
	 *   TextDomainObject instance also considered unavailable if its text property is empty or null   
	 */
	public static enum UnavailableI18nDataSubstitution {
		SHOW_IN_DEFAULT_LANGUAGE,
		DO_NOT_SHOW,		
	}
	
	@Transient
	private Map<I18nLanguage, I18nMeta> metaMap;

	@Id
	@Column(name="meta_id")
	private Integer metaId;
	
	@OneToMany(fetch=FetchType.EAGER, cascade={CascadeType.ALL})
	@JoinColumn(name="meta_id", referencedColumnName="meta_id")		
	private List<I18nMeta> i18nMetas;
	
	@Enumerated(EnumType.STRING)
	@Column(name="missing_i18n_show_rule")
	/**
	 * Please note, that currently this value must not be changed 
	 * client API. It used internally to cache I18n data.
	 */
	private UnavailableI18nDataSubstitution unavailableI18nDataSubstitution;
	
	
	
	
	// Attributes:
	
	@Column(name="doc_type", nullable=false)
	// Discrimination column - from old code:
	// DocumentDomainObject document = DocumentDomainObject.fromDocumentTypeId(documentTypeId);
	private Integer documentType;
	
	@Column(name="owner_id", nullable=false)
	private Integer creatorId;
	
	@Column(name="permissions", nullable=false)
	private Boolean restrictedOneMorePrivilegedThanRestrictedTwo;
	
    @Column(name="shared", nullable=false)
    private Boolean linkableByOtherUsers;
    
    @Column(name="show_meta", nullable=false)
    private Boolean linkedForUnauthorizedUsers;
    
    @Column(name="lang_prefix", nullable=false)
    private String languageIso639_2;
    
    @Column(name="date_created", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDatetime;
    
    @Column(name="date_modified", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDatetime;
    
    // NB! Same as modifiedDatetime
    @Transient
    //@Column(name="date_modified")
    //@Temporal(TemporalType.TIMESTAMP)
    private Date actualModifiedDatetime;
               
    @Column(name="disable_search", nullable=false)
    private Boolean searchDisabled;
           
    @Column(name="target", nullable=false)
	private String target;
	                        
    @Column(name="archived_datetime", nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date archivedDatetime;
           
    @Column(name="publisher_id", nullable=true)
    private Integer publisherId;
                  
    @Column(name="status", nullable=true)
    // Should be converted after set - old code: 
    // Document.PublicationStatus publicationStatus = publicationStatusFromInt(publicationStatusInt);
    // document.setPublicationStatus(publicationStatus); 
    private Integer publicationStatusInt;
                       
    @Column(name="publication_start_datetime", nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationStartDatetime;
        
    @Column(name="publication_end_datetime", nullable=true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publicationEndDatetime;

    // Lazy loaded:
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
    @JoinTable(
    	name = "document_properties",
    	joinColumns = @JoinColumn(name = "meta_id"))    		
    @org.hibernate.annotations.MapKey(columns = @Column(name="key_name"))    		
    @Column(name = "value")    
    private Map<String, String> properties = new HashMap<String, String>();
    
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
   	@JoinTable(
    	name = "document_categories",
    	joinColumns = @JoinColumn(name = "meta_id"))
    @Column(name = "category_id", nullable = false)    
    private Set<Integer> categoryIds = new HashSet<Integer>();
    
    
    @org.hibernate.annotations.CollectionOfElements(fetch=FetchType.EAGER)
   	@JoinTable(
    	name = "meta_section",
    	joinColumns = @JoinColumn(name = "meta_id"))
    @Column(name = "section_id", nullable = false)    
    private Set<Integer> sectionIds = new HashSet<Integer>();
    
    
	@Override
	public Meta clone() {
		try {
			Meta clone = (Meta)super.clone();
			
			clone.metaMap = null;
			clone.unavailableI18nDataSubstitution = unavailableI18nDataSubstitution;
			
			if (i18nMetas != null) {
				clone.i18nMetas = new LinkedList<I18nMeta>();
			
				for (I18nMeta i18nMeta: i18nMetas) {
					clone.i18nMetas.add(i18nMeta.clone());	
				}
			}
			
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}	
	
	public Integer getMetaId() {
		return metaId;
	}

	public void setMetaId(Integer metaId) {
		this.metaId = metaId;
	}

	public List<I18nMeta> getI18nMetas() {
		return i18nMetas;
	}
	
	public void setI18nMetas(List<I18nMeta> i18nParts) {
		this.i18nMetas = i18nParts;		
	}

	public UnavailableI18nDataSubstitution getUnavailableI18nDataSubstitution() {
		return unavailableI18nDataSubstitution;
	}

	public void setUnavailableI18nDataSubstitution(UnavailableI18nDataSubstitution unavailableI18nDataSubstitution) {
		this.unavailableI18nDataSubstitution = unavailableI18nDataSubstitution;
	}
	
	// TODO i18n : refactor
	public synchronized I18nMeta getI18nMeta(I18nLanguage language) {
		if (metaMap == null) {
			metaMap = new HashMap<I18nLanguage, I18nMeta>();
			
			if (i18nMetas != null) {
				for (I18nMeta i18nMeta: i18nMetas) {
					metaMap.put(i18nMeta.getLanguage(), i18nMeta);
				}
			}			
		}
		
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
}