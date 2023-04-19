SearchDocumentService
=====================


In order to init SearchDocumentService bean need to use - ``Imcms.getServices().getManagedBean(SearchDocumentService.class)``

Use API
-------

.. code-block:: jsp

    List<DocumentStoredFieldsDTO> searchDocuments(SearchQueryDTO searchQuery);

    List<DocumentStoredFieldsDTO> searchDocuments(String searchQuery);


Description how to use SearchQueryDTO
"""""""""""""""""""""""""""""""""""""

.. code-block:: jsp

    final SearchQueryDTO searchQueryDTO = new SearchQueryDTO();
    final String headlineValue = "Headline_Testing_Value_Remove_Added";
    searchQueryDTO.setTerm(headlineValue);
    searchDocumentService.searchDocuments(searchQueryDTO);

