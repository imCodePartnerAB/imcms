ImageHistoryService
===================


In order to init ImageHistoryService need to use - Imcms.getServices().getManagedBean(ImageHistoryService.class)

Use API
-------

.. code-block:: jsp

    void save(ImageJPA image);

    void save(ImageDTO image, LanguageJPA language, Version version);

    List<ImageHistoryDTO> getAll(ImageDTO image);

Description about fields ImageHistoryDTO
""""""""""""""""""""""""""""""""""""""""

  - **User modifiedBy**
  - **LocalDateTime modifiedAt** - created date

And ImageHistoryDTO has parent ImageDTO read about image :doc:`ImageService</API-documentation/service-beans/imageService>` 