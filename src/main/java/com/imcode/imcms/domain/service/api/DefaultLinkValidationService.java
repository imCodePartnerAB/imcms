package com.imcode.imcms.domain.service.api;

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
import com.imcode.imcms.persistence.entity.Meta;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private static final String LINK_ATTRIBUTE_VALIDATION_REGEX = ".*href\\s*=\\s*\"" + LINK_VALIDATION_REGEX + "\".*";
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

    private boolean isHostReachable(String protocol, String host) {
        final int timeOutMillis = 1000;
        try {
            URL url = new URL(protocol + host);
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

    private boolean isPageFound(String protocol, String host) {
        URL url = null;
        boolean isPageFound = false;
        try {
            url = new URL(protocol + host);
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

        Pattern patternTexts = Pattern.compile(LINK_ATTRIBUTE_VALIDATION_REGEX);
        Pattern patternUrl = Pattern.compile(LINK_VALIDATION_REGEX);

        for (Document doc : documentsToTest) {
            DocumentStoredFieldsDTO dtoFieldsDocument = new DocumentStoredFieldsDTO();
            dtoFieldsDocument.setId(doc.getId());
            dtoFieldsDocument.setAlias(doc.getAlias());
            dtoFieldsDocument.setType(doc.getType());
            dtoFieldsDocument.setDocumentStatus(doc.getDocumentStatus());

            dtoFieldsDocument.setTitle(commonContentService.getOrCreateCommonContents(doc.getId(),
                    doc.getLatestVersion().getId()).get(0).getHeadline()); // TODO: 14.12.18 check correct language

            for (Language language : languageService.getAll()) {
                Set<Text> publicTexts = textService.getPublicTexts(doc.getId(), language);
                Set<String> publicImageLinks = imageService.getPublicImageLinks(doc.getId(), language);
                //if (doc instanceof UrlDocumentDTO)  //todo: found the best solution
                if (doc.getType().equals(Meta.DocumentType.URL)) {
                    DocumentURL documentURL = documentUrlService.getByDocId(doc.getId());
                    ValidationLink link = new ValidationLink();
                    link.setDocumentData(dtoFieldsDocument);
                    Matcher matcher = patternUrl.matcher(documentURL.getUrl());
                    verifyValidationLink(validationLinks, matcher, link);
                } else {
                    for (Text text : publicTexts) {
                        ValidationLink link = new ValidationLink();
                        link.setDocumentData(dtoFieldsDocument);
                        Matcher matcher = patternTexts.matcher(text.getText());
                        verifyValidationLink(validationLinks, matcher, link);
                    }
                    for (String imageUrlLink : publicImageLinks) {
                        ValidationLink link = new ValidationLink();
                        link.setDocumentData(dtoFieldsDocument);
                        Matcher matcher = patternUrl.matcher(imageUrlLink);
                        verifyValidationLink(validationLinks, matcher, link);
                    }
                }
            }
        }
        if (onlyBrokenLinks) {
            validationLinks = validationLinks.stream()
                    .filter(link -> !(link.isHostFound() || link.isHostReachable() || link.isPageFound()))
                    .collect(Collectors.toList());
        }
        return validationLinks;
    }

    private void verifyValidationLink(List<ValidationLink> validationLinks, Matcher m, ValidationLink link) {
        if (m.find()) {
            String protocol = m.group(1);
            String host = m.group(2);
            link.setUrl(protocol + host);

            if (isHostFound(protocol, host)) {
                link.setHostFound(true);
                if (isHostReachable(protocol, host)) {
                    link.setHostReachable(true);
                    if (isPageFound(protocol, host)) {
                        link.setPageFound(true);
                    }
                }
            }
            validationLinks.add(link);
        }
    }
}
