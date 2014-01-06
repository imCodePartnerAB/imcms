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

  val view = new ImageSelectView

  imcmsServices.getConfig.getImagePath |>> { imagePath =>
    view.dirs.addItem(imagePath, imagePath.getName)

    view.dirs.addValueChangeHandler { _ =>
      view.images.removeAllItems()
      for {
        dir <- view.dirs.selectionOpt
        imageFile <- dir.listFiles()
      } {
        view.images.addItem(imageFile, imageFile.getName)
      }
    }
  }

  view.dirs.selectFirst()

  def selectionOpt(): Option[File] = {
    view.images.selectionOpt
  }
}
