package com.imcode
package imcms.dao

import scala.collection.JavaConverters._
import imcode.server.document.textdocument._

object ImageUtil {

  /** Inits text docs images sources. */
  def initImagesSources(images: JList[ImageDomainObject]) = images |>> { _.asScala.foreach(initImageSource) }

  /** Inits text doc's image source. */
  def initImageSource(image: ImageDomainObject) = image |>> { _ =>
    for (url <- image.asOption.map(_.getUrl).map(_.trim)) {
      image.setSource(
        image.getType.intValue match {
          case ImageSource.IMAGE_TYPE_ID__FILE_DOCUMENT =>
            // This type is used in file docs ONLY
            sys.error("Illegal image source type - IMAGE_TYPE_ID__FILE_DOCUMENT. Id: %s, no: %s. url: %s.".format(image.getId, image.getNo, url))

          case ImageSource.IMAGE_TYPE_ID__IMAGES_PATH_RELATIVE_PATH => new ImagesPathRelativePathImageSource(url)
          case ImageSource.IMAGE_TYPE_ID__IMAGE_ARCHIVE => new ImageArchiveImageSource(url)
          case _ => new NullImageSource
        }
      )
    }
  }
}
