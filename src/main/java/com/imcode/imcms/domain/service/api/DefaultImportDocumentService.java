package com.imcode.imcms.domain.service.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.component.DocumentsCache;
import com.imcode.imcms.domain.component.ImageImporter;
import com.imcode.imcms.domain.component.MenuImporter;
import com.imcode.imcms.domain.dto.*;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.mapping.DocumentMapper;
import com.imcode.imcms.model.Document;
import com.imcode.imcms.model.ImportDocumentStatus;
import com.imcode.imcms.model.Language;
import com.imcode.imcms.model.Text;
import imcode.server.Imcms;
import imcode.server.LanguageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.imcode.imcms.model.ImportDocumentStatus.*;

@Log4j2
@Service
public class DefaultImportDocumentService implements ImportDocumentService {
	private final Path importDirectoryPath;
	private final ObjectMapper mapper;
	private final TextService textService;
	private final CommonContentService commonContentService;
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;
	private final DocumentMapper documentMapper;
	private final DocumentsCache documentsCache;
	private final LanguageService languageService;
	private final MenuImporter menuImporter;
	private final ImageImporter imageImporter;
	private final DelegatingByTypeDocumentService defaultDelegatingByTypeDocumentService;
	private final BiConsumer<ImportDocumentDTO, Document> updateFromImported;
	private final Function<ImportDocumentDTO, UberDocumentDTO> importDocumentToDocument;
	private final TriFunction<ImportTextDTO, Integer, Language, Text> importTextToText;

	private final ExecutorService importExecutor = Executors.newSingleThreadExecutor();
	private Future importFuture = CompletableFuture.completedFuture(null);
	private final ImportProgress progress = new ImportProgress();

	public DefaultImportDocumentService(Path importDirectoryPath,
	                                    ObjectMapper mapper,
	                                    TextService textService,
	                                    CommonContentService commonContentService,
	                                    BasicImportDocumentInfoService basicImportDocumentInfoService,
	                                    DocumentMapper documentMapper,
	                                    DocumentsCache documentsCache,
	                                    LanguageService languageService,
	                                    MenuImporter menuImporter,
	                                    ImageImporter imageImporter,
	                                    DelegatingByTypeDocumentService defaultDelegatingByTypeDocumentService,
	                                    BiConsumer<ImportDocumentDTO, Document> updateFromImported,
	                                    Function<ImportDocumentDTO, UberDocumentDTO> importDocumentToDocument,
	                                    TriFunction<ImportTextDTO, Integer, Language, Text> importTextToText) {

		this.importDirectoryPath = importDirectoryPath;
		this.mapper = mapper;
		this.textService = textService;
		this.commonContentService = commonContentService;
		this.basicImportDocumentInfoService = basicImportDocumentInfoService;
		this.documentMapper = documentMapper;
		this.documentsCache = documentsCache;
		this.languageService = languageService;
		this.menuImporter = menuImporter;
		this.imageImporter = imageImporter;
		this.defaultDelegatingByTypeDocumentService = defaultDelegatingByTypeDocumentService;
		this.updateFromImported = updateFromImported;
		this.importDocumentToDocument = importDocumentToDocument;
		this.importTextToText = importTextToText.andThen(textService::save);
	}

	@Override
	public void importDocuments(int startId, int endId, boolean autoImportMenus) {
		final int[] availableImportDocumentIds = getAvailableImportDocumentIdsForImport(startId, endId);
		startImportDocumentsTask(availableImportDocumentIds, autoImportMenus);
	}

	@Override
	public void importDocuments(int[] importDocIds, boolean autoImportMenus) {
		startImportDocumentsTask(importDocIds, autoImportMenus);
	}

	private void startImportDocumentsTask(int[] importDocIds, boolean autoImportMenus) {
		if (!importFuture.isDone()) {
			log.warn("Cannot start new importing process. Another one is working!");
			return;
		}

		importFuture = importExecutor.submit(new ImportDocumentsTask(importDocIds, autoImportMenus));
	}

	@Override
	public ImportProgress getImportingProgress() {
		return progress;
	}

	@Override
	public void removeAlias(int importDocId) {
		final String alias = "import/" + importDocId;

		commonContentService.removeAlias(alias);
		Imcms.getServices().getDocumentMapper().invalidateDocument(basicImportDocumentInfoService.toMetaId(importDocId));
		log.info(String.format("Alias: %s in document with id(rb4): %d has been removed!", alias, importDocId));

	}

	@Override
	public void removeAliases(int[] importDocIds) {
		final int[] importedDocumentIds = getImportedDocumentIds(importDocIds);
		for (int importDocId : importedDocumentIds) {
			removeAlias(importDocId);
		}
	}

	@Override
	public void removeAliasesInRange(int startId, int endId) {
		final int[] importedDocumentIds = getImportedDocumentIds(startId, endId);
		for (int importDocId : importedDocumentIds) {
			removeAlias(importDocId);
		}
	}

	@Override
	public void replaceAlias(int importDocId) {
		final String alias = "/import/" + importDocId;
		final int metaId = basicImportDocumentInfoService.toMetaId(importDocId);

		final List<Text> textsContainingAlias = textService.getTextsContaining(alias);
		textsContainingAlias.forEach(text -> {
			final String modifiedText = text.getText().replaceAll(alias, '/' + Integer.toString(metaId));
			text.setText(modifiedText);

			textService.save(text);
		});
		log.info(String.format("Alias: %s in document texts with id(rb4): %d has been replaced to: %s!", alias, importDocId, importDocId));

		removeAlias(importDocId);
	}

