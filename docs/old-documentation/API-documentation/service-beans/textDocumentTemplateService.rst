TextDocumentTemplateService
===========================


Init or get instance TextDocumentTemplateService over global Imcms.getServices ``Imcms.getServices().getTextDocumentTemplateService();``

.. warning:: This init instance over Imcms.getServices().getTextDocumentTemplateService() working from 10 version

Use API
-------

.. code-block:: jsp

    TextDocumentTemplate save(TextDocumentTemplate saveMe);

    Optional<TextDocumentTemplate> get(int docId);

    List<TextDocumentTemplateDTO> getByTemplateName(String templateName);

Example usages
""""""""""""""

.. code-block:: jsp

    TextDocumentTemplateService textDocumentTemplateService = Imcms.getServices().getTextDocumentTemplateService();

    Optional<TextDocumentTemplate> textDocumentTemplateOptional = textDocumentTemplateService.get(1001);
    TextDocumentTemplate textDocumentTemplate = textDocumentTemplateOptional.orElse(new TextDocumentTemplateDTO());
    textDocumentTemplate.setTemplateName("newTemplate");
    textDocumentTemplateService.save(textDocumentTemplate);





