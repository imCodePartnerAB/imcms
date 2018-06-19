Logging in
==========

When ImCMS is running, it's URL consist of next parts:

    * Protocol - ``http`` or ``https``
    * Host - ``localhost`` if running locally, or any another if it is dev/prod
    * Port - usually ``:8080`` or another *(optional)*
    * Context path - could be anything after previous parts *(optional)*

The simplest variation is ``https://imcode.com``, and for local is ``http://localhost:8080/imcms``

There is a URL reserved for logging in - ``/login``. Simply add it after ImCMS URL, so you can get this (using previous
examples): ``https://imcode.com/login``. You should see next page
(this is an old design):

    .. image:: images/imcms-login-page.png

By default login and password is the same - ``admin``.