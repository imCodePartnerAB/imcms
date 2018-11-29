package com.imcode.imcms.persistence.repository;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.UrlDocumentDataInitializer;
import com.imcode.imcms.components.datainitializer.VersionDataInitializer;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Version;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class DocumentUrlRepositoryTest extends WebAppSpringTestConfig {

    private static final int TEST_VERSION_INDEX = 0;
    private static final int DEFAULT_DOC_ID = 1001;

    @Autowired
    private DocumentUrlRepository documentUrlRepository;

    @Autowired
    private UrlDocumentDataInitializer documentDataInitializer;

    @Autowired
    private VersionDataInitializer versionDataInitializer;

    @Test
    public void saveOneUrlDocument_Expect_Saved() {
        final DocumentUrlJPA savedUrlDocument = getDocumentUrlList(1).get(0);
        assertNotNull(savedUrlDocument);
        assertTrue(documentUrlRepository.findAll().contains(savedUrlDocument));
    }

    @Test
    public void deleteOneUrlDocById_When_OneSpecifiedUrlDocumentIsSaved_Expect_Deleted() {
        final DocumentUrlJPA savedUrlDocument = getDocumentUrlList(1).get(0);
        documentUrlRepository.delete(savedUrlDocument.getId());
        assertFalse(documentUrlRepository.findAll().contains(savedUrlDocument));
    }

    @Test
    public void searchDocumentById_WhenDocumentIsSaved_Expect_Found() {
        final DocumentUrlJPA savedUrlDocument = getDocumentUrlList(1).get(0);
        assertNotNull(savedUrlDocument);
        assertEquals(savedUrlDocument, documentUrlRepository.findOne(savedUrlDocument.getId()));
    }

    @Test
    public void searchDocumentById_WhenDocumentIsNotSaved_Expect_NotFound() {
        final List<DocumentUrlJPA> savedDocuments = getDocumentUrlList(3);
        final int searchingId = savedDocuments.stream()
                .mapToInt(DocumentUrlJPA::getId)
                .max()
                .orElse(0) + 1;

        assertNull(documentUrlRepository.findOne(searchingId));
    }

    @Test
    public void findUrlDocumentByDocIdAndVersionNo_WhenSpecifiedUrlDocIsSaved_Expect_Found() {
        final List<DocumentUrlJPA> savedDocuments = getDocumentUrlList(3);
        final DocumentUrlJPA expectedDocument = savedDocuments.get(1);

        final int docId = expectedDocument.getVersion().getDocId();
        final int versionNo = expectedDocument.getVersion().getNo();

        final DocumentUrlJPA foundDocument = documentUrlRepository.findByDocIdAndVersionNo(docId, versionNo);

        assertNotNull(foundDocument);
        assertEquals(expectedDocument, foundDocument);
    }

    @Test
    public void findUrlDocumentsByDocId_When_AllDocumentsHasSpecifiedDocId_Expect_Found() {
        final int documentListSize = 5;
        final List<DocumentUrlJPA> savedDocuments = getDocumentUrlList(documentListSize);
        final List<DocumentUrlJPA> foundDocuments = documentUrlRepository.findByDocId(DEFAULT_DOC_ID);

        assertEquals(documentListSize, foundDocuments.size());
        assertEquals(savedDocuments, foundDocuments);
    }

    @Test
    public void findUrlDocumentsByDocId_When_DocIdNotExist_Expect_NotFound() {
        final int documentListSize = 3;
        getDocumentUrlList(documentListSize);
        final List<DocumentUrlJPA> foundDocuments = documentUrlRepository.findByDocId(DEFAULT_DOC_ID + 1);

        assertEquals(0, foundDocuments.size());
    }

    @Test
    public void deleteUrlDocumentsByDocId_When_DocIdExist_Expect_Deleted() {
        final List<DocumentUrlJPA> savedDocuments = getDocumentUrlList(3);

        documentUrlRepository.delete(documentUrlRepository.findByDocId(DEFAULT_DOC_ID));

        final List<DocumentUrlJPA> foundDocumentList = documentUrlRepository.findAll();
        savedDocuments.forEach(documentUrlJPA -> assertFalse(foundDocumentList.contains(documentUrlJPA)));
    }

    @Test
    public void findByVersion() {
        final DocumentUrlDTO expectedURL = documentDataInitializer.createUrlDocument().getDocumentURL();
        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, expectedURL.getDocId());
        final List<DocumentUrlJPA> byVersion = documentUrlRepository.findByVersion(version);

        assertEquals(1, byVersion.size());

        final DocumentUrlDTO actualURL = new DocumentUrlDTO(byVersion.get(0));

        assertEquals(expectedURL, actualURL);
    }

    private List<DocumentUrlJPA> getDocumentUrlList(int number) {
        return IntStream.range(0, number)
                .mapToObj(i -> {
                    final String someData = "test" + i;
                    DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
                    documentUrlJPA.setUrlFrameName(someData);
                    documentUrlJPA.setUrl(someData);
                    documentUrlJPA.setUrlLanguagePrefix("t" + i);
                    documentUrlJPA.setUrlTarget(someData);
                    documentUrlJPA.setUrlText(someData);

                    final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX + i, DEFAULT_DOC_ID);
                    documentUrlJPA.setVersion(version);

                    return documentUrlRepository.saveAndFlush(documentUrlJPA);
                })
                .collect(Collectors.toList());
    }
}