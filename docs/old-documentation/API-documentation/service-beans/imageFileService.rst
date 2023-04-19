ImageFileService
================


In order to init ImageFileService need to use -  ``Imcms.getServices().getManagedBean(ImageFileService.class)``

Use API
-------
.. code-block:: jsp

    List<ImageFileDTO> saveNewImageFiles(String folder, List<MultipartFile> files) throws IOException;

    List<ImageFileUsageDTO> deleteImage(ImageFileDTO imageFileDTO) throws IOException;

    List<ImageFileUsageDTO> getImageFileUsages(String imageFileDTOPath);


Description how convert file to MockMultipartFile
"""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: jsp

    String name = "testFile";
    String originalName = "img-test.jpg";
    String contentType = null;
    byte[] content = Files.readAllBytes(new File(pathName..).toPath());

    MockMultipartFile file = new MockMultipartFile("file", "img1-test.jpg", null, content);

Description fields in ImageFileDTO
""""""""""""""""""""""""""""""""""

#. String name;
#. String path;
#. Format format;
#. String uploaded; - lastModifiedDate
#. String resolution;
#. String size;
#. Integer width;
#. Integer height

Description fields ImageFileUsageDTO
""""""""""""""""""""""""""""""""""""

#. Integer docId;
#. Integer version;
#. Integer elementIndex;
#. String comment;


