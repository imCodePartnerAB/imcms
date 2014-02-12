package com.imcode.imcms.mapping;

import com.imcode.imcms.api.*;
import com.imcode.imcms.dao.*;
import com.imcode.imcms.dao.TextDocDao;
import com.imcode.imcms.mapping.orm.*;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.textdocument.*;
import imcode.server.user.UserDomainObject;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;

import org.springframework.transaction.annotation.Transactional;

/**
 * This class is not a part of public API. It's methods must not be called directly.
 *
 * @see com.imcode.imcms.mapping.DocumentSaver
 */

//hibernate.batch_update_versioned
public class DocumentStoringVisitor extends DocumentVisitor {

    protected ImcmsServices services;

    private static final int FILE_BUFFER_LENGTH = 2048;
    private static final int DB_FIELD_MAX_LENGTH__FILENAME = 255;

    protected MetaDao metaDao;
    protected TextDocDao textDocDao;
    protected DocVersionDao docVersionDao;
    protected DocLanguageDao docLanguageDao;

    public DocumentStoringVisitor(ImcmsServices services) {
        this.services = services;
        this.metaDao = services.getManagedBean(MetaDao.class);
        this.textDocDao  = services.getManagedBean(TextDocDao.class);
        this.docVersionDao = services.getManagedBean(DocVersionDao.class);
        this.docLanguageDao = services.getManagedBean(DocLanguageDao.class);
    }

