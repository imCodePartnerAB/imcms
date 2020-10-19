VersionService
==============

Init or get instance VersionService over global Imcms.getServices ``Imcms.getServices().getVersionService();``

.. warning:: This init instance over Imcms.getServices().getVersionService() working from 10 version

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


About function interface you can read `here <https://www.baeldung.com/java-8-functional-interfaces/>`_.