	@Override
	public void replaceAliases(int[] metaIds) {
		final int[] importedDocumentIds = getImportedDocumentIds(metaIds);
		for (int importDocId : importedDocumentIds) {
			replaceAlias(importDocId);
		}
	}

	@Override
	public void replaceAliasesInRange(int startId, int endId) {
		final int[] importedDocumentIds = getImportedDocumentIds(startId, endId);
		for (int importDocId : importedDocumentIds) {
			replaceAlias(importDocId);
		}
	}

	private int[] getAvailableImportDocumentIdsForImport(int startId, int endId) {
		return IntStream.rangeClosed(startId, endId)
				.filter(id -> Files.exists(importDirectoryPath.resolve(id + ".json")))
				.toArray();
	}

	private int[] getImportedDocumentIds(int startId, int endId) {
		return IntStream.rangeClosed(startId, endId)
				.filter(id -> basicImportDocumentInfoService.exists(id) && basicImportDocumentInfoService.isImported(id))
				.toArray();
	}

	private int[] getImportedDocumentIds(int[] metaIds) {
		return IntStream.of(metaIds)
				.filter(id -> basicImportDocumentInfoService.exists(id) && basicImportDocumentInfoService.isImported(id))
				.toArray();
	}

	@RequiredArgsConstructor
	private class ImportDocumentsTask implements Runnable {
		private final int[] importDocsId;
		private final boolean autoImportMenus;
		private final List<Integer> importDocumentIdsUnderImporting = new ArrayList<>();

		private static final String DOCUMENT_UPDATED_MSG_PATTERN = "Document updated, importDocId: {}, docId: {}";
		private static final String DOCUMENT_IMPORTED_MSG_PATTERN = "Document imported, importDocId: {}, docId: {}";

		@Override
		public void run() {
			log.info("Started importing documents thread for {} docs!", importDocsId.length);
			progress.reset();
			progress.setTotalSize(importDocsId.length);

			for (int importDocId : importDocsId) {
				importDocument(importDocId);
			}

			importDocumentIdsUnderImporting.clear();
			documentMapper.invalidateDocuments(getMetaIds(importDocsId));
			documentsCache.invalidateCache();

			log.info("Importing documents thread end!");
		}

		private void importDocument(int importDocId) {
			importDocumentIdsUnderImporting.add(importDocId);

			log.info("Trying to import document(rb4): {}", importDocId);
			final BasicImportDocumentInfoDTO basicImportDocument = basicImportDocumentInfoService.getById(importDocId).orElse(null);
			if (basicImportDocument == null) {
				progress.increment();
				log.warn("Document is not uploaded: {}", importDocId);
				return;
			}

			final ImportDocumentStatus status = basicImportDocument.getStatus();
			if (status.equals(IMPORTED) || status.equals(SKIP)) {
				progress.increment();
				log.info("Document skipped because of status: {}, id: {}", status, basicImportDocument.getId());
				return;
			}

			final Path pathToImportDocument = importDirectoryPath.resolve(importDocId + ".json");
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
				final Language language = languageService.findByCode(LanguageMapper.convert639_2to639_1(importDocument.getDefaultLanguage()));

				importDocumentTexts(metaId, language, importDocument.getTexts());
				importDocumentImages(metaId, language, importDocument.getImages());

				final boolean menusImported = importMenuDocuments(metaId, importDocument);
				if (menusImported) {
					defaultDelegatingByTypeDocumentService.publishDocument(metaId, Imcms.getUser().getId());
					log.info("Document {} published", metaId);
				} else {
					log.warn("Document {} not published because of problems with menu importing", metaId);
				}

				basicImportDocument.setMetaId(metaId);
				basicImportDocument.setStatus(IMPORTED);
				log.info(!status.equals(UPDATE) ? DOCUMENT_IMPORTED_MSG_PATTERN : DOCUMENT_UPDATED_MSG_PATTERN, importDocId, metaId);
			} catch (Exception e) {
				basicImportDocument.setStatus(FAILED);
				log.error(String.format("Import failed, importDocId: %s", importDocId), e);
			} finally {
				progress.increment();
				basicImportDocumentInfoService.save(basicImportDocument);
			}
		}

		private void importDocumentImages(int metaId, Language language, List<ImportImageDTO> importImages) throws IOException {
			for (ImportImageDTO importImage : importImages) {
				imageImporter.importDocumentImage(metaId, language, importImage);
			}
		}

		private void importDocumentTexts(int metaId, Language language, List<ImportTextDTO> importTexts) {
			for (ImportTextDTO importText : importTexts) {
				importTextToText.apply(importText, metaId, language);
			}
		}


		private boolean importMenuDocuments(int metaId, ImportDocumentDTO importDocument) {
			if (autoImportMenus) {
				log.info("Trying to auto import menus");
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

			return menuImporter.importMenu(metaId, importDocument.getMenus());
		}

		private int[] getMetaIds(int[] importDocsId) {
			return Arrays.stream(importDocsId)
					.map(basicImportDocumentInfoService::toMetaId)
					.filter(Objects::nonNull)
					.toArray();
		}
	}

}
