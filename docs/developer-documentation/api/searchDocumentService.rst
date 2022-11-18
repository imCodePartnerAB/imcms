SearchDocumentService
=====================
**com.imcode.imcms.domain.service**

In this article:
    - `Initialization`_
    - `Methods`_
    - `Additional classes`_
    - `Usage examples`_

This service is designed to search for documents.

--------------
Initialization
--------------

.. code-block:: java

    SearchDocumentService searchDocumentService = Imcms.getServices().getSearchDocumentService();

-------
Methods
-------

.. code-block:: java

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(SearchQueryDTO searchQuery, boolean limitSearch);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(SearchQueryDTO searchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, boolean limitSearch);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, PageRequestDTO page, boolean limitSearch);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery, PageRequestDTO page);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, PageRequestDTO page, boolean limitSearch);

    SolrDocumentList searchDocumentsReturnSolrDocumentList(String stringSearchQuery, PageRequestDTO page);

**********
Parameters
**********

``SearchQueryDTO searchQuery`` - represent a query as an object.

``PageRequestDTO page`` - represent sorting and paging.

``String searchQuery`` - represent a custom query as a string.

By default, the search returns only published documents and those to which the user has access.
To remove this condition set ``boolean limitSearch`` to false.

************
Return Types
************

``List<DocumentStoredFieldsDTO>`` - a list of objects that represent basic data about a document.

``SolrDocumentList`` - represent a list of SolrDocuments returned from a search.
Use a key name from ``DocumentIndex`` to get needed values.

.. code-block:: java

    solrDocumentList.get(0).getFieldValue(DocumentIndex.FIELD_META_HEADLINE);

------------------
Additional classes
------------------

*************
DocumentIndex
*************
**imcode.server.document.index**

contain the names of the fields by which the values are obtained and by which the values are set during the indexing of the document.

**************
SearchQueryDTO
**************
**com.imcode.imcms.domain.dto**

``String term`` - free text. Use "" to search a quote.

``Integer userId`` - filter by creator id.

``List<Integer> categoriesId`` - the document must have these categories.

``PageRequestDTO page`` - represent sorting and paging.

**************
PageRequestDTO
**************
**com.imcode.imcms.domain.dto**

``String property`` - sort key. Use a key name from ``DocumentIndex``.

``Sort.Direction direction`` - sorting direction.

``int skip`` - number of documents to skip.

``int size`` - number of documents to return.

***********************
DocumentStoredFieldsDTO
***********************
**com.imcode.imcms.domain.dto**

represent basic data about a document.

--------------
Usage examples
--------------

.. code-block:: java

        SearchDocumentService searchDocumentService = Imcms.getServices().getSearchDocumentService();

        // search using SearchQueryDTO and get List<DocumentStoredFieldsDTO>
        final SearchQueryDTO searchQuery = new SearchQueryDTO("some text");
        searchQuery.setPage(new PageRequestDTO(DocumentIndex.FIELD__ID, org.springframework.data.domain.Sort.Direction.DESC, 10, 20));
        final List<DocumentStoredFieldsDTO> documentStoredFieldsDTOList = searchDocumentService.searchDocuments(searchQuery);
        for(DocumentStoredFieldsDTO documentStoredFields: documentStoredFieldsDTOList){
            System.out.println("id: " + documentStoredFields.getId());
            System.out.println("title: " + documentStoredFields.getTitle());
        }

        // search using query as string and get SolrDocumentList
        final String query = "+" + DocumentIndex.FIELD__TEXT + ":" + "some text" + " " +
                "+" + DocumentIndex.FIELD__CREATOR_ID + ":" + "1";
        final SolrDocumentList solrDocuments = searchDocumentService.searchDocumentsReturnSolrDocumentList();
        for (final SolrDocument solrDocument : solrDocuments) {
            System.out.println("id: " + solrDocument.getFieldValue(DocumentIndex.FIELD__ID));
            System.out.println("title: " + solrDocument.getFieldValue(DocumentIndex.FIELD__META_HEADLINE));
        }
