package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.mapping.DocumentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentValidatingServiceTest {

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private DocumentService<DocumentDTO> documentService;

    @InjectMocks
    private DefaultDocumentValidatingService documentValidatingService;

    @Test
    public void isTextDocument_When_DocumentHasTextType_Expect_True() {
        final String testIdentifier = "test_identifier";
        final int testDocId = 1;

        when(documentMapper.toDocumentId(testIdentifier)).thenReturn(testDocId);
        when(documentService.get(testDocId)).thenReturn(new TextDocumentDTO());

        assertTrue(documentValidatingService.isTextDocument(testIdentifier));
    }

    @Test
    public void isTextDocument_When_DocumentHasNotTextType_Expect_False() {
        final String testIdentifier = "test_identifier";
        final int testDocId = 1;

        when(documentMapper.toDocumentId(testIdentifier)).thenReturn(testDocId);
        when(documentService.get(testDocId)).thenReturn(new FileDocumentDTO());

        assertFalse(documentValidatingService.isTextDocument(testIdentifier));
    }

}