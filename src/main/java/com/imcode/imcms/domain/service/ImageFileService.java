package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ExifDTO;
import com.imcode.imcms.domain.dto.ImageFileDTO;
import com.imcode.imcms.domain.dto.ImageFileUsageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service for Images Content Manager.
 * CRUD operations with image files.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 31.10.17.
 */
public interface ImageFileService {

    List<ImageFileDTO> saveNewImageFiles(String folder, List<MultipartFile> files) throws IOException;

    List<ImageFileUsageDTO> deleteImage(ImageFileDTO imageFileDTO) throws IOException;

    List<ImageFileUsageDTO> getImageFileUsages(String imageFileDTOPath);

	ImageFileDTO moveImageFile(String destinationFolder, String filePath) throws IOException;

    ImageFileDTO editCommentMetadata(String path, ExifDTO.CustomExifDTO customExif) throws IOException;
}
