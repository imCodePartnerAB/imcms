package imcode.server.document.index;


import org.apache.solr.common.SolrDocument;

import java.util.Collection;
import java.util.Date;

/**
 * Document's fields stored in a Solr index.
 */
public class DocumentStoredFields {

    private final SolrDocument solrDocument;

    public DocumentStoredFields(SolrDocument solrDocument) {
        this.solrDocument = solrDocument;
    }

    public int metaId() {
        return (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__META_ID);
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

    public int type() {
        return (Integer) solrDocument.getFieldValue(DocumentIndex.FIELD__DOC_TYPE_ID);
    }

    public String phase() {
        return (String) solrDocument.getFieldValue(DocumentIndex.FIELD__PHASE);
    }

    public Date createdDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__CREATED_DATETIME);
    }

    public Date modifiedDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__MODIFIED_DATETIME);
    }

    public Date publicationDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_START_DATETIME);
    }

    public Date archivingDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__ARCHIVED_DATETIME);
    }

    public Date expireDt() {
        return (Date) solrDocument.getFieldValue(DocumentIndex.FIELD__PUBLICATION_END_DATETIME);
    }

    public Collection<Integer> parentsIds() {
        return (Collection<Integer>) (Collection<?>) solrDocument.getFieldValues(DocumentIndex.FIELD__PARENT_ID);
    }

    public Collection<Integer> childrenIds() {
        return (Collection<Integer>) (Collection<?>) solrDocument.getFieldValues(DocumentIndex.FIELD__CHILD_ID);
    }
}
