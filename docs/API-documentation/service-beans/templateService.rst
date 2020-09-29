TemplateService
===============


In this article:
    - `Introduction`_
    - `Use API`_



Introduction
------------

Use API
-------

Init or get instance TemplateService over global Imcms.getServices ``Imcms.getServices().getTemplateService();``

.. code-block:: jsp

TemplateService templateService = Imcms.getServices().getTemplateService();

List<Template> getAll();

templateService.save(Template saveMe);

File getTemplateDirectory();

Optional<Template> get(String name);

Path getTemplateAdminPath(String templateName);

Path getPhysicalPathTemplateAdmin(String templateName);

Path getPhysicalPath(String name);

Path saveTemplateFile(Template template, byte[] content, OpenOption writeMode);

void delete(Integer id);

void replaceTemplateFile(Path oldTemplate, Path newTemplate);

.. note::
``replaceTemplateFile`` provide replace all documents which uses old template on newTemplate!
If template exists only one in db, it will throw exception, because we can not delete existing last template!

.. seealso::
How to map simple object to JPA/DTO, and how to correct use DTOs classes
