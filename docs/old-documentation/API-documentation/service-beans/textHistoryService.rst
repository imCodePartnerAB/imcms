TextHistoryService
==================


Init or get instance TextHistoryService over global Imcms.getServices ``Imcms.getServices().getManagedBean(TextHistoryService.class)``


Use API
-------

.. code-block:: jsp

    void save(Text text);

    List<TextHistoryDTO> getAll(TextDTO textDTO);

.. note::
    Check also documentation :doc:`TextService</API-documentation/service-beans/textService>`