Search Tag
==========


In this article:
    - `Introduction`_
    - `Use in template`_
    - `Paging integration`_


Introduction
------------
ImCMS support powerful and flex search engine that integrate with well-known Apache Solr. Each document in the system index in solr.
Each content item on page indexing too: ImCMS provide searching by text content, document title, document alias, document menu description. Of course searching
depends on current language, that user has already been selected.


Use in template
---------------

For using search in template all that needed are insert search-tag in the desired place.

.. code-block:: jsp

    <imcms:search searchRequest="" skip="0" take="20">
        <imcms:searchitem>
            Some action with ${searchItem} here
        </imcms:searchitem>
    </imcms:search>


Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+--------------------+--------------+--------------------------------------------------+
| Attribute          | Type         | Description                                      |
+====================+==============+==================================================+
| searchRequest      | String       | Optional property. Set request is searching in   |
+--------------------+--------------+--------------------------------------------------+
| skip               | Integer      | Optional property. It is describing how many     |
|                    |              | items in result should be skipped                |
+--------------------+--------------+--------------------------------------------------+
| take               | Integer      | Optional property. It is describing how many     |
|                    |              | items in result should be taken                  |
+--------------------+--------------+--------------------------------------------------+

Example:
""""""""
.. code-block:: jsp

    <%@taglib prefix="imcms" uri="imcms" %>

    <!DOCTYPE html>
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>Template</title>
        <meta charset="utf-8"/>
    </head>
    <body>
    <imcms:search searchRequest="" skip="0" take="20">
        <ul class="simple-post-list">
            <imcms:searchitem>
                <li>
                    <div class="post-info">
                        <a href="${pageContext.request.contextPath}/${searchItem.foundDocument.alias}">${searchItem.foundDocument.headline}</a>

                        <div class="post-meta">
                                ${searchItem.foundDocument.modifiedDatetime}
                        </div>
                    </div>
                </li>
            </imcms:searchitem>
        </ul>
    </imcms:search>
    </body>
    </html>



Paging integration
------------------

By default ``search`` tag provide paging. It is mean that ``pager`` tag can be inserted in to ``search`` tag body.
(see also :doc:`Pager Tag </design/tag-engine/pager>` section).

For configure ``search`` tag to using paging look at example above.



Example:
""""""""
.. code-block:: jsp

    <imcms:search searchRequest="" skip="0" take="20">
        <ul class="simple-post-list">
            <imcms:searchitem>
                <li>
                    <div class="post-info">
                        <a href="${pageContext.request.contextPath}/${searchItem.foundDocument.alias}">${searchItem.foundDocument.headline}</a>

                        <div class="post-meta">
                                ${searchItem.foundDocument.modifiedDatetime}
                        </div>
                    </div>
                </li>
            </imcms:searchitem>
        </ul>
        <imcms:pager visibleItemCount="6">
            <ul class="pagination pull-right">
                <li><a href="${firstPagerItem.link}">1</a></li>
                <imcms:pageritem>
                    <c:choose>
                        <c:when test="${pagerItem.showed}">
                            <li class="active"><a href="${pagerItem.link}">${pagerItem.pageNumber}</a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="${pagerItem.link}">${pagerItem.pageNumber}</a></li>
                        </c:otherwise>
                    </c:choose>
                </imcms:pageritem>
                <li><a href="${lastPagerItem.link}">2</a>
            </ul>
        </imcms:pager>
    </imcms:search>







