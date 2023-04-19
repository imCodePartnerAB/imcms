ImcmsUserLanguage
=================

In this scope we have possibles make different manipulation with current user imcms. You have to
get instance ``UserDomainObject`` from global Imcms class, and then we can check everything.

.. warning::
Use global Imcms.getUser() only for work with current user! Otherwise, use :doc:`UserService</API-documentation/service-beans/userService>`

Example usages imcms user
"""""""""""""""""""""""""

.. code-block:: jsp

   UserDomainObject user = Imcms.getUser();


Example usages get current lang user
""""""""""""""""""""""""""""""""""""

.. code-block:: jsp

   String language = Imcms.getUser().getLanguage();