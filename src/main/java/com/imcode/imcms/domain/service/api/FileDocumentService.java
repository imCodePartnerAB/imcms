package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.DocumentDTO;
import com.imcode.imcms.domain.dto.DocumentFileDTO;
import com.imcode.imcms.domain.dto.FileDocumentDTO;
import com.imcode.imcms.domain.service.DocumentFileService;
import com.imcode.imcms.domain.service.DocumentService;
import com.imcode.imcms.util.Value;
import imcode.server.Config;
import imcode.server.document.index.DocumentIndex;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.Tika;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
@Service
@Transactional
public class FileDocumentService implements DocumentService<FileDocumentDTO> {

    private final Logger logger = LogManager.getLogger(getClass());
    private final DocumentService<DocumentDTO> defaultDocumentService;
    private final DocumentFileService documentFileService;
    private final File filesRoot;
    private final Predicate<DocumentFileDTO> fileDocFileFilter;
    private final Tika tika = Value.with(new Tika(), t -> t.setMaxStringLength(-1));

    @SneakyThrows
    public FileDocumentService(DocumentService<DocumentDTO> documentService,
                               DocumentFileService documentFileService,
                               @org.springframework.beans.factory.annotation.Value("${FilePath}") Resource filesRoot,
                               Config config) {

        this.defaultDocumentService = documentService;
        this.documentFileService = documentFileService;
        this.filesRoot = filesRoot.getFile();
        this.fileDocFileFilter = buildFileDocFilter(config);
    }

    private static String getExtension(String filename) {
        return FilenameUtils.getExtension(org.apache.commons.lang3.StringUtils.trimToEmpty(filename)).toLowerCase();
    }

    @Override
    public long countDocuments() {
        return defaultDocumentService.countDocuments();
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
    public FileDocumentDTO copy(int docId) {
        final int copiedDocId = defaultDocumentService.copy(docId).getId();

        documentFileService.copy(docId, copiedDocId);

        return get(copiedDocId);
    }

    @Override
    public boolean publishDocument(int docId, int userId) {
        return defaultDocumentService.publishDocument(docId, userId);
    }

    @Override
    public void deleteByDocId(Integer docIdToDelete) {
        documentFileService.deleteByDocId(docIdToDelete);
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
                .ifPresent(documentFileDTO -> {

                    final File file = new File(filesRoot, documentFileDTO.getFilename());

                    if (!file.exists()) {
                        return;
                    }

                    solrInputDocument.addField(DocumentIndex.FIELD__MIME_TYPE, documentFileDTO.getMimeType());

                    try (final InputStream fileInputStream = new FileInputStream(file)) {
                        final Metadata metadata = new Metadata();
                        metadata.set(HttpHeaders.CONTENT_DISPOSITION, documentFileDTO.getFilename());
                        metadata.set(HttpHeaders.CONTENT_TYPE, documentFileDTO.getMimeType());

                        final String content = tika.parseToString(fileInputStream, metadata);
                        solrInputDocument.addField(DocumentIndex.FIELD__TEXT, content);

                    } catch (Exception e) {
                        logger.error(String.format("Unable to index doc %d file '%s'.", docId, documentFileDTO), e);
                    }
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

    @Override
    public FileDocumentDTO createFromParent(Integer parentDocId) {
        return Value.with(
                new FileDocumentDTO(defaultDocumentService.createFromParent(parentDocId)),
                fileDocumentDTO -> fileDocumentDTO.setFiles(new ArrayList<>())
        );
    }

    @Override
    public List<FileDocumentDTO> getDocumentsByTemplateName(String templateName) {
        return null;
    }

    @Override
    public int countDocumentsByTemplateName(String templateName) {
        return 0;
    }

    @Override
    public String getUniqueAlias(String alias) {
        return defaultDocumentService.getUniqueAlias(alias);
    }

}
