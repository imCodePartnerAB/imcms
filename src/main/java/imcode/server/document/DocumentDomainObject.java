package imcode.server.document;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.UserService;
import com.imcode.imcms.domain.dto.CategoryDTO;
import com.imcode.imcms.mapping.*;
import com.imcode.imcms.mapping.container.DocRef;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.util.l10n.LocalizedMessage;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * Parent of all imCMS document types.
 * <p>
 * Holds document meta and a content.
 */
public abstract class DocumentDomainObject implements Cloneable, Serializable {

    public static final int ID_NEW = 0;

    /**
     * Document's alias.
     * Can be set, changed and removed by an user which have rights to modify document information.
     * Must not be used as a hardcoded identity to access documents through API.
     *
     * @see DefaultDocumentMapper#getDocument(String)
     */
    private static final long serialVersionUID = 9196527330127566553L;

    private static final Logger log = LogManager.getLogger(DocumentDomainObject.class);

    private volatile DocumentMeta meta = new DocumentMeta();

    private volatile DocumentCommonContent commonContent = DocumentCommonContent.builder().build();

    private volatile int versionNo = DocumentVersion.WORKING_VERSION_NO;

    /*
     * Stub.
     * Documents instances are created directly (using new) only in tests.
     * In production instances are created via factories that are responsible for injecting an appropriate language.
     */
    private volatile Language language;

    /**
     * Factory method. Creates new document.
     *
     * @param documentTypeId document type id.
     * @param <T> Corresponding docuemnt type
     * @return new document
     */
    public static <T extends DocumentDomainObject> T fromDocumentTypeId(int documentTypeId) {
        DocumentDomainObject document;

        final DocumentType documentType = DocumentType.values()[documentTypeId];

        switch (documentType) {
            case TEXT:
                document = new TextDocumentDomainObject();
                break;
            case URL:
                document = new UrlDocumentDomainObject();
                break;
            case FILE:
                document = new FileDocumentDomainObject();
                break;
            case HTML:
                document = new HtmlDocumentDomainObject();
                break;
            default:
                String errorMessage = "Unknown document-type-id: " + documentTypeId;
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
        }

        document.setLanguage(Imcms.getServices().getLanguageService().getDefaultLanguage());
        document.setVersionNo(DocumentVersion.WORKING_VERSION_NO);

        return (T) document;
    }

    private static boolean isActiveAtTime(DocumentMeta meta, Date now) {
        return isPublishedAtTime(meta, now) && !hasBeenArchivedAtTime(meta, now);
    }

    private static boolean hasBeenArchivedAtTime(DocumentMeta meta, Date time) {
        Date archivedDatetime = meta.getArchivedDatetime();
        return archivedDatetime != null && archivedDatetime.before(time);
    }

    private static boolean isPublishedAtTime(DocumentMeta meta, Date date) {
        boolean statusIsApproved = Document.PublicationStatus.APPROVED.equals(meta.getPublicationStatus());
		Date publicationStartDatetime = meta.getPublicationStartDatetime();
	    Date publicationEndDatetime = meta.getPublicationEndDatetime();

	    return publicationHasStartedAtTime(meta, date) && statusIsApproved &&
			    (!publicationHasEndedAtTime(meta, date) || publicationEndDatetime.before(publicationStartDatetime));
    }

    private static boolean publicationHasStartedAtTime(DocumentMeta meta, Date date) {
        Date publicationStartDatetime = meta.getPublicationStartDatetime();
        return publicationStartDatetime != null && publicationStartDatetime.before(date);
    }

    private static boolean publicationHasEndedAtTime(DocumentMeta meta, Date date) {
        Date publicationEndDatetime = meta.getPublicationEndDatetime();
        return publicationEndDatetime != null && publicationEndDatetime.before(date);
    }

