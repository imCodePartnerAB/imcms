BankId Application Settings
===========================


To use BankId logging you need to add next authentication properties to your application properties:

.. code-block:: properties

        authentication-method-1 = loginPassword
        authentication-method-2 = cgi

Next property sets the authentication method URL:

.. code-block:: properties

        cgi-authentication-method-url = *url*

where **url** is different:

    * for testing: https://m00-mg-local.testidp.funktionstjanster.se/samlv2/idp/req/0/0

    * for production: https://m00-mg-local.idp.funktionstjanster.se/samlv2/idp/req/0/0

And the last one - server name, like **localhost:8080** or **https://www.somesite.se** :

.. code-block:: properties

        server-name = *actual server name*
