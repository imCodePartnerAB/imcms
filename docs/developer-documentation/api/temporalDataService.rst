TemporalDataService
===================
**com.imcode.imcms.domain.service**

This service is designed to manage indexes and all types of cache.

.. seealso:: You can get acquainted with the cache in the :doc:`Cache </developer-documentation/api/documentsCache>` article.

.. seealso:: You can get acquainted with the indexing in the :doc:`Apache Solr </developer-documentation/api/solr>` article.

**************
Initialization
**************

.. code-block:: java

    TemporalDataService temporalDataService = Imcms.getServices().getManagedBean(TemporalDataService.class);

*******
Methods
*******

.. code-block:: java

    void invalidatePublicDocumentCache();

    void invalidateStaticContentCache();

    void invalidateOtherContentCache();

    long rebuildDocumentIndexAndGetDocumentsAmount();
