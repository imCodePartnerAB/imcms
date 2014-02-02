package imcode.server.document.index;


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
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__META_HEADLINE);
    }

    public String alias() {
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__ALIAS);
    }

    public String languageCode() {
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__LANGUAGE_CODE);
    }

    public int documentType() {
        return (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__DOC_TYPE_ID);
    }

    public int publicationStatusId() {
        return (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__STATUS);
    }

    public Date createdDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__CREATED_DATETIME);
    }

    public Date modifiedDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__MODIFIED_DATETIME);
    }

    public Date publicationStartDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_START_DATETIME);
    }

    public Date archivingDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__ARCHIVED_DATETIME);
    }

    public Date publicationEndDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_END_DATETIME);
    }

    public int parentsCount() {
        return (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__PARENTS_COUNT);
    }

    public int childrenCount() {
        return (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__CHILDREN_COUNT);
    }
}
