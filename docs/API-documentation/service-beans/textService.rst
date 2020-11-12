TextService
===========

In order to init TextService bean need to use - ``Imcms.getServices().getManagedBean(TextService.class)``

Use API
-------

.. code-block:: jsp

    Text getText(Text textRequestData);

    Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    List<TextJPA> getText(Integer index, String key);

    Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text save(Text text);

    Set<Text> getPublicTexts(int docId, Language language);

    Text filter(Text text);