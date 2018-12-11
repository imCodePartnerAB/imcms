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

    private static boolean isReachable(String host, int openPort) {
        final int timeOutMillis = 1000;
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(host, openPort), timeOutMillis);
                if (soc.isConnected())
                    soc.close();
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private boolean resultHostFound(ValidationLink link, String host, String protocol) {
        if (protocol == null) {
            link.setHostFound(true);
        } else {
            try {
                InetAddress.getByName(host);
                link.setHostFound(true);
            } catch (UnknownHostException e) {
                link.setHostFound(false);
            }
        }
        return link.isHostFound();
    }

    private boolean resultHostReachable(ValidationLink link, String protocol, String host) {
        URL url = null;
        List<String> protocols = new ArrayList<>();
        protocols.add("http://");
        protocols.add("https://");
        if (link.isHostFound()) {
            for (String protoc : protocols) {
                try {
                    if (protocol == null) {
                        url = new URL(protoc + host);
                    } else {
                        url = new URL(protocol + host);
                    }
                    link.setHostReachable(isReachable(url.getHost(), url.getDefaultPort()));
                } catch (MalformedURLException e) {
                    link.setHostReachable(false);
                }
            }
        }
        return link.isHostReachable();
    }

    private boolean resultPageFound(ValidationLink link, String protocol, String host) {
        URL url = null;
        try {
            url = new URL(protocol + host);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            try (AutoCloseable autoCloseable = () -> httpConnection.disconnect()) {
                link.setPageFound(HttpURLConnection.HTTP_OK == httpConnection.getResponseCode());
            }
        } catch (Exception e) {
            link.setPageFound(false);
        }
        return link.isPageFound();
    }

    public List<ValidationLink> validateDocumentsLinks(boolean onlyBrokenLinks, int startDocumentId, int endDocumentId) {
        List<ValidationLink> validationLinks = new ArrayList<>();
        List<Integer> rangeIds = docRepository.getDocumentIdsInRange(startDocumentId, endDocumentId);
        List<Document> documentsToTest = rangeIds
                .stream()
                .map(id -> defaultDocumentService.get(id))
                .collect(Collectors.toList());

        Pattern patternTexts = Pattern.compile(LINK_ATTRIBUTE_VALIDATION_REGEX);
        Pattern patternUrl = Pattern.compile(LINK_VALIDATION_REGEX);

        for (Document doc : documentsToTest) {
            ValidationLink link = new ValidationLink();
            DocumentStoredFieldsDTO dtoFieldsDocument = new DocumentStoredFieldsDTO();
            dtoFieldsDocument.setId(doc.getId());
            dtoFieldsDocument.setAlias(doc.getAlias());
            dtoFieldsDocument.setType(doc.getType());
            dtoFieldsDocument.setDocumentStatus(doc.getDocumentStatus());
            dtoFieldsDocument.setTitle(commonContentService.getOrCreateCommonContents(doc.getId(),
                    doc.getLatestVersion().getId()).get(0).getHeadline());
            link.setDocumentData(dtoFieldsDocument);

            if (rangeIds.contains(dtoFieldsDocument.getId())) {
                for (Language language : languageService.getAll()) {
                    Set<Text> publicTexts = textService.getPublicTexts(doc.getId(), language);
                    Set<String> publicImageLinks = imageService.getPublicImageLinks(doc.getId(), language);
                    for (Text text : publicTexts) {
                        Matcher m = patternTexts.matcher(text.getText());
                        if (m.find()) {
                            String host = m.group(2);
                            String protocol = m.group(1);
                            link.setUrl(protocol + host);

                            if (resultHostFound(link, host, protocol)) {
                                if (resultHostReachable(link, protocol, host)) {
                                    if (resultPageFound(link, protocol, host)) {

                                    }
                                }
                            }
                            validationLinks.add(link);
                        }
                    }
                }
            }
        }
        return null;
    }
}
