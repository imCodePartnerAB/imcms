BankId Application Settings
===========================


To use BankId logging you need to add next authentication properties to your application properties:

.. code-block:: properties

        authentication-configuration = cgi,loginPassword

Next property sets the authentication method URL:

.. code-block:: properties

        cgi-metadata-url = *url*

where **url** is different:

    * for testing: https://m00-mg-local.testidp.funktionstjanster.se/samlv2/idp/metadata/0/0

    * for production: https://m00-mg-local.idp.funktionstjanster.se/samlv2/idp/metadata/0/0
    
Than need define role name for set of created configurations:

.. code-block:: properties

        cgi-user-role-name = *roleName*
        
You can define and use **roleName** whatever you want, like BankID, CGIuser etc.

And the last one - server name, like **http://localhost:8080** or **https://www.somesite.se** :

.. code-block:: properties

        server-name = *actual server name*
