ImageFolderService
==================


ImageFolderDTO getImageFolder();

    boolean createImageFolder(ImageFolderDTO folderToCreate);

    boolean renameFolder(ImageFolderDTO renameMe);

    boolean canBeDeleted(ImageFolderDTO folderToCheck) throws IOException;

    boolean deleteFolder(ImageFolderDTO deleteMe) throws IOException;

    ImageFolderDTO getImagesFrom(ImageFolderDTO folderToGetImages);

    List<ImageFolderItemUsageDTO> checkFolder(ImageFolderDTO folderToCheck);