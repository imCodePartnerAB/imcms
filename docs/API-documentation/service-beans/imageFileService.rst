ImageFileService
================


    List<ImageFileDTO> saveNewImageFiles(String folder, List<MultipartFile> files) throws IOException;

    List<ImageFileUsageDTO> deleteImage(ImageFileDTO imageFileDTO) throws IOException;

    List<ImageFileUsageDTO> getImageFileUsages(String imageFileDTOPath);