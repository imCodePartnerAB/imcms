package com.imcode
package imcms
package admin.docadmin.image

import _root_.imcode.server.document.textdocument.ImageDomainObject

import com.google.common.base.Optional
import com.imcode.imcms.mapping.container.{DocRef, DocVersionRef}
import com.imcode.imcms.mapping.TextDocumentContentLoader
import com.imcode.imcms.vaadin.{Current, Editor}
import com.imcode.imcms.vaadin.component._

import scala.collection.mutable
import scala.collection.Set
import scala.collection.JavaConverters._

import com.vaadin.server._
import com.vaadin.ui.Button
import imcode.server.document.TextDocumentUtils


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

    // fixme create language if not exists
    val textDocMapper: TextDocumentContentLoader = ???
    val versionRef = DocVersionRef.buillder.docId(docRef.getDocId).docVersionNo(docRef.getDocVersionNo).build()
    for ((language, image) <- textDocMapper.getImages(versionRef, imageNo).asScala) {
      val imageEditor = new ImageEditor(Some(image))
      view.tsImages.addTab(imageEditor.view, language.getNativeName, Theme.Icon.Language.flag(language))
      editors += imageEditor
    }
  }

  override def collectValues(): ErrorsOrData = {
    Right(editors.map(_.collectValues().right.get.get).to[Set])
  }
}
