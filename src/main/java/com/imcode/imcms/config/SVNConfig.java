package com.imcode.imcms.config;

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

@Configuration
public class SVNConfig {
	@Value("${svn.url}")
	private String repositoryUrl;
	@Value("${svn.username}")
	private String username;
	@Value("${svn.password}")
	private String password;

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
