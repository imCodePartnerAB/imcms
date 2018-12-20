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
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultLinkValidationService implements LinkValidationService {

    private static final String LINK_VALIDATION_REGEX = "(http.?:\\/\\/)?(.*)";
    private static final String LINK_ATTRIBUTE_VALIDATION_REGEX = ".*href\\s*=\\s*\"(http.?:\\/\\/)?(.*?)\"";
    private final Pattern patternTexts = Pattern.compile(LINK_ATTRIBUTE_VALIDATION_REGEX); // need add in methods?
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

    private boolean isHostFound(String protocol, String host) {
        boolean isHostFound = false;
        if (protocol == null) {
            isHostFound = true;
        } else {
            try {
                InetAddress.getByName(host);
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
            dtoFieldsDocument.setType(doc.getType());
            dtoFieldsDocument.setDocumentStatus(doc.getDocumentStatus());
            dtoFieldsDocument.setTitle(commonContentService.getOrCreateCommonContents(doc.getId(),
                    doc.getLatestVersion().getId()).get(doc.getCurrentVersion().getId()).getHeadline());

            if (doc.getType().equals(Meta.DocumentType.URL)) {
                DocumentURL documentURL = documentUrlService.getByDocId(doc.getId());
                EditLink editLink = new EditLink();
                editLink.setMetaId(documentURL.getDocId());
                editLink.setTitle(dtoFieldsDocument.getTitle());
                ValidationLink link = new ValidationLink();
                ValidationLink link2 = new ValidationLink();
                link.setDocumentData(dtoFieldsDocument);
                link.setEditLink(editLink);
                link2.setDocumentData(dtoFieldsDocument);
                link2.setEditLink(editLink);
                ValidationLink validationLink = verifyValidationLink(documentURL.getUrl(), link, patternUrl);
                ValidationLink validationLink2 = verifyValidationLinkOnOppositeProtocol(link2, documentURL.getUrl(), patternUrl);
                if (null == validationLink || null == validationLink2) {
                    continue;
                } else {
                    validationLinks.add(validationLink);
                    validationLinks.add(validationLink2);
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
                    ValidationLink link = new ValidationLink();
                    ValidationLink link2 = new ValidationLink();
                    link.setDocumentData(dtoFieldsDocument);
                    link.setEditLink(editLink);
                    link2.setDocumentData(dtoFieldsDocument);
                    link2.setEditLink(editLink);
                    ValidationLink validationLink = verifyValidationLink(text.getText(), link, patternTexts);
                    ValidationLink validationLink2 = verifyValidationLinkOnOppositeProtocol(link2, text.getText(), patternTexts);
                    if (null == validationLink || null == validationLink2) {
                        continue;
                    } else {
                        validationLinks.add(validationLink);
                        validationLinks.add(validationLink2);
                    }
                }
                for (Image image : images) {
                    EditLink editLink = new EditLink();
                    editLink.setMetaId(dtoFieldsDocument.getId());
                    editLink.setTitle(dtoFieldsDocument.getTitle());
                    editLink.setIndex(image.getIndex());
                    ValidationLink link = new ValidationLink();
                    ValidationLink link2 = new ValidationLink();
                    link.setDocumentData(dtoFieldsDocument);
                    link.setEditLink(editLink);
                    link2.setDocumentData(dtoFieldsDocument);
                    link2.setEditLink(editLink);
                    ValidationLink validationLink = verifyValidationLink(image.getLinkUrl(), link, patternUrl);
                    ValidationLink validationLink2 = verifyValidationLinkOnOppositeProtocol(link2, image.getLinkUrl(), patternUrl);
                    if (null == validationLink || null == validationLink2) {
                        continue;
                    } else {
                        validationLinks.add(validationLink);
                        validationLinks.add(validationLink2);
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

    private ValidationLink verifyValidationLinkOnOppositeProtocol(ValidationLink link, String textUrl, Pattern pattern) {
        Matcher matcherUrl = pattern.matcher(textUrl);
        String httpProtocol = "http://";
        String httpsProtocol = "https://";
        if (matcherUrl.find()) {
            String protocol = matcherUrl.group(1);
            String host = matcherUrl.group(2);
            link.setUrl(protocol == null ? host : protocol + host);
            try {
                if (null != protocol && protocol.equals(httpProtocol)) {
                    protocol = httpsProtocol;
                    link.setUrl(protocol + host);
                    if (isHostFound(protocol, host)) {
                        link.setHostFound(true);
                        URL url = new URL(protocol + host);
                        if (isHostReachable(url)) {
                            link.setHostReachable(true);
                            if (isPageFound(url)) {
                                link.setPageFound(true);
                            }

                        }
                    }
                } else if (null != protocol && protocol.equals(httpsProtocol)) {
                    protocol = httpProtocol;
                    link.setUrl(protocol + host);
                    if (isHostFound(protocol, host)) {
                        link.setHostFound(true);
                        URL url = new URL(protocol + host);
                        if (isHostReachable(url)) {
                            link.setHostReachable(true);
                            if (isPageFound(url)) {
                                link.setPageFound(true);
                            }

                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.getMessage();
            }
            return link;
        }
        return null;
    }

    private ValidationLink verifyValidationLink(String textUrl, ValidationLink link, Pattern pattern) {
        Matcher matcherUrl = pattern.matcher(textUrl);
        if (matcherUrl.find()) {
            String protocol = matcherUrl.group(1);
            String host = matcherUrl.group(2);
            link.setUrl(protocol == null ? host : protocol + host);
            try {
                if (isHostFound(protocol, host)) {
                    link.setHostFound(true);
                    URL url = new URL(protocol + host);
                    if (isHostReachable(url)) {
                        link.setHostReachable(true);
                        if (isPageFound(url)) {
                            link.setPageFound(true);
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.getMessage();
            }
            return link;
        }
        return null;
    }
}
