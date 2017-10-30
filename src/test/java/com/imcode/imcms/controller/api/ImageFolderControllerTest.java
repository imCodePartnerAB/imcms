package com.imcode.imcms.controller.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import com.imcode.imcms.controller.AbstractControllerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class ImageFolderControllerTest extends AbstractControllerTest {

    @Override
    protected String controllerPath() {
        return "/images/folders";
    }

    @Test
    public void getImageFolder_Expect_Ok() throws Exception {
        performRequestBuilderExpectedOk(MockMvcRequestBuilders.get(controllerPath()));
    }
}
