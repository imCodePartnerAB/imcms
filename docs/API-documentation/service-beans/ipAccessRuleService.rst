IP_AccessRuleService
====================


In this article:
    - `Introduction`_
    - `Use API`_

Introduction
------------
Through the use of IpAccessRuleService bean, we can easily control and put some kind of restriction on Ip address,
for user in the system.

Use API
-------
In order to init IpAccessRuleService need to use - ``Imcms.getServices().getManagedBean(IpAccessRuleService)``

.. code-block:: jsp

    IpAccessRule getById(int id);

    List<IpAccessRule> getAll();

    IpAccessRule create(IpAccessRule rule);

    IpAccessRule update(IpAccessRule rule);

    void delete(int ruleId);

    boolean isAllowedToAccess(InetAddress accessIp, UserDomainObject user);


.. note::
  See also how to use InetAddress `here <https://docs.oracle.com/javase/8/docs/api/java/net/InetAddress.html>`_.

Description about fields IpAccessRule
"""""""""""""""""""""""""""""""""""""

#. Integer id;
#. boolean isEnabled;
#. boolean isRestricted;
#. String ipRange;
#. Integer roleId;
#. Integer userId;



