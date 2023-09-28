package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImageDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFolderDTO;
import com.imcode.imcms.domain.dto.ImportImageDTO;
import com.imcode.imcms.domain.service.ImageFileService;
import com.imcode.imcms.domain.service.ImageFolderService;
import com.imcode.imcms.domain.service.ImageService;
import com.imcode.imcms.model.Language;
import imcode.util.Utility;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
@Component
public class ImageImporter {
	private final Path importDirectoryPath;
	private final ImageService imageService;
	private final ImageFileService imageFileService;
	private final ImageFolderService imageFolderService;
	private final TriFunction<ImportImageDTO, Integer, Language, ImageDTO> importImageToImage;

	public ImageImporter(Path importDirectoryPath, ImageService imageService,
	                     ImageFileService imageFileService,
	                     ImageFolderService imageFolderService,
	                     TriFunction<ImportImageDTO, Integer, Language, ImageDTO> importImageToImage) {
		this.importDirectoryPath = importDirectoryPath;
		this.imageService = imageService;
		this.imageFileService = imageFileService;
		this.imageFolderService = imageFolderService;
		this.importImageToImage = importImageToImage;
	}

	public void importDocumentImage(Integer docId, Language language, ImportImageDTO importImage) throws IOException {
		final Path imageRealPath = importDirectoryPath.resolve("images/" + importImage.getImageUrl());
		if (!Files.exists(imageRealPath)) {
			importImage.setImageUrl(null);
			log.error(String.format("No import image with path: %s, skipping this image!", imageRealPath));
			return;
		}


		final String targetFolder = FilenameUtils.getPath(importImage.getImageUrl());

		createTargetFolder(targetFolder);
		saveImageFile(importImage, targetFolder);

		final ImageDTO image = importImageToImage.apply(importImage, docId, language);

		imageService.saveImage(image);
		imageService.regenerateImage(image);
	}

	private void createTargetFolder(String targetFolder) {
		final ImageFolderDTO imageFolderDTO = new ImageFolderDTO();
		imageFolderDTO.setPath(targetFolder);

		if (!imageFolderService.exists(imageFolderDTO)) {
			imageFolderService.createImageFolder(imageFolderDTO);
		}
	}

	private void saveImageFile(ImportImageDTO importImage, String targetFolder) throws IOException {
		final String imageUrl = importImage.getImageUrl();
		final String normalizedImageUrl = Utility.normalizeString(imageUrl).replace("(", "").replace(")", "");

		if (imageFileService.exists(normalizedImageUrl)) {
			importImage.setImageUrl(normalizedImageUrl);
			return;
		}

		final Path imageRealPath = importDirectoryPath.resolve("images/" + imageUrl);
		final ImageFileDTO imageFileDTO = imageFileService.saveNewImageFile(targetFolder, imageRealPath);

		importImage.setImageUrl(imageFileDTO.getPath());
	}
}
