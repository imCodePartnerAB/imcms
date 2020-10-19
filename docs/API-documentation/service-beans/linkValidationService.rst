LinkValidationService
=====================


Introduction
------------
Imcms allows easy a certain validate all links on text documents. You can also find list links which broken on text
documents.
Init LinkValidationService service bean -  ``Imcms.getServices().getManagedBean(LinkValidationService.class);``

.. code-block:: java

    List<ValidationLink> validateDocumentsLinks(int startDocumentId, int endDocumentId, boolean onlyBrokenLinks);


Description about fields ValidationLink
"""""""""""""""""""""""""""""""""""""""
#.     boolean pageFound;
#.     boolean hostFound;
#.     boolean hostReachable;
#.     String url;
#.     EditLink editLink;
#.     DocumentStoredFieldsDTO documentData;
#.     LinkType linkType; -  TEXT,IMAGE,URL


Description about fields EditLink
"""""""""""""""""""""""""""""""""
#.     Integer metaId;
#.     String title;
#.     Integer index;
#.     LoopEntryRef loopEntryRef;

.. note::
   See also description DocumentStoredFieldsDTO :doc:`SearchDocumentService</API-documentation/service-beans/searchDocumentService>`
