package com.imcode
package imcms
package admin.docadmin.image

import _root_.imcode.server.document.textdocument.ImageDomainObject

import com.imcode.imcms.vaadin.{Current, Editor}
import com.imcode.imcms.api.{DocRef, DocumentLanguage}

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.collection.Set

import com.imcode.imcms.dao.TextDocDao
import com.vaadin.server._
import com.vaadin.ui.{UI, Image}
import imcode.util.ImcmsImageUtils

// ImageEditParams:
// ------------------
// ?meta_id=#meta_id#
// &img=#content_id#
// &label=#label_url#
// &width=#image_width#
// &height=#image_height#
// &loop_no=#loop_no#
// &content_no=#content_no#"
class ImageEditor(docRef: DocRef, imageNo: Int) extends Editor with ImcmsServicesSupport {

  override type Data = Set[ImageDomainObject]

  override val ui = new ImageEditorUI

  private val images = mutable.LinkedHashSet.empty[ImageDomainObject]

  override def resetValues() {
    images.clear()
    imcmsServices.getManagedBean(classOf[TextDocDao]).getImages(docRef, imageNo).asScala.foreach(images.add)

    ui.tsImages.removeAllComponents()
    images.foreach { image =>
      val url = ImcmsImageUtils.getImageUrl(image, Current.contextPath)
      val tab = ui.tsImages.addTab(new Image(null, new ExternalResource(url)), image.getLanguage.getNativeName)
      // |> { tab =>
        //tab.set
      //}
    }
  }

  override def collectValues(): ErrorsOrData = {
    Right(images.to[Set])
  }
}
