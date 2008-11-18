package imcode.server.document;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.I18nException;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.api.orm.OrmHtmlDocument;
import com.imcode.imcms.api.orm.OrmUrlDocument;
import com.imcode.imcms.util.l10n.LocalizedMessage;

public abstract class DocumentDomainObject implements Cloneable, Serializable {

    public static final int ID_NEW = 0;
    public static final String DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS = "imcms.document.alias";

    protected Attributes attributes = new Attributes();
    private static Logger log = Logger.getLogger( DocumentDomainObject.class );
    
    /**
     * I18n data cache.
     * Its content depends on Meta.unavailableI18nDataSubstitution value. 
     */
    //private Map<String, String> i18nDataCache = new HashMap<String, String>();
    
    /**
     * Document meta.
     * 
     * Introduced in v5 to replace meta properties previously stored in attributes.    
     */ 
    private Meta meta = new Meta();

    @Override
    public Object clone() throws CloneNotSupportedException {
        DocumentDomainObject clone = (DocumentDomainObject)super.clone();
        if ( null != attributes ) {
            clone.attributes = (Attributes)attributes.clone();
        }
        
        if (clone.meta != null) {
        	clone.meta = meta.clone();
        }
        
        //clone.i18nDataCache = new HashMap<String, String>();
        
        return clone;
    }

    public static DocumentDomainObject fromDocumentTypeId( int documentTypeId ) {
        DocumentDomainObject document;

        switch ( documentTypeId ) {
            case DocumentTypeDomainObject.TEXT_ID :
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
                log.error( errorMessage );
                throw new IllegalArgumentException( errorMessage );
        }                
        

        return document;
    }

    public Date getArchivedDatetime() {
        return attributes.archivedDatetime;
    }

    public void setArchivedDatetime( Date v ) {
        attributes.archivedDatetime = v;
    }

    public Set<Integer> getCategoryIds() {
        return Collections.unmodifiableSet((Set<Integer>) attributes.categoryIds) ;
    }

    public Date getCreatedDatetime() {
        return attributes.createdDatetime;
    }

    public void setCreatedDatetime( Date v ) {
        attributes.createdDatetime = v;
    }

    public int getCreatorId() {
        return attributes.creatorId;
    }

    public void setCreatorId( int creatorId ) {
        attributes.creatorId = creatorId;
    }

    public void setCreator( UserDomainObject creator ) {
        setCreatorId(creator.getId());
    }

    public void setAttributes( Attributes attributes ) {
        this.attributes = attributes;
    }

    /**
     * TODO i18n: cache menu image according to Menu.unavailableI18nDataSubstitution value.
     */
    public String getHeadline() {
    	I18nLanguage language = I18nSupport.getCurrentLanguage();
    	I18nLanguage defaLanguage = I18nSupport.getDefaultLanguage();
    	String value = getHeadline(language);
    	
    	return getI18nMeta(language).getEnabled() 
			? value 
			: substituteWithDefault(language, defaLanguage)
			    ? getHeadline(defaLanguage)
			    : "";
		
    }
    
    public String getHeadline(I18nLanguage language) {
    	return getI18nMeta(language).getHeadline();
    }    

    @Deprecated
    public void setHeadline( String v ) {
    	setHeadline(I18nSupport.getCurrentLanguage(), v);
    }
    
    public void setHeadline(I18nLanguage language, String v ) {
    	getI18nMeta(language).setHeadline(v);
    }
    

    public int getId() {
        return attributes.id;
    }

    public void setId( int v ) {
        attributes.id = v;
        
        if (meta != null) {
        	meta.setMetaId(v);
        }
    }

    /**
     * TODO i18n: cache menu image according to Menu.unavailableI18nDataSubstitution value.
     */
    public String getMenuImage() {
    	I18nLanguage language = I18nSupport.getCurrentLanguage();
    	I18nLanguage defaLanguage = I18nSupport.getDefaultLanguage();
    	String value = getMenuImage(language);
    	
    	return getI18nMeta(language).getEnabled() 
			? value 
			: substituteWithDefault(language, defaLanguage)
			    ? getMenuImage(defaLanguage)
			    : "";
    }
    
    /** 
     * @param language
     * 
     * @return (original) menu image for language specified.
     */
    public String getMenuImage(I18nLanguage language) {
    	return getI18nMeta(language).getMenuImageURL();
    }
    
    /* I18n disabled: unsafe method
    public void setMenuImage( String v ) {
    	setMenuImage(I18nSupport.getCurrentLanguage(), v);
    }
    */
    
