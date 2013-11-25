package com.imcode.imcms.admin.docadmin.image

import imcode.server.document.textdocument.ImageDomainObject
import imcode.util.ImcmsImageUtils
import com.imcode.imcms.vaadin.{Editor, Current}
import com.vaadin.ui.{Label, Alignment, Image}
import com.vaadin.server.ExternalResource
import com.imcode.imcms.vaadin.component.UndefinedSize

class ImageEditor(originalImageOpt: Option[ImageDomainObject]) extends Editor {

  override type Data = Option[ImageDomainObject]

  override val widget = new ImageEditorWidget

  private var imageOpt: Option[ImageDomainObject] = None

  resetValues()

  def setImageOpt(imageOpt: Option[ImageDomainObject]) {
    this.imageOpt = imageOpt.map(_.clone())

    widget.removeAllComponents()

    val content = imageOpt match {
      case None =>
        new Label("No image") with UndefinedSize

      case Some(image) =>
        val url = ImcmsImageUtils.getImageUrl(image, Current.contextPath)
        new Image(null, new ExternalResource(url))
    }

    widget.addComponent(content)
    widget.setComponentAlignment(content, Alignment.MIDDLE_CENTER)
  }

  override def resetValues() {
    setImageOpt(originalImageOpt)
  }

  override def collectValues(): ErrorsOrData = Right(imageOpt.map(_.clone()))
}
