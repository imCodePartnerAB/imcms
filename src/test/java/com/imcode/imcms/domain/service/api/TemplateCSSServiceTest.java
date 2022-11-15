package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.TemplateCSSHistoryEntry;
import com.imcode.imcms.domain.dto.TemplateCSSVersion;
import com.imcode.imcms.domain.service.TemplateCSSService;
import com.imcode.imcms.model.Roles;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.imcode.imcms.domain.service.TemplateCSSService.CSS_EXTENSION;
import static com.imcode.imcms.domain.service.TemplateCSSService.CSS_WORKING_VERSION_SUFFIX;
import static org.junit.jupiter.api.Assertions.*;

public class TemplateCSSServiceTest extends WebAppSpringTestConfig {
	@Value("${TemplateCSSPath}")
	private Path templateCSSPath;

	@Autowired
	private TemplateCSSService templateCSSService;

	@BeforeEach
	public void beforeEach() {
		final UserDomainObject user = new UserDomainObject();
		user.addRoleId(Roles.SUPER_ADMIN.getId());
		Imcms.setUser(user);
	}

	@Test
	public void sync_Expect_CreatedTemplateCSSFiles() {
		final Set<String> templateNames = getRandomTemplateNames(3);

		templateCSSService.sync(templateNames);

		for (String templateName : templateNames) {
			assertTrue(Files.exists(templateCSSPath.resolve(templateName + CSS_EXTENSION)));
			assertTrue(Files.exists(templateCSSPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION)));
		}
	}

	@Test
	public void createTemplateCSS_Expect_CreatedFiles() {
		final Set<String> templateNames = getRandomTemplateNames(1);

		for (String templateName : templateNames) {
			templateCSSService.create(templateName);
			assertTrue(Files.exists(templateCSSPath.resolve(templateName + CSS_EXTENSION)));
			assertTrue(Files.exists(templateCSSPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION)));
		}
	}

	@Test
	public void createTemplateCSSFiles_Expect_CreatedFiles() {
		final Set<String> templateNames = getRandomTemplateNames(4);

		templateCSSService.create(templateNames);
		for (String templateName : templateNames) {
			assertTrue(Files.exists(templateCSSPath.resolve(templateName + CSS_EXTENSION)));
			assertTrue(Files.exists(templateCSSPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION)));
		}
	}

	@Test
	public void getNewlyCreatedTemplateCSSVersions_Expect_CorrectResult() {
		final Set<String> templateNames = getRandomTemplateNames(1);

		for (String templateName : templateNames) {
			templateCSSService.create(templateName);

			final String active = templateCSSService.get(templateName, TemplateCSSVersion.ACTIVE);
			final String working = templateCSSService.get(templateName, TemplateCSSVersion.WORKING);

			assertEquals("", active);
			assertEquals("", working);
			assertEquals(active, working);
		}
	}

	@Test
	public void updateTemplateCSS_Expect_VersionsNotEquals() {
		final Set<String> templateNames = getRandomTemplateNames(1);
		final String css = "css";

		for (String templateName : templateNames) {
			templateCSSService.create(templateName);

			templateCSSService.update(templateName, css);

			final String active = templateCSSService.get(templateName, TemplateCSSVersion.ACTIVE);
			final String working = templateCSSService.get(templateName, TemplateCSSVersion.WORKING);

			assertEquals("", active);
			assertEquals(css, working);
			assertNotEquals(active, working);
		}
	}

	@Test
	public void updateAndPublishTemplateCSS_Expect_VersionsEquals() {
		final Set<String> templateNames = getRandomTemplateNames(1);
		final String css = "css";

		for (String templateName : templateNames) {
			templateCSSService.create(templateName);

			templateCSSService.update(templateName, css);
			templateCSSService.publish(templateName);

			final String active = templateCSSService.get(templateName, TemplateCSSVersion.ACTIVE);
			final String working = templateCSSService.get(templateName, TemplateCSSVersion.WORKING);

			assertEquals(css, active);
			assertEquals(css, working);
			assertEquals(active, working);
		}
	}

	@Test
	public void getTemplateCSSHistory_Expect_CorrectSize() {
		final Set<String> templateNames = getRandomTemplateNames(1);

		for (String templateName : templateNames) {
			templateCSSService.create(templateName);

			templateCSSService.update(templateName, "1");
			templateCSSService.publish(templateName);
			templateCSSService.update(templateName, "2");
			templateCSSService.publish(templateName);

			assertEquals(3, templateCSSService.getHistory(templateName).size());
		}
	}

	@Test
	public void getTemplateCSSRevision_Expect_CorrectResult() {
		final Set<String> templateNames = getRandomTemplateNames(1);
		final String data0 = "0";
		final String data1 = "1";

		for (String templateName : templateNames) {
			templateCSSService.create(templateName);

			templateCSSService.update(templateName, data0);
			templateCSSService.publish(templateName);
			templateCSSService.update(templateName, data1);
			templateCSSService.publish(templateName);

			final List<TemplateCSSHistoryEntry> historyEntries = templateCSSService.getHistory(templateName);
			assertEquals(3, historyEntries.size());

			assertEquals(data1, templateCSSService.getRevision(templateName, historyEntries.get(0).getRevision()));
			assertEquals(data0, templateCSSService.getRevision(templateName, historyEntries.get(1).getRevision()));

		}
	}

	private Set<String> getRandomTemplateNames(int count) {
		final Set<String> templateNames = new HashSet<>();

		for (int i = 0; i < count; i++) {
			templateNames.add(RandomStringUtils.random(5));
		}

		return templateNames;
	}
}
