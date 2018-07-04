package com.imcode.imcms;

import com.imcode.imcms.config.TestConfig;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Merge of all frequently used annotations
 */
@Transactional
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public abstract class TransactionalWebAppSpringTestConfig {

    @Test
    @Ignore
    public void ignored() {
        // to prevent "no runnable tests" error
    }
}
