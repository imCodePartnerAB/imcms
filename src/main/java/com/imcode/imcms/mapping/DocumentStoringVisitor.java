package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.api.Loop;
import com.imcode.imcms.mapping.container.*;
import com.imcode.imcms.mapping.jpa.doc.*;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContent;
import com.imcode.imcms.mapping.jpa.doc.content.CommonContentRepository;
import com.imcode.imcms.mapping.jpa.doc.content.FileItem;
import com.imcode.imcms.mapping.jpa.doc.content.textdoc.*;
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
//todo: hibernate.batch_update_versioned
class DocumentStoringVisitor extends DocumentVisitor {

    protected ImcmsServices services;

    private static final int FILE_BUFFER_LENGTH = 2048;
    private static final int DB_FIELD_MAX_LENGTH__FILENAME = 255;

    protected DocRepository docRepository;
    protected DocVersionRepository docVersionRepository;
    protected LanguageRepository languageRepository;
    protected CommonContentRepository commonContentRepository;
    protected TextDocumentContentSaver textDocumentContentSaver;

    public DocumentStoringVisitor(ImcmsServices services) {
        this.services = services;
        this.docRepository = services.getManagedBean(DocRepository.class);
        this.docVersionRepository = services.getManagedBean(DocVersionRepository.class);
        this.languageRepository = services.getManagedBean(LanguageRepository.class);
        this.commonContentRepository = services.getManagedBean(CommonContentRepository.class);
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


    public void saveCommonContent(DocumentDomainObject doc, UserDomainObject user) {
        Language jpaLanguage = languageRepository.findByCode(doc.getLanguage().getCode());
        CommonContent jpaDcc = commonContentRepository.findByDocIdAndLanguage(doc.getId(), jpaLanguage);

        if (jpaDcc == null) {
            jpaDcc = new CommonContent();
        }

        DocumentCommonContent dcc = doc.getCommonContent();

        jpaDcc.setDocId(doc.getId());
        jpaDcc.setLanguage(jpaLanguage);
        jpaDcc.setHeadline(dcc.getHeadline());
        jpaDcc.setMenuText(dcc.getMenuText());
        jpaDcc.setMenuImageURL(dcc.getMenuImageURL());

        commonContentRepository.save(jpaDcc);
    }

    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        docRepository.deleteFileReferences(fileDocument.getRef());

        Version version = docVersionRepository.findByDocIdAndNo(fileDocument.getRef().getDocId(), fileDocument.getRef().getDocVersionNo());

        for (Map.Entry<String, FileDocumentDomainObject.FileDocumentFile> entry : fileDocument.getFiles().entrySet()) {
            String fileId = entry.getKey();
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = entry.getValue();

            String filename = fileDocumentFile.getFilename();
            if (filename.length() > DB_FIELD_MAX_LENGTH__FILENAME) {
                filename = truncateFilename(filename, DB_FIELD_MAX_LENGTH__FILENAME);
            }

            boolean isDefaultFile = fileId.equals(fileDocument.getDefaultFileId());
            FileItem fileRef = new FileItem();
            fileRef.setVersion(version);
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
}