package com.imcode.imcms.domain.dto;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.LanguageDataInitializer;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Meta.PublicationStatus;
import imcode.server.Imcms;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.index.DocumentStoredFields;
import org.apache.solr.common.SolrDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class DocumentStoredFieldsDTOTest extends WebAppSpringTestConfig {

    private static final Integer WORKING_VERSION = 0;
    private static final Integer LATEST_VERSION = 1;

    @Autowired
    private LanguageDataInitializer languageDataInitializer;

    @BeforeEach
    public void setUp() {
        final Language currentLanguage = languageDataInitializer.createData().get(0);
        Imcms.setLanguage(currentLanguage);
    }

    @Test
    public void createEmpty_When_PublicationStatusIsDisapproved_Expect_DocumentStatusIsDisapproved() {
	    final SolrDocument solrDocument = new SolrDocument();
	    solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.DISAPPROVED.ordinal());
	    solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
	    solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
	    solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
	    solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
	    solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
	    solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
	    solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

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
	    solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
	    solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
	    solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
	    solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
	    solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

	    DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
	    final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

	    assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.IN_PROCESS));
    }

    @Test
    public void createEmpty_When_DocumentArchivedDateIsInPast_Expect_DocumentStatusInProcess() {
        final Date archivedDate = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__ARCHIVED_DATETIME, archivedDate);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, LATEST_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

	    DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
	    final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.IN_PROCESS));
    }

    @Test
    public void createEmpty_When_DocumentPublicationEndDateIsInPast_And_DocumentIsNotPublished_Expect_DocumentStatusInProcess() {
        final Date publicationEnd = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, publicationEnd);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, LATEST_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.IN_PROCESS));
    }

    @Test
    public void createEmpty_When_DocumentArchivedDate_After_DocumentIsPublished_Expect_DocumentStatusArchived() {
        final Date archivedDate = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date publishedDate = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);

        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, publishedDate);
        solrDocument.addField(DocumentIndex.FIELD__ARCHIVED_DATETIME, archivedDate);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, LATEST_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.ARCHIVED));
    }

    @Test
    public void createEmpty_When_DocumentArchivedDateInPast_And_DocumentWillBePublishedInFuture_Expect_DocumentStatusPublishedWaiting() {
        final Date archivedDate = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date publishedDate = new Date(LocalDateTime.now().plusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);

        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, publishedDate);
        solrDocument.addField(DocumentIndex.FIELD__ARCHIVED_DATETIME, archivedDate);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, LATEST_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PUBLISHED_WAITING));
    }

    @Test
    public void createEmpty_When_PublicationStatusIsApproved_And_PublicationDateIsInFuture_Expect_DocumentStatusPublishedWaiting() {
        final Date published = new Date(LocalDateTime.now().plusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PUBLISHED_WAITING));
    }

    @Test
    public void createEmpty_When_PublicationEnd_After_PublicationStart_And_InPast_Expect_DocumentStatusPassed() {
        final Date published = new Date(LocalDateTime.now().minusYears(2).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date publishedEnd = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);

        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, publishedEnd);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PASSED));
    }

    @Test
    public void createEmpty_When_PublicationEnd_Before_PublicationStart_And_InPast_Expect_DocumentStatusPublished() {
        final Date published = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date publishedEnd = new Date(LocalDateTime.now().minusYears(2).toEpochSecond(ZoneOffset.UTC) * 1000);

        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, publishedEnd);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PUBLISHED));
    }

    @Test
    public void createEmpty_When_PublicationEnd_Before_PublicationStart_And_PublicationStartIsInFuture_Expect_DocumentStatusPublishedWaiting() {
        final Date published = new Date(LocalDateTime.now().plusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date publishedEnd = new Date(LocalDateTime.now().minusYears(2).toEpochSecond(ZoneOffset.UTC) * 1000);

        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, publishedEnd);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PUBLISHED_WAITING));
    }

    @Test
    public void createEmpty_When_PublicationEnd_After_ArchivedDate_And_DocumentIsPublished_Expect_DocumentStatusPassed() {
        final Date published = new Date(LocalDateTime.now().minusYears(3).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date archived = new Date(LocalDateTime.now().minusYears(2).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date publishedEnd = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);

        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
        solrDocument.addField(DocumentIndex.FIELD__ARCHIVED_DATETIME, archived);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, publishedEnd);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

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
	    solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
	    solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
	    solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
	    solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
	    solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
	    solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

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
	    solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
	    solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
	    solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
	    solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
	    solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, LATEST_VERSION);
	    solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, false);

	    DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
	    final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

	    assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.PUBLISHED));
    }

    @Test
    public void createEmpty_When_DocumentInWasteBasket_Expect_DocumentStatusWasteBasket() {
        final Date published = new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000);
        final Date publishedEnd = new Date(LocalDateTime.now().minusYears(2).toEpochSecond(ZoneOffset.UTC) * 1000);

        final SolrDocument solrDocument = new SolrDocument();
        solrDocument.addField(DocumentIndex.FIELD__STATUS, PublicationStatus.APPROVED.ordinal());
        solrDocument.addField(DocumentIndex.FIELD__META_ID, 1001);
        solrDocument.addField(DocumentIndex.FIELD__META_HEADLINE + "_en", "headline");
        solrDocument.addField(DocumentIndex.FIELD__META_ALIAS + "_en", "alias");
        solrDocument.addField(DocumentIndex.FIELD__DEFAULT_LANGUAGE_ALIAS_ENABLED, false);
        solrDocument.addField(DocumentIndex.FIELD__DOC_TYPE_ID, 2);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_START_DATETIME, published);
        solrDocument.addField(DocumentIndex.FIELD__PUBLICATION_END_DATETIME, publishedEnd);
        solrDocument.addField(DocumentIndex.FIELD__VERSION_NO, WORKING_VERSION);
        solrDocument.addField(DocumentIndex.FIELD__DISABLED_LANGUAGE_SHOW_MODE, Meta.DisabledLanguageShowMode.SHOW_IN_DEFAULT_LANGUAGE.name());
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_UNAUTHORIZED, true);
        solrDocument.addField(DocumentIndex.FIELD__LINKABLE_OTHER, true);
        solrDocument.addField(DocumentIndex.FIELD__IN_WASTE_BASKET, true);

        DocumentStoredFields storedFields = new DocumentStoredFields(solrDocument);
        final DocumentStoredFieldsDTO documentStoredFieldsDTO = new DocumentStoredFieldsDTO(storedFields);

        assertThat(documentStoredFieldsDTO.getDocumentStatus(), equalTo(DocumentStatus.WASTE_BASKET));
    }

}