package com.imcode.imcms.domain.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.document.DocumentTypeDomainObject;
import imcode.server.document.FileDocumentDomainObject;
import imcode.server.document.textdocument.ImageDomainObject;
import imcode.server.document.textdocument.ImageSource;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import imcode.server.user.UserDomainObject;
import imcode.util.io.InputStreamSource;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@Log4j2
public class DocumentExportService {
	private static final String SUMMARY_FILENAME = "summary.json";
	private static final String EXPORT_ZIP_FILENAME = "export.zip";
	private static final String DOCUMENTS_ZIP_PREFIX = "documents/";
	private static final String IMAGES_ZIP_PREFIX = "images/";
	private static final String FILES_ZIP_PREFIX = "files/";

	private final Path exportFolder;
	private final ImcmsServices imcmsServices;
	private final ObjectMapper mapper = new ObjectMapper()
			.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
			.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
			.enable(SerializationFeature.INDENT_OUTPUT)
			.setDefaultPrettyPrinter(new DefaultPrettyPrinter());

	private final List<String> exportedImages = new ArrayList<>();
	private final List<String> exportedFiles = new ArrayList<>();

	@SneakyThrows
	public DocumentExportService(ImcmsServices imcmsServices) {
		this.exportFolder = Paths.get(Imcms.getPath().getAbsolutePath(), "/WEB-INF/export/");
		this.imcmsServices = imcmsServices;

		if (!Files.exists(exportFolder)) {
			Files.createDirectories(exportFolder);
		}
	}

	public void exportDocuments(int start, int end, boolean skipExported, boolean exportImages, boolean exportFiles, UserDomainObject user) {
		final DocumentMapper documentMapper = imcmsServices.getDocumentMapper();
		final IntRange range = new IntRange(start, end);
		final DocumentExportHistory history = new DocumentExportHistory(range);
		final Iterator iterator = documentMapper.getDocumentsIteratorInRange(range);

		exportDocuments(iterator, skipExported, exportImages, exportFiles, history, user);
	}

	public void exportDocuments(Integer[] ids, boolean skipExported, boolean exportImages, boolean exportFiles, UserDomainObject user) {
		final DocumentMapper documentMapper = imcmsServices.getDocumentMapper();
		final DocumentExportHistory history = new DocumentExportHistory(ids);
		final Iterator iterator = documentMapper.getDocumentsIteratorIn(ids);

		exportDocuments(iterator, skipExported, exportImages, exportFiles, history, user);
	}

	private void exportDocuments(Iterator iterator, boolean skipExported, boolean exportImages, boolean exportFiles, DocumentExportHistory history, UserDomainObject user) {
		final DocumentMapper documentMapper = imcmsServices.getDocumentMapper();

		try (final OutputStream fileOutputStream = Files.newOutputStream(exportFolder.resolve(EXPORT_ZIP_FILENAME));
		     final ZipOutputStream zip = new ZipOutputStream(fileOutputStream);) {

			while (iterator.hasNext()) {
				final DocumentDomainObject document = (DocumentDomainObject) iterator.next();
				final Integer docId = document.getId();
				final String type = document.getDocumentType().getName().toLocalizedString("eng");
				DocumentExportHistory.ExportStatus status;

				try {
					if (!document.isPublished() || !document.isExportAllowed() || (document.isExported() && skipExported)) {
						status = DocumentExportHistory.ExportStatus.SKIPPED;
					} else {
						exportDocument(document, zip, type);
						if (exportImages) exportImages(document, zip);
						if (exportFiles) exportFiles(document, zip);

						documentMapper.markDocumentAsExported(docId, user);
						status = DocumentExportHistory.ExportStatus.SUCCESS;
					}

				} catch (IOException e) {
					status = DocumentExportHistory.ExportStatus.FAILED;
					log.error(String.format("Cannot export document with id = %s, type = %s", docId, type), e);
				}

				history.add(docId, status);
				log.info(String.format("Document with 'id = %s', 'type = %s', 'exported with status = %s'", docId, type, status));
			}

			writeHistoryToSummaryFile(history);
		} catch (IOException e) {
			log.error("Documents export failed", e);
			throw new RuntimeException("Documents export failed", e);
		} finally {
			exportedImages.clear();
			exportedFiles.clear();
		}
	}

	private void exportDocument(DocumentDomainObject document, ZipOutputStream zip, String type) throws IOException {
		final ZipArchiveEntry zipEntry = new ZipArchiveEntry(document.getId() + ".json");
		zipEntry.setComment(type);
		zip.putNextEntry(zipEntry);
		mapper.writeValue(zip, document);
	}

	private void exportImages(DocumentDomainObject document, ZipOutputStream zip) throws IOException {
		if (!document.getDocumentType().equals(DocumentTypeDomainObject.TEXT)) return;

		for (Map.Entry<Integer, ImageDomainObject> entry : ((TextDocumentDomainObject) document).getImages().entrySet()) {
			final ImageDomainObject image = entry.getValue();
			final ImageSource imageSource = image.getSource();
			final String path = imageSource.toStorageString();

			if (StringUtils.isNotBlank(path) && !exportedImages.contains(path)) {
				final ZipArchiveEntry zipEntry = new ZipArchiveEntry(IMAGES_ZIP_PREFIX + path);
				zip.putNextEntry(zipEntry);

				writeFileToZip(zip, imageSource.getInputStreamSource());
				exportedImages.add(path);
			}
		}
	}

	private void exportFiles(DocumentDomainObject document, ZipOutputStream zip) throws IOException {
		if (!document.getDocumentType().equals(DocumentTypeDomainObject.FILE)) return;

		final Map files = ((FileDocumentDomainObject) document).getFiles();
		for (Object key : files.keySet()) {
			final FileDocumentDomainObject.FileDocumentFile file = (FileDocumentDomainObject.FileDocumentFile) files.get(key);
			final String filename = file.getFilename();

			if (StringUtils.isNotBlank(filename) && !exportedFiles.contains(filename)) {
				final ZipArchiveEntry zipEntry = new ZipArchiveEntry(FILES_ZIP_PREFIX + filename);

				zip.putNextEntry(zipEntry);
				writeFileToZip(zip, file.getInputStreamSource());
				exportedFiles.add(filename);
			}
		}
	}

	private void writeFileToZip(ZipOutputStream zip, InputStreamSource source) throws IOException {
		final InputStream inputStream = source.getInputStream();
		byte[] bytes = new byte[1024];
		int length;
		while ((length = inputStream.read(bytes)) >= 0) {
			zip.write(bytes, 0, length);
		}
		inputStream.close();
	}

	private void writeHistoryToSummaryFile(DocumentExportHistory history) throws IOException {
		mapper.writeValue(exportFolder.resolve(SUMMARY_FILENAME).toFile(), history);
	}

	public Path getExportZipPath() {
		return exportFolder.resolve(EXPORT_ZIP_FILENAME);
	}

	public DocumentExportHistory getDocumentExportHistory() throws IOException {
		return mapper.readValue(exportFolder.resolve(SUMMARY_FILENAME).toFile(), DocumentExportHistory.class);
	}
}
