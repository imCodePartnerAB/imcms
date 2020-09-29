DocumentUrlService
------------------

In this article:
    - `Introduction`_
    - `Use API`_



Introduction
------------

Use API
-------

Init or get instance DocumentUrlService over global Imcms.getServices ``Imcms.getServices().getDocumentUrlService();``

.. code-block:: jsp

  Imcms.getServices().getDocumentUrlService().getByDocId(int docId);

  Imcms.getServices().getDocumentUrlService().save(DocumentURL documentURL);

  Imcms.getServices().getDocumentUrlService().copy(int fromDocId, int toDocId);

  Imcms.getServices().getDocumentUrlService().removeId(DocumentUrlJPA documentURL, Version version);









