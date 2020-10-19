CategoryTypeService
===================



Use API
-------

.. code-block:: jsp

    CategoryTypeService categoryTypeService = Imcms.getServices().getManagedBean(CategoryTypeService.class);

    Optional<CategoryType> get(int id);

    List<CategoryType> getAll();

    CategoryType create(CategoryType saveMe);

    CategoryType update(CategoryType updateMe);

    void delete(int id);

.. note::
   About type Optional<T> can read  `here <https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html>`_.




