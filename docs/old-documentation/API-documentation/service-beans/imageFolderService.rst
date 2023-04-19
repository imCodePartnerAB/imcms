ImageFolderService
==================

In order to init ImageFolderService need to use -  ``Imcms.getServices().getManagedBean(ImageFolderService.class)``

Use API
-------
.. code-block:: java

    ImageFolderDTO getImageFolder();

    boolean createImageFolder(ImageFolderDTO folderToCreate);

    boolean renameFolder(ImageFolderDTO renameMe);

    boolean canBeDeleted(ImageFolderDTO folderToCheck) throws IOException;

    boolean deleteFolder(ImageFolderDTO deleteMe) throws IOException;

    ImageFolderDTO getImagesFrom(ImageFolderDTO folderToGetImages);

    List<ImageFolderItemUsageDTO> checkFolder(ImageFolderDTO folderToCheck);

Description about fields ImageFolderDTO
"""""""""""""""""""""""""""""""""""""""

#. String name;
#. String path;
#. List<ImageFileDTO> files;
#. List<ImageFolderDTO> folders;

 see also :doc:`ImageFileService</API-documentation/service-beans/imageFileService>`

Description about fields ImageFolderItemUsageDTO
""""""""""""""""""""""""""""""""""""""""""""""""

#. String filePath;
#. String imageName;
#. List<ImageFileUsageDTO> usages;

