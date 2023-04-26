package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.DocumentFileJPA;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.DocumentFileRepository;
import com.imcode.imcms.persistence.repository.MetaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class FileDocumentDataInitializer extends DocumentDataInitializer {
    private final DocumentFileRepository documentFileRepository;

    public FileDocumentDataInitializer(MetaRepository metaRepository,
                                       DocumentFileRepository documentFileRepository,
                                       BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                                       VersionDataInitializer versionDataInitializer,
                                       CommonContentDataInitializer commonContentDataInitializer) {

        super(metaRepository, metaToDocumentDTO, versionDataInitializer, commonContentDataInitializer);
        this.documentFileRepository = documentFileRepository;
    }

    public FileDocumentDTO createFileDocument() {
        final DocumentDTO documentDTO = createData(Meta.DocumentType.FILE);
        final FileDocumentDTO fileDocumentDTO = new FileDocumentDTO(documentDTO);

        final DocumentFileJPA documentFileJPA = new DocumentFileJPA();
        documentFileJPA.setDocId(documentDTO.getId());
        documentFileJPA.setVersionIndex(Version.WORKING_VERSION_INDEX);
        documentFileJPA.setFileId("test_id");
        documentFileJPA.setFilename("test_name");
        documentFileJPA.setOriginalFilename("test_name");
        documentFileJPA.setMimeType("test");

        final List<DocumentFileDTO> documentFileDTOS = new ArrayList<>();
        documentFileDTOS.add(new DocumentFileDTO(documentFileRepository.save(documentFileJPA)));
        fileDocumentDTO.setFiles(documentFileDTOS);

        return fileDocumentDTO;
    }

    @Override
    public void cleanRepositories(int createdDocId) {
        super.cleanRepositories(createdDocId);
//        documentFileRepository.deleteAll();
    }
}
