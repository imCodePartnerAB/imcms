package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.config.WebTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, WebTestConfig.class})
public class TextServiceTest {

    @Autowired
    private TextService textService;

    @Test
    public void testTextServiceInjected() {
        assertNotNull(textService);
    }

}
