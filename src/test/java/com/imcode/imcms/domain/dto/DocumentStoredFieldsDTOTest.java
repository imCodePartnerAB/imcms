package com.imcode.imcms.domain.dto;

import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import imcode.server.user.UserDomainObject;
import org.apache.solr.common.SolrDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
public class DocumentStoredFieldsDTOTest {

    @BeforeEach
    public void setUp() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLanguageIso639_2("eng"); // user lang should exist in common content
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        Imcms.setUser(user);
    }

    @Test
    public void createEmpty_When_PublicationStatusIsDisapproved_Expect_DocumentStatusIsDisapproved() {
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.DISAPPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__ALIAS, "alias");
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.DISAPPROVED));
    }

    @Test
    public void createEmpty_When_PublicationStatusIsNew_Expect_DocumentStatusIsInProcess() {
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.NEW.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__ALIAS, "alias");
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.IN_PROCESS));
    }

    @Test
    public void createEmpty_When_DocumentArchivedDateIsInPast_Expect_DocumentStatusArchived() {
        final Date archivedDate = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__ALIAS, "alias");
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__ARCHIVED_DATETIME, archivedDate);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.ARCHIVED));
    }

    @Test
    public void createEmpty_When_DocumentPublicationEndDateIsInPast_Expect_DocumentStatusPassed() {
        final Date publicationEnd = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__ALIAS, "alias");
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, publicationEnd);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PASSED));
    }

    @Test
    public void createEmpty_When_PublicationStatusIsApprovedAndPublicationDateIsInFuture_Expect_DocumentStatusPublishedWaiting() {
        final Date published = new Date(LocalDateTime.now().plusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__ALIAS, "alias");
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PUBLISHED_WAITING));
    }

    @Test
    public void createEmpty_When_PublicationStatusIsApprovedAndPublicationDateIsInPast_Expect_DocumentStatusPublished() {
        final Date published = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__ALIAS, "alias");
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PUBLISHED));
    }

}