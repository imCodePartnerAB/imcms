ImageService
============


In this article:
    - `Use API`_
    - `Description ImageDTO`_
    - `Description fields imageDTO`_


.. warning:: This init instance over Imcms.getServices().getImageService() working from 10 version

Use API
-------

Init or get instance ImageService over global Imcms.getServices ``Imcms.getServices().getImageService();``

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


Description ImageDTO
--------------------

Imcms support a few variables to create imageDTO -

ImageDTO image = new ImageDTO(Integer index, Integer docId, LoopEntryRef loopEntryRef, String langCode);

ImageDTO image = new ImageDTO(Integer index, Integer docId);

Description fields imageDTO
---------------------------

#. ``setIndex(Integer)`` - ``no`` index image
#. ``setDocId(Integer)`` - identified document where location image
#. ``setInText(boolean)`` - location image into text-aria.
#. ``setAllLanguage(boolean)`` - support all language image;
#. ``setAlternateText(String)`` - alternative text for image;
#. ``setSizeFormatted(String)`` - current size image (GB, Mb and etc)
#. ``setLoopEntryRef(LoopEntryRefDTO)`` - location into loop-area. (Check about loopEntryRef in :doc:`LoopService</API-documentation/service-beans/loopService>`.)
#. ``setFormat(Format)`` - set current format image;
#. ``setCropRegion(ImageCropRegionDTO)`` - x1,x2,y1,y2 crop region for image;
#. ``setSource(ImageSource)``
#. ``setSpaceAround(SpaceAroundDTO)``
#. ``setResize(Resize)``
#. ``setRotateDirection(RotateDirection)``



