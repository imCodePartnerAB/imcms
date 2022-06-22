package com.imcode.imcms.mapping;

import com.imcode.imcms.api.Document;
import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.model.Category;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.RestrictedPermissionJPA;
import imcode.server.Imcms;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.RoleIdToDocumentPermissionSetTypeMappings;
import lombok.Data;
import org.apache.commons.lang.NullArgumentException;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static imcode.server.document.DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS;

/**
 * Document's meta.
 * <p>
 * Shared by all versions of the same document.
 */
@Data
public class DocumentMeta implements Serializable, Cloneable {

	private static final long serialVersionUID = 7849963298323476235L;
	private volatile Integer id;
	private volatile int defaultVersionNo = DocumentVersion.WORKING_VERSION_NO;
	/**
	 * Disabled language's content show option.
	 */
	private volatile DisabledLanguageShowMode disabledLanguageShowMode = DisabledLanguageShowMode.DO_NOT_SHOW;
	private volatile Boolean defaultLanguageAliasEnabled;
	private volatile Integer documentTypeId;
	private volatile Boolean linkableByOtherUsers;
	private volatile Boolean linkedForUnauthorizedUsers;
	private volatile Boolean visible;
	/**
	 * (Saved) value of modified dt at the time this meta was actually loaded.
	 * When loaded from the db its value is set to modifiedDatetime.
	 * Used to test if modifiedDatetime was changed explicitly.
	 *
	 * @see com.imcode.imcms.mapping.DocumentSaver#updateDocument
	 */
	private volatile Date actualModifiedDatetime;
	private volatile boolean searchDisabled;
	private volatile String target;
	private volatile Date createdDatetime;
	private volatile Date modifiedDatetime;
	private volatile Date archivedDatetime;
	private volatile Date publicationStartDatetime;
	private volatile Date publicationEndDatetime;
	private volatile Integer creatorId;
	// we haven't modifierId field
    private volatile Integer archiverId;
    private volatile Integer publisherId;
    private volatile Integer depublisherId;
    private volatile Map<String, String> properties = new ConcurrentHashMap<>();
    private volatile Set<Category> categories = new CopyOnWriteArraySet<>();
    private volatile Set<String> keywords = new CopyOnWriteArraySet<>();
    private volatile RoleIdToDocumentPermissionSetTypeMappings roleIdToDocumentPermissionSetTypeMappings = new RoleIdToDocumentPermissionSetTypeMappings();
    private volatile Document.PublicationStatus publicationStatus = Document.PublicationStatus.NEW;
    private volatile Set<RestrictedPermissionJPA> restrictedPermissions;

    @Override
    public DocumentMeta clone() {
        try {
            DocumentMeta clone = (DocumentMeta) super.clone();

            clone.disabledLanguageShowMode = disabledLanguageShowMode;
            clone.properties = new ConcurrentHashMap<>(properties);
            clone.categories = new CopyOnWriteArraySet<>(categories);

            clone.keywords = new CopyOnWriteArraySet<>(keywords);

            if (roleIdToDocumentPermissionSetTypeMappings != null) {
                clone.roleIdToDocumentPermissionSetTypeMappings = roleIdToDocumentPermissionSetTypeMappings.clone();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

	public void setKeywords(Set<String> keywords) {
		this.keywords = new CopyOnWriteArraySet<>(keywords != null ? keywords : Collections.emptySet());
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

	/**
	 * key paired alias with language (e.g. langCode => alias)
	 */
	public Map<String, String> getAliases() {
		final List<Language> languages = Imcms.getServices().getLanguageService().getAvailableLanguages();
		final Map<String, String> aliases = new HashMap<>();

		languages.forEach(language -> {
			String key = DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS + '.' + language.getCode();
			String value = properties.get(key);
			aliases.put(language.getCode(), value);
		});

		return aliases;
	}

	public void setAliases(Map<String, String> aliases) {
		if (aliases.isEmpty()) {
			removeAliases();
		} else {
			properties.putAll(aliases);
		}
	}

	public void removeAliases() {
		final List<Language> languages = Imcms.getServices().getLanguageService().getAvailableLanguages();
		languages.forEach(language -> {
			properties.remove(DocumentDomainObject.DOCUMENT_PROPERTIES__IMCMS_DOCUMENT_ALIAS + '.' + language.getCode());
		});
	}

	/**
	 * Document show mode for disabled language.
	 */
	public enum DisabledLanguageShowMode {
		SHOW_IN_DEFAULT_LANGUAGE,
		DO_NOT_SHOW,
	}
}
