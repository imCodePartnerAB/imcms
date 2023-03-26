package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentWasteBasketDTO;
import com.imcode.imcms.domain.service.DocumentWasteBasketService;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.DocumentWasteBasket;
import com.imcode.imcms.persistence.entity.DocumentWasteBasketJPA;
import com.imcode.imcms.persistence.entity.User;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.persistence.repository.DocumentWasteBasketRepository;
import imcode.server.Imcms;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultDocumentWasteBasketService implements DocumentWasteBasketService {

    private final DocumentMapper documentMapper;
    private final MetaRepository metaRepository;
    private final DocumentWasteBasketRepository documentWasteBasketRepository;

    public DefaultDocumentWasteBasketService(DocumentMapper documentMapper,
                                             MetaRepository metaRepository,
                                             DocumentWasteBasketRepository documentWasteBasketRepository) {
        this.documentMapper = documentMapper;
        this.metaRepository = metaRepository;
        this.documentWasteBasketRepository = documentWasteBasketRepository;
    }

    @Override
    public List<Integer> getAllIdsFromWasteBasket() {
        return documentWasteBasketRepository.findAllMetaIds();
    }

    @Override
    public List<DocumentWasteBasket> getAllFromWasteBasket() {
        return documentWasteBasketRepository.findAll().stream()
                .map(DocumentWasteBasketDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDocumentInWasteBasket(int docId) {
        return documentWasteBasketRepository.existsById(docId);
    }

    @Override
    public void putToWasteBasket(int docId){
        if(isDocumentInWasteBasket(docId)) return;

        final DocumentWasteBasketJPA documentWasteBasketJPA = new DocumentWasteBasketJPA();
        documentWasteBasketJPA.setMeta(metaRepository.getOne(docId));
        documentWasteBasketJPA.setAddedBy(new User(Imcms.getUser()));

        documentWasteBasketRepository.saveAndFlush(documentWasteBasketJPA);

        documentMapper.invalidateDocument(docId);
    }

    @Override
    public void putToWasteBasket(List<Integer> docIds){
        docIds = new ArrayList<>(docIds);
        docIds.removeAll(getAllIdsFromWasteBasket());

        final User user = new User(Imcms.getUser());

        final List<DocumentWasteBasketJPA> documentWasteBasketJPAS = new ArrayList<>();
        for(Integer docId: docIds){
            DocumentWasteBasketJPA documentWasteBasketJPA = new DocumentWasteBasketJPA();
            documentWasteBasketJPA.setMeta(metaRepository.getOne(docId));
            documentWasteBasketJPA.setAddedBy(user);

            documentWasteBasketJPAS.add(documentWasteBasketJPA);
        }
        documentWasteBasketRepository.saveAll(documentWasteBasketJPAS);
        documentWasteBasketRepository.flush();

        docIds.forEach(documentMapper::invalidateDocument);
    }

    @Override
    public void pullFromWasteBasket(int docId){
        documentWasteBasketRepository.deleteByMetaId(docId);
        documentWasteBasketRepository.flush();

        documentMapper.invalidateDocument(docId);
    }

    @Override
    public void pullFromWasteBasket(List<Integer> docIds){
        documentWasteBasketRepository.deleteByMetaIdIn(docIds);
        documentWasteBasketRepository.flush();

        docIds.forEach(documentMapper::invalidateDocument);
    }

}
