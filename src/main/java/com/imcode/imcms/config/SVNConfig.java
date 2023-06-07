package com.imcode.imcms.config;

import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.component.SVNService;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.TemplateCSSService;
import com.imcode.imcms.domain.service.api.DefaultTemplateCSSService;
import com.imcode.imcms.domain.service.api.DummyTemplateCSSService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc2.SvnOperationFactory;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@Configuration
public class SVNConfig {
	private boolean svnUse;
	private String templateCSSDirectory;
	private String repositoryUrl;
	private String username;
	private String password;
	private Path localSVNRepositoryFolder;

	@Getter
	private ISVNAuthenticationManager authenticationManager;
	@Getter
	private SVNURL svnRepositoryURL;
	@Getter
	private SVNRepository svnRepository;
	@Getter
	private SvnOperationFactory svnOperationFactory;

	public SVNConfig(@Value("#{${svn.use} ?: true}") boolean svnUse,
					 @Value("${TemplateCSSPath}") String templateCSSDirectory,
					 @Value("${svn.url:#{null}}") String repositoryUrl,
					 @Value("${svn.username:#{null}}") String username,
					 @Value("${svn.password:#{null}}") String password,
					 ServletContext servletContext) {

		this.svnUse = svnUse;
		this.templateCSSDirectory = templateCSSDirectory;
		this.repositoryUrl = repositoryUrl;
		this.username = username;
		this.password = password;

		this.localSVNRepositoryFolder = Path.of(servletContext.getRealPath("/") + "WEB-INF/svn");
	}

	@PostConstruct
	private void init() throws IOException, SVNException {
		if (!svnUse) return;

		if (StringUtils.isEmpty(repositoryUrl)) {
			log.warn("svn.url not provided");
			if (Files.exists(localSVNRepositoryFolder)) {
				this.repositoryUrl = "file://" + localSVNRepositoryFolder.toAbsolutePath();
				log.info("Reusing local SVN repository with path: {}", repositoryUrl);
			} else {
				Files.createDirectory(localSVNRepositoryFolder);
				this.repositoryUrl = SVNRepositoryFactory.createLocalRepository(localSVNRepositoryFolder.toFile(), true, true).toString();
				log.info(String.format("Created local SVN repository with path: %s", repositoryUrl));
			}
		}

		this.authenticationManager = SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
		this.svnRepositoryURL = SVNURL.parseURIEncoded(repositoryUrl);
		this.svnRepository = setUpSvnRepository();
		this.svnOperationFactory = setUpSvnOperationFactory();
	}

	@Bean("templateCSSService")
	public TemplateCSSService templateCSSService(SVNService svnService,
												 DocumentsCache documentsCache,
												 DocumentService<DocumentDTO> documentService,
												 ServletContext servletContext) {

		if (!svnUse)
			log.warn("Template CSS feature disabled -'svn.use '= false. Creating DummyTemplateCSSService as mock for TemplateCSSService");

		return svnUse ? new DefaultTemplateCSSService(svnService, documentsCache, documentService, servletContext, templateCSSDirectory) : new DummyTemplateCSSService();
	}

	private SVNRepository setUpSvnRepository() {
		try {
			final SVNRepository repository = SVNRepositoryFactory.create(svnRepositoryURL);
			repository.setAuthenticationManager(authenticationManager);

			log.info(String.format("Created connection with SVN repository: %s", svnRepositoryURL));

			return repository;
		} catch (SVNException e) {
			throw new RuntimeException(String.format("Cannot create repository: %s", svnRepositoryURL), e);
		}
	}

	private SvnOperationFactory setUpSvnOperationFactory() {
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		svnOperationFactory.setAuthenticationManager(authenticationManager);

		return svnOperationFactory;
	}
}
