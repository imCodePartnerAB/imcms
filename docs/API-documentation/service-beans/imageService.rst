ImageService
============


In this article:
    - `Introduction`_
    - `Use API`_


Introduction
------------

Use API
-------

Init or get instance DocumentUrlService over global Imcms.getServices ``Imcms.getServices().getImageService();``

.. code-block:: jsp

    Imcms.getServices().getImageService().getImage(ImageDTO dataHolder);

    Imcms.getServices().getImageService().getImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Imcms.getServices().getImageService().getPublicImage(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Imcms.getServices().getImageService().getUsedImagesInWorkingAndLatestVersions(String imageURL);

    Imcms.getServices().getImageService().getImagesAllVersionAndLanguages(int docId, Language language);

    Imcms.getServices().getImageService().saveImage(ImageDTO imageDTO);

    Imcms.getServices().getImageService().deleteByDocId(Integer docIdToDelete);

    Imcms.getServices().getImageService().getPublicImageLinks(int docId, Language language);

    Imcms.getServices().getImageService().deleteImage(ImageDTO imageDTO);




