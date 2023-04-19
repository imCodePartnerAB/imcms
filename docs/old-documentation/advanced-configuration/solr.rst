Apache Solr
===========

In this article:
    - `Introduction`_
    - `Configuration`_


Introduction
------------

ImCMS provides great feature that calls Solr - it is powerful searching system. Solr presents as stand alone server that
can be run with or separate to main system. Solr is highly reliable, scalable and fault tolerant, providing distributed
indexing, replication and load-balanced querying, automated failover and recovery, centralized configuration and more.
Solr powers the search and navigation features of many of the world's largest internet sites.


Configuration
-------------

By default way solr has integrated with ImCMS system yet and works perfectly without any additional configuration.
But ImCMS support remote Solr server and all that is needed is put url address to Solr in ImCMS configuration.


Example:
^^^^^^^^


.. code-block:: xml

    # Remote SOLr server URL
    # Type: Http(s) URL; optional
    # Unless specified imCMS uses embedded SOLr server.
    SolrUrl = http://urltosolrserver.com/