    public void setMenuImage( I18nLanguage language, String v ) {
    	getI18nMeta(language).setMenuImageURL(v);
    }    
    
    /**
     * Unsafe method. 
     *  
     * Use getKeywords(I18nLanguage language) instead.
     */
    @Deprecated
    public Set getKeywords() {
    	return getKeywords(I18nSupport.getCurrentLanguage());
    }
    
    /**
     * @return keywords 
     */
    public Set getKeywords(I18nLanguage language) {
    	return getI18nMeta(language).getKeywords();
    }
    
    /* I18n disabled: unsafe method
    public void setKeywords( Set keywords ) {
    	setKeywords(I18nSupport.getCurrentLanguage(), keywords);
    }
    */
    
    public void setKeywords(I18nLanguage language, Set keywords ) {
    	getI18nMeta(language).setKeywords(keywords);
    }
    

    public void setProperties( Map properties ) {
        attributes.properties = properties;
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap((Map<String, String>)attributes.properties) ;
    }

    public String getProperty(String key) {
        Map<String,String> properties = (Map<String, String>) attributes.properties;
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        Map<String, String> properties = (Map<String, String>) attributes.properties;
        properties.put(key, value);
    }

    public void removeProperty(String key) {
        Map<String, String> properties = (Map<String, String>) attributes.properties;
        properties.remove(key);
    }

    public String getLanguageIso639_2() {
        return attributes.languageIso639_2;
    }

    public void setLanguageIso639_2( String languageIso639_2 ) {
        attributes.languageIso639_2 = languageIso639_2;
    }

    /**
     * TODO i18n: cache menu image according to Menu.unavailableI18nDataSubstitution value.
     */
    public String getMenuText() {
    	I18nLanguage language = I18nSupport.getCurrentLanguage();
    	I18nLanguage defaLanguage = I18nSupport.getDefaultLanguage();
    	
    	String value = getMenuText(language);
    	
    	return getI18nMeta(language).getEnabled() 
			? value 
			: substituteWithDefault(language, defaLanguage)
			    ? getMenuText(defaLanguage)
			    : "";
    }
    
    public boolean substituteWithDefault(I18nLanguage language, I18nLanguage defaultLanguage) {    	
    	return !language.equals(defaultLanguage)
    		&& meta.getUnavailableI18nDataSubstitution() == Meta.UnavailableI18nDataSubstitution.SHOW_IN_DEFAULT_LANGUAGE
    		&& getI18nMeta(defaultLanguage).getEnabled();
    }
    
    public String getMenuText(I18nLanguage language) {
    	return getI18nMeta(language).getMenuText();
    }    

    /** Unsafe method */
    @Deprecated    
    public void setMenuText( String v ) {
    	setMenuText(I18nSupport.getCurrentLanguage(), v);
    }
    
    public void setMenuText( I18nLanguage language, String v ) {
    	getI18nMeta(language).setMenuText(v);
    }
    

    public Date getModifiedDatetime() {
        return attributes.modifiedDatetime;
    }

    public void setModifiedDatetime( Date v ) {
        attributes.modifiedDatetime = v;
    }

    public void setActualModifiedDatetime( Date modifiedDatetime ) {
        this.attributes.actualModifiedDatetime = modifiedDatetime;
    }

    public Date getActualModifiedDatetime() {
        return attributes.actualModifiedDatetime;
    }

    public Date getPublicationEndDatetime() {
        return attributes.publicationEndDatetime;
    }

    public void setPublicationEndDatetime( Date datetime ) {
        attributes.publicationEndDatetime = datetime;
    }

    public Date getPublicationStartDatetime() {
        return attributes.publicationStartDatetime;
    }

    public void setPublicationStartDatetime( Date v ) {
        attributes.publicationStartDatetime = v;
    }

    public Integer getPublisherId() {
        return attributes.publisherId;
    }

    public void setPublisher( UserDomainObject user ) {
        setPublisherId(new Integer(user.getId())) ;
    }

    public void setPublisherId(Integer publisherId) {
        attributes.publisherId = publisherId ;
    }

    public RoleIdToDocumentPermissionSetTypeMappings getRoleIdsMappedToDocumentPermissionSetTypes() {
        return (RoleIdToDocumentPermissionSetTypeMappings)getRolePermissionMappings().clone();
    }

