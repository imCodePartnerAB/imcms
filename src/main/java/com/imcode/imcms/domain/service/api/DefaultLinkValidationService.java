package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.EditLink;
import com.imcode.imcms.api.ValidationLink;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.model.*;
import com.imcode.imcms.persistence.entity.ImageJPA;
import com.imcode.imcms.persistence.entity.Meta;
import imcode.server.Imcms;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DefaultLinkValidationService implements LinkValidationService {

    private static final String LINK_VALIDATION_REGEX = "(http.?:\\/\\/)?(.*)";
    private static final String LINK_ATTRIBUTE_VALIDATION_REGEX = "<a\\s+(?:[^>]*?\\s+)?href\\s*=\\s*\"(http.?:\\/\\/)?(.*?)\"";
    private static final String PROTOCOL_HTTP = "http";
    private static final String PROTOCOL_HTTPS = "https";
	private final Pattern patternTexts = Pattern.compile(LINK_ATTRIBUTE_VALIDATION_REGEX);
    private final Pattern patternUrl = Pattern.compile(LINK_VALIDATION_REGEX);
    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocRepository docRepository;
    private final LanguageService languageService;
    private final TextService textService;
    private final CommonContentService commonContentService;
    private final ImageService imageService;
    private final DocumentUrlService documentUrlService;


    public DefaultLinkValidationService(DocumentService<DocumentDTO> defaultDocumentService,
                                        DocRepository docRepository,
                                        LanguageService languageService,
                                        TextService textService,
                                        CommonContentService commonContentService,
                                        ImageService imageService,
                                        DocumentUrlService documentUrlService) {
        this.defaultDocumentService = defaultDocumentService;
        this.docRepository = docRepository;
        this.languageService = languageService;
        this.textService = textService;
        this.commonContentService = commonContentService;
        this.imageService = imageService;
        this.documentUrlService = documentUrlService;
    }

    private boolean isHostFound(URL url) {
        boolean isHostFound;
        if (url.getProtocol() == null) {
            isHostFound = true;
        } else {
            try {
                InetAddress.getByName(url.getHost());
                isHostFound = true;
            } catch (UnknownHostException e) {
                isHostFound = false;
            }
        }
        return isHostFound;
    }

    private boolean isHostReachable(URL url) {
        final int timeOutMillis = 1000;
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(url.getHost(), url.getDefaultPort()), timeOutMillis);
                if (soc.isConnected())
                    soc.close();
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private boolean isPageFound(URL url) {
        boolean isPageFound;
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestMethod(HttpMethod.HEAD);
			httpConnection.setInstanceFollowRedirects(false);

            try (AutoCloseable autoCloseable = httpConnection::disconnect) {
                isPageFound = HttpURLConnection.HTTP_OK == httpConnection.getResponseCode();
            }
        } catch (Exception e) {
            isPageFound = false;
        }
        return isPageFound;
    }

    public List<ValidationLink> validateDocumentsLinks(int startDocumentId, int endDocumentId, boolean onlyBrokenLinks) {
        List<ValidationLink> validationLinks = new ArrayList<>();
        List<Integer> rangeIds = docRepository.getDocumentIdsInRange(startDocumentId, endDocumentId);
        List<Document> documentsToTest = rangeIds
                .stream()
                .map(defaultDocumentService::get)
                .collect(Collectors.toList());

        for (Document doc : documentsToTest) {
	        DocumentStoredFieldsDTO dtoFieldsDocument = new DocumentStoredFieldsDTO();
	        dtoFieldsDocument.setId(doc.getId());
            dtoFieldsDocument.setDocumentStatus(doc.getDocumentStatus());

            for(CommonContent commonContent: commonContentService.getOrCreateCommonContents(doc.getId(), doc.getLatestVersion().getId())){
                if(Imcms.getLanguage().getId().equals(commonContent.getLanguage().getId())){
                    dtoFieldsDocument.setAlias(commonContent.getAlias());
                    dtoFieldsDocument.setTitle(commonContent.getHeadline());
                    break;
                }
            }

	        if (doc.getType().equals(Meta.DocumentType.URL)) {
		        DocumentURL documentURL = documentUrlService.getByDocId(doc.getId());
		        List<String> validUrl = getValidUrls(documentURL.getUrl(), patternUrl);

		        if (!validUrl.isEmpty()) {
			        EditLink editLink = new EditLink();
			        editLink.setMetaId(documentURL.getDocId());
			        editLink.setTitle(dtoFieldsDocument.getTitle());

                    ValidationLink link = new ValidationLink();
                    link.setDocumentData(dtoFieldsDocument);
                    link.setEditLink(editLink);
                    link.setUrl(validUrl.get(0));
                    link.setLinkType(ValidationLink.LinkType.URL);

                    validationLinks.addAll(validationLinksChecked(link, patternUrl));
                }
            }

            for (Language language : languageService.getAvailableLanguages()) {
                Set<Text> publicTexts = textService.getPublicTexts(doc.getId(), language);
                Set<ImageJPA> images = imageService.getImagesAllVersionAndLanguages(doc.getId(), language);
                for (Text text : publicTexts) {
                    EditLink editLink = new EditLink();
                    editLink.setMetaId(dtoFieldsDocument.getId());
                    editLink.setTitle(dtoFieldsDocument.getTitle());
                    editLink.setIndex(text.getIndex());
                    editLink.setLoopEntryRef(text.getLoopEntryRef());

                    ValidationLink link = new ValidationLink();
                    link.setDocumentData(dtoFieldsDocument);
                    link.setEditLink(editLink);
                    List<String> validUrls = getValidUrls(text.getText(), patternTexts);
                    for (String url : validUrls) {
                        ValidationLink validateTextLink = new ValidationLink();
                        validateTextLink.setDocumentData(dtoFieldsDocument);
                        validateTextLink.setEditLink(editLink);
                        validateTextLink.setUrl(url);
                        validateTextLink.setLinkType(ValidationLink.LinkType.TEXT);

                        validationLinks.addAll(validationLinksChecked(validateTextLink, patternUrl));
                    }
                }
                for (ImageJPA image : images) {
                    List<String> validUrls = getValidUrls(image.getLinkUrl(), patternUrl);

                    for (String url : validUrls) {
                        if (!url.equals("")) {
                            EditLink editLink = new EditLink();
                            editLink.setMetaId(dtoFieldsDocument.getId());
                            editLink.setTitle(dtoFieldsDocument.getTitle());
                            editLink.setIndex(image.getIndex());
                            editLink.setLoopEntryRef(image.getLoopEntryRef());

                            ValidationLink link = new ValidationLink();
                            link.setDocumentData(dtoFieldsDocument);
                            link.setEditLink(editLink);
                            link.setUrl(url);
                            link.setLinkType(ValidationLink.LinkType.IMAGE);

                            validationLinks.addAll(validationLinksChecked(link, patternUrl));
                        }
                    }
                }
            }
        }
        if (onlyBrokenLinks) {
            validationLinks = validationLinks.stream()
                    .filter(link -> !(link.isHostFound() && link.isHostReachable() && link.isPageFound()))
                    .collect(Collectors.toList());
        }
        return validationLinks;
    }

    @Override
    public Boolean isExternal(String url) {
        return isExternal(url, patternUrl);
    }

    @Override
    public Boolean isExternal(String url, Pattern pattern) {
        final Matcher matcher = pattern.matcher(url);
        if (!matcher.find()){
            return null;
        }

        final String protocol = matcher.group(1);
        // if protocol null its mean that url is on current host
        return protocol != null;
    }

    private List<ValidationLink> validationLinksChecked(ValidationLink link, Pattern pattern) {
        List<ValidationLink> links = new ArrayList<>();

        final Boolean isExternal = isExternal(link.getUrl(), pattern);
        if (isExternal != null) {
            if (isExternal) {
                links.add(verifyValidationLink(link));
            } else {
                link.setHostFound(true);
                link.setHostReachable(true);
                links.addAll(checkRelativeLink(link));
            }
        }
        return links;
    }

    private List<String> getValidUrls(String textUrl, Pattern pattern) {
        List<String> urls = new ArrayList<>();
        Matcher matcherUrl = pattern.matcher(textUrl);
        while (matcherUrl.find()) {
            String protocol = matcherUrl.group(1);
            String host = matcherUrl.group(2);
            urls.add(protocol == null ? host : protocol + host);
        }
        return urls;
    }

    private Set<ValidationLink> checkRelativeLink(ValidationLink link) {
	    final UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromCurrentRequestUri().replacePath(link.getUrl());

        Set<ValidationLink> links = new HashSet<>();
        try {
            ValidationLink cloneLink = (ValidationLink) link.clone();
	        link.setUrl(uriBuilder.scheme(PROTOCOL_HTTP).toUriString());
            links.add(verifyValidationLink(link));

	        cloneLink.setUrl(uriBuilder.scheme(PROTOCOL_HTTPS).toUriString());
            links.add(verifyValidationLink(cloneLink));
        } catch (CloneNotSupportedException e) {
            log.error(e.getMessage());
        }
        return links;
    }

    private ValidationLink verifyValidationLink(ValidationLink link) {
        try {
            URL url = new URL(link.getUrl());
            if (isHostFound(url)) {
                link.setHostFound(true);
                if (isHostReachable(url)) {
                    link.setHostReachable(true);
                    if (isPageFound(url)) {
                        link.setPageFound(true);
                    }
                }
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        }
        return link;
    }
}
