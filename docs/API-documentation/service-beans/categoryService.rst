CategoryService
===============

In this article:
    - `Introduction`_
    - `Use API`_
    - `Description about Category`_


Introduction
------------

Imcms has single interface support and different manipulation with categories for documents.
We can easy, create/update/delete/get all categories over this bean CategoryService.
Of course you can use old version category service with oldest API objects - you have to use CategoryMapper,
more information about it :doc:`CategoryMapper</API-documentation/service-beans/categoryMapper>`
That be init new bean CategoryService and work with methods need to look at the code below:

Use API
-------

.. code-block:: jsp

    CategoryService categoryService = Imcms.getServices().getManagedBean(CategoryService.class);

    List<Category> getAll();

    Optional<Category> getById(int id);

    Category save(Category saveMe);  //create new Category

    Category update(Category updateMe);

    void delete(int id);

    List<CategoryJPA> getCategoriesByCategoryType(Integer id);

Description about Category
--------------------------
Category object has any fields:

#. Integer ``id`` - identifier category object
#. String ``name`` - name category object
#. String ``description`` - something description about category
#. CategoryTypeDTO ``type`` - type category which relation current the category
