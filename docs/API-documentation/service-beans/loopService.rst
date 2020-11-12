LoopService
===========

In this article:
    - `Use API`_
    - `Example usages loopService API`_


.. warning:: This init instance over Imcms.getServices().getLoopService() working from 10 version

Use API
-------

Init or get instance LoopService over global Imcms.getServices ``Imcms.getServices().getLoopService();``

.. code-block:: jsp

    Imcms.getServices().getLoopService().getLoop(int loopIndex, int docId);

    Imcms.getServices().getLoopService().getLoopPublic(int loopIndex, int docId);

    Imcms.getServices().getLoopService().getLoop(int loopIndex, int docId, Function<Integer, Version> versionGetter);

    Imcms.getServices().getLoopService().saveLoop(Loop loopDTO);

    Imcms.getServices().getLoopService().buildLoopEntryRef(int loopIndex, int entryIndex);

    Imcms.getServices().getLoopService().deleteByDocId(Integer docIdToDelete);

    Imcms.getServices().getLoopService().removeId(LoopJPA dto, Version version);

    Imcms.getServices().getLoopService().createVersionedContent(Version workingVersion, Version newVersion);

Example usages loopService API
------------------------------

.. code-block:: jsp

   LoopService loopService = Imcms.getServices().getLoopService();
   int loopIndex = 1;
   int docId = 1001;

   Loop loop = loopService.getLoop(loopIndex, docId);

Example map JPA to DTO and versa
""""""""""""""""""""""""""""""""

In order to map JPA to DTO and versa classes, you have to use super constructor -
.. code-block:: jsp

   - new LoopJPA(loop);
   - new LoopDTO(loop);


