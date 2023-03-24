package com.imcode.imcms.domain.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.dto.UberDocumentDTO;
import com.imcode.imcms.domain.dto.BasicImportDocumentInfoDTO;
import com.imcode.imcms.domain.dto.ImportDocumentDTO;
import com.imcode.imcms.domain.dto.ImportProgress;
import com.imcode.imcms.domain.factory.FileImportMapper;
import com.imcode.imcms.domain.factory.ImageImportMapper;
import com.imcode.imcms.domain.factory.MenuImportMapper;
import com.imcode.imcms.domain.factory.TextImportMapper;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.ImportDocumentStatus;
import com.imcode.imcms.model.Language;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.imcode.imcms.model.ImportDocumentStatus.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class DocumentImporter {
	@Value("/WEB-INF/import/")
	private String importFolderName;
	private Path importFolderPath;
	private final ServletContext servletContext;
	private final ObjectMapper mapper;
	private final LanguageService languageService;
	private final TextImportMapper textImportMapper;
	private final MenuImportMapper menuImportMapper;
	private final FileImportMapper fileImportMapper;
	private final ImageImportMapper imageImportMapper;
	private final ImportProgress progress = new ImportProgress();
	private final DelegatingByTypeDocumentService documentService;
	private final BiConsumer<ImportDocumentDTO, Document> updateFromImported;
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;
	private final Function<ImportDocumentDTO, UberDocumentDTO> importDocumentToDocument;
	private final ExecutorService importExecutor = Executors.newSingleThreadExecutor();
	private Future importFuture = CompletableFuture.completedFuture(null);

	@PostConstruct
	private void init() throws IOException {
		importFolderPath = Path.of(servletContext.getRealPath("/"), importFolderName);
		if (!Files.exists(importFolderPath)) {
			Files.createDirectory(importFolderPath);
		}
	}

	public void importDocuments(int[] importDocsId) {
		importFuture = importExecutor.submit(() -> {
			try {
				progress.setTotalSize(importDocsId.length);
				for (int importDocId : importDocsId) {
					importDocument(importDocId);
					progress.increment();
				}
			} finally {
				progress.reset();
			}
		});
	}

	public boolean isImportDone() {
		return importFuture.isDone();
	}

	public ImportProgress getImportProgress() {
		return progress;
	}

	private void importDocument(int importDocId) {
		final BasicImportDocumentInfoDTO basicImportDocument = basicImportDocumentInfoService.getById(importDocId)
				.orElseThrow(() -> new NoSuchElementException(String.format("There is no import document with importDocId: %s", importDocId)));

		final ImportDocumentStatus status = basicImportDocument.getStatus();
		if (status.equals(IMPORTED) || status.equals(SKIP)) {
			log.info(String.format("Document skipped because of status: %s, id: %d", status, basicImportDocument.getId()));
			return;
		}

		try (final InputStream inputStream = Files.newInputStream(importFolderPath.resolve(importDocId + ".json"));) {
			final ImportDocumentDTO importDocument = mapper.readValue(inputStream, ImportDocumentDTO.class);

			Document document;
			if (status.equals(UPDATE)) {
				document = documentService.get(basicImportDocument.getMetaId());
				updateFromImported.accept(importDocument, document);
			} else {
				document = importDocumentToDocument.apply(importDocument);
			}

			document = documentService.save(document);
			final Integer metaId = document.getId();

			importDocumentContent(metaId, importDocument);
			documentService.publishDocument(metaId, Imcms.getUser().getId());

			basicImportDocument.setMetaId(metaId);
			basicImportDocument.setStatus(IMPORTED);

			if (basicImportDocument.getMetaId() == null) {
				log.info(String.format("Document successfully imported, importDocId: %s, docId: %s", importDocId, metaId));
			} else {
				log.info(String.format("Document successfully updated, importDocId: %s, docId: %s", importDocId, metaId));
			}

		} catch (Exception e) {
			basicImportDocument.setStatus(FAILED);
			log.error(String.format("Import failed, importDocId: %s", importDocId), e);
		}

		basicImportDocumentInfoService.save(basicImportDocument);
	}

	private void importDocumentContent(int metaId, ImportDocumentDTO importDocument) {
		final Language language = languageService.findByCode(LanguageMapper.convert639_2to639_1(importDocument.getDefaultLanguage()));

		textImportMapper.mapAndSave(importDocument.getId(), metaId, language, importDocument.getTexts());
		menuImportMapper.mapAndSave(importDocument.getId(), metaId, importDocument.getMenus());
	}
}
