package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.SVNService;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.TemplateCSSHistoryEntry;
import com.imcode.imcms.domain.dto.TemplateCSSVersion;
import com.imcode.imcms.domain.service.TemplateCSSService;
import imcode.server.Imcms;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.PathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNProperties;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.imcode.imcms.domain.component.SVNService.IMCODE_USERNAME_PROPERTY;
import static com.imcode.imcms.domain.component.SVNService.SVN_WEBAPP_PATH;

@Service("templateCSSService")
public class DefaultTemplateCSSService implements TemplateCSSService {
	private final SVNService svnService;
	private final Path templateCSSAbsoluteDirPath;
	private final String templateCSSDirectory;

	public DefaultTemplateCSSService(SVNService svnService,
	                                 @Value("${TemplateCSSPath}") Path templateCSSAbsoluteDirPath,
	                                 @Value("${TemplateCSSPath}") String templateCSSDirectory) {
		this.svnService = svnService;
		this.templateCSSAbsoluteDirPath = templateCSSAbsoluteDirPath;
		this.templateCSSDirectory = templateCSSDirectory;
	}

	@SneakyThrows
	@PostConstruct
	private void init() {
		if (!Files.exists(templateCSSAbsoluteDirPath)) {
			Files.createDirectories(templateCSSAbsoluteDirPath);
		}
		if (!svnService.exists(SVN_WEBAPP_PATH + templateCSSDirectory)) {
			svnService.createFolder(SVN_WEBAPP_PATH + templateCSSDirectory);
		}
	}

	@Override
	public void sync(final Set<String> templateNames) {
		final Path target = Path.of(SVN_WEBAPP_PATH, templateCSSDirectory);

		svnService.checkout(target, templateCSSAbsoluteDirPath.toAbsolutePath());
		this.create(templateNames);
	}

	@Override
	public void create(String templateName) {
		createTemplateCSS(Set.of(templateName));
	}

	@Override
	public void create(Set<String> templateNames) {
		createTemplateCSS(templateNames);
	}

	@Override
	public String get(String templateName, TemplateCSSVersion version) {
		try {
			String templateCSSFilename;
			if (version == TemplateCSSVersion.WORKING) {
				templateCSSFilename = templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION;
			} else {
				templateCSSFilename = templateName + CSS_EXTENSION;
			}

			return Files.readString(templateCSSAbsoluteDirPath.resolve(templateCSSFilename));
		} catch (IOException e) {
			throw new RuntimeException("Error reading file", e);
		}
	}

	@Override
	public String getRevision(String templateName, Long revision) {
		return svnService.get(toSVNRelativePath(templateName), revision).toString();
	}

	@Override
	public void update(String templateName, String css) {
		try {
			final Path workingTemplateCSSPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION);

			Files.writeString(workingTemplateCSSPath, css);
		} catch (IOException e) {
			throw new RuntimeException("Error updating WORKING version of template CSS file", e);
		}
	}

	@Override
	public void publish(String templateName) {
		try {
			final Path cssPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_EXTENSION);
			final Path workingCSSPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION);

			final String message = String.format("User: %s. Updating template css file.", getCurrentUserName());
			final Map<String, String> properties = Map.of(IMCODE_USERNAME_PROPERTY, getCurrentUserName());

			Files.write(cssPath, Files.readAllBytes(workingCSSPath));
			svnService.commit(cssPath, message, properties);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean existsLocally(String templateName) {
		return Files.exists(templateCSSAbsoluteDirPath.resolve(templateName + CSS_EXTENSION));
	}

	@Override
	public boolean existsOnSVN(String templateName) {
		return svnService.exists(toSVNRelativePath(templateName));
	}

	@Override
	public boolean existsOnSVN(String templateName, Long revision) {
		return svnService.exists(toSVNRelativePath(templateName), revision);
	}

	@Override
	public boolean equalsVersions(String templateName) {
		try {
			final Path templateCSSPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_EXTENSION);
			final Path workingTemplateCSSPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION);

			return PathUtils.fileContentEquals(templateCSSPath, workingTemplateCSSPath);
		} catch (IOException e) {
			throw new RuntimeException("Cannot perform content comparison between ACTIVE and WORKING versions", e);
		}
	}

	@Override
	public boolean equalsWorkingVersion(String templateName, String css) {
		final Path templateCSSPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION);

		try (final Reader cssReader = new BufferedReader(new StringReader(css));
		     final Reader fileReader = new BufferedReader(new FileReader(templateCSSPath.toFile()))) {

			return IOUtils.contentEquals(fileReader, cssReader);
		} catch (IOException e) {
			throw new RuntimeException("Error performing content comparison between WORKING version and given css code", e);
		}
	}

	@Override
	public List<TemplateCSSHistoryEntry> getHistory(String templateName) {
		final String templateCSSSVNPath = toSVNRelativePath(templateName);

		return svnService.getRevisions(templateCSSSVNPath)
				.stream()
				.map(svnLogEntry -> {
					final SVNProperties svnProperties = svnLogEntry.getRevisionProperties();

					final TemplateCSSHistoryEntry historyEntry = new TemplateCSSHistoryEntry();
					final AuditDTO modified = new AuditDTO();
					modified.setDateTime(svnLogEntry.getDate());
					modified.setBy(svnProperties.getStringValue(IMCODE_USERNAME_PROPERTY));

					historyEntry.setRevision(svnLogEntry.getRevision());
					historyEntry.setModified(modified);

					return historyEntry;
				})
				.sorted((o1, o2) -> o2.getModified().getFormattedDate().compareTo(o1.getModified().getFormattedDate()))
				.collect(Collectors.toList());
	}

	private void createTemplateCSS(Set<String> templateNames) {
		try {
			for (String templateName : templateNames) {
				final Path templateCSSPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_EXTENSION);
				final Path workingTemplateCSSPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION);

				final String message = String.format("User: %s. Creating template css file.", getCurrentUserName());
				final Map<String, String> properties = Map.of(IMCODE_USERNAME_PROPERTY, getCurrentUserName());

				if (!Files.exists(templateCSSPath)) {
					Files.createFile(templateCSSPath);
					Files.copy(templateCSSPath, workingTemplateCSSPath);

					svnService.add(templateCSSPath);
					svnService.commit(templateCSSPath, message, properties);
				} else {
					if (!Files.exists(workingTemplateCSSPath)) {
						Files.copy(templateCSSPath, workingTemplateCSSPath);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String getCurrentUserName() {
		return Imcms.getUser() == null ? "imcode" : Imcms.getUser().getFullName();
	}

	private String toSVNRelativePath(String templateName) {
		return SVN_WEBAPP_PATH + templateCSSDirectory + templateName + CSS_EXTENSION;
	}
}
