package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentTemplateDTO;
import com.imcode.imcms.model.CommonContent;
import com.imcode.imcms.persistence.entity.Meta;
import com.imcode.imcms.persistence.entity.TextDocumentTemplateJPA;
import com.imcode.imcms.persistence.repository.MetaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class TextDocumentDataInitializer extends DocumentDataInitializer {

    private final TemplateDataInitializer templateDataInitializer;

    public TextDocumentDataInitializer(MetaRepository metaRepository,
                                       BiFunction<Meta, List<CommonContent>, DocumentDTO> metaToDocumentDTO,
                                       VersionDataInitializer versionDataInitializer,
                                       TemplateDataInitializer templateDataInitializer,
                                       CommonContentDataInitializer commonContentDataInitializer) {

        super(metaRepository, metaToDocumentDTO, versionDataInitializer, commonContentDataInitializer);
        this.templateDataInitializer = templateDataInitializer;
    }

    public TextDocumentDTO createTextDocument(String templateName) {
        final DocumentDTO documentDTO = createData(Meta.DocumentType.TEXT);
        final TextDocumentTemplateJPA textDocumentTemplate = templateDataInitializer.createData(
                documentDTO.getId(), templateName, templateName
        );
        final TextDocumentDTO textDocumentDTO = new TextDocumentDTO(documentDTO);
        textDocumentDTO.setTemplate(new TextDocumentTemplateDTO(textDocumentTemplate));

        return textDocumentDTO;
    }

    public TextDocumentDTO createTextDocument() {
        return createTextDocument("demo");
    }

    public List<TextDocumentDTO> createTextDocuments(Integer count) {
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
