TemporalDataService
===================

In order to init TemporalDataService bean need to use - ``Imcms.getServices().getManagedBean(TemporalDataService.class)``

.. note::
   See also :doc:`DocumentCache</API-documentation/core/documentCache>`


Use API
-------

.. code-block:: jsp

    void invalidatePublicDocumentCache();

    void invalidateStaticContentCache();

    void invalidateOtherContentCache();

    long rebuildDocumentIndexAndGetDocumentsAmount();

    long getAmountOfIndexedDocuments();

    String getDateInvalidateDocumentCache() throws IOException;

    String getDateStaticContentCache() throws IOException;

    String getDateInvalidateContentCache() throws IOException;

    String getDateDocumentReIndex() throws IOException;

    String getDateAddedInCacheDocuments() throws IOException;

    void addDocumentsInCache(HttpServletRequest request);

    int getTotalAmountTextDocDataForCaching();