package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImportProgress;
import com.imcode.imcms.domain.service.ArchiveImportDocumentExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Log4j2
@Service
public class DefaultArchiveImportDocumentExtractionService implements ArchiveImportDocumentExtractionService {
	private final Path importDirectoryPath;
	private final ArchiveImportHandler archiveImportHandler;

	private final ImportProgress extractionProgress = new ImportProgress();
	private final ExecutorService archiveExtractionExecutor = Executors.newSingleThreadExecutor();
	private Future archiveExtractionFuture = CompletableFuture.completedFuture(null);

	public DefaultArchiveImportDocumentExtractionService(Path importDirectoryPath,
	                                                     ArchiveImportHandler archiveImportHandler) {
		this.importDirectoryPath = importDirectoryPath;
		this.archiveImportHandler = archiveImportHandler;
	}

	@Override
	public void extract(MultipartFile file) {
		if (!archiveExtractionFuture.isDone()) {
			log.warn("Cannot start new exporting process. Another one is working!");
			return;
		}

		final Path zipPath = saveZipFile(file);
		archiveExtractionFuture = archiveExtractionExecutor.submit(new ArchiveExtractionTask(zipPath));
	}

	@Override
	public ImportProgress getProgress() {
		return extractionProgress;
	}

	private Path saveZipFile(MultipartFile file) {
		try {
			final String filename = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss").withZone(ZoneId.systemDefault()).format(Instant.now());
			final Path localZipFilePath = importDirectoryPath.resolve(filename + ".zip");

			file.transferTo(localZipFilePath);

			return localZipFilePath;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@RequiredArgsConstructor
	private class ArchiveExtractionTask implements Runnable {
		private final Path zipPath;

		@Override
		public void run() {
			extractionProgress.reset();

			try (final ZipFile zipFile = verifyAndGetZipFile(zipPath)) {
				extractionProgress.setTotalSize(zipFile.size());

				final Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					final ZipEntry zipEntry = entries.nextElement();
					final String filename = zipEntry.getName();
					final Path filePath = importDirectoryPath.resolve(filename);

					createDirectories(filePath);
					extractEntry(filePath, filename, zipFile, zipEntry);

					if (isJson(filename)) {
						archiveImportHandler.handleDocument(filePath);
					}

					extractionProgress.increment();
				}
			} catch (Exception e) {
				extractionProgress.setError();
				log.error("Zip extraction failed", e);
			}
		}

		private ZipFile verifyAndGetZipFile(Path localZipFilePath) throws IOException {
			final ZipFile zipFile = new ZipFile(localZipFilePath.toFile());

			if (zipFile.size() == 0) {
				zipFile.close();
				throw new RuntimeException("Uploaded zip is empty!");
			}

			return zipFile;
		}

		private void createDirectories(Path filePath) {
			final Path parentDirectory = filePath.getParent();
			if (parentDirectory != null && Files.notExists(parentDirectory)) {
				try {
					Files.createDirectories(parentDirectory);
				} catch (IOException e) {
					throw new RuntimeException("Failed to create directory " + parentDirectory);
				}
			}
		}

		private void extractEntry(Path filePath, String filename, ZipFile zipFile, ZipEntry zipEntry) {
			try (final FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
			     final InputStream inputStream = zipFile.getInputStream(zipEntry)) {

				int length;
				final byte[] buffer = new byte[4096];
				while ((length = inputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, length);
				}

			} catch (IOException e) {
				throw new RuntimeException(String.format("Error saving zip entry with name: %s, path: %s", filename, filePath), e);
			}
		}

		private boolean isImage(String name) {
			return name.startsWith("images");
		}

		private boolean isFile(String name) {
			return name.startsWith("files");
		}

		private boolean isJson(String name) {
			return !isImage(name) && !isFile(name) && name.endsWith("json");
		}
	}
}
