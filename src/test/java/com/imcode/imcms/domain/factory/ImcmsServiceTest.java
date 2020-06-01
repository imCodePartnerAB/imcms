package com.imcode.imcms.domain.factory;

import com.imcode.imcms.WebAppSpringTestConfig;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.domain.exception.UnsupportedDocumentTypeException;
import com.imcode.imcms.domain.service.DocumentService;
import imcode.server.ImcmsServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.imcode.imcms.persistence.entity.Meta.DocumentType.FILE;
import static com.imcode.imcms.persistence.entity.Meta.DocumentType.HTML;
import static com.imcode.imcms.persistence.entity.Meta.DocumentType.TEXT;
import static com.imcode.imcms.persistence.entity.Meta.DocumentType.URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImcmsServiceTest extends WebAppSpringTestConfig {

    @Autowired
    private ImcmsServices imcmsServices;

    @Autowired
    private DocumentService<TextDocumentDTO> textDocumentService;
    @Autowired
    private DocumentService<FileDocumentDTO> fileDocumentService;
    @Autowired
    private DocumentService<UrlDocumentDTO> urlDocumentService;

    @Test
    public void getTextDocumentServiceBean_WhenTypeTEXT_Expect_CorrectBeanClass() {
        assertEquals(textDocumentService, imcmsServices.getDocumentServiceByType(TEXT));
    }

    @Test
    public void getFileDocumentServiceBean_WhenTypeFILE_Expect_CorrectBeanClass() {
        assertEquals(fileDocumentService, imcmsServices.getDocumentServiceByType(FILE));
    }

    @Test
    public void getUrlDocumentServiceBean_WhenTypeUrl_Expect_CorrectBeanClass() {
        assertEquals(urlDocumentService, imcmsServices.getDocumentServiceByType(URL));
    }

    @Test
    public void getDocumentServiceBean_WhenTypeNoExists_Expect_CorrectException() {
        assertThrows(UnsupportedDocumentTypeException.class, () -> imcmsServices.getDocumentServiceByType(HTML));
    }
}
