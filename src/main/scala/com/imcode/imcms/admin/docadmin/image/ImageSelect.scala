package com.imcode
package imcms
package admin.docadmin.image

import com.imcode.imcms.ImcmsServicesSupport
import com.imcode.imcms.vaadin.event._
import com.imcode.imcms.vaadin.data._
import imcode.util.ImcmsImageUtils
import com.imcode.imcms.vaadin.Current
import com.vaadin.ui.Image
import com.vaadin.server.ExternalResource
import java.io.File

class ImageSelect extends ImcmsServicesSupport {

  val widget = new ImageSelectWidget

  imcmsServices.getConfig.getImagePath |>> { imagePath =>
    widget.dirs.addItem(imagePath, imagePath.getName)

    widget.dirs.addValueChangeHandler { _ =>
      widget.images.removeAllItems()
      for {
        dir <- widget.dirs.selectionOpt
        imageFile <- dir.listFiles()
      } {
        widget.images.addItem(imageFile, imageFile.getName)
      }
    }
  }

  widget.dirs.selectFirst()

  def selectionOpt(): Option[File] = {
    widget.images.selectionOpt
  }
}
