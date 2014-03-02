package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.doc.*;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import com.imcode.imcms.mapping.jpa.doc.content.FileDocItem;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.LoopEntryRef;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates or updates document content.
 *
 * @see com.imcode.imcms.mapping.DocumentSaver
 */
//todo: init using spring
//todo: hibernate.batch_update_versioned
public class DocumentStoringVisitor extends DocumentVisitor {

    protected ImcmsServices services;

    private static final int FILE_BUFFER_LENGTH = 2048;
    private static final int DB_FIELD_MAX_LENGTH__FILENAME = 255;

    // todo: @Inject
    protected DocRepository docRepository;
    protected DocVersionRepository docVersionRepository;
    protected LanguageRepository languageRepository;

    protected TextRepository textRepository;
    protected ImageRepository imageRepository;
    protected LoopRepository loopRepository;
    protected MenuRepository menuRepository;
    protected TemplateNamesRepository templateNamesRepository;
    protected IncludeRepository includeRepository;
    protected CommonContentRepository commonContentRepository;
    protected EntityConverter entityConverter;

    public DocumentStoringVisitor(ImcmsServices services) {
        this.services = services;
        this.docRepository = services.getManagedBean(DocRepository.class);
        this.docVersionRepository = services.getManagedBean(DocVersionRepository.class);
        this.languageRepository = services.getManagedBean(LanguageRepository.class);
        this.textRepository = services.getManagedBean(TextRepository.class);
        this.imageRepository = services.getManagedBean(ImageRepository.class);
        this.loopRepository = services.getManagedBean(LoopRepository.class);
        this.menuRepository = services.getManagedBean(MenuRepository.class);
        this.templateNamesRepository = services.getManagedBean(TemplateNamesRepository.class);
        this.includeRepository = services.getManagedBean(IncludeRepository.class);
        this.commonContentRepository = services.getManagedBean(CommonContentRepository.class);
        this.entityConverter = services.getManagedBean(EntityConverter.class);
    }

    /**
     * Saves (possibly rewrites) file if its InputStreamSource has been changed.
     *
     * @param fileDocumentFile
     * @param fileId
     */
    protected void saveFileDocumentFile(DocVersionRef docVersionRef, FileDocumentDomainObject.FileDocumentFile fileDocumentFile,
                                        String fileId) {
        try {
            InputStreamSource inputStreamSource = fileDocumentFile.getInputStreamSource();
            InputStream in;
            try {
                in = inputStreamSource.getInputStream();
            } catch (FileNotFoundException e) {
                throw new UnhandledException("The file for filedocument " + docVersionRef
                        + " has disappeared.", e);
            }
            if (null == in) {
                return;
            }

            File file = getFileForFileDocumentFile(docVersionRef, fileId);

            FileInputStreamSource fileInputStreamSource = new FileInputStreamSource(file);
            boolean sameFileOnDisk = file.exists() && inputStreamSource.equals(fileInputStreamSource);
            if (sameFileOnDisk) {
                in.close();
                return;
            }

            byte[] buffer = new byte[FILE_BUFFER_LENGTH];
            final OutputStream out = new FileOutputStream(file);
            try {
                for (int bytesRead; -1 != (bytesRead = in.read(buffer)); ) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
                in.close();
            }
            fileDocumentFile.setInputStreamSource(fileInputStreamSource);
        } catch (IOException e) {
            throw new UnhandledException(e);
        }
    }


    /**
     * Returns file for FileDocumentFile.
     */
    public static File getFileForFileDocumentFile(DocVersionRef docVersionRef, String fileId) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        String filename = getFilenameForFileDocumentFile(docVersionRef, fileId);

