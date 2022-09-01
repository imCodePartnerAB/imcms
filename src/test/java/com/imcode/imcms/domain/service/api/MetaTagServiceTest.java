package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.exception.AlreadyExistsException;
import com.imcode.imcms.domain.service.MetaTagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;

@Transactional
public class MetaTagServiceTest extends WebAppSpringTestConfig {
	private static final String name = "new-tag";

	@Autowired
	private MetaTagService metaTagService;

	@Test
	public void saveMetaTag_When_NameIsAvailable_Expect_Saved() {
		Assertions.assertDoesNotThrow(() -> metaTagService.saveMetaTag(name));
	}

	@Test
	public void saveMetaTag_When_NameIsNotAvailable_Expect_ExceptionThrown() {
		Assertions.assertDoesNotThrow(() -> metaTagService.saveMetaTag(name));
		Assertions.assertThrows(AlreadyExistsException.class, () -> metaTagService.saveMetaTag(name));
	}

}
