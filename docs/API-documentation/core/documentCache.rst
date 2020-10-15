DocumentCache
=============


Description
"""""""""""
DocumentCache bean allow easy manipulation caches for the document/page and also global cache.

Use API
-------
Init DocumentCache - ``Imcms.getServices().getManagedBean(DocumentCache.class)``

.. code-block:: jsp

    String calculateKey(HttpServletRequest request);

    String calculateKey(final String documentIdString, final String langCode);

    void setCache(Ehcache cache);

    PageInfo getPageInfoFromCache(String key);

    void invalidateDoc(Integer id, String alias);

    void invalidateItem(String key);

    void invalidateCache();

    void invalidateDoc(HttpServletRequest request);

    boolean isDocumentAlreadyCached(String cacheKey);

    void setDisableCachesByProperty();

    long getAmountOfCachedDocuments();

    void setAmountOfCachedDocuments(Integer number); // using only in scope imcms

    String getDisabledCacheValue();