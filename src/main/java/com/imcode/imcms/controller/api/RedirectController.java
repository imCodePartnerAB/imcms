package com.imcode.imcms.controller.api;

import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletContext;
import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
@RestController
@RequestMapping("/redirect")
public class RedirectController {
	private final DocumentMapper documentMapper;
	private final ServletContext servletContext;

	public RedirectController(DocumentMapper documentMapper,
	                          ServletContext servletContext) {
		this.documentMapper = documentMapper;
		this.servletContext = servletContext;
	}

	@GetMapping
	public RedirectView validateReturnUrl(@RequestParam String returnUrl, @RequestParam(required = false) String metaId) {
		final ServletUriComponentsBuilder uriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri();

		if (StringUtils.isNotEmpty(returnUrl) && (documentMapper.toDocumentId(returnUrl) != null || isInternal(returnUrl))) {
			return new RedirectView(toRedirectUrl(uriComponentsBuilder, returnUrl));
		}

		if (StringUtils.isNotEmpty(returnUrl) && isInSubDomain(returnUrl, uriComponentsBuilder)) {
			return new RedirectView(returnUrl);
		}

		if (StringUtils.isNotEmpty(metaId)) {
			return new RedirectView(toRedirectUrl(uriComponentsBuilder, "/servlet/AdminDoc?meta_id=" + metaId));
		}

		final int startDocument = Imcms.getServices().getSystemData().getStartDocument();
		return new RedirectView(toRedirectUrl(uriComponentsBuilder, String.valueOf(startDocument)));
	}

	private boolean isInternal(String url) {
		return !url.startsWith("http://") && !url.startsWith("https://");
	}

	private boolean isInSubDomain(String url, ServletUriComponentsBuilder uriComponentsBuilder) {
		try {
			final String currentRootSubdomain = getRootSubdomain(uriComponentsBuilder.build().toString());
			final String targetRootSubdomain = getRootSubdomain(url);

			return currentRootSubdomain.equalsIgnoreCase(targetRootSubdomain);
		} catch (URISyntaxException e) {
			log.error("Incorrect URL", e);
		}

		return false;
	}

	private String getRootSubdomain(String url) throws URISyntaxException {
		final URI uri = new URI(url);
		final String host = uri.getHost();

		if (host != null) {
			final String[] subdomains = host.split("\\.");
			if (subdomains.length >= 2) {
				return subdomains[subdomains.length - 2] + "." + subdomains[subdomains.length - 1];
			}
		}

		return "";
	}

	private String toRedirectUrl(ServletUriComponentsBuilder uriComponentsBuilder, String url) {
		return uriComponentsBuilder.replacePath(servletContext.getContextPath() + (url.startsWith("/") ? url : '/' + url)).build().toString();
	}
}
