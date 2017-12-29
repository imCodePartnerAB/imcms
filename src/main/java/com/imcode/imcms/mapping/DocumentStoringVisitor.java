package com.imcode.imcms.mapping;

import com.imcode.imcms.api.DocumentVersion;
import com.imcode.imcms.mapping.container.VersionRef;
import com.imcode.imcms.mapping.jpa.doc.DocRepository;
import com.imcode.imcms.mapping.jpa.doc.VersionRepository;
import com.imcode.imcms.persistence.entity.DocumentFile;
import com.imcode.imcms.persistence.entity.Version;
import com.imcode.imcms.persistence.repository.LanguageRepository;
import imcode.server.Imcms;
import imcode.server.document.DocumentVisitor;
import imcode.server.document.FileDocumentDomainObject;
import imcode.util.io.FileInputStreamSource;
import imcode.util.io.FileUtility;
import imcode.util.io.InputStreamSource;
import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
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

    private static final int FILE_BUFFER_LENGTH = 2048;
    private static final int DB_FIELD_MAX_LENGTH__FILENAME = 255;
    protected VersionRepository versionRepository;
    protected LanguageRepository languageRepository;
    DocRepository docRepository;
    TextDocumentContentSaver textDocumentContentSaver;

    DocumentStoringVisitor(DocRepository docRepository,
                           VersionRepository versionRepository,
                           LanguageRepository languageRepository,
                           TextDocumentContentSaver textDocumentContentSaver) {

        this.docRepository = docRepository;
        this.versionRepository = versionRepository;
        this.languageRepository = languageRepository;
        this.textDocumentContentSaver = textDocumentContentSaver;
    }

    /**
     * Returns file for FileDocumentFile.
     */
    public static File getFileForFileDocumentFile(VersionRef versionRef, String fileId) {
        File filePath = Imcms.getServices().getConfig().getFilePath();
        String filename = getFilenameForFileDocumentFile(versionRef, fileId);

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
     * @return FileDocumentFile filename
     */
    public static String getFilenameForFileDocumentFile(VersionRef versionRef, String fileId) {
        int docId = versionRef.getDocId();
        int docVersionNo = versionRef.getNo();

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
     * Saves (possibly rewrites) file if its InputStreamSource has been changed.
     */
    protected void saveFileDocumentFile(VersionRef versionRef, FileDocumentDomainObject.FileDocumentFile fileDocumentFile,
                                        String fileId) {
        try {
            InputStreamSource inputStreamSource = fileDocumentFile.getInputStreamSource();
            InputStream in;
            try {
                in = inputStreamSource.getInputStream();
            } catch (FileNotFoundException e) {
                throw new UnhandledException("The file for filedocument " + versionRef
                        + " has disappeared.", e);
            }
            if (null == in) {
                return;
            }

            File file = getFileForFileDocumentFile(versionRef, fileId);

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
     * Saves or updates file document
     */
    public void visitFileDocument(FileDocumentDomainObject fileDocument) {
        docRepository.deleteFileDocContent(fileDocument.getRef());

        Version version = versionRepository.findByDocIdAndNo(fileDocument.getId(), fileDocument.getVersionNo());

        for (Map.Entry<String, FileDocumentDomainObject.FileDocumentFile> entry : fileDocument.getFiles().entrySet()) {
            String fileId = entry.getKey();
            FileDocumentDomainObject.FileDocumentFile fileDocumentFile = entry.getValue();

            String filename = fileDocumentFile.getFilename();
            if (filename.length() > DB_FIELD_MAX_LENGTH__FILENAME) {
                filename = truncateFilename(filename, DB_FIELD_MAX_LENGTH__FILENAME);
            }

            boolean isDefaultFile = fileId.equals(fileDocument.getDefaultFileId());
            DocumentFile documentFile = new DocumentFile();
            documentFile.setVersion(version);
            documentFile.setFileId(fileId);
            documentFile.setFilename(filename);
            documentFile.setDefaultFileId(isDefaultFile);
            documentFile.setMimeType(fileDocumentFile.getMimeType());
            documentFile.setCreatedAsImage(fileDocumentFile.isCreatedAsImage());

            docRepository.saveFileDocFile(documentFile);

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
        String basename = StringUtils.removeEnd(filename, extensions);
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