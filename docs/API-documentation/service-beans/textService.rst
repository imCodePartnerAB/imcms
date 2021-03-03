TextService
===========

In order to init TextService bean need to use - ``Imcms.getServices().getManagedBean(TextService.class)``

Use API
-------

.. code-block:: jsp

    List<TextJPA> getByDocId(Integer docId);

    Text getText(Text textRequestData);

    Text getText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    List<TextJPA> getText(Integer index, String key);

    Text getPublicText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text getLikePublishedText(int docId, int index, String langCode, LoopEntryRef loopEntryRef);

    Text save(Text text);

    Set<Text> getPublicTexts(int docId, Language language);

    Set<Text> getLikePublishedTexts(int docId, Language language);

    Text filter(Text text);

Fields TextJPA
""""""""""""""

#. Integer id;
#. Version version;
#. Integer index;
#. String text;
#. Language language;
#. LoopEntryRefJPA loopEntryRefJPA;
#. boolean likePublished; - flag forced published text on document