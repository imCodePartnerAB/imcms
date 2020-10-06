TextDocumentTemplateService
===========================


Init or get instance TextDocumentTemplateService over global Imcms.getServices ``Imcms.getServices().getTextDocumentTemplateService();``

.. code-block:: jsp

    TextDocumentTemplate save(TextDocumentTemplate saveMe);

    Optional<TextDocumentTemplate> get(int docId);

    List<TextDocumentTemplateDTO> getByTemplateName(String templateName);





