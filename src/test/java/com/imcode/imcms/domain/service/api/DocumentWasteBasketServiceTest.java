package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.DocumentDataInitializer;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentWasteBasketDTO;
import com.imcode.imcms.domain.service.DocumentWasteBasketService;
import com.imcode.imcms.model.Roles;
import com.imcode.imcms.persistence.entity.DocumentWasteBasketJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.persistence.repository.DocumentWasteBasketRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class DocumentWasteBasketServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private DocumentWasteBasketService documentWasteBasketService;

    @Autowired
    private DocumentWasteBasketRepository documentWasteBasketRepository;

    @Autowired
    private MetaRepository metaRepository;

    @Autowired
    private DocumentDataInitializer documentDataInitializer;

    @BeforeEach
    public void setUp() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        user.setLogin("admin");
        user.addRoleId(Roles.SUPER_ADMIN.getId());
        user.setLanguageIso639_2("eng");
        Imcms.setUser(user); // means current user is admin now
    }

    @AfterEach
    public void clearTestData() {
        documentDataInitializer.cleanRepositories();
        documentWasteBasketRepository.deleteAll();
    }

    @Test
    public void getAllIdsFromWasteBasket_When_TableIsEmpty_Expected_EmptyList() {
        assertTrue(documentWasteBasketRepository.findAll().isEmpty());

        assertTrue(documentWasteBasketService.getAllIdsFromWasteBasket().isEmpty());
    }

    @Test
    public void getAllIdsFromWasteBasket_Expected_ListWithIds(){
        assertTrue(documentWasteBasketRepository.findAll().isEmpty());

        final List<Integer> docIds = List.of(documentDataInitializer.createData().getId(), documentDataInitializer.createData().getId());
        for(Integer docId: docIds){
            DocumentWasteBasketJPA documentWasteBasketJPA = new DocumentWasteBasketJPA();
            documentWasteBasketJPA.setMeta(metaRepository.getOne(docId));
            documentWasteBasketJPA.setAddedBy(new User(Imcms.getUser()));
            documentWasteBasketRepository.save(documentWasteBasketJPA);
            assertTrue(documentWasteBasketRepository.findById(docId).isPresent());
        }

        assertFalse(documentWasteBasketRepository.findAll().isEmpty());
        assertEquals(documentWasteBasketService.getAllIdsFromWasteBasket(), documentWasteBasketRepository.findAllMetaIds());
    }

    @Test
    public void getAllFromWasteBasket_When_TableIsEmpty_Expected_EmptyList(){
        assertTrue(documentWasteBasketRepository.findAll().isEmpty());

        assertTrue(documentWasteBasketService.getAllIdsFromWasteBasket().isEmpty());
    }

    @Test
    public void getAllFromWasteBasket_Expected_ListWithdocumentWasteBaskets(){
        assertTrue(documentWasteBasketRepository.findAll().isEmpty());

        final List<Integer> docIds = List.of(documentDataInitializer.createData().getId(), documentDataInitializer.createData().getId());

        for(Integer docId: docIds){
            DocumentWasteBasketJPA documentWasteBasketJPA = new DocumentWasteBasketJPA();
            documentWasteBasketJPA.setMeta(metaRepository.getOne(docId));
            documentWasteBasketJPA.setAddedBy(new User(Imcms.getUser()));
            documentWasteBasketRepository.save(documentWasteBasketJPA);
            assertTrue(documentWasteBasketRepository.findById(docId).isPresent());
        }

        assertFalse(documentWasteBasketRepository.findAll().isEmpty());
        assertEquals(documentWasteBasketService.getAllFromWasteBasket(), documentWasteBasketRepository.findAll().stream().map(DocumentWasteBasketDTO::new).collect(Collectors.toList()));
    }

    @Test
    public void isDocumentInWasteBasket_When_ThereAreNoDocumentsInTheWasteBasket_Expected_False(){
        assertTrue(documentWasteBasketRepository.findAll().isEmpty());
        assertFalse(documentWasteBasketService.isDocumentInWasteBasket(1111));
    }

    @Test
    public void isDocumentInWasteBasket_When_ThereAreDocumentsInTheWasteBasket_Expected_True(){
        final DocumentDTO document = documentDataInitializer.createData();
        documentWasteBasketService.putToWasteBasket(document.getId());
        documentWasteBasketRepository.flush();

        assertTrue(documentWasteBasketService.isDocumentInWasteBasket(document.getId()));
    }

    @Test
    public void putToWasteBasket_Expected_CreatedEntity(){
        final Date dateBeforePut = new Date();

        final DocumentDTO document = documentDataInitializer.createData();
        documentWasteBasketService.putToWasteBasket(document.getId());
        documentWasteBasketRepository.flush();

        final Optional<DocumentWasteBasketJPA> documentWasteBasketJPA = documentWasteBasketRepository.findById(document.getId());
        assertTrue(documentWasteBasketJPA.isPresent());
        assertEquals(document.getId(), documentWasteBasketJPA.get().getMetaId());
        assertEquals(Imcms.getUser().getId(), documentWasteBasketJPA.get().getAddedBy().getId());

        final Date addedDate = documentWasteBasketJPA.get().getAddedDatetime();
        assertTrue(addedDate.after(dateBeforePut) || addedDate.equals(dateBeforePut));
    }

    @Test
    public void putToWasteBasket_When_DocumentAlreadyInWasteBasket_Expected_DoesNotThrowException(){
        final DocumentDTO document = documentDataInitializer.createData();
        documentWasteBasketService.putToWasteBasket(document.getId());
        documentWasteBasketRepository.flush();

        assertEquals(1, documentWasteBasketRepository.findAllMetaIds().size());

        assertDoesNotThrow(() -> {
            documentWasteBasketService.putToWasteBasket(document.getId());
            documentWasteBasketRepository.flush();
        });

        assertEquals(1, documentWasteBasketRepository.findAllMetaIds().size());
    }

    @Test
    public void putToWasteBasket_When_PassMultipleIds_Expected_CreatedEntities(){
        assertTrue(documentWasteBasketRepository.findAll().isEmpty());

        final Date dateBeforePut = new Date();

        final List<Integer> docIds = List.of(documentDataInitializer.createData().getId(), documentDataInitializer.createData().getId());

        documentWasteBasketService.putToWasteBasket(docIds);
        documentWasteBasketRepository.flush();

        assertEquals(docIds.size(), documentWasteBasketRepository.findAllMetaIds().size());

        for(Integer docId: docIds){
            final Optional<DocumentWasteBasketJPA> documentWasteBasketJPA = documentWasteBasketRepository.findById(docId);
            assertTrue(documentWasteBasketJPA.isPresent());
            assertEquals(docId, documentWasteBasketJPA.get().getMetaId());
            assertEquals(Imcms.getUser().getId(), documentWasteBasketJPA.get().getAddedBy().getId());

            final Date addedDate = documentWasteBasketJPA.get().getAddedDatetime();
            assertTrue(addedDate.after(dateBeforePut) || addedDate.equals(dateBeforePut));
        }
    }

    @Test
    public void putToWasteBasket_When_PassMultipleIds_And_SomeDocumentsAlreadyInWasteBasket_Expected_CreatedEntities_And_DoesNotThrowException(){
        final List<Integer> docIds = List.of(documentDataInitializer.createData().getId(), documentDataInitializer.createData().getId());

        documentWasteBasketService.putToWasteBasket(docIds);
        documentWasteBasketRepository.flush();

        assertEquals(docIds.size(), documentWasteBasketRepository.findAllMetaIds().size());

        final List<Integer> docIds2 = new ArrayList<>(List.of(documentDataInitializer.createData().getId(), documentDataInitializer.createData().getId()));
        docIds2.addAll(docIds);

        assertDoesNotThrow(() -> {
            documentWasteBasketService.putToWasteBasket(docIds2);
            documentWasteBasketRepository.flush();
        });

        assertTrue(documentWasteBasketRepository.findAllMetaIds().containsAll(docIds2));
    }

    @Test
    public void pullFromWasteBasket_Expected_DeletedEntity(){
        final DocumentDTO data = documentDataInitializer.createData();
        DocumentWasteBasketJPA documentWasteBasketJPA = new DocumentWasteBasketJPA();
        documentWasteBasketJPA.setMeta(metaRepository.getOne(data.getId()));
        documentWasteBasketJPA.setAddedBy(new User(Imcms.getUser()));
        documentWasteBasketRepository.save(documentWasteBasketJPA);

        assertTrue(documentWasteBasketRepository.findById(data.getId()).isPresent());

        documentWasteBasketService.pullFromWasteBasket(data.getId());

        assertFalse(documentWasteBasketRepository.findById(data.getId()).isPresent());
    }

    @Test
    public void pullFromWasteBasket_When_PassMultipleIds_Expected_DeletedEntities(){
        final List<Integer> docIds = List.of(documentDataInitializer.createData().getId(), documentDataInitializer.createData().getId());

        for(Integer docId: docIds){
            DocumentWasteBasketJPA documentWasteBasketJPA = new DocumentWasteBasketJPA();
            documentWasteBasketJPA.setMeta(metaRepository.getOne(docId));
            documentWasteBasketJPA.setAddedBy(new User(Imcms.getUser()));
            documentWasteBasketRepository.save(documentWasteBasketJPA);
            assertTrue(documentWasteBasketRepository.findById(docId).isPresent());
        }

        documentWasteBasketService.pullFromWasteBasket(docIds);

        docIds.forEach(docId -> assertFalse(documentWasteBasketRepository.findById(docId).isPresent()));
    }

}
