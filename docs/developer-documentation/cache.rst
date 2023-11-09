Cache
=====

In this article:
    - `Public Cache`_
    - `Other Cache`_
    - `Clearing Cache`_
    - `API`_

ImCMS has several cache groups: public (for published versions) and other (for working versions).

------------
Public Cache
------------

The response to a request for the published version of the Text Document has an ETag
(identifier for a specific version of a resource) header to help the browser cache this.

*********************
Public Document Cache
*********************

The *Public Document Cache* stores page representations of *public Text* documents.
The system fully caches the response to the request for the public version of the Text Document.
That is, when any user visits the published page for the first time, the system builds, returns the page
and saves the page in the cache, and on the second visit to the page, the system will do much less work, just return the cache.
Thus, we unload the system.

*But what if the template has dynamic content that cannot be fully cached?*

*Solution 1*. Fill dynamic data using JS instead of template engine, jsp or similar.

*Solution 2*. Disable *Public Document Cache* for the whole system. You can do it in the project properties.

.. code-block:: properties

    #Control disabled cache has only boolean values false/true. True - disable add data to cache
    disabledCache=

*Solution 3*. Disable *Public Document Cache* for the a specific document. You can do it in *Page Info* -> *Сache* tab.

    .. image:: _static/page-info-cache.png

    **Cache for unauthorized users** checkbox - enable/disable document caching for an *unauthorized* user.

    **Cache for authorized users** checkbox - enable/disable document caching for an *authorized* user (admins always see the original).

********************
Public Content Cache
********************

The system keeps a cache of the results of finding the content of the *public version*, currently only images.

************
Static Cache
************

The system caches such files: .jpg, .jpeg, .gif, .png, .ico, .wof, .pdf, .ttf, .svg.

-----------
Other Cache
-----------

The system keeps a cache of the results of finding the content of the *working version*, currently only images.

The system caches such files: .jsp, .js, .css.

--------------
Clearing Cache
--------------

In order to clear the *Public Document Cache* for a single document, use *Page Info* -> *Cache* tab -> *Clear Cache* button.

In order to clear a *cache group* for the entire system, use the *Admin Page* -> *Index And Cache* tab.

---
API
---

.. seealso:: You can manage Public Document Cache using ``DocumentsCache`` class.
    Check out the API :doc:`here </developer-documentation/api/documentsCache>`.

.. seealso:: You can manage all types of cache using ``DefaultTemporalDataService`` class.
    Check out the API :doc:`here </developer-documentation/api/temporalDataService>`.

How you can get ``EhCacheCacheManager``:

.. code-block:: java

	Imcms.getServices().getManagedBean("ehCacheCacheManager", EhCacheCacheManager.class);