    static LifeCyclePhase getLifeCyclePhaseAtTime(DocumentDomainObject doc, Date time) {
        DocumentMeta meta = doc.getMeta();
	    Date publicationStartDatetime = meta.getPublicationStartDatetime();
	    Date publicationEndDatetime = meta.getPublicationEndDatetime();
        LifeCyclePhase lifeCyclePhase;

        Document.PublicationStatus publicationStatus = meta.getPublicationStatus();
        if (publicationStatus == Document.PublicationStatus.NEW) {
            lifeCyclePhase = LifeCyclePhase.NEW;
        } else if (publicationStatus == Document.PublicationStatus.DISAPPROVED) {
            lifeCyclePhase = LifeCyclePhase.DISAPPROVED;
        } else {
	        if (publicationHasEndedAtTime(meta, time) && (publicationHasStartedAtTime(meta, time) && publicationStartDatetime.before(publicationEndDatetime))
			        || publicationStartDatetime == null) {
                lifeCyclePhase = LifeCyclePhase.UNPUBLISHED;
            } else if (publicationHasStartedAtTime(meta, time)) {
                if (hasBeenArchivedAtTime(meta, time)) {
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

    private static DocumentDomainObject asDefaultUser(int docId, Language language) {
        DocumentMapper documentMapper = Imcms.getServices().getDocumentMapper();

        DocGetterCallback docGetterCallback = Imcms.getServices()
                .getImcmsAuthenticatorAndUserAndRoleMapper()
                .getDefaultUser() // check callback as default user because admin should see docs in menu as others
                .getDocGetterCallback();

        docGetterCallback.setLanguage(language);
        return docGetterCallback.getDoc(docId, documentMapper);
    }

    public static DocumentDomainObject asDefaultUser(DocumentDomainObject document) {
        return DocumentDomainObject.asDefaultUser(document.getId(), document.getLanguage());
    }

    @Override
    public DocumentDomainObject clone() {
        DocumentDomainObject clone;

        try {
            clone = (DocumentDomainObject) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        if (clone.meta != null) {
            clone.meta = meta.clone();
        }

        return clone;
    }

    /**
     * Returns this document's version no.
     * @return number of version
     */
    public int getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(int no) {
        versionNo = no;
    }

    public DocRef getRef() {
        return DocRef.of(getId(), getVersionNo(), getLanguage().getCode());
    }

    public VersionRef getVersionRef() {
        return VersionRef.of(getId(), getVersionNo());
    }

    public Date getArchivedDatetime() {
        return meta.getArchivedDatetime();
    }

    public void setArchivedDatetime(Date v) {
        meta.setArchivedDatetime(v);
    }

    private Integer getArchiverId() {
        return meta.getArchiverId();
    }

    public void setArchiverId(Integer id) {
        meta.setArchiverId(id);
    }

    public Set<Category> getCategories() {
        return meta.getCategories();
    }

    public void setCategories(Set<Category> categories) {
        meta.setCategories(categories);
    }

    public Date getCreatedDatetime() {
        return meta.getCreatedDatetime();
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

    public String getHeadline() {
        return commonContent.getHeadline();
    }

    public void setHeadline(String v) {
        setCommonContent(DocumentCommonContent.builder(getCommonContent()).headline(v).build());
    }

    public int getId() {
        Integer id = meta.getId();
        return id == null ? ID_NEW : id;
    }

    public void setId(int id) {
        meta.setId(id == ID_NEW ? null : id);
    }

    public Set<String> getKeywords() {
        return meta.getKeywords();
    }

    public void setKeywords(Set<String> keywords) {
        meta.setKeywords(keywords);
    }

    public Map<String, String> getProperties() {
        return meta.getProperties();
    }

    public void setProperties(Map<String, String> properties) {
        meta.setProperties(properties);
    }

    public String getProperty(String key) {
        Map<String, String> properties = meta.getProperties();
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        Map<String, String> properties = meta.getProperties();
        properties.put(key, value);
    }

    public String getMenuText() {
        return commonContent.getMenuText();
    }

    public void setMenuText(String v) {
        setCommonContent(DocumentCommonContent.builder(getCommonContent()).menuText(v).build());
    }

    public Date getModifiedDatetime() {
        return meta.getModifiedDatetime();
    }

    public void setModifiedDatetime(Date v) {
        meta.setModifiedDatetime(v);
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

    private Integer getDepublisherId() {
        return meta.getDepublisherId();
    }

    public void setDepublisherId(Integer id) {
        meta.setDepublisherId(id);
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

    public void setPublisherId(Integer publisherId) {
        meta.setPublisherId(publisherId);
    }

    public void setPublisher(UserDomainObject user) {
        setPublisherId(user.getId());
    }

    private Integer getModifierId() {
        return Imcms.getServices().getDocumentMapper().getDocumentVersionInfo(getId()).getLatestVersion().getModifiedBy().getId();
    }

    public void setDocumentPermissionSetTypeForRoleId(Integer roleId, Meta.Permission permission) {
        this.getRolePermissionMappings().setPermissionSetTypeForRole(roleId, permission);
    }

    public Meta.Permission getDocumentPermissionSetTypeForRoleId(Integer roleId) {
        return this.getRolePermissionMappings().getPermissionSetTypeForRole(roleId);
    }

    public RoleIdToDocumentPermissionSetTypeMappings getRoleIdsMappedToDocumentPermissionSetTypes() {
        return getRolePermissionMappings().clone();
    }

    private RoleIdToDocumentPermissionSetTypeMappings getRolePermissionMappings() {
        return meta.getRoleIdToDocumentPermissionSetTypeMappings();
    }

    public Document.PublicationStatus getPublicationStatus() {
        return meta.getPublicationStatus();
    }

    public void setPublicationStatus(Document.PublicationStatus status) {
        meta.setPublicationStatus(status);
    }

    public String getTarget() {
        return meta.getTarget();
    }

    public void setTarget(String v) {
        meta.setTarget(v);
    }

    public boolean isArchived() {
        return hasBeenArchivedAtTime(meta, new Date());
    }

    public boolean isLinkableByOtherUsers() {
        return meta.getLinkableByOtherUsers();
    }

    public void setLinkableByOtherUsers(boolean linkableByOtherUsers) {
        meta.setLinkableByOtherUsers(linkableByOtherUsers);
    }

    public boolean isLinkedForUnauthorizedUsers() {
        return meta.getLinkedForUnauthorizedUsers();
    }

    public void setLinkedForUnauthorizedUsers(boolean linkedForUnauthorizedUsers) {
        meta.setLinkedForUnauthorizedUsers(linkedForUnauthorizedUsers);
    }

    public boolean isCacheForUnauthorizedUsers() {
        return meta.isCacheForUnauthorizedUsers();
    }

    public void setCacheForUnauthorizedUsers(boolean cacheForUnauthorizedUsers) {
        meta.setCacheForUnauthorizedUsers(cacheForUnauthorizedUsers);
    }

    public boolean isCacheForAuthorizedUsers() {
        return meta.isCacheForAuthorizedUsers();
    }

    public void setCacheForAuthorizedUsers(boolean cacheForAuthorizedUsers) {
        meta.setCacheForAuthorizedUsers(cacheForAuthorizedUsers);
    }

    public boolean isPublished() {
        return isPublishedAtTime(meta, new Date());
    }

    public boolean hasDisapprovedStatus() {
        return Document.PublicationStatus.DISAPPROVED.equals(meta.getPublicationStatus());
    }

    public boolean hasNewStatus() {
        return Document.PublicationStatus.NEW.equals(meta.getPublicationStatus());
    }

    public boolean isActive() {
        return isActiveAtTime(meta, new Date());
    }

    public boolean isSearchDisabled() {
        return meta.isSearchDisabled();
    }

    public void setSearchDisabled(boolean searchDisabled) {
        meta.setSearchDisabled(searchDisabled);
    }

    public boolean isVisible(){
        return meta.getVisible();
    }

    public void setVisible(boolean visible){
        meta.setVisible(visible);
    }

    public void addCategory(Category category) {
        meta.getCategories().add(category);
    }

    @Deprecated
    public void addCategory(com.imcode.imcms.api.Category category) {
        final CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setType(category.getType());

        addCategory(categoryDTO);
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

	public void removeCategoryId(int categoryId) {
		meta.getCategories().remove(categoryId);
	}

	public abstract void accept(DocumentVisitor documentVisitor);

	public LifeCyclePhase getLifeCyclePhase() {
		return getLifeCyclePhaseAtTime(this, new Date());
	}

	public String getAlias() {
//		final Language defaultLanguageCode = Imcms.getServices().getLanguageService().getDefaultLanguage();
//		final CommonContent defaultLanguageCommonContent = Imcms.getServices().getCommonContentService().getOrCreate(getId(), versionNo, defaultLanguageCode);
//
//		final String alias = isDefaultLanguageAliasEnabled() ? defaultLanguageCommonContent.getAlias() : commonContent.getAlias();
//
//		return StringUtils.defaultIfBlank(alias, null);
        return commonContent.getAlias();
	}

	public void setAlias(String alias) {
		setCommonContent(DocumentCommonContent.builder(getCommonContent()).alias(alias).build());
	}

	public String getName() {
		return StringUtils.defaultIfBlank(getAlias(), String.valueOf(getId()));
	}

	public DocumentMeta getMeta() {
		return meta;
	}

	public void setMeta(DocumentMeta meta) {
		Objects.requireNonNull(meta, "meta argument can not be null.");

		this.meta = meta.clone();
	}

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = Objects.requireNonNull(language, "language argument can not be null.");
    }

    public DocumentCommonContent getCommonContent() {
        return commonContent;
    }

    public void setCommonContent(DocumentCommonContent commonContent) {
        this.commonContent = Objects.requireNonNull(commonContent, "commonContent argument can not be null.");
    }

    public Date[] getArrDates() {
        return new Date[]{
                getCreatedDatetime(),
                getModifiedDatetime(),
                getArchivedDatetime(),
                getPublicationStartDatetime(),
                getPublicationEndDatetime()
        };
    }

    public boolean isNew() {
        return getId() == ID_NEW;
    }

    public String[] getByUsersArr(UserService userService) {

	    Integer[] usersIds = new Integer[]{
			    getCreatorId(),
			    getModifierId(),
			    getArchiverId(),
			    getPublisherId(),
			    getDepublisherId(),
	    };

	    return Stream.of(usersIds)
			    .map(userId -> Optional.ofNullable(userId)
					    .map(id -> userService.getUser(id).getLoginName())
					    .orElse("--"))
			    .toArray(String[]::new);
    }

	public Boolean isDefaultLanguageAliasEnabled() {
		 return meta.getDefaultLanguageAliasEnabled() != null && meta.getDefaultLanguageAliasEnabled();
	}

	public void setDefaultLanguageAlias(boolean enabled) {
		meta.setDefaultLanguageAliasEnabled(enabled);
	}

	public DocumentMeta.DisabledLanguageShowMode getDisabledLanguageShowMode() {
		return meta.getDisabledLanguageShowMode();
	}

	public void setDisabledLanguageShowMode(String disabledLanguageShowMode) {
		meta.setDisabledLanguageShowMode(DocumentMeta.DisabledLanguageShowMode.valueOf(disabledLanguageShowMode));
	}

    public boolean isInWasteBasket(){
        return meta.getDocumentWasteBasket() != null;
    }
}
