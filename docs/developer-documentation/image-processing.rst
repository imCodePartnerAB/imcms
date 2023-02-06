Image Processing
================

In this article:
    - `Endpoint to Receive`_
    - `Image Storage`_
    - `Image Compressor`_
    - `ImageMagick`_
    - `API`_

-------------------
Endpoint to Receive
-------------------

The system has an endpoint to receive images - ``<domain-name>/image?path=<path>``.
The path must be specified without the main folder with images.

-------------
Image Storage
-------------

The project structure has a main folder with original images and a ``/generated`` folder with generated images.
The system generates the image and saves it in the ``/generated`` folder after saving the image in the image editor.

You can specify the main folder for storing images in the project properties.
**This folder should be in webapp.** Default value - ``images``.

.. code-block:: properties

    ImagePath=

.. seealso:: Read also about :doc:`Storage types </developer-documentation/storage>`.

----------------
Image Compressor
----------------

ImCMS is adapted for several image compression services.

* **ImageMagick** (default) - a utility on the server without restrictions, compresses worse, but faster than others.
* **Resmush** - a free external service. Restrictions: you can only compress JPEG, PNG up to 5 mb. https://resmush.it/
* **ImageOptim** - a paid external service with no limits. https://imageoptim.com/api

You can select the desired service in the project properties.

.. code-block:: properties

    # Select a compression service. Available options: imageOptim, resmush, default(imageMagick)
    image.compression.service =

You must configure some properties additionally if you chose the ImageOptim or Resmush service.

For Resmush:

.. code-block:: properties

    # reSmush.it is a free API that provides image optimization. Limitations: less than 5 mb, jpeg and png.
    # Docs: https://resmush.it/api
    image.compression.resmush.url = http://api.resmush.it/ws.php
    # Value between 0 and 100. Recommended: 80
    image.compression.resmush.quality =

For ImageOptim:

.. code-block:: properties

    # ImageOptim is a paid API that provides image optimization.
    # Docs: https://imageoptim.com/api/post
    image.compression.imageoptim.url = https://im2.io
    # ImageOptim account.
    image.compression.imageoptim.username =
    # Available options: lossless, high, medium(balanced quality/filesize tradeoff), low.
    image.compression.imageoptim.quality =

-----------
ImageMagick
-----------
ImageMagick is a free and open-source software suite for displaying, converting, and editing image files.

ImCMS uses ImageMagick to work with images. You need to download this software to use any features related to image.

You also need to configure the next property if you use Windows.

.. code-block:: properties

    # ImageMagick is a software suite for creating, editing and composing images. It can be downloaded from http://www.imagemagick.org.
    # This path should lead to where ImageMagick is installed, and is required only on windows.
    # For example: C:\\Program Files\\ImageMagick-6.9.6-Q16
    ImageMagickPath=

---
API
---

.. seealso:: Read how to create and compress images in the :doc:`ImcmsImageUtils</developer-documentation/api/imcmsImageUtils>`
    and :doc:`ImageCompressor</developer-documentation/api/imageCompressor>` API articles.