    private RoleIdToDocumentPermissionSetTypeMappings getRolePermissionMappings() {
        return attributes.roleIdToDocumentPermissionSetTypeMappings;
    }

    public void setRoleIdsMappedToDocumentPermissionSetTypes( RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings ) {
        attributes.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings.clone();
    }

    public Set getSectionIds() {
        return Collections.unmodifiableSet((Set) attributes.sectionIds) ;
    }

    public void setSectionIds( Set sectionIds ) {
        attributes.sectionIds = sectionIds;
    }

    public Document.PublicationStatus getPublicationStatus() {
        return attributes.publicationStatus;
    }

    public void setPublicationStatus( Document.PublicationStatus status ) {
        if (null == status) {
            throw new NullArgumentException("status") ;
        }
        attributes.publicationStatus = status;
    }

    public String getTarget() {
        return attributes.target;
    }

    public void setTarget( String v ) {
        attributes.target = v;
    }

    public boolean isArchived() {
        return hasBeenArchivedAtTime( new Date() );
    }

    public boolean isLinkableByOtherUsers() {
        return attributes.linkableByOtherUsers;
    }

    public void setLinkableByOtherUsers( boolean linkableByOtherUsers ) {
        attributes.linkableByOtherUsers = linkableByOtherUsers;
    }

    public boolean isRestrictedOneMorePrivilegedThanRestrictedTwo() {
        return attributes.restrictedOneMorePrivilegedThanRestrictedTwo;
    }

    public void setRestrictedOneMorePrivilegedThanRestrictedTwo( boolean b ) {
        attributes.restrictedOneMorePrivilegedThanRestrictedTwo = b;
    }

