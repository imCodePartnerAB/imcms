package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.factory.DocumentDtoFactory;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.DocumentService;
import imcode.server.Config;
import imcode.server.document.index.DocumentIndex;
import org.apache.commons.io.FilenameUtils;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service for work with File Documents only.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 29.12.17.
 */
@Transactional
public class FileDocumentService implements DocumentService<FileDocumentDTO> {

    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentFileService documentFileService;
    private final DocumentDtoFactory documentDtoFactory;
    private final Predicate<DocumentFileDTO> fileDocFileFilter;

    public FileDocumentService(DocumentService<DocumentDTO> documentService,
                               DocumentDtoFactory documentDtoFactory,
                               DocumentFileService documentFileService,
                               Config config) {

        this.defaultDocumentService = documentService;
        this.documentFileService = documentFileService;
        this.documentDtoFactory = documentDtoFactory;
        this.fileDocFileFilter = buildFileDocFilter(config);
    }

    @Override
    public FileDocumentDTO createEmpty() {
        return documentDtoFactory.createEmptyFileDocument();
    }

    @Override
    public FileDocumentDTO get(int docId) {
        final FileDocumentDTO fileDocument = new FileDocumentDTO(defaultDocumentService.get(docId));

        final List<DocumentFileDTO> documentFiles = documentFileService.getByDocId(docId)
                .stream()
                .map(DocumentFileDTO::new)
                .collect(Collectors.toList());

        fileDocument.setFiles(documentFiles);

        return fileDocument;
    }

    public FileDocumentDTO save(FileDocumentDTO saveMe) {
        final int savedDocId = defaultDocumentService.save(saveMe).getId();
        documentFileService.saveAll(saveMe.getFiles(), savedDocId);

        return saveMe;
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    private static String getExtension(String filename) {
        return FilenameUtils.getExtension(org.apache.commons.lang3.StringUtils.trimToEmpty(filename)).toLowerCase();
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        defaultDocumentService.deleteByDocId(docIdToDelete);
    }

    @Override
    public SolrInputDocument index(int docId) {

        final SolrInputDocument solrInputDocument = defaultDocumentService.index(docId);

        final FileDocumentDTO fileDocumentDTO = get(docId);

        fileDocumentDTO.getFiles()
                .stream()
                .filter(DocumentFileDTO::isDefaultFile)
                .findFirst()
                .filter(fileDocFileFilter)
                .ifPresent(file -> {

//                    if (file.isFileInputStreamSource() && !file.getFile().exists()) {
//                        return;
//                    }

                    solrInputDocument.addField(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType());
                });

        return solrInputDocument;
    }

    private Predicate<DocumentFileDTO> buildFileDocFilter(Config config) {
        final Set<String> disabledFileExtensions = config.getIndexDisabledFileExtensionsAsSet();
        final Set<String> disabledFileMimes = config.getIndexDisabledFileMimesAsSet();
        final boolean noIgnoredFileNamesAndExtensions = disabledFileExtensions.isEmpty() && disabledFileMimes.isEmpty();

        return documentFileDTO -> {
            if (noIgnoredFileNamesAndExtensions) {
                return true;

            } else {
                final String ext = getExtension(documentFileDTO.getFilename());
                final String mime = getExtension(documentFileDTO.getMimeType());

                return !(disabledFileExtensions.contains(ext) || disabledFileMimes.contains(mime));
            }
        };
    }
}
