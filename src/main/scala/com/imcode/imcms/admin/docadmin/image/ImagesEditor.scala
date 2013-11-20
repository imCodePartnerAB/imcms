package com.imcode
package imcms
package admin.docadmin.image

import _root_.imcode.server.document.textdocument.ImageDomainObject

import com.imcode.imcms.vaadin.{Current, Editor}
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.api.{DocRef, DocumentLanguage}

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.collection.Set

import com.imcode.imcms.dao.TextDocDao
import com.vaadin.server._
import com.vaadin.ui.{UI, Image}
import _root_.imcode.util.ImcmsImageUtils

// ImageEditParams:
// ------------------
// ?meta_id=#meta_id#
// &img=#content_id#
// &label=#label_url#
// &width=#image_width#
// &height=#image_height#
// &loop_no=#loop_no#
// &content_no=#content_no#"
class ImagesEditor(docRef: DocRef, imageNo: Int) extends Editor with ImcmsServicesSupport {

  override type Data = Set[ImageDomainObject]

  private val editors = collection.mutable.MutableList.empty[ImageEditor]

  override val widget: ImagesEditorWidget = new ImagesEditorWidget |>> { w =>
    w.miClear.setCommandHandler { _ =>
      if (editors.nonEmpty) {
        editors(widget.tsImages.getTabIndex).setImageOpt(None)
      }
    }

    w.miChoose.setCommandHandler { _ =>

    }
  }

  override def resetValues() {
    widget.tsImages.removeAllComponents()
    editors.clear()

    for (image <- imcmsServices.getManagedBean(classOf[TextDocDao]).getImages(docRef, imageNo, None, true).asScala) {
      val imageEditor = new ImageEditor(Some(image))
      widget.tsImages.addTab(imageEditor.widget, image.getLanguage.getNativeName, Theme.Icon.Language.flag(image.getLanguage))
      editors += imageEditor
    }
  }

  override def collectValues(): ErrorsOrData = {
    Right(editors.map(_.collectValues().right.get.get).to[Set])
  }
}
