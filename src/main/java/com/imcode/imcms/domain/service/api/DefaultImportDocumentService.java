package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.component.ArchiveImportDocumentExtractor;
import com.imcode.imcms.domain.component.DocumentImporter;
import com.imcode.imcms.domain.dto.ImportProgress;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import com.imcode.imcms.domain.service.CommonContentService;
import com.imcode.imcms.domain.service.ImportDocumentService;
import com.imcode.imcms.domain.service.TextService;
import com.imcode.imcms.model.Text;
import imcode.server.Imcms;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

@Log4j2
@Service
@RequiredArgsConstructor
public class DefaultImportDocumentService implements ImportDocumentService {

	@Value("/WEB-INF/import/")
	private String importFolderName;
	private Path importFolderPath;
	private final ServletContext servletContext;
	private final TextService textService;
	private final CommonContentService commonContentService;
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;
	private final ArchiveImportDocumentExtractor archiveImportDocumentExtractor;
	private final DocumentImporter documentImporter;

	@PostConstruct
	private void init() throws IOException {
		importFolderPath = Path.of(servletContext.getRealPath("/"), importFolderName);
		if (!Files.exists(importFolderPath)) {
			Files.createDirectory(importFolderPath);
		}
	}

	@Override
	public void extractAndSave(MultipartFile file) {
		archiveImportDocumentExtractor.extract(file);
	}

	@Override
	public boolean isExtractingDone() {
		return archiveImportDocumentExtractor.isDone();
	}

	@Override
	public ImportProgress getExtractionProgress() {
		return archiveImportDocumentExtractor.getProgress();
	}

	@Override
	public boolean isImportingDone() {
		return documentImporter.isImportDone();
	}

	@Override
	public void importDocuments(int startId, int endId) {
		final int[] availableImportDocumentIds = getAvailableImportDocumentIdsForImport(startId, endId);

		documentImporter.importDocuments(availableImportDocumentIds);
	}

	@Override
	public ImportProgress getImportingProgress() {
		return documentImporter.getImportProgress();
	}


	@Override
	public void removeAliases(int startId, int endId) {
		final int[] importedDocumentIds = getImportedDocumentIds(startId, endId);
		for (int importDocId : importedDocumentIds) {
			removeAlias(importDocId);
		}
	}

	@Override
	public void removeAlias(int importDocId) {
		final String alias = "import/" + importDocId;

		commonContentService.removeAlias(alias);
		Imcms.getServices().getDocumentMapper().invalidateDocument(basicImportDocumentInfoService.toMetaId(importDocId));
		log.info(String.format("Alias: %s in document with id(rb4): %d has been removed!", alias, importDocId));

	}

	@Override
	public void replaceAliases(int startId, int endId) {
		final int[] importedDocumentIds = getImportedDocumentIds(startId, endId);

		for (int importDocId : importedDocumentIds) {
			final String alias = "/import/" + importDocId;
			final int metaId = basicImportDocumentInfoService.toMetaId(importDocId);

			final List<Text> textsContainingAlias = textService.getTextsContaining(alias);
			textsContainingAlias.forEach(text -> {
				final String modifiedText = text.getText().replaceAll(alias, '/' + Integer.toString(metaId));
				text.setText(modifiedText);

				textService.save(text);
			});

			removeAlias(importDocId);
		}

	}

	private int[] getAvailableImportDocumentIdsForImport(int startId, int endId) {
		return IntStream.rangeClosed(startId, endId)
				.filter(id -> Files.exists(importFolderPath.resolve(id + ".json")))
				.toArray();
	}

	private int[] getImportedDocumentIds(int startId, int endId) {
		return IntStream.rangeClosed(startId, endId)
				.filter(id -> basicImportDocumentInfoService.exists(id) && basicImportDocumentInfoService.isImported(id))
				.toArray();
	}

}
