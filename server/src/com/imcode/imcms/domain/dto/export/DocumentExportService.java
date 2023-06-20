package com.imcode.imcms.domain.dto.export;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.imcode.imcms.mapping.DocumentMapper;
import imcode.server.Imcms;
import imcode.server.ImcmsServices;
import imcode.server.document.DocumentDomainObject;
import imcode.server.user.UserDomainObject;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.lang.math.IntRange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.zip.ZipOutputStream;

@Log4j2
public class DocumentExportService {
	private static final String SUMMARY_FILENAME = "summary.json";
	private static final String EXPORT_ZIP_FILENAME = "export.zip";
	private final Path exportFolder;
	private final ImcmsServices imcmsServices;
	private final ObjectMapper mapper = new ObjectMapper()
			.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
			.enable(SerializationFeature.INDENT_OUTPUT)
			.setDefaultPrettyPrinter(new DefaultPrettyPrinter());

	@SneakyThrows
	public DocumentExportService(ImcmsServices imcmsServices) {
		this.exportFolder = Paths.get(Imcms.getPath().getAbsolutePath(), "/WEB-INF/export/");
		this.imcmsServices = imcmsServices;

		if (!Files.exists(exportFolder)) {
			Files.createDirectories(exportFolder);
		}
	}

	public void exportDocuments(int start, int end, boolean skipExported, UserDomainObject user) {
		final DocumentMapper documentMapper = imcmsServices.getDocumentMapper();
		final IntRange range = new IntRange(start, end);
		final DocumentExportHistory history = new DocumentExportHistory(range);
		final Iterator iterator = documentMapper.getDocumentsIterator(range);

		exportDocuments(iterator, skipExported, history, user);
	}

	public void exportDocuments(Integer[] ids, boolean skipExported, UserDomainObject user) {
		final DocumentMapper documentMapper = imcmsServices.getDocumentMapper();
		final DocumentExportHistory history = new DocumentExportHistory(ids);
		final Iterator iterator = documentMapper.getDocumentsIterator(ids);

		exportDocuments(iterator, skipExported, history, user);
	}

	private void exportDocuments(Iterator iterator, boolean skipExported, DocumentExportHistory history, UserDomainObject user) {
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
						final byte[] bytes = mapper.writeValueAsBytes(document);
						final ZipArchiveEntry zipEntry = new ZipArchiveEntry(docId + ".json");

						zipEntry.setComment(type);

						zip.putNextEntry(zipEntry);
						zip.write(bytes);

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
			throw new RuntimeException("Documents export failed", e);
		}
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
