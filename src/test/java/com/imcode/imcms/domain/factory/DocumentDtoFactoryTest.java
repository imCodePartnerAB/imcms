package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.dto.TextDocumentDTO;
import com.imcode.imcms.domain.dto.UrlDocumentDTO;
import com.imcode.imcms.persistence.entity.Meta;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class DocumentDtoFactoryTest {

    @Mock
    private CommonContentFactory commonContentFactory;

    @InjectMocks
    private DocumentDtoFactory documentDtoFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(commonContentFactory.createCommonContents()).thenReturn(new ArrayList<>());
    }

    @Test
    public void createEmpty() {
        final DocumentDTO emptyDocument = documentDtoFactory.createEmpty();

        assertNull(emptyDocument.getType());
        assertNull(emptyDocument.getId());
    }

    @Test
    public void createEmptyFileDocument() {
        final FileDocumentDTO emptyFileDocument = documentDtoFactory.createEmptyFileDocument();

        assertThat(emptyFileDocument.getType(), equalTo(Meta.DocumentType.FILE));
        assertNull(emptyFileDocument.getId());
    }

    @Test
    public void createEmptyTextDocument() {
        final TextDocumentDTO emptyTextDocument = documentDtoFactory.createEmptyTextDocument();

        assertThat(emptyTextDocument.getType(), equalTo(Meta.DocumentType.TEXT));
        assertNull(emptyTextDocument.getId());
    }

    @Test
    public void createEmptyUrlDocument() {
        final UrlDocumentDTO emptyUrlDocument = documentDtoFactory.createEmptyUrlDocument();

        assertThat(emptyUrlDocument.getType(), equalTo(Meta.DocumentType.URL));
        assertNull(emptyUrlDocument.getId());
    }

}
