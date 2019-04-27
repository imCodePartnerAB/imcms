package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.domain.dto.TemplateDTO;
import com.imcode.imcms.domain.service.TemplateService;
import com.imcode.imcms.model.Template;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class TemplateServiceTest extends WebAppSpringTestConfig {

    @Value("WEB-INF/templates/text")
    private File templateDirectory;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateDataInitializer dataInitializer;

    private List<Template> templatesExpected;

    @BeforeEach
    public void setUp() {
        dataInitializer.cleanRepositories();
        templatesExpected = dataInitializer.createData(5);
    }

    @Test
    public void getAllTest() {
        assertEquals(templatesExpected, templateService.getAll());
    }

    @Test
    public void getByName() throws IOException {
        final String templateName = "testttt123";
        final File templateFile = new File(templateDirectory, templateName + ".jsp");
        templateFile.createNewFile();

        try {
            final Template templateDTO = dataInitializer.createData(templateName);
            final Optional<Template> templateOptional = templateService.getTemplateOptional(templateName);
            assertTrue(templateOptional.isPresent());
            final Template templateResult = templateOptional.get();
            assertEquals(templateDTO, templateResult);

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void save() throws Exception {
        final String templateName = "testttt123";
        final File templateFile = new File(templateDirectory, templateName + ".jsp");

        try {
            assertTrue(templateFile.createNewFile());

            Template templateDTO = new TemplateDTO(templateName, false, null);
            templateService.save(templateDTO);
            final Optional<Template> oTemplate = templateService.getTemplateOptional(templateName);
            assertTrue(oTemplate.isPresent());

            templateDTO = oTemplate.get();
            final Optional<Template> templateOptional = templateService.getTemplateOptional(templateName);
            assertTrue(templateOptional.isPresent());
            final Template templateResult = templateOptional.get();
            assertEquals(templateDTO.getName(), templateResult.getName());
            assertEquals(templateDTO.isHidden(), templateResult.isHidden());

        } finally {
            assertTrue(templateFile.delete());
        }
    }

    @Test
    public void save_When_NoTemplate_Expect_Null() {
        final String dummyName = "test_" + System.currentTimeMillis();
        final Template templateDTO = new TemplateDTO(dummyName, false, null);

        templateService.save(templateDTO);

        assertFalse(templateService.getTemplateOptional(dummyName).isPresent());
    }

}
