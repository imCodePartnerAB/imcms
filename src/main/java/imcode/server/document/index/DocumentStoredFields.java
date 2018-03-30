package imcode.server.document.index;


import com.imcode.imcms.domain.dto.DocumentStatus;
import com.imcode.imcms.persistence.entity.Meta.DocumentType;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import imcode.server.Imcms;
import org.apache.solr.common.SolrDocument;

import java.util.Date;

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
        final String language = Imcms.getUser().getLanguage();
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__META_HEADLINE + "_" + language);
    }

    public String alias() {
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__ALIAS);
    }

    public String languageCode() {
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__LANGUAGE_CODE);
    }

    public DocumentType documentType() {
        final Integer typeOrdinal = (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__DOC_TYPE_ID);
        return DocumentType.values()[typeOrdinal];
    }

    public PublicationStatus publicationStatus() {
        final Integer statusOrdinal = (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__STATUS);
        return PublicationStatus.values()[statusOrdinal];
    }

    public Date created() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__CREATED_DATETIME);
    }

    public Date modified() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__MODIFIED_DATETIME);
    }

    public Date publicationStart() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_START_DATETIME);
    }

    public Date archived() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__ARCHIVED_DATETIME);
    }

    public Date publicationEndDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_END_DATETIME);
    }

    public DocumentStatus documentStatus() {
        final PublicationStatus publicationStatus = publicationStatus();

        if (PublicationStatus.NEW.equals(publicationStatus)) {
            return DocumentStatus.IN_PROCESS;

        } else if (PublicationStatus.DISAPPROVED.equals(publicationStatus)) {
            return DocumentStatus.DISAPPROVED;

        } else if (isDateInPast(archived())) {
            return DocumentStatus.ARCHIVED;

        } else if (isDateInPast(publicationEndDt())) {
            return DocumentStatus.PASSED;

        } else if (PublicationStatus.APPROVED.equals(publicationStatus) && isDateInPast(publicationStart())) {
            return DocumentStatus.PUBLISHED;

        } else if (PublicationStatus.APPROVED.equals(publicationStatus) && isDateInFuture(publicationStart())) {
            return DocumentStatus.PUBLISHED_WAITING;

        } else { // should newer happen
            return DocumentStatus.PUBLISHED;
        }
    }

    private boolean isDateInPast(Date dateToCheck) {
        return (dateToCheck != null) && new Date().after(dateToCheck);
    }

    private boolean isDateInFuture(Date dateToCheck) {
        return (dateToCheck != null) && new Date().before(dateToCheck);
    }

}
