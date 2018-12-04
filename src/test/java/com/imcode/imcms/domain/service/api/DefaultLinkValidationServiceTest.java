package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.api.DocumentLanguage;
import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.mapping.DocGetterCallback;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import imcode.server.Imcms;
import imcode.server.user.UserDomainObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Transactional
public class DefaultLinkValidationServiceTest extends WebAppSpringTestConfig {

    private static final String REGEX_FOR_HTML_REFERENCE = ".*href\\s*=\\s*\"(http.?:\\/\\/)?(.*)\".*";
    @Autowired
    private DocumentService<DocumentDTO> defaultDocumentService;
    @Autowired
    private DocRepository docRepository;
    @Autowired
    private TextService textService;
    @Autowired
    private LanguageService languageService;

    private static boolean isReacheble(String host, int openPort) {
        final int timeOutMillis = 1000;
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(host, openPort), timeOutMillis);
                if (soc.isConnected()) {
                    soc.close();
                }
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        final UserDomainObject user = new UserDomainObject(1);
        DocGetterCallback docGetterCallback = user.getDocGetterCallback();
        DocumentLanguage language = DocumentLanguage.builder()
                .code("en")
                .build();

        docGetterCallback.setLanguage(language);
        Imcms.setUser(user);
    }

    @Test
    public void getAllEntities_When_RangeIdsExist_Expected_CorrectEntities() {

    }

    @Test
    public void getAllEntities_When_DocumentsNotExistInRangeIds_Expected_EmptyResult() {

    }

    @Test
    public void getAllBrokenEntities_When_RangeIdsExist__Expected_CorrectEntities() {

    }

    @Test
    public void getAllBrokenEntities_When_RangeIdsNotExist__Expected_EmptyResult() {

    }

    @Test
    public void getHost_When_HostExist__Expected_HostIsTrue() {

    }

    @Test
    public void getHost_When_HostNotExist__Expected_HostFalse() {

    }

    @Test
    public void getHostReachable_When_HostReachableExist__Expected_HostReachableTrue() {

    }

    @Test
    public void getHostReachable_When_HostReachableNotExist__Expected_HostReachableFalse() {

    }

    @Test
    public void getPageFound_When_PageExist__Expected_PageIsTrue() {

    }

    @Test
    public void getPageFound_When_PageNotExist__Expected_PageFalse() {

    }


}
