package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.component.SVNService;
import com.imcode.imcms.domain.dto.AuditDTO;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.TemplateCSSHistoryEntry;
import com.imcode.imcms.domain.dto.TemplateCSSVersion;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TemplateCSSService;
import com.imcode.imcms.model.CommonContent;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.imcode.imcms.domain.component.SVNService.IMCODE_USERNAME_PROPERTY;
import static com.imcode.imcms.domain.component.SVNService.SVN_WEBAPP_PATH;

public class DefaultTemplateCSSService implements TemplateCSSService {
	private final SVNService svnService;
	private final DocumentsCache documentsCache;
	private final DocumentService<DocumentDTO> documentService;
	private final Path templateCSSAbsoluteDirPath;
	private final String templateCSSDirectory;

	public DefaultTemplateCSSService(SVNService svnService,
									 DocumentsCache documentsCache,
									 DocumentService<DocumentDTO> documentService,
									 ServletContext context,
									 String templateCSSDirectory) {
		this.svnService = svnService;
		this.documentsCache = documentsCache;
		this.documentService = documentService;
		this.templateCSSAbsoluteDirPath = Path.of(context.getRealPath("/"), templateCSSDirectory);
		this.templateCSSDirectory = templateCSSDirectory;
	}

	@PostConstruct
	private void init() {
		try {
			if (!Files.exists(templateCSSAbsoluteDirPath)) {
				Files.createDirectories(templateCSSAbsoluteDirPath);
			}
			if (!checkIfExistsOnSVN(SVN_WEBAPP_PATH + templateCSSDirectory, null)) {
				svnService.createFolder(SVN_WEBAPP_PATH + templateCSSDirectory);
			}
		} catch (IOException | SVNException e) {
			throw new RuntimeException("Problem with template css folder creation ", e);
		}
	}

	@Override
	public void sync(final Set<String> templateNames) {
		final String target = SVN_WEBAPP_PATH + templateCSSDirectory;
		try {
			svnService.checkout(target, templateCSSAbsoluteDirPath.toAbsolutePath());

			this.create(templateNames);
		} catch (SVNException e) {
			throw new RuntimeException(
					String.format("Cannot checkout remote template css folder. Target: %s, destination: %s", target, templateCSSAbsoluteDirPath.toAbsolutePath()), e);
		}
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
		try {
			return svnService.get(toSVNRelativePath(templateName), revision).toString();
		} catch (SVNException e) {
			throw new RuntimeException("Problem with file loading form svn repository", e);
		}
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
	public void rename(String templateName, String newTemplateName) {
		try {
			final String templateCSSSVNPath = toSVNRelativePath(templateName);
			final String newTemplateCSSSVNPath = toSVNRelativePath(newTemplateName);

			final Path templateCSSWorkPath = templateCSSAbsoluteDirPath.resolve(templateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION);
			final Path newTemplateCSSWorkPath = templateCSSAbsoluteDirPath.resolve(newTemplateName + CSS_WORKING_VERSION_SUFFIX + CSS_EXTENSION);

			final String message = String.format("User: %s. Renaming template css file.", getCurrentUserName());
			final Map<String, String> properties = Map.of(IMCODE_USERNAME_PROPERTY, getCurrentUserName());

			svnService.move(templateCSSSVNPath, newTemplateCSSSVNPath, properties, message);
			svnService.checkout(SVN_WEBAPP_PATH + templateCSSDirectory, templateCSSAbsoluteDirPath);
			Files.move(templateCSSWorkPath, newTemplateCSSWorkPath);

		} catch (IOException | SVNException e) {
			throw new RuntimeException("Cannot rename template css file", e);
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

			documentService.getDocumentsByTemplateName(templateName).forEach(documentDTO -> {
				final Integer docId = documentDTO.getId();
				final List<String> aliases = documentDTO.getCommonContents().stream().map(CommonContent::getAlias).collect(Collectors.toList());

				documentsCache.invalidateDoc(docId, aliases);
			});
		} catch (IOException | SVNException e) {
			throw new RuntimeException("Problems with template css publishing", e);
		}
	}

	@Override
	public boolean existsLocally(String templateName) {
		return Files.exists(templateCSSAbsoluteDirPath.resolve(templateName + CSS_EXTENSION));
	}

	@Override
	public boolean existsOnSVN(String templateName) {
		return checkIfExistsOnSVN(templateName, null);
	}

	@Override
	public boolean existsOnSVN(String templateName, Long revision) {
		return checkIfExistsOnSVN(templateName, revision);
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
		try {
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
		} catch (SVNException e) {
			throw new RuntimeException("Cannot receive all template css revisions from svn repository", e);
		}
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
		} catch (IOException | SVNException e) {
			throw new RuntimeException("Errors with template css creation", e);
		}
	}

	private String getCurrentUserName() {
		final UserDomainObject user = Imcms.getUser();
		return (user != null && StringUtils.isNotBlank(user.getFullName())) ? user.getFullName() : "imcode";
	}

	private boolean checkIfExistsOnSVN(String templateName, Long revision) {
		try {
			return svnService.exists(toSVNRelativePath(templateName), revision);
		} catch (SVNException e) {
			throw new RuntimeException("Cannot check if template css file exists on svn", e);
		}
	}

	private String toSVNRelativePath(String templateName) {
		return SVN_WEBAPP_PATH + templateCSSDirectory + templateName + CSS_EXTENSION;
	}
}