    public boolean isPublished() {
        return isPublishedAtTime( new Date() );
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

    public void setSearchDisabled( boolean searchDisabled ) {
        attributes.searchDisabled = searchDisabled;
    }

    public boolean isLinkedForUnauthorizedUsers() {
        return attributes.linkedForUnauthorizedUsers;
    }

    public void setLinkedForUnauthorizedUsers( boolean linkedForUnauthorizedUsers ) {
        attributes.linkedForUnauthorizedUsers = linkedForUnauthorizedUsers;
    }

    public void addCategoryId( int categoryId ) {
        Set categoryIds = (Set) attributes.categoryIds;
        categoryIds.add(categoryId);
    }

    public void addSectionId( int sectionId ) {
        Set sectionIds = (Set) attributes.sectionIds;
        sectionIds.add( sectionId );
    }

    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DocumentDomainObject ) ) {
            return false;
        }

        final DocumentDomainObject document = (DocumentDomainObject)o;

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

    private boolean hasBeenArchivedAtTime( Date time ) {
        Date archivedDatetime = this.attributes.archivedDatetime;
        return archivedDatetime != null && archivedDatetime.before( time );
    }

    public void removeAllCategories() {
        attributes.categoryIds = new HashSet<Integer>();
    }

    public void removeAllSections() {
        attributes.sectionIds = new HashSet<Integer>();
    }

    public void removeCategoryId( int categoryId ) {
        Set categoryIds = (Set) attributes.categoryIds;
        categoryIds.remove(categoryId);
    }

    public void setDocumentPermissionSetTypeForRoleId( RoleId roleId, DocumentPermissionSetTypeDomainObject permissionSetType ) {
        getRolePermissionMappings().setPermissionSetTypeForRole(roleId, permissionSetType) ;
    }

    public DocumentPermissionSetTypeDomainObject getDocumentPermissionSetTypeForRoleId( RoleId roleId ) {
        return getRolePermissionMappings().getPermissionSetTypeForRole( roleId );
    }

    private boolean isPublishedAtTime( Date date ) {
        boolean statusIsApproved = Document.PublicationStatus.APPROVED.equals(getPublicationStatus());
        return statusIsApproved && publicationHasStartedAtTime(date)
               && !publicationHasEndedAtTime(date);
    }

    private boolean publicationHasStartedAtTime(Date date) {
        Date publicationStartDatetime = attributes.publicationStartDatetime;
        return publicationStartDatetime != null && publicationStartDatetime.before( date );
    }

    private boolean publicationHasEndedAtTime( Date date ) {
        Date publicationEndDatetime = attributes.publicationEndDatetime;
        return publicationEndDatetime != null && publicationEndDatetime.before( date );
    }

    public DocumentPermissionSets getPermissionSets() {
        return this.attributes.permissionSets;
    }

    public DocumentPermissionSets getPermissionSetsForNewDocuments() {
        return attributes.permissionSetsForNewDocuments;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public abstract void accept( DocumentVisitor documentVisitor );

    public LifeCyclePhase getLifeCyclePhase() {
        return getLifeCyclePhaseAtTime(new Date());
    }

    LifeCyclePhase getLifeCyclePhaseAtTime(Date time) {
        LifeCyclePhase lifeCyclePhase;
        Document.PublicationStatus publicationStatus = getPublicationStatus();
        if ( Document.PublicationStatus.NEW.equals(publicationStatus) ) {
            lifeCyclePhase = LifeCyclePhase.NEW;
        } else if ( Document.PublicationStatus.DISAPPROVED.equals(publicationStatus) ) {
            lifeCyclePhase = LifeCyclePhase.DISAPPROVED;
        } else {
            if ( publicationHasEndedAtTime(time) ) {
                lifeCyclePhase = LifeCyclePhase.UNPUBLISHED;
            } else if ( publicationHasStartedAtTime(time) ) {
                if ( hasBeenArchivedAtTime(time) ) {
                    lifeCyclePhase = LifeCyclePhase.ARCHIVED;
                } else {
                    lifeCyclePhase = LifeCyclePhase.PUBLISHED;
                }
            } else {
                lifeCyclePhase = LifeCyclePhase.APPROVED;
            }
        }
        return lifeCyclePhase ;
    }

    public void setCategoryIds(Set categoryIds) {
        attributes.categoryIds = categoryIds;
    }


    public void setPermissionSets(DocumentPermissionSets permissionSets) {
        attributes.permissionSets = permissionSets ;
    }

    public void setPermissionSetsForNew(DocumentPermissionSets permissionSetsForNew) {
        attributes.permissionSetsForNewDocuments = permissionSetsForNew;
    }

    public String getAlias() {
        return getProperty(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
    }

    public void setAlias(String alias) {
        if(alias==null) {
            removeProperty(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS);
        }else{
            setProperty(DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS, alias);
        }
    }

    public String getName() {
        return StringUtils.defaultString(getAlias(), getId()+"");
    }

    public static class Attributes implements Cloneable, Serializable {

        private Date archivedDatetime;

        private Date createdDatetime;

        private int creatorId;

        private String languageIso639_2;
        
        private boolean linkableByOtherUsers;

        private int id;
        private Date modifiedDatetime;
        private Date actualModifiedDatetime;
        private boolean restrictedOneMorePrivilegedThanRestrictedTwo;
        private Date publicationStartDatetime;
        private Date publicationEndDatetime;
        private Integer publisherId;
        private boolean searchDisabled;
        private Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;
        private String target;
        private boolean linkedForUnauthorizedUsers;

        // Replaced lazy loaded:
        private DocumentPermissionSets permissionSets = new DocumentPermissionSets();
        private DocumentPermissionSets permissionSetsForNewDocuments = new DocumentPermissionSets();
                  
        private Map<String, String> properties = new HashMap<String, String>();        
        private Set<Integer> categoryIds = new HashSet<Integer>();                
        private Set<Integer> sectionIds = new HashSet<Integer>();        
        private RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings =
        	new RoleIdToDocumentPermissionSetTypeMappings();
                

        public Object clone() throws CloneNotSupportedException {
            Attributes clone = (Attributes)super.clone();
                        
            clone.properties = new HashMap<String, String>(properties);
            clone.sectionIds = new HashSet<Integer>(sectionIds);
            clone.categoryIds = new HashSet<Integer>(categoryIds);
            clone.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings.clone();
                                    
            clone.permissionSets = permissionSets.clone() ;
            clone.permissionSetsForNewDocuments = permissionSetsForNewDocuments.clone() ;
            return clone;
        }
    }

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
		
	// TODO i18n refactor
	public I18nMeta getI18nMeta(I18nLanguage language) {
		if (meta == null) {
			throw new I18nException("Meta for document [" 
					+ getId() + "] is not set.");
		}
		
		I18nMeta i18nMeta = meta.getI18nMeta(language);
		
		if (i18nMeta == null) {
			throw new I18nException("No I18nMeta found for document [" 
					+ getId() + "], language [" + language + "].");
		}
		
		return i18nMeta;
	}

    /**
     * For legacy code support:
     * When saving document copy as new document its shared references should be cloned.
     *   
     * @see DocumentSaver.saveNewDocument       
     */
    public void cloneSharedForNewDocument() {}
}
