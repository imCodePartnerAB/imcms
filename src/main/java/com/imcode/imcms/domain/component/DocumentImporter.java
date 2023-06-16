package com.imcode.imcms.domain.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.factory.FileImportMapper;
import com.imcode.imcms.domain.factory.ImageImportMapper;
import com.imcode.imcms.domain.factory.MenuImportMapper;
import com.imcode.imcms.domain.factory.TextImportMapper;
import com.imcode.imcms.domain.service.BasicImportDocumentInfoService;
import com.imcode.imcms.domain.service.DelegatingByTypeDocumentService;
import com.imcode.imcms.domain.service.LanguageService;
import com.imcode.imcms.mapping.DocumentMapper;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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

	private final DocumentMapper documentMapper;
	private final DocumentsCache documentsCache;

	private final LanguageService languageService;
	private final TextImportMapper textImportMapper;
	private final MenuImportMapper menuImportMapper;
	private final FileImportMapper fileImportMapper;
	private final ImageImportMapper imageImportMapper;
	private final ImportProgress progress = new ImportProgress();
	private final DelegatingByTypeDocumentService defaultDelegatingByTypeDocumentService;
	private final BiConsumer<ImportDocumentDTO, Document> updateFromImported;
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;
	private final Function<ImportDocumentDTO, UberDocumentDTO> importDocumentToDocument;
	private final ExecutorService importExecutor = Executors.newSingleThreadExecutor();
	private Future importFuture = CompletableFuture.completedFuture(null);

	final Set<Integer> importDocumentIdsUnderImporting = new HashSet<>();

	private static final String DOCUMENT_UPDATED_MSG_PATTERN = "Document updated, importDocId: {}, docId: {}";
	private static final String DOCUMENT_IMPORTED_MSG_PATTERN = "Document imported, importDocId: {}, docId: {}";

	@PostConstruct
	private void init() throws IOException {
		importFolderPath = Path.of(servletContext.getRealPath("/"), importFolderName);
		if (!Files.exists(importFolderPath)) {
			Files.createDirectory(importFolderPath);
		}
	}

	public boolean isImportDone() {
		return importFuture.isDone();
	}

	public ImportProgress getImportProgress() {
		return progress;
	}

	public void importDocuments(int[] importDocsId) {
		if (!importFuture.isDone()) {
			log.warn("Cannot start new importing process. Another one is working!");
			return;
		}

		importFuture = importExecutor.submit(() -> {
			try {
				log.info("Started importing documents thread for {} docs!", importDocsId.length);
				progress.setTotalSize(importDocsId.length);

				for (int importDocId : importDocsId) {
					importDocument(importDocId);
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				progress.reset();
				importDocumentIdsUnderImporting.clear();

				final int[] docIds = Arrays.stream(importDocsId).map(basicImportDocumentInfoService::toMetaId).filter(Objects::nonNull).toArray();
				documentMapper.invalidateDocuments(docIds);
				documentsCache.invalidateCache();

				log.info("Importing documents thread end!");
			}
		});
	}

	private void importDocument(int importDocId) {
		importDocumentIdsUnderImporting.add(importDocId);

		log.info("Trying to import document(rb4): {}", importDocId);
		final BasicImportDocumentInfoDTO basicImportDocument = basicImportDocumentInfoService.getById(importDocId).orElse(null);
		if (basicImportDocument == null) {
			log.warn("Document is not uploaded: {}", importDocId);
			return;
		}

		final ImportDocumentStatus status = basicImportDocument.getStatus();
		if (status.equals(IMPORTED) || status.equals(SKIP)) {
			log.info("Document skipped because of status: {}, id: {}", status, basicImportDocument.getId());
			return;
		}

		final Path pathToImportDocument = importFolderPath.resolve(importDocId + ".json");
		try (final InputStream inputStream = Files.newInputStream(pathToImportDocument)) {
			final ImportDocumentDTO importDocument = mapper.readValue(inputStream, ImportDocumentDTO.class);

			Document document;
			if (status.equals(UPDATE)) {
				document = defaultDelegatingByTypeDocumentService.get(basicImportDocument.getMetaId());
				updateFromImported.accept(importDocument, document);
			} else {
				document = importDocumentToDocument.apply(importDocument);
			}

			final Integer metaId = defaultDelegatingByTypeDocumentService.save(document).getId();

			if (importDocumentContent(metaId, importDocument)) {
				defaultDelegatingByTypeDocumentService.publishDocument(metaId, Imcms.getUser().getId());
				log.info("Document {} published", metaId);
			} else {
				log.warn("Document {} not published because of problems with content importing", metaId);
			}

			log.info(!status.equals(UPDATE) ? DOCUMENT_IMPORTED_MSG_PATTERN : DOCUMENT_UPDATED_MSG_PATTERN, importDocId, metaId);

			basicImportDocument.setMetaId(metaId);
			basicImportDocument.setStatus(IMPORTED);

		} catch (Exception e) {
			basicImportDocument.setStatus(FAILED);
			log.error(String.format("Import failed, importDocId: %s", importDocId), e);
		} finally {
			progress.increment();
		}

		basicImportDocumentInfoService.save(basicImportDocument);
	}

	private boolean importDocumentContent(int metaId, ImportDocumentDTO importDocument) {
		final Language language = languageService.findByCode(LanguageMapper.convert639_2to639_1(importDocument.getDefaultLanguage()));

		textImportMapper.mapAndSave(importDocument.getId(), metaId, language, importDocument.getTexts());

		importMenuDocuments(importDocument);

		return menuImportMapper.mapAndSave(metaId, importDocument.getMenus());
	}

	private void importMenuDocuments(ImportDocumentDTO importDocument) {
		log.info("Started menu importing in document {}!", importDocument.getId());
		for (ImportMenuDTO importMenu : importDocument.getMenus()) {
			log.info("Menu index: {}", importMenu.getIndex());

			for (ImportMenuItemDTO menuItem : importMenu.getMenuItems()) {
				final Integer documentId = menuItem.getDocumentId();

				if (!importDocument.getId().equals(documentId) && !importDocumentIdsUnderImporting.contains(documentId)) {
					log.info("Trying to import document from menu");
					importDocument(documentId);
				}
			}
		}
	}
}
