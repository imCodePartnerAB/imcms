package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class DocumentDtoFactoryTest {

    @Mock
    private CommonContentFactory commonContentFactory;

    @InjectMocks
    private DocumentDtoFactory documentDtoFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(commonContentFactory.createCommonContents()).thenReturn(new ArrayList<>());
    }

    @Test
    public void createEmpty() {
        final DocumentDTO emptyDocument = documentDtoFactory.createEmpty();

        assertNull(emptyDocument.getType());
        assertNull(emptyDocument.getId());
    }

    @Test
    public void createEmptyFileDocument() {
        final FileDocumentDTO emptyFileDocument = documentDtoFactory.createEmptyFileDocument();

        assertThat(emptyFileDocument.getType(), equalTo(Meta.DocumentType.FILE));
        assertNull(emptyFileDocument.getId());
    }

    @Test
    public void createEmptyTextDocument() {
        final TextDocumentDTO emptyTextDocument = documentDtoFactory.createEmptyTextDocument();

        assertThat(emptyTextDocument.getType(), equalTo(Meta.DocumentType.TEXT));
        assertNull(emptyTextDocument.getId());
    }

    @Test
    public void createEmptyUrlDocument() {
        final UrlDocumentDTO emptyUrlDocument = documentDtoFactory.createEmptyUrlDocument();

        assertThat(emptyUrlDocument.getType(), equalTo(Meta.DocumentType.URL));
        assertNull(emptyUrlDocument.getId());
    }

    @Test
    public void createEmpty_When_PublicationStatusIsDisapproved_Expect_DocumentStatusIsDisapproved() {
        final DocumentDTO empty = documentDtoFactory.createEmpty();
        empty.setPublicationStatus(Meta.PublicationStatus.DISAPPROVED);
        final DocumentDTO emptyDocument = new DocumentDTO(empty);

        assertThat(emptyDocument.getDocumentStatus(), equalTo(Document.DocumentStatus.DISAPPROVED));
    }

    @Test
    public void createEmpty_When_PublicationStatusIsNew_Expect_DocumentStatusIsInProcess() {
        final DocumentDTO empty = documentDtoFactory.createEmpty();
        empty.setPublicationStatus(Meta.PublicationStatus.NEW);
        final DocumentDTO emptyDocument = new DocumentDTO(empty);

        assertThat(emptyDocument.getDocumentStatus(), equalTo(Document.DocumentStatus.IN_PROCESS));
    }

    @Test
    public void createEmpty_When_DocumentArchivedDateIsInPast_Expect_DocumentStatusArchived() {
        final DocumentDTO empty = documentDtoFactory.createEmpty();
        final AuditDTO archived = new AuditDTO();
        archived.setDateTime(new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000));
        empty.setArchived(archived);
        empty.setPublicationStatus(Meta.PublicationStatus.APPROVED);

        final DocumentDTO emptyDocument = new DocumentDTO(empty);

        assertThat(emptyDocument.getDocumentStatus(), equalTo(Document.DocumentStatus.ARCHIVED));
    }

    @Test
    public void createEmpty_When_DocumentPublicationEndDateIsInPast_Expect_DocumentStatusPassed() {
        final DocumentDTO empty = documentDtoFactory.createEmpty();
        final AuditDTO publicationEnd = new AuditDTO();
        publicationEnd.setDateTime(new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000));
        empty.setPublicationEnd(publicationEnd);
        empty.setPublicationStatus(Meta.PublicationStatus.APPROVED);

        final DocumentDTO emptyDocument = new DocumentDTO(empty);

        assertThat(emptyDocument.getDocumentStatus(), equalTo(Document.DocumentStatus.PASSED));
    }

    @Test
    public void createEmpty_When_PublicationStatusIsApprovedAndPublicationDateIsInFuture_Expect_DocumentStatusPublishedWaiting() {
        final AuditDTO published = new AuditDTO();
        published.setDateTime(new Date(LocalDateTime.now().plusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000));

        final DocumentDTO empty = documentDtoFactory.createEmpty();
        empty.setPublicationStatus(Meta.PublicationStatus.APPROVED);
        empty.setPublished(published);

        final DocumentDTO emptyDocument = new DocumentDTO(empty);

        assertThat(emptyDocument.getDocumentStatus(), equalTo(Document.DocumentStatus.PUBLISHED_WAITING));
    }

    @Test
    public void createEmpty_When_PublicationStatusIsApprovedAndPublicationDateIsInPast_Expect_DocumentStatusPublished() {
        final AuditDTO published = new AuditDTO();
        published.setDateTime(new Date(LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC) * 1000));

        final DocumentDTO empty = documentDtoFactory.createEmpty();
        empty.setPublicationStatus(Meta.PublicationStatus.APPROVED);
        empty.setPublished(published);

        final DocumentDTO emptyDocument = new DocumentDTO(empty);

        assertThat(emptyDocument.getDocumentStatus(), equalTo(Document.DocumentStatus.PUBLISHED));
    }
}