    /**
     * Saves (possibly rewrites) file if its InputStreamSource has been changed.
     *
     * @param fileDocumentFile
     * @param fileId
     */
    protected void saveFileDocumentFile(DocRef docRef, FileDocumentDomainObject.FileDocumentFile fileDocumentFile,
                                        String fileId) {
        try {
            InputStreamSource inputStreamSource = fileDocumentFile.getInputStreamSource();
            InputStream in;
            try {
                in = inputStreamSource.getInputStream();
            } catch (FileNotFoundException e) {
                throw new UnhandledException("The file for filedocument " + docRef
                        + " has disappeared.", e);
            }
            if (null == in) {
                return;
            }

            File file = getFileForFileDocumentFile(docRef, fileId);

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
    public static File getFileForFileDocumentFile(DocRef docRef, String fileId) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        String filename = getFilenameForFileDocumentFile(docRef, fileId);

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
    public static String getFilenameForFileDocumentFile(DocRef docRef, String fileId) {
        int docId = docRef.getDocId();
        int docVersionNo = docRef.getDocVersionNo();

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
        DocVersion docVersion = docVersionDao.getVersion(textDocument.getId(), textDocument.getVersionNo());
        DocLanguage docLanguage = docLanguageDao.getByCode(textDocument.getLanguage().getCode());

        textDocDao.deleteTexts(textDocument.getRef());
        textDocDao.flush();

        for (Map.Entry<Integer, TextDomainObject> e : textDocument.getTexts().entrySet()) {
            saveTextDocumentText(TextDocItemRef.of(textDocument.getRef(), e.getKey(), e.getValue()), user);
        }

        for (Map.Entry<TextDocumentDomainObject.LoopItemKey, TextDomainObject> e : textDocument.getLoopTexts().entrySet()) {
            saveTextDocumentText(TextDocItemRef.of(textDocument.getRef(), e.getKey().itemNo, e.getValue()), user);
        }
    }

    /**
     * @param textDocument
     * @param user
     */
    public void updateTextDocumentContentLoops(TextDocumentDomainObject textDocument, UserDomainObject user) {
        textDocDao.deleteLoops(textDocument.getRef());
        textDocDao.flush();

        for (Map.Entry<Integer, ContentLoop> e : textDocument.getContentLoops().entrySet()) {
            ContentLoop loop = e.getValue();
            TextDocLoop ormLoop = new TextDocLoop();
            List<TextDocLoopItem> ormItems = new LinkedList<>();

            for (Content content : loop.getItems()) {
                ormItems.add(new TextDocLoopItem(content.getNo(), content.isEnabled()));
            }

            ormLoop.setNo(e.getKey());
            ormLoop.setItems(ormItems);

            textDocDao.saveLoop(ormLoop);
        }
    }


    @Transactional
    public void updateDocumentAppearance(DocumentDomainObject doc, UserDomainObject user) {
        DocRef docRef = doc.getRef();
        DocLanguage docLanguage = docLanguageDao.getByCode(docRef.getDocLanguage().getCode());

        metaDao.deleteAppearance(docRef);

        DocumentAppearance appearance = doc.getAppearance();
        DocAppearance ormAppearance = new DocAppearance();
        ormAppearance.setDocId(docRef.getDocId());
        ormAppearance.setLanguage(docLanguage);
        ormAppearance.setHeadline(appearance.getHeadline());
        ormAppearance.setMenuText(appearance.getMenuText());
        ormAppearance.setMenuImageURL(appearance.getMenuImageURL());

        metaDao.saveAppearance(ormAppearance);
    }


    /**
     * Saves text document's text.
     *
     * @param textRef
     * @param user
     */
    @Transactional
    public void saveTextDocumentText(TextDocItemRef<TextDomainObject> textRef, UserDomainObject user) {
        DocVersion docVersion = docVersionDao.getVersion(textRef.getDocRef().getDocId(), textRef.getDocRef().getDocVersionNo());
        DocLanguage docLanguage = docLanguageDao.getByCode(textRef.getDocRef().getDocLanguage().getCode());

        TextDomainObject text = textRef.getItem();
        TextDocText ormText = new TextDocText();

        ormText.setDocLanguage(docLanguage);
        ormText.setDocVersion(docVersion);
        ormText.setText(text.getText());
        ormText.setType(TextDocType.values()[text.getType()]);

        ContentLoopItemRef loopItemRef = text.getContentLoopRef();

        if (loopItemRef != null) {
            ormText.setLoopItemRef(new TextDocLoopItemRef(loopItemRef.getLoopNo(), loopItemRef.getContentNo()));
        }

        textDocDao.saveText(ormText);

        // fixme: implement history
        //TextDocTextHistory textHistory = new TextDocTextHistory(textRef, user);
        //textDao.saveTextHistory(textHistory);
    }

    /**
     * Saves text document's image.
     */
    @Transactional
    public void saveTextDocumentImage(ImageDomainObject image, UserDomainObject user) {
        image.setUrl(image.getSource().toStorageString());
        image.setType(image.getSource().getTypeId());

       // fixme: implement
       // textDocDao.saveImage(image);

       // TextDocImageHistory textDocImageHistory = new TextDocImageHistory(image, user);
       // textDocDao.saveImageHistory(textDocImageHistory);
    }


    @Transactional
    // fixme: implement
    void updateTextDocumentImages(TextDocumentDomainObject doc, UserDomainObject user) {
//        DocLanguage language = doc.getLanguage();
//
//        textDocDao.deleteImages(doc.getRef(), language);
//        textDocDao.flush();
//
//        for (ImageDomainObject image : doc.getImages().values()) {
//            image.setId(null);
//            image.setDocRef(doc.getRef());
//            image.setLanguage(language);
//
//            saveTextDocumentImage(image, user);
//        }
//
//        for (ImageDomainObject image : doc.getLoopImages().values()) {
//            image.setId(null);
//            image.setDocRef(doc.getRef());
//            image.setLanguage(language);
//
//            saveTextDocumentImage(image, user);
//        }
    }


    @Transactional
    public void updateTextDocumentIncludes(TextDocumentDomainObject doc) {
        int docId = doc.getId();

        metaDao.deleteIncludes(docId);

        for (Map.Entry<Integer, Integer> entry : doc.getIncludesMap().entrySet()) {
            Include include = new Include();
            include.setId(null);
            include.setDocId(docId);
            include.setIndex(entry.getKey());
            include.setIncludedDocumentId(entry.getValue());

            metaDao.saveInclude(include);
        }
    }


    @Transactional
    public void updateTextDocumentTemplateNames(TextDocumentDomainObject textDocument, UserDomainObject user) {
        int docId = textDocument.getId();

        TemplateNames templateNames = textDocument.getTemplateNames();

        templateNames.setDocId(docId);

        metaDao.saveTemplateNames(templateNames);
    }


    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        metaDao.deleteFileReferences(fileDocument.getRef());

        DocVersion docVersion = docVersionDao.getVersion(fileDocument.getRef().getDocId(), fileDocument.getRef().getDocVersionNo());

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

            metaDao.saveFileReference(fileRef);

            saveFileDocumentFile(fileDocument.getRef(), fileDocumentFile, fileId);
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
        DocRef docRef = doc.getRef();

        textDocDao.deleteMenus(DocVersionRef.of(doc.getId(), docRef.getDocVersionNo()));

        for (Map.Entry<Integer, MenuDomainObject> entry : doc.getMenus().entrySet()) {
            updateTextDocumentMenu(TextDocItemRef.of(docRef, entry.getKey(), entry.getValue()), user);
        }
    }

    // fixme: implement
    public void updateTextDocumentMenu(TextDocItemRef<MenuDomainObject> menuRef, UserDomainObject user) {
        DocVersion docVersion = docVersionDao.getVersion(menuRef.getDocRef().getDocId(), menuRef.getDocRef().getDocVersionNo());

        MenuDomainObject menu = menuRef.getItem();
        TextDocMenu ormMenu = new TextDocMenu();
        Map<Integer, TextDocMenuItem> ormItems = new HashMap<>();

        for (Map.Entry<Integer, MenuItemDomainObject> e : menu.getItemsMap().entrySet()) {
            TextDocMenuItem ormItem = new TextDocMenuItem();
            ormItem.setSortKey(e.getValue().getSortKey());
            ormItem.setTreeSortIndex(e.getValue().getTreeSortIndex());
            ormItems.put(e.getKey(), ormItem);
        }

        ormMenu.setDocVersion(docVersion);
        ormMenu.setNo(menuRef.getItemNo());
        ormMenu.setSortOrder(menu.getSortOrder());
        ormMenu.setItems(ormItems);

        textDocDao.saveMenu(ormMenu);

        // fixme: save history
        // TextDocMenuHistory menuHistory = new TextDocMenuHistory(menu, user);
        // textDocDao.saveMenuHistory(menuHistory);
    }
}