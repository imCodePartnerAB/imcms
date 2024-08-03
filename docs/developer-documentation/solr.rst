Apache Solr
===========

In this article:
    - `Introduction`_
    - `Configuration`_
    - `Index Management`_
    - `API`_

------------
Introduction
------------

ImCMS uses Solr to quickly find documents.

Solr is a fast full text search engine. Solr runs as a standalone full-text search server.

In order to search a document, Apache Solr performs the following operations in sequence:

    *Indexing*: converts the documents into a machine-readable format.

    *Querying*: understanding the terms of a query asked by the user. These terms can be images or keywords, for example.

    *Mapping*: Solr maps the user query to the documents stored in the database to find the appropriate result.

    *Ranking*: as soon as the engine searches the indexed documents, it ranks the outputs by their relevance.

-------------
Configuration
-------------

By default way Solr has integrated with ImCMS and works perfectly without any additional configuration.
But ImCMS support remote Solr server and all that is needed is put url address to Solr in project properties.

.. code-block:: properties

	# Remote SOLr server URL
	# Type: Http(s) URL; optional
	# Unless specified imCMS uses embedded Solr server.
	SolrUrl=
	ImageFilesMetadataSolrUrl=

----------------
Index Management
----------------

Indexing of a document starts after it is saved or its contents are changed.

You can start indexing all documents using *Admin page* -> *Index/Cache* tab -> *Reindex documents* button.

---
API
---

How to start indexing the desired document

.. code-block:: java

    int docId = 1001;
	Imcms.getServices().getDocumentService().index(docId);

``imcode.server.document.index.DocumentIndex`` contain the name of the keys that are used when indexing documents.

.. seealso:: Read how use quick search in :doc:`SearchDocumentService</developer-documentation/api/searchDocumentService>` API article.

.. seealso:: Read how reindex all documents in :doc:`TemporalDataService</developer-documentation/api/temporalDataService>` API article.
