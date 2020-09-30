LoopService
===========

In this article:
    - `Introduction`_
    - `Use API`_


Introduction
------------


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

