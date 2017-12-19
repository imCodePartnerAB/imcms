package imcode.server.document.index.service.impl;

import com.imcode.imcms.util.Value;
import imcode.server.Config;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.Tika;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@Component
public class DocumentContentIndexer {

    private final Logger logger = Logger.getLogger(getClass());
    private final Predicate<FileDocumentDomainObject.FileDocumentFile> fileDocFileFilter;

    private Tika tika = Value.with(new Tika(), t -> t.setMaxStringLength(-1));

    @Autowired
    public DocumentContentIndexer(Config config) {
        this.fileDocFileFilter = buildFileDocFilter(config);
    }

    private static String getExtension(String filename) {
        return FilenameUtils.getExtension(org.apache.commons.lang3.StringUtils.trimToEmpty(filename)).toLowerCase();
    }

    private Predicate<FileDocumentDomainObject.FileDocumentFile> buildFileDocFilter(Config config) {
        final Set<String> disabledFileExtensions = config.getIndexDisabledFileExtensionsAsSet();
        final Set<String> disabledFileMimes = config.getIndexDisabledFileMimesAsSet();
        final boolean noIgnoredFileNamesAndExtensions = disabledFileExtensions.isEmpty() && disabledFileMimes.isEmpty();

        return fileDocumentFile -> {
            if (noIgnoredFileNamesAndExtensions) {
                return true;

            } else {
                final String ext = getExtension(fileDocumentFile.getFilename());
                final String mime = getExtension(fileDocumentFile.getMimeType());

                return !(disabledFileExtensions.contains(ext) || disabledFileMimes.contains(mime));
            }
        };
    }

    public SolrInputDocument index(DocumentDomainObject doc, SolrInputDocument indexDoc) {
        return doc instanceof TextDocumentDomainObject
                ? indexTextDoc((TextDocumentDomainObject) doc, indexDoc)
                : doc instanceof FileDocumentDomainObject
                ? indexFileDoc((FileDocumentDomainObject) doc, indexDoc)
                : indexDoc;
    }

    /**
     * Texts and images are not taken from textDocument. Instead they are queried from DB.
     */
    private SolrInputDocument indexTextDoc(TextDocumentDomainObject doc, SolrInputDocument indexDoc) {
        indexDoc.addField(DocumentIndex.FIELD__TEMPLATE, doc.getTemplateName());

        doc.getTexts().forEach((no, textDO) -> {
            String textValue = textDO.getText();

            indexDoc.addField(DocumentIndex.FIELD__NONSTRIPPED_TEXT, textValue);
            indexDoc.addField(DocumentIndex.FIELD__TEXT, textValue);
            indexDoc.addField(DocumentIndex.FIELD__TEXT + no, textValue);
        });

        doc.getLoopTexts().forEach((loopItemRef, textDO) -> {
            int no = loopItemRef.getItemNo();
            String textValue = textDO.getText();

            indexDoc.addField(DocumentIndex.FIELD__NONSTRIPPED_TEXT, textValue);
            indexDoc.addField(DocumentIndex.FIELD__TEXT, textValue);
            indexDoc.addField(DocumentIndex.FIELD__TEXT + no, textValue);
        });

        doc.getImages().forEach((no, imageDO) -> {
            Optional.ofNullable(StringUtils.trimToNull(imageDO.getLinkUrl())).ifPresent(imageLinkUrl ->
                    indexDoc.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl));
        });

        doc.getLoopImages().forEach((loopItemRef, imageDO) -> {
            Optional.ofNullable(StringUtils.trimToNull(imageDO.getLinkUrl())).ifPresent(imageLinkUrl ->
                    indexDoc.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl));
        });

        return indexDoc;
    }

    private SolrInputDocument indexFileDoc(FileDocumentDomainObject doc, SolrInputDocument indexDoc) {
        Optional.ofNullable(doc.getDefaultFile()).filter(fileDocFileFilter).ifPresent(file -> {

            if (file.isFileInputStreamSource() && !file.getFile().exists()) {
                return;
            }

            indexDoc.addField(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType());
            Metadata metadata = Value.with(new Metadata(), m -> {
                m.set(HttpHeaders.CONTENT_DISPOSITION, file.getFilename());
                m.set(HttpHeaders.CONTENT_TYPE, file.getMimeType());
            });

            try {
                String content = tika.parseToString(file.getInputStreamSource().getInputStream(), metadata);
                indexDoc.addField(DocumentIndex.FIELD__TEXT, content);
            } catch (Exception e) {
                logger.error(String.format("Unable to index doc %d file '%s'.", doc.getId(), file), e);
            }
        });

        return indexDoc;
    }
}