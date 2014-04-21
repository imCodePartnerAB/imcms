package imcode.server.document.index.service.impl;

import com.imcode.imcms.util.Value;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.index.DocumentIndex;
import imcode.server.document.textdocument.MenuItemDomainObject;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.Tika;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DocumentContentIndexer {

    private final Logger logger = Logger.getLogger(getClass());
    private final Predicate<FileDocumentDomainObject.FileDocumentFile> fileDocFileFilter;

    private Tika tika = Value.update(new Tika(), t -> t.setMaxStringLength(-1));

    public DocumentContentIndexer(Predicate<FileDocumentDomainObject.FileDocumentFile> fileDocFileFilter) {
        this.fileDocFileFilter = fileDocFileFilter;
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
    public SolrInputDocument indexTextDoc(TextDocumentDomainObject doc, SolrInputDocument indexDoc) {
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
            int no = loopItemRef.getItemNo();
            Optional.ofNullable(StringUtils.trimToNull(imageDO.getLinkUrl())).ifPresent(imageLinkUrl ->
                    indexDoc.addField(DocumentIndex.FIELD__IMAGE_LINK_URL, imageLinkUrl));
        });

        Set<Integer> childrenIds = doc.getMenus().values().stream()
                .flatMap(menu -> menu.getMenuItemsUnsorted().stream().map(MenuItemDomainObject::getDocumentId))
                .collect(Collectors.toSet());


        indexDoc.addField(DocumentIndex.FIELD__HAS_CHILDREN, !childrenIds.isEmpty());
        indexDoc.addField(DocumentIndex.FIELD__CHILDREN_COUNT, childrenIds.size());

        childrenIds.forEach(id -> indexDoc.addField(DocumentIndex.FIELD__CHILD_ID, id));

        return indexDoc;
    }


    public SolrInputDocument indexFileDoc(FileDocumentDomainObject doc, SolrInputDocument indexDoc) {
        Optional.ofNullable(doc.getDefaultFile()).filter(fileDocFileFilter::test).ifPresent(file -> {
            indexDoc.addField(DocumentIndex.FIELD__MIME_TYPE, file.getMimeType());
            Metadata metadata = Value.update(new Metadata(), m -> {
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