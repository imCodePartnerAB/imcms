package com.imcode.imcms.components.datainitializer;

import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.persistence.entity.Template;
import com.imcode.imcms.persistence.repository.TemplateRepository;
import com.imcode.imcms.util.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TemplateDataInitializer extends AbstractTestDataInitializer<Integer, List<TemplateDTO>> {

    private final TemplateRepository templateRepository;
    private final Function<Template, TemplateDTO> templateToTemplateDTO;

    public TemplateDataInitializer(TemplateRepository templateRepository, Function<Template, TemplateDTO> templateToTemplateDTO) {
        super(templateRepository);
        this.templateRepository = templateRepository;
        this.templateToTemplateDTO = templateToTemplateDTO;
    }

    @Override
    public List<TemplateDTO> createData(Integer howMuch) {
        return IntStream.range(0, howMuch)
                .mapToObj(i -> Value.with(new Template(), template -> {
                    template.setName("template" + i);
                    template.setHidden(Math.random() < 0.5);
                }))
                .map(templateRepository::saveAndFlush)
                .map(templateToTemplateDTO)
                .collect(Collectors.toList());
    }

}
