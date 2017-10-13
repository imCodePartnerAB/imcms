package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.components.datainitializer.TemplateDataInitializer;
import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.dto.TemplateDTO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TemplateServiceTest {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateDataInitializer dataInitializer;

    private List<TemplateDTO> templatesExpected;

    @Before
    public void setUp() throws Exception {
        dataInitializer.cleanRepositories();
        templatesExpected = dataInitializer.createData(5);
    }

    @After
    public void tearDown() throws Exception {
        dataInitializer.cleanRepositories();
    }

    @Test
    public void getAllTest() {
        Assert.assertEquals(templatesExpected, templateService.getAll());
    }

}
