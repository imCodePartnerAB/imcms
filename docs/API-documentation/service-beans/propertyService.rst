PropertyService
===============


In order to init PropertyService bean need to use - ``Imcms.getServices().getManagedBean(PropertyService.class)``

.. code-block:: jsp

    Property getByDocIdAndName(int docId, String name);

    Integer getDocIdByAlias(String alias);

    List<String> findAllAliases();

    List<Property> findByDocId(int docId);

    Boolean existsByAlias(String alias);


