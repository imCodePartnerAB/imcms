package com.imcode.imcms.mapping;

import com.imcode.imcms.api.*;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.dao.*;
import com.imcode.imcms.mapping.dao.DocLanguageDao;
import com.imcode.imcms.mapping.dao.DocVersionDao;
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
    protected DocDao docDao;
    protected DocVersionDao docVersionDao;
    protected DocLanguageDao docLanguageDao;

    protected TextDocTextDao textDocTextDao;
    protected TextDocLoopDao textDocLoopDao;
    protected TextDocMenuDao textDocMenuDao;
    protected TextDocTemplateNamesDao textDocTemplateNamesDao;
    protected TextDocIncludeDao textDocIncludeDao;
    protected DocCommonContentDao docCommonContentDao;

    public DocumentStoringVisitor(ImcmsServices services) {
        this.services = services;
        this.docDao = services.getManagedBean(DocDao.class);
        this.docVersionDao = services.getManagedBean(DocVersionDao.class);
        this.docLanguageDao = services.getManagedBean(DocLanguageDao.class);
        this.textDocTextDao = services.getManagedBean(TextDocTextDao.class);
        this.textDocLoopDao = services.getManagedBean(TextDocLoopDao.class);
        this.textDocMenuDao = services.getManagedBean(TextDocMenuDao.class);
        this.textDocTemplateNamesDao = services.getManagedBean(TextDocTemplateNamesDao.class);
        this.textDocIncludeDao = services.getManagedBean(TextDocIncludeDao.class);
        this.docCommonContentDao = services.getManagedBean(DocCommonContentDao.class);
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
        DocVersion docVersion = docVersionDao.findByDocIdAndNo(textDocument.getId(), textDocument.getVersionNo());
        DocLanguage docLanguage = docLanguageDao.findByCode(textDocument.getLanguage().getCode());

        textDocTextDao.deleteByDocVersionAndDocLanguage(docVersion, docLanguage);

        for (Map.Entry<Integer, TextDomainObject> e : textDocument.getTexts().entrySet()) {
            saveTextDocumentText(TextDocTextContainer.of(textDocument.getRef(), e.getKey(), e.getValue()), user);
        }

        for (Map.Entry<LoopItemRef, TextDomainObject> e : textDocument.getLoopTexts().entrySet()) {
            saveTextDocumentText(TextDocTextContainer.of(textDocument.getRef(), e.getKey().getItemNo(), e.getValue()), user);
        }
    }

    /**
     * @param textDocument
     * @param user
     */
    public void updateTextDocumentContentLoops(TextDocumentDomainObject textDocument, UserDomainObject user) {
        DocRef docRef = textDocument.getRef();

        DocVersion docVersion = docVersionDao.findByDocIdAndNo(docRef.getDocId(), docRef.getDocVersionNo());
        List<TextDocLoop> textDocLoops = textDocLoopDao.findByDocVersion(docVersion);
        textDocLoopDao.delete(textDocLoops);

        for (Map.Entry<Integer, Loop> loopAndNo : textDocument.getLoops().entrySet()) {
            Loop loop = loopAndNo.getValue();
            TextDocLoop ormLoop = new TextDocLoop();
            List<TextDocLoop.Entry> ormItems = new LinkedList<>();

            for (Map.Entry<Integer, Boolean> loopEntry : loop.getEntries().entrySet()) {
                ormItems.add(new TextDocLoop.Entry(loopEntry.getKey(), loopEntry.getValue()));
            }

            ormLoop.setNo(loopAndNo.getKey());
            ormLoop.setEntries(ormItems);

            textDocLoopDao.save(ormLoop);
        }
    }


    @Transactional
    public void saveDocumentCommonContent(DocumentDomainObject doc, UserDomainObject user) {
        DocLanguage ormLanguage = docLanguageDao.findByCode(doc.getLanguage().getCode());
        DocCommonContent ormDcc = docCommonContentDao.findByDocIdAndDocLanguage(doc.getId(), ormLanguage);

        if (ormDcc == null) {
            ormDcc = new DocCommonContent();
        }

        DocumentCommonContent dcc = doc.getCommonContent();

        ormDcc.setDocId(doc.getId());
        ormDcc.setDocLanguage(ormLanguage);
        ormDcc.setHeadline(dcc.getHeadline());
        ormDcc.setMenuText(dcc.getMenuText());
        ormDcc.setMenuImageURL(dcc.getMenuImageURL());

        docCommonContentDao.save(ormDcc);
    }


    /**
     * Saves text document's text.
     *
     * @param textContainer
     * @param user
     */
    @Transactional
    public void saveTextDocumentText(TextDocTextContainer textContainer, UserDomainObject user) {
        DocVersion docVersion = docVersionDao.findByDocIdAndNo(textContainer.getDocRef().getDocId(), textContainer.getDocRef().getDocVersionNo());
        DocLanguage docLanguage = docLanguageDao.findByCode(textContainer.getDocRef().getDocLanguageCode());

        TextDomainObject text = textContainer.getText();
        TextDocText ormText = new TextDocText();

        ormText.setDocLanguage(docLanguage);
        ormText.setDocVersion(docVersion);
        ormText.setText(text.getText());
        ormText.setType(TextDocTextType.values()[text.getType()]);
        LoopEntryRef loopEntryRef = textContainer.getLoopEntryRef();
        if (loopEntryRef != null) {
            ormText.setLoopEntryRef(new TextDocLoopEntryRef(loopEntryRef.getLoopNo(), loopEntryRef.getEntryNo()));
        }

        textDocTextDao.save(ormText);

        // fixme: implement history
        //TextDocTextHistory textHistory = new TextDocTextHistory(textRef, user);
        //textDao.saveTextHistory(textHistory);
    }


    /**
     * Saves text document's image.
     */
    @Transactional
    public void saveTextDocumentImage(TextDocImageContainer imageContainer, UserDomainObject user) {
        ImageDomainObject image = imageContainer.getImage();

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

        textDocIncludeDao.deleteByDocId(docId);

        for (Map.Entry<Integer, Integer> entry : doc.getIncludesMap().entrySet()) {
            TextDocInclude textDocInclude = new TextDocInclude();
            textDocInclude.setId(null);
            textDocInclude.setDocId(docId);
            textDocInclude.setNo(entry.getKey());
            textDocInclude.setIncludedDocumentId(entry.getValue());

            textDocIncludeDao.save(textDocInclude);
        }
    }


    @Transactional
    public void updateTextDocumentTemplateNames(TextDocumentDomainObject textDocument, UserDomainObject user) {
        int docId = textDocument.getId();

        TextDocTemplateNames templateNames = OrmToApi.toOrm(textDocument.getTemplateNames());

        templateNames.setDocId(docId);

        textDocTemplateNamesDao.save(templateNames);
    }


    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        docDao.deleteFileReferences(fileDocument.getRef());

        DocVersion docVersion = docVersionDao.findByDocIdAndNo(fileDocument.getRef().getDocId(), fileDocument.getRef().getDocVersionNo());

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

            docDao.saveFileReference(fileRef);

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

        DocVersion docVersion = docVersionDao.findByDocIdAndNo(docVersionRef.getDocId(), docVersionRef.getDocVersionNo());

        textDocMenuDao.deleteByDocVersion(docVersion);

        for (Map.Entry<Integer, MenuDomainObject> entry : doc.getMenus().entrySet()) {
            updateTextDocumentMenu(TextDocMenuContainer.of(docVersionRef, entry.getKey(), entry.getValue()), user);
        }
    }

    // fixme: implement
    public void updateTextDocumentMenu(TextDocMenuContainer menuWrapper, UserDomainObject user) {
        DocVersion docVersion = docVersionDao.findByDocIdAndNo(menuWrapper.getDocId(), menuWrapper.getDocVersionNo());

        MenuDomainObject menu = menuWrapper.getMenu();
        TextDocMenu ormMenu = new TextDocMenu();
        Map<Integer, TextDocMenuItem> ormItems = new HashMap<>();

        for (Map.Entry<Integer, MenuItemDomainObject> e : menu.getItemsMap().entrySet()) {
            TextDocMenuItem ormItem = new TextDocMenuItem();
            ormItem.setSortKey(e.getValue().getSortKey());
            ormItem.setTreeSortIndex(e.getValue().getTreeSortIndex());
            ormItems.put(e.getKey(), ormItem);
        }

        ormMenu.setDocVersion(docVersion);
        ormMenu.setNo(menuWrapper.getMenuNo());
        ormMenu.setSortOrder(menu.getSortOrder());
        ormMenu.setItems(ormItems);

        textDocMenuDao.save(ormMenu);

        // fixme: save history
        // TextDocMenuHistory menuHistory = new TextDocMenuHistory(menu, user);
        // textDocDao.saveMenuHistory(menuHistory);
    }
}