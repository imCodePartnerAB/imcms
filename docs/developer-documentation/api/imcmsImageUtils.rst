ImcmsImageUtils
===============
**imcode.util**

Utility for working with images.

-------
Methods
-------

.. code-block:: java

    static byte[] compressImage(byte[] imageContent, Format format)

compress the image (using the compressor selected in the system).
If an exception drops out during compression by a external service, then image will be compressed by the default compressor.

------------------

.. code-block:: java

    static byte[] generateImage(ImageData image)

generate an image with the specified properties (size, format and so on).

------------------
Additional classes
------------------

*********
ImageData
*********
**com.imcode.imcms.domain.dto**

``imcode.server.document.textdocument.ImageSource source``

``int width`` - image width in pixels.

``int height`` - image height in pixels.

``imcode.util.image.Resize resize`` - resizing conditions (for example, resize if size is greater after - GREATER_THAN).

``imcode.util.image.Format format`` - image file format.

``com.imcode.imcms.domain.dto.ImageCropRegionDTO cropRegion`` - new image borders (X1, X2, Y1, Y2).