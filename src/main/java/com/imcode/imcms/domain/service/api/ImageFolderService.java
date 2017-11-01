package com.imcode.imcms.domain.service.api;

import com.imcode.imcms.domain.dto.ImageFolderDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.function.Function;

/**
 * Service for Images Content Manager.
 * CRUD operations with image folders and content.
 *
 * @author Serhii Maksymchuk from Ubrainians for imCode
 * 30.10.17.
 */
@Service
@Transactional
public class ImageFolderService {

    private final Function<File, ImageFolderDTO> fileToImageFolderDTO;

    @Value("${ImagePath}")
    private File imagesPath;

    public ImageFolderService(Function<File, ImageFolderDTO> fileToImageFolderDTO) {
        this.fileToImageFolderDTO = fileToImageFolderDTO;
    }

    public ImageFolderDTO getImageFolder() {
        return fileToImageFolderDTO.apply(imagesPath);
    }
}
