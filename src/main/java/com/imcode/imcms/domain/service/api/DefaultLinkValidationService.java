package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.api.ValidationLink;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentStoredFieldsDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.LinkValidationService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.model.Document;
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
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultLinkValidationService implements LinkValidationService {

    private static final String REGEX_FOR_HTML_REFERENCE = ".*href\\s*=\\s*\"(http.?:\\/\\/)?(.*)\".*";
    private DocumentService<DocumentDTO> defaultDocumentService;
    private DocRepository docRepository;
    private LanguageService languageService;
    private TextService textService;


    public DefaultLinkValidationService(DocumentService<DocumentDTO> defaultDocumentService,
                                        DocRepository docRepository,
                                        LanguageService languageService,
                                        TextService textService) {
        this.defaultDocumentService = defaultDocumentService;
        this.docRepository = docRepository;
        this.languageService = languageService;
        this.textService = textService;
    }

    private static boolean isReacheble(String host, int openPort) {
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

    public List<ValidationLink> validateDocumentsLinks(boolean onlyBrokenLinks, int startDocumentId, int endDocumentId) {
        List<Integer> ids = docRepository.getDocumentIdsInRange(startDocumentId, endDocumentId);
        List<Document> documentsToTest = ids
                .stream()
                .map(id -> defaultDocumentService.get(id))
                .collect(Collectors.toList());

        Pattern pattern = Pattern.compile(REGEX_FOR_HTML_REFERENCE);
        for (Document doc : documentsToTest) {
            ValidationLink link = new ValidationLink();
            DocumentStoredFieldsDTO dtoFieldsDocument = new DocumentStoredFieldsDTO();
            dtoFieldsDocument.setId(doc.getId());
            dtoFieldsDocument.setAlias(doc.getAlias());
            dtoFieldsDocument.setType(doc.getType());
            dtoFieldsDocument.setDocumentStatus(doc.getDocumentStatus());
            dtoFieldsDocument.setTitle(doc.getCommonContents().get(0).getHeadline());
            link.setDocumentData(dtoFieldsDocument);


            if (ids.contains(dtoFieldsDocument.getId())) {
                for (Language language : languageService.getAll()) {
                    Set<Text> publicTexts = textService.getPublicTexts(doc.getId(), language);
                    for (Text text : publicTexts) {
                        Matcher m = pattern.matcher(text.getText());
                        if (m.find()) {
                            String host = m.group(2);
                            String protocol = m.group(1);

                            if (protocol == null) {
                                link.setHostFound(true);
                            } else {
                                try {
                                    InetAddress.getByName(host);
                                } catch (UnknownHostException e) {
                                    link.setHostFound(false);
                                    return null;
                                }
                            }

                            URL url = null;

                            if (link.isHostFound()) {
                                try {
                                    if (protocol == null) {
                                        url = new URL("https://" + host);

                                    } else {
                                        url = new URL(protocol + host);
                                    }
                                    link.setHostReachable(isReacheble(url.getHost(), url.getDefaultPort()));
                                } catch (MalformedURLException e) {
                                    link.setHostReachable(false);
                                    return null;
                                }
                            }

                            if (link.isHostReachable()) {
                                //url = new URL(protocol + host);
                                HttpURLConnection httpConnection = null;
                                //TRy with resources;
                                try {
                                    httpConnection = (HttpURLConnection) url.openConnection();
                                    link.setPageFound(HttpURLConnection.HTTP_OK == httpConnection.getResponseCode());
                                } catch (IOException e) {
                                    link.setPageFound(false);
                                    return null;
                                } finally {
                                    httpConnection.disconnect();
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
