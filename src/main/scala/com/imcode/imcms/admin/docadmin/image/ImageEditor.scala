package com.imcode
package imcms
package admin.docadmin.image

import com.imcode.imcms.vaadin.Editor
import imcode.server.document.textdocument.ImageDomainObject
import com.imcode.imcms.api.{DocRef, DocumentLanguage}
import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.collection.Set
import com.imcode.imcms.dao.TextDocDao

class ImageEditor(docRef: DocRef, no: Int) extends Editor with ImcmsServicesSupport {

  override type Data = Set[ImageDomainObject]

  override val ui = new ImageEditorUI

  private val images = mutable.LinkedHashSet.empty[ImageDomainObject]

  override def resetValues() {
    images.clear()
    imcmsServices.getManagedBean(classOf[TextDocDao]).getImages(docRef, no).asScala.foreach(images.add)

    ui.tsImages.removeAllComponents()
    images.foreach { image =>
      //ui.tsImages.addTab()
    }
  }

  override def collectValues(): ErrorsOrData = {
    Right(images.to[Set])
  }
}
