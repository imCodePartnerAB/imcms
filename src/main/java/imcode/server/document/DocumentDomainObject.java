package imcode.server.document;

import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.RoleId;
import imcode.server.user.UserDomainObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.I18nException;
import com.imcode.imcms.api.I18nLanguage;
import com.imcode.imcms.api.I18nMeta;
import com.imcode.imcms.api.I18nSupport;
import com.imcode.imcms.api.Meta;
import com.imcode.imcms.mapping.DocumentSaver;
import com.imcode.imcms.util.l10n.LocalizedMessage;

public abstract class DocumentDomainObject implements Cloneable, Serializable {

	public static final int ID_NEW = 0;
	public static final String DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS = "imcms.document.alias";

	protected Attributes attributes = new Attributes();
	private static Logger log = Logger.getLogger(DocumentDomainObject.class);
	
	/**
	 * Document meta.
	 */
	private Meta meta = new Meta();


    /**
     * Document version.
     */
    private DocumentVersion version;


	@Override
	public DocumentDomainObject clone() {
		DocumentDomainObject clone;

		try {
			clone = (DocumentDomainObject) super.clone();

			if (null != attributes) {
				clone.attributes = attributes.clone();
			}
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

		if (clone.meta != null) {
			clone.meta = meta.clone();
		}

        if (version != null) {
            clone.version = version.clone();
        }

		return clone;
	}
	
	/**
	 * Returns this document's version.
	 */
	public DocumentVersion getVersion() {
		return version;
	}


	/**
	 * Returns this document's version.
	 */
	public void setVersion(DocumentVersion version) {
		this.version = version;
	}
	
	

	/**
	 * Factory method. Creates new document.
	 * 
	 * @param documentTypeId document type id.
	 *  
	 * @return new document
	 */
	public static DocumentDomainObject fromDocumentTypeId(int documentTypeId) {
		DocumentDomainObject document;

		switch (documentTypeId) {
		case DocumentTypeDomainObject.TEXT_ID:
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
			log.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		return document;
	}

	public Date getArchivedDatetime() {
		return meta.getArchivedDatetime();
	}

	public void setArchivedDatetime(Date v) {
		meta.setArchivedDatetime(v);
	}

	public Set<Integer> getCategoryIds() {
		return Collections
				.unmodifiableSet((Set<Integer>) meta.getCategoryIds());
	}

	public Date getCreatedDatetime() {
		return meta.getCreatedDatetime();
	}

	public void setCreatedDatetime(Date v) {
		meta.setCreatedDatetime(v);
	}

	public int getCreatorId() {
		return meta.getCreatorId();
	}

	public void setCreatorId(int creatorId) {
		meta.setCreatorId(creatorId);
	}

	public void setCreator(UserDomainObject creator) {
		setCreatorId(creator.getId());
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Use getHeadline(I18nLanguage language) instead
	 */
	@Deprecated	
	public String getHeadline() {
		return getHeadline(I18nSupport.getCurrentLanguage());
	}

	public String getHeadline(I18nLanguage language) {
		return getI18nMeta(language).getHeadline();
	}

	@Deprecated
	public void setHeadline(String v) {
		setHeadline(I18nSupport.getCurrentLanguage(), v);
	}

	public void setHeadline(I18nLanguage language, String v) {
		getI18nMeta(language).setHeadline(v);
	}

	public int getId() {
		Integer id = meta.getId();
		return id == null ? ID_NEW : id.intValue();

	}

	public void setId(int v) {
		meta.setId(v);
	}

	
	/**
	 * Use getMenuImage(I18nLanguage language) instead
	 */
	@Deprecated
	public String getMenuImage() {
		return getMenuImage(I18nSupport.getCurrentLanguage());
	}

	/**
	 * @param language
	 * 
	 * @return (original) menu image for language specified.
	 */
	public String getMenuImage(I18nLanguage language) {
		return getI18nMeta(language).getMenuImageURL();
	}

	/*
	 * I18n disabled: unsafe method public void setMenuImage( String v ) {
	 * setMenuImage(I18nSupport.getCurrentLanguage(), v); }
	 */

	public void setMenuImage(I18nLanguage language, String v) {
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
	@Deprecated
	public Set getKeywords(I18nLanguage language) {
		return getI18nMeta(language).getKeywords();
	}

	/*
	 * I18n disabled: unsafe method public void setKeywords( Set keywords ) {
	 * setKeywords(I18nSupport.getCurrentLanguage(), keywords); }
	 */
	@Deprecated
	public void setKeywords(I18nLanguage language, Set keywords) {
		getI18nMeta(language).setKeywords(keywords);
	}

	public void setProperties(Map properties) {
		meta.setProperties(properties);
	}

	public Map<String, String> getProperties() {
		return Collections
				.unmodifiableMap((Map<String, String>) meta.getProperties());
	}

	public String getProperty(String key) {
		Map<String, String> properties = (Map<String, String>) meta.getProperties();
		return properties.get(key);
	}

	public void setProperty(String key, String value) {
		Map<String, String> properties = (Map<String, String>) meta.getProperties();
		properties.put(key, value);
	}

	public void removeProperty(String key) {
		Map<String, String> properties = (Map<String, String>) meta.getProperties();
		properties.remove(key);
	}

	@Deprecated
	// TODO: throw OperationNotSupported
	/**
	 * Use getMenuText(I18nLanguage language) instead
	 */	
	public String getMenuText() {
		return getMenuText(I18nSupport.getCurrentLanguage());
	}

	@Deprecated
	public void setMenuText(String v) {
		setMenuText(I18nSupport.getCurrentLanguage(), v);
	}	

	public String getMenuText(I18nLanguage language) {
		return getI18nMeta(language).getMenuText();
	}


	public void setMenuText(I18nLanguage language, String v) {
		getI18nMeta(language).setMenuText(v);
	}

	public Date getModifiedDatetime() {
		return meta.getModifiedDatetime();
	}

	public void setModifiedDatetime(Date v) {
		meta.setModifiedDatetime(v);
	}

	public void setActualModifiedDatetime(Date modifiedDatetime) {
		meta.setActualModifiedDatetime(modifiedDatetime);
	}

	public Date getActualModifiedDatetime() {
		return meta.getActualModifiedDatetime();
	}

	public Date getPublicationEndDatetime() {
		return meta.getPublicationEndDatetime();
	}

	public void setPublicationEndDatetime(Date datetime) {
		meta.setPublicationEndDatetime(datetime);
	}

	public Date getPublicationStartDatetime() {
		return meta.getPublicationStartDatetime();
	}

	public void setPublicationStartDatetime(Date v) {
		meta.setPublicationStartDatetime(v);
	}

	public Integer getPublisherId() {
		return meta.getPublisherId();
	}

	public void setPublisher(UserDomainObject user) {
		setPublisherId(user.getId());
	}

	public void setPublisherId(Integer publisherId) {
		meta.setPublisherId(publisherId);
	}

	public RoleIdToDocumentPermissionSetTypeMappings getRoleIdsMappedToDocumentPermissionSetTypes() {
		return (RoleIdToDocumentPermissionSetTypeMappings) getRolePermissionMappings()
				.clone();
	}

	private RoleIdToDocumentPermissionSetTypeMappings getRolePermissionMappings() {
		return attributes.roleIdToDocumentPermissionSetTypeMappings;
	}

	public void setRoleIdsMappedToDocumentPermissionSetTypes(
			RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings) {
		attributes.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings
				.clone();
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
		return meta.getTarget();
	}

	public void setTarget(String v) {
		meta.setTarget(v);
	}

	public boolean isArchived() {
		return hasBeenArchivedAtTime(new Date());
	}

	public boolean isLinkableByOtherUsers() {
		return meta.getLinkableByOtherUsers();
	}

	public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
		meta.setLinkableByOtherUsers(linkableByOtherUsers);
	}

	public boolean isRestrictedOneMorePrivilegedThanRestrictedTwo() {
		return meta.getRestrictedOneMorePrivilegedThanRestrictedTwo();
	}

	public void setRestrictedOneMorePrivilegedThanRestrictedTwo(boolean b) {
		meta.setRestrictedOneMorePrivilegedThanRestrictedTwo(b);
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
		return meta.getSearchDisabled();
	}

	public void setSearchDisabled(boolean searchDisabled) {
		meta.setSearchDisabled(searchDisabled);
	}

	public boolean isLinkedForUnauthorizedUsers() {
		return meta.getLinkedForUnauthorizedUsers();
	}

	public void setLinkedForUnauthorizedUsers(boolean linkedForUnauthorizedUsers) {
		meta.setLinkedForUnauthorizedUsers(linkedForUnauthorizedUsers);
	}

	public void addCategoryId(int categoryId) {
		Set categoryIds = (Set) meta.getCategoryIds();
		categoryIds.add(categoryId);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DocumentDomainObject)) {
			return false;
		}

		final DocumentDomainObject document = (DocumentDomainObject) o;

		return getId() == document.getId();

	}

	public abstract DocumentTypeDomainObject getDocumentType();

	public final int getDocumentTypeId() {
		return getDocumentType().getId();
	}

	public final LocalizedMessage getDocumentTypeName() {
		return getDocumentType().getName();
	}

	public int hashCode() {
		return getId();
	}

	private boolean hasBeenArchivedAtTime(Date time) {
		Date archivedDatetime = meta.getArchivedDatetime();
		return archivedDatetime != null && archivedDatetime.before(time);
	}

	public void removeAllCategories() {
		meta.setCategoryIds(new HashSet<Integer>());
	}

	public void removeCategoryId(int categoryId) {
		Set categoryIds = (Set) meta.getCategoryIds();
		categoryIds.remove(categoryId);
	}

	public void setDocumentPermissionSetTypeForRoleId(RoleId roleId,
			DocumentPermissionSetTypeDomainObject permissionSetType) {
		getRolePermissionMappings().setPermissionSetTypeForRole(roleId,
				permissionSetType);
	}

	public DocumentPermissionSetTypeDomainObject getDocumentPermissionSetTypeForRoleId(
			RoleId roleId) {
		return getRolePermissionMappings().getPermissionSetTypeForRole(roleId);
	}

	private boolean isPublishedAtTime(Date date) {
		boolean statusIsApproved = Document.PublicationStatus.APPROVED
				.equals(getPublicationStatus());
		return statusIsApproved && publicationHasStartedAtTime(date)
				&& !publicationHasEndedAtTime(date);
	}

	private boolean publicationHasStartedAtTime(Date date) {
		Date publicationStartDatetime = meta.getPublicationStartDatetime();
		return publicationStartDatetime != null
				&& publicationStartDatetime.before(date);
	}

	private boolean publicationHasEndedAtTime(Date date) {
		Date publicationEndDatetime = meta.getPublicationEndDatetime();
		return publicationEndDatetime != null
				&& publicationEndDatetime.before(date);
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

	public abstract void accept(DocumentVisitor documentVisitor);

	public LifeCyclePhase getLifeCyclePhase() {
		return getLifeCyclePhaseAtTime(new Date());
	}

	LifeCyclePhase getLifeCyclePhaseAtTime(Date time) {
		LifeCyclePhase lifeCyclePhase;
		Document.PublicationStatus publicationStatus = getPublicationStatus();
		if (Document.PublicationStatus.NEW.equals(publicationStatus)) {
			lifeCyclePhase = LifeCyclePhase.NEW;
		} else if (Document.PublicationStatus.DISAPPROVED
				.equals(publicationStatus)) {
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

	public void setCategoryIds(Set categoryIds) {
		meta.setCategoryIds(categoryIds);
	}

	public void setPermissionSets(DocumentPermissionSets permissionSets) {
		attributes.permissionSets = permissionSets;
	}

	public void setPermissionSetsForNew(
			DocumentPermissionSets permissionSetsForNew) {
		attributes.permissionSetsForNewDocuments = permissionSetsForNew;
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

		private Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;

		private DocumentPermissionSets permissionSets = new DocumentPermissionSets();
		private DocumentPermissionSets permissionSetsForNewDocuments = new DocumentPermissionSets();

		private RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings = new RoleIdToDocumentPermissionSetTypeMappings();

        @Override
		public Attributes clone() throws CloneNotSupportedException {
			Attributes clone = (Attributes) super.clone();

			clone.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings
					.clone();

			clone.permissionSets = permissionSets.clone();
			clone.permissionSetsForNewDocuments = permissionSetsForNewDocuments
					.clone();
			return clone;
		}
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		if (meta == null) {
			throw new NullPointerException("Meta argument can not be null.");
		}

		this.meta = meta.clone();
	}

	/**
	 * Returns I18nMeta for a language. 
 	 * 
	 * Wraps method invocation to meta instance.
	 * 
	 * @throws I18nException if wrapped method call return null.
	 */
	public I18nMeta getI18nMeta(I18nLanguage language) {
		I18nMeta i18nMeta = meta.getI18nMeta(language);

		if (i18nMeta == null) {
			throw new I18nException("No I18nMeta found for document ["
					+ getId() + "], language [" + language + "].");
		}

		return i18nMeta;
	}

	/**
	 * // TODO: refactor into visitor
	 * 
	 * For legacy code support: When saving document copy as new document its
	 * dependencies metaId should be set to null.
	 * 
	 * @see DocumentSaver.saveNewDocument
	 */
	public void setDependenciesMetaIdToNull() {
		meta.setId(null);
		
    	Set<I18nMeta> i18nMetas = meta.getI18nMetas();
    	
    	if (i18nMetas != null) {
    		for (I18nMeta i18nMeta: i18nMetas) {
    			i18nMeta.setId(null);
    			i18nMeta.setMetaId(null);
    		}
    	}		
	}
}
