package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.persistence.entity.TemplateJPA;
import com.imcode.imcms.persistence.entity.TextDocumentTemplate;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import com.imcode.imcms.persistence.repository.TextDocumentTemplateRepository;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TemplateDataInitializer extends TestDataCleaner {

    private final TextDocumentTemplateRepository textDocumentTemplateRepository;
    private final TemplateRepository templateRepository;
    private final Function<TemplateJPA, TemplateDTO> templateToTemplateDTO;

    public TemplateDataInitializer(TextDocumentTemplateRepository textDocumentTemplateRepository,
                                   TemplateRepository templateRepository,
                                   Function<TemplateJPA, TemplateDTO> templateToTemplateDTO) {
        super(templateRepository, textDocumentTemplateRepository);
        this.textDocumentTemplateRepository = textDocumentTemplateRepository;
        this.templateRepository = templateRepository;
        this.templateToTemplateDTO = templateToTemplateDTO;
    }

    public List<TemplateDTO> createData(Integer howMuch) {
        return IntStream.range(0, howMuch)
                .mapToObj(i -> Value.with(new TemplateJPA(), template -> {
                    template.setName("template" + i);
                    template.setHidden(Math.random() < 0.5);
                }))
                .map(templateRepository::saveAndFlush)
                .map(templateToTemplateDTO)
                .collect(Collectors.toList());
    }

    public TemplateDTO createData(final String name) {
        return Value.apply(new TemplateJPA(), template -> {
            template.setName(name);
            template.setHidden(Math.random() < 0.5);

            templateRepository.saveAndFlush(template);
            return templateToTemplateDTO.apply(template);
        });
    }

    public void createData(int docId, String templateName, String childrenTemplate) {
        final TextDocumentTemplate textDocumentTemplate = new TextDocumentTemplate();
        textDocumentTemplate.setTemplateName(templateName);
        textDocumentTemplate.setChildrenTemplateName(childrenTemplate);
        textDocumentTemplate.setDocId(docId);
        textDocumentTemplate.setTemplateGroupId(0); // dummy group

        textDocumentTemplateRepository.save(textDocumentTemplate);
    }
}
