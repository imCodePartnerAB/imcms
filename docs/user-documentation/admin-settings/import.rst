Export/Import Documents
=======================

In this article:
    - `Export Documents`_
    - `Import Documents`_

.. |upload| image:: _static/import/upload-button.png

.. |idRange| image:: _static/import/start-end-ids.png

.. |list| image:: _static/import/list-button.png

.. |filter| image:: _static/import/filter-options.png

.. |import| image:: _static/import/import-btn.png

.. |successExport| image:: _static/import/success-export.png

.. |excludedExport| image:: _static/import/excluded-export.png

.. |failedExport| image:: _static/import/failed-export.png

----------------
Export Documents
----------------

**1. Navigate to AdminManager.**

**2. Select Exportera dokument(Export documents) and proceed to it.**

.. image:: _static/import/adminmanager-export-docs-tab.png

**3. Here specify range of documents(start id and last id). It is better to use small ranges because large ones will be longer to wait.**

.. image:: _static/import/export-document-page.png

**4. Export documents manager overview.**

.. image:: _static/import/export-doc-page-explanation-1.png

.. image:: _static/import/export-doc-page-explanation-2.png

1 - *Hoppa över exporterat( Skip exported)* – always turned on. When turned on skips already exported documents from export.

2 - Each document can be included/excluded from export. Checked -> export.
This value saved in database so every user that has access to this page will see it.

.. note:: Document will not be included in export if it is not published.

3 - Indicates document export status:

- |successExport| - means document successfully exported.
- |excludedExport| - means document has been excluded from export and was skipped during export.
- |failedExport| - means document failed to export. Something unexpected happened. Check logs.

.. note:: If document failed to export so during the next export system will try to export it again if it is included in export.

4 - Document status.

5 - *Exportera(Export)* button. Click and system will export document within range.


**5. Indicates that system exports documents. Wait until page reload and show summary page.**

.. image:: _static/import/export-progress-indication.png

**6. Summary page.**

.. image:: _static/import/summary-page-1.png

.. image:: _static/import/summary-page-2.png

1 - Export status.

2 - Download zipped exported documents in json format(export.zip).

----------------
Import Documents
----------------

.. image:: _static/import/import.png

|upload| - used to upload zipped documents in json format.

|idRange| - user can specify id range. Can be empty for list button(e.g. ...-…, 1-…, ...-999). Cannot be empty for import button. User has to specify range.

|list| - used to show basic import documents info within range.

|filter| - used to filter basic import documents by status.

|import| - used to import documents within range.

*******************************
Import entity reference manager
*******************************

Import entity reference manager used for each import entity reference(role, category, category type, template).

.. image:: _static/import/import-entity-reference-manager.png

.. note::
    If no category or category type specified – then new one with import name will be created in system.

    If no role specified then new one will be created.

    If no template specified then default one will be used. User has to upload template by himself.

Import statuses:

- **IMPORTED** – means document imported and can be updated using UPDATE statuses.
- **UPDATE** – document will be updated from id.json file. You can import it many as many times as you want.
- **SKIP** – document will be skipped during import.
- **FAILED** – document importation failed. Check logs for more info.
