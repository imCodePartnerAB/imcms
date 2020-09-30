TextDocumentTemplateService
===========================


In this article:
    - `Introduction`_
    - `Use API`_



Introduction
------------

Use API
-------

Init or get instance TextDocumentTemplateService over global Imcms.getServices ``Imcms.getServices().getTextDocumentTemplateService();``

.. code-block:: jsp

    TextDocumentTemplate save(TextDocumentTemplate saveMe);

    Optional<TextDocumentTemplate> get(int docId);

    List<TextDocumentTemplateDTO> getByTemplateName(String templateName);





