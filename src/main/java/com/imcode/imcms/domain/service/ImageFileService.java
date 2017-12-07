package com.imcode.imcms.domain.service;

import com.imcode.imcms.domain.dto.ImageFileDTO;
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

    boolean deleteImage(ImageFileDTO imageFileDTO) throws IOException;

}
