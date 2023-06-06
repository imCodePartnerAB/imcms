package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.TemplateCSSHistoryEntry;
import com.imcode.imcms.domain.dto.TemplateCSSVersion;
import com.imcode.imcms.domain.service.TemplateCSSService;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.List;
import java.util.Set;


@Log4j2
public class DummyTemplateCSSService implements TemplateCSSService {
	private final String SERVICE_DISABLED_MESSAGE = "{}. User defined CSS(template CSS feature) disabled in properties - 'svn.use'";

	@Override
	public void sync(Set<String> templateNames) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot sync template and template css");
	}

	@Override
	public void create(String templateName) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot create template CSS file");
	}

	@Override
	public void create(Set<String> templateNames) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot create template CSS files");
	}

	@Override
	public String get(String templateName, TemplateCSSVersion version) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot retrieve template CSS file data by version");
		return "";
	}

	@Override
	public String getRevision(String templateName, Long revision) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot retrieve template CSS file data by revision");
		return "";
	}

	@Override
	public void update(String templateName, String css) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot update template CSS file");
	}

	@Override
	public void rename(String templateName, String newTemplateName) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot rename template CSS file");
	}

	@Override
	public void publish(String templateName) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot publish template CSS");
	}

	@Override
	public boolean existsLocally(String templateName) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot check if template CSS file exists locally");
		return false;
	}

	@Override
	public boolean existsOnSVN(String templateName) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot check if template CSS file exists on SVN");
		return false;
	}

	@Override
	public boolean existsOnSVN(String templateName, Long revision) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot check if template CSS revision exists on SVN");
		return false;
	}

	@Override
	public List<TemplateCSSHistoryEntry> getHistory(String templateName) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot retrieve template CSS history");
		return Collections.emptyList();
	}

	@Override
	public boolean equalsVersions(String templateName) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot compare template CSS versions");
		return false;
	}

	@Override
	public boolean equalsWorkingVersion(String templateName, String css) {
		log.warn(SERVICE_DISABLED_MESSAGE, "Cannot compare working versions");
		return false;
	}
}
