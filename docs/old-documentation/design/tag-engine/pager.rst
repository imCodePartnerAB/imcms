Pager Tag
=========

In this article:
    - `Introduction`_
    - `Use in template`_

Introduction
------------
For today in the WEB world nobody can`t image complex web-page with repeated material without simple pager. ImCMS make developers life easier.
ImCMS ``pager`` tag that split repeated content on separate pages.

Use in template
---------------

For configure ``pager`` tag in template. Consider code below.

.. code-block:: jsp

    <imcms:pager visibleItemCount="6">
        ${firstPagerItem} <!--use firstPagerItem here to hold link on first page-->
        <imcms:pageritem>
            ${pagerItem} <!--use pagerItem here-->
        </imcms:pageritem>
        ${lastPagerItem} <!--use firstPagerItem here to hold link on last page-->
    </imcms:pager>


.. note::
    ``pager`` tag must be inside tag, that implements ``IPageableTag`` interface.


Available list of tag attributes:
"""""""""""""""""""""""""""""""""

+--------------------+--------------+--------------------------------------------------+
| Attribute          | Type         | Description                                      |
+====================+==============+==================================================+
| visibleItemCount   | Integer      | Optional attribute. Describe how many page links |
|                    |              | will be shown at once                            |
+--------------------+--------------+--------------------------------------------------+

Example:
""""""""
Consider example below. Since ``search`` tag implements ``IPageableTag`` - ``pager`` tag can be inserted into it.

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
        <imcms:pager visibleItemCount="6">
            <ul class="pagination pull-right">
                <li><a href="${firstPagerItem.link}">a</a></li>
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
                <li><a href="${lastPagerItem.link}">1</a>
            </ul>
        </imcms:pager>
    </imcms:search>
    </body>
    </html>







