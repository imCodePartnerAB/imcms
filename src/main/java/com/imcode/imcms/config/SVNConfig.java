package com.imcode.imcms.config;

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
	private String repositoryUrl;
	private String username;
	private String password;
	private Path localSVNRepositoryFolder;

	public SVNConfig(@Value("${svn.url:#{null}}") String repositoryUrl,
					 @Value("${svn.username:#{null}}") String username,
					 @Value("${svn.password:#{null}}") String password,
					 ServletContext servletContext) {

		this.repositoryUrl = repositoryUrl;
		this.username = username;
		this.password = password;

		this.localSVNRepositoryFolder = Path.of(servletContext.getRealPath("/") + "WEB-INF/svn");
	}

	@PostConstruct
	private void init() throws IOException, SVNException {
		if (StringUtils.isNotEmpty(repositoryUrl)) return;

		log.warn("svn.url not provided");
		if (Files.exists(localSVNRepositoryFolder)) {
			this.repositoryUrl = "file://" + localSVNRepositoryFolder.toAbsolutePath();
			log.info("Reusing local SVN repository with path: {}", repositoryUrl);
			return;
		}

		Files.createDirectory(localSVNRepositoryFolder);
		this.repositoryUrl = SVNRepositoryFactory.createLocalRepository(localSVNRepositoryFolder.toFile(), true, true).toString();
		log.info(String.format("Created local SVN repository with path: %s", repositoryUrl));
	}

	@Bean
	public ISVNAuthenticationManager svnAuthenticationManager() {
		return SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
	}

	@Bean
	public SVNURL svnRepositoryURL() {
		try {
			return SVNURL.parseURIEncoded(repositoryUrl);
		} catch (SVNException e) {
			throw new RuntimeException(String.format("Incorrect repository URL: %s", repositoryUrl), e);
		}
	}

	@Bean
	public SVNRepository svnRepository() {
		try {
			final SVNRepository repository = SVNRepositoryFactory.create(svnRepositoryURL());
			repository.setAuthenticationManager(svnAuthenticationManager());

			log.info(String.format("Created connection with SVN repository: %s", svnRepositoryURL()));

			return repository;
		} catch (SVNException e) {
			throw new RuntimeException(String.format("Cannot create repository: %s", svnRepositoryURL()), e);
		}
	}

	@Bean
	public SvnOperationFactory svnOperationFactory() {
		final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		svnOperationFactory.setAuthenticationManager(svnAuthenticationManager());

		return svnOperationFactory;
	}
}
