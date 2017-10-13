package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testFindAll() throws Exception {
        assertNotNull(userService.findAll(true, true));
        assertNotNull(userService.findAll(true, false));
        assertNotNull(userService.findAll(false, false));
        assertNotNull(userService.findAll(false, true));
    }

    @Test
    public void findByNamePrefix() throws Exception {
        assertNotNull(userService.findByNamePrefix("prefix", true));
        assertNotNull(userService.findByNamePrefix("prefix", false));
    }

}
