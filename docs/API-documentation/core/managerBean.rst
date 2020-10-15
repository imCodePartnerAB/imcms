ManagerBean
===========


In this article:
    - `Introduction`_
    - `Init ManagerBean`_


Introduction
------------
Imcms has support any service beans in different files over - ManagerBean.
With the help of ManagerBean we can easy call different implementation current service bean, which will be inject
to this the ManagerBean.


.. note::
   In the first step must be check exits getters current bean in `Imcms.getService()` if don't -
   we can use ManagerBean with inject need type service bean!


Init ManagerBean
----------------
.. code-block:: jsp

    <T> T currentServiceBean = Imcms.getServices().getManagedBean(Class<T> requiredType);


Block parameters:
"""""""""""""""""

+---------------------+--------------+--------------------------------------------------+
| Parameter           | Type         | Description                                      |
+=====================+==============+==================================================+
| requiredType        | Class<T>     | Service bean which was injected                  |
+---------------------+--------------+--------------------------------------------------+
