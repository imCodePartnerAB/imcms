DocumentsCache
==============
**com.imcode.imcms.domain.component**

This service is designed to manage Public Document Cache.

.. seealso:: You can get acquainted with the cache in the :doc:`Cache </developer-documentation/api/documentsCache>` article.

**************
Initialization
**************

.. code-block:: java

    DocumentsCache documentsCache = Imcms.getServices().getManagedBean(DocumentsCache.class);

*******
Methods
*******

.. code-block:: java

	void invalidateDoc(Integer id, Collection<String> aliases);

delete document from Public Document Cache. You must specify the id and aliases of the document due to particular qualities.

------------------

.. code-block:: java

	void invalidateCache();

delete public cache. That is, delete all documents from the Public Document Cache and delete the published content cache.
