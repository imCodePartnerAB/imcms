package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.EditLink;
import com.imcode.imcms.api.ValidationLink;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.DocumentUrlService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.LinkValidationService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.DocumentURL;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import com.imcode.imcms.persistence.entity.Image;
import com.imcode.imcms.persistence.entity.Meta;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ucar.httpservices.HTTPAuthStore.log;

@Service
public class DefaultLinkValidationService implements LinkValidationService {

    private static final String LINK_VALIDATION_REGEX = "(http.?:\\/\\/)?(.*)";
    private static final String LINK_ATTRIBUTE_VALIDATION_REGEX = ".*href\\s*=\\s*\"(http.?:\\/\\/)?(.*?)\"";
    private static final String PROTOCOL_HTTP = "http://";
    private static final String PROTOCOL_HTTPS = "https://";
    private final Pattern patternTexts = Pattern.compile(LINK_ATTRIBUTE_VALIDATION_REGEX);
    private final Pattern patternUrl = Pattern.compile(LINK_VALIDATION_REGEX);
    private DocumentService<DocumentDTO> defaultDocumentService;
    private DocRepository docRepository;
    private LanguageService languageService;
    private TextService textService;
    private CommonContentService commonContentService;
    private ImageService imageService;
    private DocumentUrlService documentUrlService;


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
        boolean isHostFound = false;
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
        boolean isPageFound = false;
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            try (AutoCloseable autoCloseable = () -> httpConnection.disconnect()) {
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
                .map(id -> defaultDocumentService.get(id))
                .collect(Collectors.toList());

        for (Document doc : documentsToTest) {

            DocumentStoredFieldsDTO dtoFieldsDocument = new DocumentStoredFieldsDTO();
            dtoFieldsDocument.setId(doc.getId());
            dtoFieldsDocument.setAlias(doc.getAlias());
            dtoFieldsDocument.setDocumentStatus(doc.getDocumentStatus());
            dtoFieldsDocument.setTitle(commonContentService.getOrCreateCommonContents(doc.getId(),
                    doc.getLatestVersion().getId()).get(doc.getCurrentVersion().getId()).getHeadline());

            if (doc.getType().equals(Meta.DocumentType.URL)) {
                DocumentURL documentURL = documentUrlService.getByDocId(doc.getId());
                EditLink editLink = new EditLink();
                editLink.setMetaId(documentURL.getDocId());
                editLink.setTitle(dtoFieldsDocument.getTitle());

                ValidationLink link = new ValidationLink();
                link.setDocumentData(dtoFieldsDocument);
                link.setEditLink(editLink);
                link.setUrl(documentURL.getUrl());
                link.setLinkType("URL");
                String isValidUrl = checkValidUrl(link, patternUrl);

                if (!isValidUrl.isEmpty()) {
                    link.setUrl(isValidUrl);
                    validationLinks.addAll(validationLinksChecked(link, documentURL.getUrl(), patternUrl));
                }
            }

            for (Language language : languageService.getAll()) {
                Set<Text> publicTexts = textService.getPublicTexts(doc.getId(), language);
                Set<Image> images = imageService.getImagesAllVersionAndLanguages(doc.getId(), language);
                for (Text text : publicTexts) {
                    EditLink editLink = new EditLink();
                    editLink.setMetaId(dtoFieldsDocument.getId());
                    editLink.setTitle(dtoFieldsDocument.getTitle());
                    editLink.setIndex(text.getIndex());
                    editLink.setLoopEntryRef(text.getLoopEntryRef());

                    ValidationLink link = new ValidationLink();
                    link.setDocumentData(dtoFieldsDocument);
                    link.setEditLink(editLink);
                    link.setUrl(text.getText());
                    link.setLinkType("TEXT"); //
                    String isValidUrl = checkValidUrl(link, patternTexts);
                    if (!isValidUrl.isEmpty()) {
                        link.setUrl(isValidUrl);
                        validationLinks.addAll(validationLinksChecked(link, text.getText(), patternTexts));
                    }
                }
                for (Image image : images) {
                    EditLink editLink = new EditLink();
                    editLink.setMetaId(dtoFieldsDocument.getId());
                    editLink.setTitle(dtoFieldsDocument.getTitle());
                    editLink.setIndex(image.getIndex());
                    editLink.setLoopEntryRef(image.getLoopEntryRef());

                    ValidationLink link = new ValidationLink();
                    link.setDocumentData(dtoFieldsDocument);
                    link.setEditLink(editLink);
                    link.setUrl(image.getLinkUrl());
                    link.setLinkType("IMAGE");
                    String isValidUrl = checkValidUrl(link, patternUrl);

                    if (!isValidUrl.isEmpty()) {
                        link.setUrl(isValidUrl);
                        validationLinks.addAll(validationLinksChecked(link, image.getLinkUrl(), patternUrl));
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

    private List<ValidationLink> validationLinksChecked(ValidationLink link, String textUrl, Pattern pattern) {
        List<ValidationLink> links = new ArrayList<>();
        Matcher matcherUrl = pattern.matcher(textUrl);
        if (matcherUrl.find()) {
            String protocol = matcherUrl.group(1);
            if (null == protocol) {  // if protocol null its mean that url is on current host
                link.setHostFound(true);
                link.setHostReachable(true);
                links.addAll(checkRelativeLink(link));
            } else {
                links.add(verifyValidationLink(link));
            }

        }
        return links;
    }

    private String checkValidUrl(ValidationLink link, Pattern pattern) {
        Matcher matcherUrl = pattern.matcher(link.getUrl());
        String empty = "";
        if (matcherUrl.find()) {
            String protocol = matcherUrl.group(1);
            String host = matcherUrl.group(2);

            return protocol == null ? host : protocol + host;
        }
        return empty;
    }

    private Set<ValidationLink> checkRelativeLink(ValidationLink link) {
        Set<ValidationLink> links = new HashSet<>();
        try {
            ValidationLink cloneLink = (ValidationLink) link.clone();

            link.setUrl(PROTOCOL_HTTP + link.getUrl());
            links.add(verifyValidationLink(link));

            cloneLink.setUrl(PROTOCOL_HTTPS + cloneLink.getUrl());
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
