package com.imcode.imcms.domain.service.api;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imcode.imcms.domain.service.*;
import com.imcode.imcms.model.ImportEntityReferenceType;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.imcode.imcms.model.ImportDocumentStatus.IMPORT;

@Log4j2
@Component
public class ArchiveImportHandler {
	private final JsonPointer toCategories = JsonPointer.compile("/categories");
	private final JsonPointer toRoles = JsonPointer.compile("/roles");
	private final JsonPointer toTemplate = JsonPointer.compile("/template");

	private final Path importDirectoryPath;
	private final ObjectMapper mapper;
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;
	private final BasicImportDocumentInfoService basicImportDocumentInfoService;
	private final ImageFolderService imageFolderService;
	private final ImageFileService imageFileService;
	private final ImageService imageService;

	public ArchiveImportHandler(Path importDirectoryPath,
	                            ObjectMapper mapper,
	                            ImportEntityReferenceManagerService importEntityReferenceManagerService,
	                            BasicImportDocumentInfoService basicImportDocumentInfoService,
	                            ImageFolderService imageFolderService,
	                            ImageFileService imageFileService,
	                            ImageService imageService) {
		this.importDirectoryPath = importDirectoryPath;
		this.mapper = mapper;
		this.importEntityReferenceManagerService = importEntityReferenceManagerService;
		this.basicImportDocumentInfoService = basicImportDocumentInfoService;
		this.imageFolderService = imageFolderService;
		this.imageFileService = imageFileService;
		this.imageService = imageService;
	}

	public void handleDocument(Path path) {
		createImportEntityReferences(path);
		createBasicDocumentInfo(path);
	}

	private void createImportEntityReferences(Path path) {
		try (final InputStream inputStream = Files.newInputStream(path)) {
			final JsonNode jsonNode = mapper.readTree(inputStream);

			final JsonNode categoriesNode = jsonNode.at(toCategories);
			final List<String> categories = collectNames(categoriesNode, ImportEntityReferenceType.CATEGORY);
			final List<String> categoryTypes = collectNames(categoriesNode, ImportEntityReferenceType.CATEGORY_TYPE);
			final List<String> roles = collectNames(jsonNode.at(toRoles), ImportEntityReferenceType.ROLE);
			final String template = jsonNode.at(toTemplate).asText();

			createImportEntityReferences(categories, ImportEntityReferenceType.CATEGORY);
			createImportEntityReferences(categoryTypes, ImportEntityReferenceType.CATEGORY_TYPE);
			createImportEntityReferences(roles, ImportEntityReferenceType.ROLE);
			createImportEntityReferences(List.of(template), ImportEntityReferenceType.TEMPLATE);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Cannot read json to ImportDocumentDTO, zipEntryName: %s", FilenameUtils.getName(path.toString())), e);
		}
	}

	private void createImportEntityReferences(List<String> names, ImportEntityReferenceType type) {
		for (String name : names) {
			if (StringUtils.isNotBlank(name) && !importEntityReferenceManagerService.existsByName(name, type)) {
				importEntityReferenceManagerService.createReference(name, type);
			}
		}
	}

	private List<String> collectNames(JsonNode node, ImportEntityReferenceType type) {
		if (!node.isArray() || node.isMissingNode() || node.isEmpty()) return List.of();

		return StreamSupport.stream(node.spliterator(), true)
				.map(jsonNode -> {
					if (type.equals(ImportEntityReferenceType.CATEGORY_TYPE)) {
						jsonNode = jsonNode.path("category_type");
					}

					return jsonNode.path("name").asText();
				})
				.toList();
	}

	private void createBasicDocumentInfo(Path path) {
		final int importDocId = Integer.parseInt(FilenameUtils.getBaseName(path.toString()));
		if (basicImportDocumentInfoService.exists(importDocId)) return;

		basicImportDocumentInfoService.create(importDocId, IMPORT);
	}
}
