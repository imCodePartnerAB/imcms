SystemPropertyService
=====================

In order to init SystemPropertyService bean need to use - ``Imcms.getServices().getManagedBean(SystemPropertyService.class)``

Use API
-------

.. code-block:: jsp

    List<SystemProperty> findAll();

    SystemProperty findByName(String name);

    SystemProperty update(SystemProperty systemProperty);

    void deleteById(Integer id);

    SystemProperty findById(Integer id);


Example usages
""""""""""""""

.. code-block:: jsp

    SystemPropertyService systemPropertyService = Imcms.getServices().getManagedBean(SystemPropertyService.class);

    SystemProperty systemPropertyName = systemPropertyService.findByName("systemPropertyName");

    systemPropertyName.setValue("false");

    systemPropertyService.update(systemPropertyName);

    List<SystemProperty> allProps = systemPropertyService.findAll();