        return new File(filePath, filename);
    }


    /**
     * Returns FileDocumentFile filename.
     * <p/>
     * File name is a unique combination of doc id, doc version no and fileId (when not a blank).
     * For backward compatibility a doc version no is omitted if it equals to 0 (working version).
     * <p/>
     * If fieldId is not blank its added to filename as an extension.
     * <p/>
     * Examples:
     * 1002.xxx - 1002 is a doc id, doc version no is 0 and xxx is fileId.
     * 1002_3.xxx - 1002 is a doc id, 3 is a version no and xxx is fileId.
     * 1002_2 - 1002 is a doc id, 2 is a version no and fileId is blank.
     *
     * @param fileId
     * @return FileDocumentFile filename
     */
    public static String getFilenameForFileDocumentFile(DocVersionRef docVersionRef, String fileId) {
        int docId = docVersionRef.getDocId();
        int docVersionNo = docVersionRef.getDocVersionNo();

        String filename = "" + docId;

        if (docVersionNo != DocumentVersion.WORKING_VERSION_NO) {
            filename += ("_" + docVersionNo);
        }

        if (StringUtils.isNotBlank(fileId)) {
            filename += "." + FileUtility.escapeFilename(fileId);
        }

        return filename;
    }


    /**
     * Saves text document text fields.
     * <p/>
     * Deletes all existing text fields and then inserts new.
     *
     * @param textDocument
     * @param user
     */
    @Transactional
    void updateTextDocumentTexts(TextDocumentDomainObject textDocument, UserDomainObject user) {
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(textDocument.getId(), textDocument.getVersionNo());
        Language language = languageRepository.findByCode(textDocument.getLanguage().getCode());

        textRepository.deleteByDocVersionAndLanguage(docVersion, language);

        for (Map.Entry<Integer, TextDomainObject> e : textDocument.getTexts().entrySet()) {
            saveTextDocumentText(TextDocTextContainer.of(textDocument.getRef(), e.getKey(), e.getValue()), user);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, TextDomainObject> e : textDocument.getLoopTexts().entrySet()) {
            saveTextDocumentText(TextDocTextContainer.of(textDocument.getRef(), e.getKey().getItemNo(), e.getValue()), user);
        }
    }

    /**
     * @param textDocument
     * @param user
     */
    public void updateTextDocumentContentLoops(TextDocumentDomainObject textDocument, UserDomainObject user) {
        DocRef docRef = textDocument.getRef();

        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop> loops = loopRepository.findByDocVersion(docVersion);
        loopRepository.delete(loops);

        for (Map.Entry<Integer, Loop> loopAndNo : textDocument.getLoops().entrySet()) {
            Loop loop = loopAndNo.getValue();
            com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop ormLoop = new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop();
            List<com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry> ormItems = new LinkedList<>();

            for (Map.Entry<Integer, Boolean> loopEntry : loop.getEntries().entrySet()) {
                ormItems.add(new com.imcode.imcms.mapping.jpa.doc.content.textdoc.Loop.Entry(loopEntry.getKey(), loopEntry.getValue()));
            }

            ormLoop.setNo(loopAndNo.getKey());
            ormLoop.setEntries(ormItems);

            loopRepository.save(ormLoop);
        }
    }


    @Transactional
    public void saveDocumentCommonContent(DocumentDomainObject doc, UserDomainObject user) {
        Language ormLanguage = languageRepository.findByCode(doc.getLanguage().getCode());
        CommonContent ormDcc = commonContentRepository.findByDocIdAndLanguage(doc.getId(), ormLanguage);

        if (ormDcc == null) {
            ormDcc = new CommonContent();
        }

        DocumentCommonContent dcc = doc.getCommonContent();

        ormDcc.setDocId(doc.getId());
        ormDcc.setLanguage(ormLanguage);
        ormDcc.setHeadline(dcc.getHeadline());
        ormDcc.setMenuText(dcc.getMenuText());
        ormDcc.setMenuImageURL(dcc.getMenuImageURL());

        commonContentRepository.save(ormDcc);
    }


    /**
     * Saves text document's text.
     *
     * @param textContainer
     * @param user
     */
    @Transactional
    public void saveTextDocumentText(TextDocTextContainer textContainer, UserDomainObject user) {
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(textContainer.getDocRef().getDocId(), textContainer.getDocRef().getDocVersionNo());
        Language language = languageRepository.findByCode(textContainer.getDocRef().getDocLanguageCode());

        TextDomainObject text = textContainer.getText();
        Text ormText = new Text();

        ormText.setLanguage(language);
        ormText.setDocVersion(docVersion);
        ormText.setNo(textContainer.getTextNo());
        ormText.setText(text.getText());
        ormText.setType(TextType.values()[text.getType()]);
        com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef = textContainer.getLoopEntryRef();
        if (loopEntryRef != null) {
            ormText.setLoopEntryRef(new LoopEntryRef(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo()));
        }

        textRepository.save(ormText);

        // fixme: history
        //TextDocTextHistory textHistory = new TextDocTextHistory(textRef, user);
        //textRepository.saveTextHistory(textHistory);
    }


    /**
     * Saves text document's image.
     */
    @Transactional
    public void saveTextDocumentImage(TextDocImageContainer imageContainer, UserDomainObject user) {
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(imageContainer.getDocRef().getDocId(), imageContainer.getDocRef().getDocVersionNo());
        Language language = languageRepository.findByCode(imageContainer.getDocRef().getDocLanguageCode());

        ImageDomainObject image = imageContainer.getImage();

        Image ormImage = entityConverter.toEntity(image);

        ormImage.setNo(imageContainer.getImageNo());
        ormImage.setDocVersion(docVersion);
        ormImage.setLanguage(language);

        com.imcode.imcms.mapping.container.LoopEntryRef loopEntryRef = imageContainer.getLoopEntryRef();
        if (loopEntryRef != null) {
            ormImage.setLoopEntryRef(new LoopEntryRef(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo()));
        }

        imageRepository.save(ormImage);

        // fixme:  history
        // TextDocImageHistory textDocImageHistory = new TextDocImageHistory(image, user);
        // textDocRepository.saveImageHistory(textDocImageHistory);
    }


    @Transactional
    void updateTextDocumentImages(TextDocumentDomainObject doc, UserDomainObject user) {
        DocRef docRef = doc.getRef();
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        Language language = languageRepository.findByCode(docRef.getDocLanguageCode());

        imageRepository.deleteByDocVersionAndLanguage(docVersion, language);

        for (Map.Entry<Integer, ImageDomainObject> entry : doc.getImages().entrySet()) {
            TextDocImageContainer imageContainer = TextDocImageContainer.of(docRef, entry.getKey(), entry.getValue());

            saveTextDocumentImage(imageContainer, user);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemRef, ImageDomainObject> entry : doc.getLoopImages().entrySet()) {
            TextDocumentDomainObject.LoopItemRef loopItemRef = entry.getKey();
            TextDocImageContainer imageContainer = TextDocImageContainer.of(docRef, loopItemRef.getEntryRef(), loopItemRef.getItemNo(), entry.getValue());

            saveTextDocumentImage(imageContainer, user);
        }
    }


    @Transactional
    public void updateTextDocumentIncludes(TextDocumentDomainObject doc) {
        int docId = doc.getId();

        includeRepository.deleteByDocId(docId);

        for (Map.Entry<Integer, Integer> entry : doc.getIncludesMap().entrySet()) {
            Include include = new Include();
            include.setId(null);
            include.setDocId(docId);
            include.setNo(entry.getKey());
            include.setIncludedDocumentId(entry.getValue());

            includeRepository.save(include);
        }
    }


    @Transactional
    public void updateTextDocumentTemplateNames(TextDocumentDomainObject textDocument, UserDomainObject user) {
        int docId = textDocument.getId();

        TemplateNames templateNames = entityConverter.toEntity(textDocument.getTemplateNames());

        templateNames.setDocId(docId);

        templateNamesRepository.save(templateNames);
    }


    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        docRepository.deleteFileReferences(fileDocument.getRef());

        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(fileDocument.getRef().getDocId(), fileDocument.getRef().getDocVersionNo());

        for (Map.Entry<String, FileDocumentDomainObject.FileDocumentFile> entry : fileDocument.getFiles().entrySet()) {
            String fileId = entry.getKey();
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = entry.getValue();

            String filename = fileDocumentFile.getFilename();
            if (filename.length() > DB_FIELD_MAX_LENGTH__FILENAME) {
                filename = truncateFilename(filename, DB_FIELD_MAX_LENGTH__FILENAME);
            }

            boolean isDefaultFile = fileId.equals(fileDocument.getDefaultFileId());
            FileDocItem fileRef = new FileDocItem();
            fileRef.setDocVersion(docVersion);
            fileRef.setFileId(fileId);
            fileRef.setFilename(filename);
            fileRef.setDefaultFileId(isDefaultFile);
            fileRef.setMimeType(fileDocumentFile.getMimeType());
            fileRef.setCreatedAsImage(fileDocumentFile.isCreatedAsImage());

            docRepository.saveFileReference(fileRef);

            saveFileDocumentFile(fileDocument.getVersionRef(), fileDocumentFile, fileId);
        }

        DocumentMapper.deleteOtherFileDocumentFiles(fileDocument);
    }

    private String truncateFilename(String filename, int length) {
        String truncatedFilename = StringUtils.left(filename, length);
        String extensions = getExtensionsFromFilename(filename);
        if (extensions.length() > length) {
            return truncatedFilename;
        }
        String basename = StringUtils.chomp(filename, extensions);
        String truncatedBasename = StringUtils.substring(basename, 0, length - extensions.length());
        truncatedFilename = truncatedBasename + extensions;
        return truncatedFilename;
    }

    private String getExtensionsFromFilename(String filename) {
        String extensions = "";
        Matcher matcher = Pattern.compile("(?:\\.\\w+)+$").matcher(filename);
        if (matcher.find()) {
            extensions = matcher.group();
        }
        return extensions;
    }

    public void updateTextDocumentMenus(TextDocumentDomainObject doc, UserDomainObject user) {
        DocVersionRef docVersionRef = doc.getVersionRef();

        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());

        menuRepository.deleteByDocVersion(docVersion);

        for (Map.Entry<Integer, MenuDomainObject> entry : doc.getMenus().entrySet()) {
            updateTextDocumentMenu(TextDocMenuContainer.of(docVersionRef, entry.getKey(), entry.getValue()), user);
        }
    }

    public void updateTextDocumentMenu(TextDocMenuContainer menuWrapper, UserDomainObject user) {
        DocVersion docVersion = docVersionRepository.findByDocIdAndNo(menuWrapper.getDocId(), menuWrapper.getDocVersionNo());

        MenuDomainObject menu = menuWrapper.getMenu();
        Menu ormMenu = new Menu();
        Map<Integer, MenuItem> ormItems = new HashMap<>();

        for (Map.Entry<Integer, MenuItemDomainObject> e : menu.getItemsMap().entrySet()) {
            MenuItem ormItem = new MenuItem();
            ormItem.setSortKey(e.getValue().getSortKey());
            ormItem.setTreeSortIndex(e.getValue().getTreeSortIndex());
            ormItems.put(e.getKey(), ormItem);
        }

        ormMenu.setDocVersion(docVersion);
        ormMenu.setNo(menuWrapper.getMenuNo());
        ormMenu.setSortOrder(menu.getSortOrder());
        ormMenu.setItems(ormItems);

        menuRepository.save(ormMenu);

        // fixme: history
        // TextDocMenuHistory menuHistory = new TextDocMenuHistory(menu, user);
        // textDocRepository.saveMenuHistory(menuHistory);
    }
}