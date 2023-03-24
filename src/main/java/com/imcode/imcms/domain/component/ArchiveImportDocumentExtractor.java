package com.imcode.imcms.domain.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.domain.dto.ImportDocumentDTO;
import com.imcode.imcms.domain.dto.ImportProgress;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import com.imcode.imcms.domain.service.ImportDocumentReferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.imcode.imcms.model.ImportDocumentStatus.IMPORT;

@Log4j2
@Component
@RequiredArgsConstructor
public class ArchiveImportDocumentExtractor {
	@Value("/WEB-INF/import/")
	private String importFolderName;
	private Path importFolderPath;
	private final ObjectMapper mapper;
	private final ServletContext servletContext;
	private final ImportProgress extractionProgress = new ImportProgress();
	private final ImportDocumentReferenceService importDocumentReferenceService;
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;
	private final ExecutorService zipExtractExecutor = Executors.newSingleThreadExecutor();
	private Future zipExtractFuture = CompletableFuture.completedFuture(null);

	@PostConstruct
	private void init() throws IOException {
		importFolderPath = Path.of(servletContext.getRealPath("/"), importFolderName);
		if (!Files.exists(importFolderPath)) {
			Files.createDirectory(importFolderPath);
		}
	}

	public void extract(MultipartFile file) {
		if (!zipExtractFuture.isDone()) {
			return;
		}

		final Path zipPath = saveZipFile(file);

		zipExtractFuture = zipExtractExecutor.submit(() -> {
			try (final ZipFile zipFile = verifyAndGetZip(zipPath)) {

				extractionProgress.setTotalSize(zipFile.size());
				final Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					final ZipEntry zipEntry = entries.nextElement();

					saveZipEntry(zipFile, zipEntry);
					createReferences(zipFile, zipEntry);
					createBasicDocumentInfo(zipEntry);

					extractionProgress.increment();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				extractionProgress.reset();
			}
		});
	}

	public ImportProgress getProgress() {
		return extractionProgress;
	}

	public boolean isDone() {
		return zipExtractFuture.isDone();
	}

	private Path saveZipFile(MultipartFile file) {
		try {
			final String filename = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now());
			final Path localZipFilePath = importFolderPath.resolve(filename + ".zip");

			file.transferTo(localZipFilePath);

			return localZipFilePath;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ZipFile verifyAndGetZip(Path localZipFilePath) throws IOException {
		final ZipFile zipFile = new ZipFile(localZipFilePath.toFile());

		if (zipFile.size() == 0) {
			zipFile.close();
			throw new RuntimeException("Uploaded zip is empty!");
		}

		return zipFile;
	}

	private void saveZipEntry(ZipFile zipFile, ZipEntry zipEntry) {
		final Path filePath = importFolderPath.resolve(zipEntry.getName());

		try (final FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
		     final InputStream inputStream = zipFile.getInputStream(zipEntry)) {

			int length;
			final byte[] buffer = new byte[4096];
			while ((length = inputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, length);
			}

		} catch (IOException e) {
			throw new RuntimeException(String.format("Error saving zip entry with name: %s, path: %s", zipEntry.getName(), filePath), e);
		}
	}

	private void createReferences(ZipFile zipFile, ZipEntry zipEntry) {
		try (final InputStream inputStream = zipFile.getInputStream(zipEntry)) {
			final ImportDocumentDTO importDocument = mapper.readValue(inputStream, ImportDocumentDTO.class);

			importDocumentReferenceService.createReferences(importDocument);
		} catch (IOException e) {
			log.error(String.format("Cannot read json to ImportDocumentDTO, zipEntryName: %s", zipEntry.getName()), e);
		} catch (Exception e) {
			log.error(e);
		}
	}

	private BasicImportDocumentInfoDTO createBasicDocumentInfo(ZipEntry zipEntry) {
		final int importDocId = Integer.parseInt(FilenameUtils.removeExtension(zipEntry.getName()));

		return basicImportDocumentInfoService
				.getById(importDocId)
				.orElseGet(() -> basicImportDocumentInfoService.create(importDocId, IMPORT));
	}
}
