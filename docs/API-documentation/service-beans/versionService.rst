VersionService
==============


In this article:
    - `Introduction`_
    - `Use API`_



Introduction
------------

Use API
-------

Init or get instance VersionService over global Imcms.getServices ``Imcms.getServices().getVersionService();``

.. code-block:: jsp

    Version getDocumentWorkingVersion(int docId);

    Version getLatestVersion(int docId);

    Version getVersion(int docId, Function<Integer, Version> versionReceiver);

    Version create(int docId);

    Version create(int docId, int userId);

    Version findByDocIdAndNo(int docId, int no);

    List<Version> findByDocId(int docId);

    Version findDefault(int docId);

    Version findWorking(int docId);

    deleteByDocId(Integer docId);

    boolean hasNewerVersion(int docId);

    void updateWorkingVersion(int docId);