TemplateGroupService
====================


.. warning::
    In the future Imcms won't be support template group service!



In order to init TemplateGroupService bean need to use - Imcms.getServices().getManagedBean(TemplateGroupService.class)

Use API
-------

.. code-block:: jsp

    List<TemplateGroup> getAll();

    TemplateGroup save(TemplateGroup templateGroup);

    TemplateGroup edit(TemplateGroup templateGroup);

    TemplateGroup get(String name);

    TemplateGroup get(Integer groupId);

    void remove(Integer id);