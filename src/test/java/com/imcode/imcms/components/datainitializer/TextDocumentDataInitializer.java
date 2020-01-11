package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.MetaRepository;
import com.imcode.imcms.util.function.TernaryFunction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextDocumentDataInitializer extends DocumentDataInitializer {

    private final TemplateDataInitializer templateDataInitializer;

    public TextDocumentDataInitializer(MetaRepository metaRepository,
                                       TernaryFunction<Meta, Version, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                                       VersionDataInitializer versionDataInitializer,
                                       TemplateDataInitializer templateDataInitializer,
                                       CommonContentDataInitializer commonContentDataInitializer) {

        super(metaRepository, metaToDocumentDTO, versionDataInitializer, commonContentDataInitializer);
        this.templateDataInitializer = templateDataInitializer;
    }

    public TextDocumentDTO createTextDocument() {
        templateDataInitializer.cleanRepositories();
        final DocumentDTO documentDTO = createData(Meta.DocumentType.TEXT);
        final TextDocumentTemplateJPA template = templateDataInitializer.createData(
                documentDTO.getId(), "demo", "demo"
        );
        final TextDocumentDTO textDocumentDTO = new TextDocumentDTO(documentDTO);
        textDocumentDTO.setTemplate(new TextDocumentTemplateDTO(template));

        return textDocumentDTO;
    }

    public List<TextDocumentDTO> createTextDocuments(Integer count) {
        templateDataInitializer.cleanRepositories();
        List<TextDocumentDTO> textDocuments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            textDocuments.add(createTextDocument());
        }

        return textDocuments;
    }

    @Override
    public void cleanRepositories(int createdDocId) {
        templateDataInitializer.cleanRepositories();
        super.cleanRepositories(createdDocId);
    }
}
