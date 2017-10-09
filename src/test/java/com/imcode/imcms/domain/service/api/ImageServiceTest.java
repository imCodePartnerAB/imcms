package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.config.TestConfig;
import com.imcode.imcms.domain.exception.DocumentNotExistException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
public class ImageServiceTest {

    private static final int TEST_IMAGE_INDEX = 1;

    @Autowired
    private ImageService imageService;


    @Test(expected = DocumentNotExistException.class)
    public void getImage_When_DocumentNotExist_Expect_Exception() {
        int nonExistingDocId = 0;
        imageService.getImage(nonExistingDocId, TEST_IMAGE_INDEX);// should throw exception
    }
}
