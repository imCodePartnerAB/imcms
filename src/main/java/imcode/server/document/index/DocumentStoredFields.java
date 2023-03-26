package imcode.server.document.index;


import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import imcode.server.Imcms;
import org.apache.solr.common.SolrDocument;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static imcode.util.Utility.*;

/**
 * Document's fields stored in a Solr index.
 */
public class DocumentStoredFields {

	private final SolrDocument solrDocument;

	public DocumentStoredFields(SolrDocument solrDocument) {
		this.solrDocument = solrDocument;
	}

	public int id() {
		return Integer.parseInt(solrDocument.getFieldValue(DocumentIndex.FIELD__META_ID).toString());
	}

	public int versionNo() {
		return (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__VERSION_NO);
	}

	public String headline() {
		return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__META_HEADLINE + "_" + getCurrentLanguage());
	}

	public String alias() {
		return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__META_ALIAS + "_" + getCurrentLanguage());
	}

	private String getCurrentLanguage() {
		final String currentLanguage = Imcms.getLanguage().getCode();
		final String defaultLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage().getCode();

		return isLanguageEnabled(currentLanguage)
				? currentLanguage
				: (isLanguageEnabled(defaultLanguage) && isShownInDefaultLanguage()) ? defaultLanguage : "";
	}

	private boolean isLanguageEnabled(String languageCode) {
		return enabledLanguages().contains(languageCode);
	}

	public List<String> languages() {
		return (List<String>) solrDocument.getFieldValue(DocumentIndex.FIELD__LANGUAGE_CODE);
	}

	public List<String> enabledLanguages() {
		final Collection<Object> enabledLanguages = solrDocument.getFieldValues(DocumentIndex.FIELD__ENABLED_LANGUAGE_CODE);

		if (enabledLanguages == null) {
			return Collections.emptyList();
        }

		return enabledLanguages.stream()
				.map(obj -> (String) obj)
				.collect(Collectors.toList());
	}

	public DocumentType documentType() {
		final Integer typeOrdinal = (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__DOC_TYPE_ID);
		return DocumentType.values()[typeOrdinal];
	}

	public PublicationStatus publicationStatus() {
		final Integer statusOrdinal = (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__STATUS);
		return PublicationStatus.values()[statusOrdinal];
	}

	public boolean isShownInDefaultLanguage() {
		final String disableLanguageShowMode = (String) solrDocument.getFieldValue(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE);
		return Meta.DisabledLanguageShowMode.valueOf(disableLanguageShowMode) == Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE;
	}

	public boolean isDefaultLanguageAliasEnabled() {
		return (boolean) solrDocument.getFieldValue(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED);
	}

	public boolean isShownTitle() {
		final String currentLanguage = Imcms.getLanguage().getCode();
		final String defaultLanguage = Imcms.getServices().getLanguageService().getDefaultLanguage().getCode();

		return isLanguageEnabled(currentLanguage) || (isShownInDefaultLanguage() && isLanguageEnabled(defaultLanguage));
	}

	public Date created() {
		return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__CREATED_DATETIME);
	}

	public String createdBy() {
		return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__CREATOR_NAME);
	}

	public Date modified() {
		return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__MODIFIED_DATETIME);
	}

    public String modifiedBy() {
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__MODIFIER_NAME);
    }

    public Date publicationStart() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_START_DATETIME);
    }

    public String publicationStartBy() {
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLISHER_NAME);
    }

    public Date archived() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__ARCHIVED_DATETIME);
    }

    public Date publicationEndDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_END_DATETIME);
    }

    public DocumentStatus documentStatus() {
        final PublicationStatus publicationStatus = publicationStatus();

		if(isInWasteBasket()){
			return DocumentStatus.WASTE_BASKET;
		}

        if (PublicationStatus.NEW.equals(publicationStatus)) {
            return DocumentStatus.IN_PROCESS;
        }
        if (PublicationStatus.DISAPPROVED.equals(publicationStatus)) {
            return DocumentStatus.DISAPPROVED;
        }

        if (publicationStart() == null) {
            return DocumentStatus.IN_PROCESS;
        }
        if ((isDateInPast.test(publicationEndDt()) && isDateBefore.test(publicationStart(), publicationEndDt()))
                || publicationStart().equals(publicationEndDt())) {
            return DocumentStatus.PASSED;
        }
        if (isDateInPast.test(archived()) && !isDateInFuture.test(publicationStart())) {
            return DocumentStatus.ARCHIVED;
        }
        if (isDateInFuture.test(publicationStart())) {
            return DocumentStatus.PUBLISHED_WAITING;
        }

        return DocumentStatus.PUBLISHED;
    }

	public boolean linkableForUnauthorizedUsers() {
		return (Boolean) solrDocument.getFieldValue(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED);
	}

	public boolean linkableByOtherUsers() {
		return (Boolean) solrDocument.getFieldValue(DocumentIndex.FIELD__LINKABLE_OTHER);
	}

	public boolean isInWasteBasket(){
		return (Boolean) solrDocument.getFieldValue(DocumentIndex.FIELD__IN_WASTE_BASKET);
	}
}
