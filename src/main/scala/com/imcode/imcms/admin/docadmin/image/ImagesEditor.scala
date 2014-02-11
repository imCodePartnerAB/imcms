package com.imcode
package imcms
package admin.docadmin.image

import _root_.imcode.server.document.textdocument.ImageDomainObject

import com.imcode.imcms.api.{DocVersionRef, TextDocItemRef, DocumentLanguage, DocRef}
import com.imcode.imcms.vaadin.{Current, Editor}
import com.imcode.imcms.vaadin.component._

import scala.collection.mutable
import scala.collection.JavaConverters._
import scala.collection.Set

import com.imcode.imcms.dao.TextDocDao
import com.vaadin.server._
import com.vaadin.ui.{Button, UI, Image}
import _root_.imcode.util.ImcmsImageUtils
import org.slf4j.LoggerFactory
import com.imcode.imcms.vaadin.component.dialog.OkCancelDialog

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

  override val view: ImagesEditorView = new ImagesEditorView |>> { w =>
    w.miClear.setCommandHandler { _ =>
      if (editors.nonEmpty) {
        editors(view.tsImages.getTabIndex).setImageOpt(None)
      }
    }

    w.miChoose.setCommandHandler { _ =>
      val dlg = new ImageSelectDialog("Choose image")

      dlg.setOkButtonHandler {
        for (imageFile <- dlg.imageSelect.selectionOpt()) {
          val image = new ImageDomainObject

          //image.setUrl()

          editors(view.tsImages.getTabIndex).setImageOpt(Some(image))

          dlg.close()
        }
      }

      dlg.setSize(600, 500, Sizeable.Unit.PIXELS)
      dlg.show()
    }
  }

  override def resetValues() {
    view.tsImages.removeAllComponents()
    editors.clear()

    val versionRef = DocVersionRef.buillder.docId(docRef.getDocId).docVersionNo(docRef.getDocVersionNo)
    for (image <- imcmsServices.getManagedBean(classOf[TextDocDao]).getImagesInAllLanguages(versionRef, imageNo, None, true)) {
      val imageEditor = new ImageEditor(Some(image))
      view.tsImages.addTab(imageEditor.view, image.getLanguage.getNativeName, Theme.Icon.Language.flag(image.getLanguage))
      editors += imageEditor
    }
  }

  override def collectValues(): ErrorsOrData = {
    Right(editors.map(_.collectValues().right.get.get).to[Set])
  }
}
