DocumentUrlService
==================


.. warning:: This init instance over Imcms.getServices().getDocumentUrlService() working from 10 version

Init or get instance DocumentUrlService over global Imcms.getServices ``Imcms.getServices().getDocumentUrlService();``

.. code-block:: jsp

  Imcms.getServices().getDocumentUrlService().getByDocId(int docId);

  Imcms.getServices().getDocumentUrlService().save(DocumentURL documentURL);

  Imcms.getServices().getDocumentUrlService().copy(int fromDocId, int toDocId);

  Imcms.getServices().getDocumentUrlService().removeId(DocumentUrlJPA documentURL, Version version);


Description about DocumentUrl
-----------------------------

How to create DocumentUrl -

.. code-block:: jsp

    DocumentUrlDTO.createDefault(); //will create default document url with empty fields.

    DocumentUrlDTO documentUrlDTO = new DocumentUrlDTO();

        documentUrlDTO.setUrlFrameName("");
        documentUrlDTO.setUrlTarget("");
        documentUrlDTO.setUrl("");
        documentUrlDTO.setUrlText("");
        documentUrlDTO.setUrlLanguagePrefix("");

If need to create ``DocumentUrlJPA`` , so need to inject data to constructor current type - new DocumentUrlJPA(DocumentUrlDTO);








