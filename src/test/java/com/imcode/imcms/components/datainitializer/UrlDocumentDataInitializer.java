package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentUrlDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.DocumentUrlJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentUrlRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
public class UrlDocumentDataInitializer extends DocumentDataInitializer {

    private final VersionDataInitializer versionDataInitializer;
    private final DocumentUrlRepository documentUrlRepository;

    public UrlDocumentDataInitializer(MetaRepository metaRepository,
                                      BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                                      VersionDataInitializer versionDataInitializer,
                                      CommonContentDataInitializer commonContentDataInitializer,
                                      DocumentUrlRepository documentUrlRepository) {

        super(metaRepository, metaToDocumentDTO, versionDataInitializer, commonContentDataInitializer);
        this.versionDataInitializer = versionDataInitializer;
        this.documentUrlRepository = documentUrlRepository;
    }

    public UrlDocumentDTO createUrlDocument() {
        final DocumentDTO documentDTO = createData(Meta.DocumentType.URL);
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(documentDTO);

        DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl("test");
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");

        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, documentDTO.getId());
        documentUrlJPA.setVersion(version);

        final DocumentUrlDTO documentUrlDTO =
                new DocumentUrlDTO(documentUrlRepository.saveAndFlush(documentUrlJPA));

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        return urlDocumentDTO;
    }

    public UrlDocumentDTO createUrlDocument(String url) {
        final DocumentDTO documentDTO = createData(Meta.DocumentType.URL);
        final UrlDocumentDTO urlDocumentDTO = new UrlDocumentDTO(documentDTO);

        DocumentUrlJPA documentUrlJPA = new DocumentUrlJPA();
        documentUrlJPA.setUrlFrameName("test");
        documentUrlJPA.setUrl(url);
        documentUrlJPA.setUrlLanguagePrefix("t");
        documentUrlJPA.setUrlTarget("test");
        documentUrlJPA.setUrlText("test");

        final Version version = versionDataInitializer.createData(TEST_VERSION_INDEX, documentDTO.getId());
        documentUrlJPA.setVersion(version);

        final DocumentUrlDTO documentUrlDTO =
                new DocumentUrlDTO(documentUrlRepository.saveAndFlush(documentUrlJPA));

        urlDocumentDTO.setDocumentURL(documentUrlDTO);

        return urlDocumentDTO;
    }

    @Override
    public void cleanRepositories(int createdDocId) {
        super.cleanRepositories(createdDocId);
//        documentUrlRepository.deleteAll();
    }
}
