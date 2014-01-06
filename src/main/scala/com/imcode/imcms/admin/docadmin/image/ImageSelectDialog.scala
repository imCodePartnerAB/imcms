package com.imcode
package imcms
package admin.docadmin.image

import com.imcode.imcms.vaadin.component.dialog.{Resizable, CustomSizeDialog, OkCancelDialog}
import java.io.File


class ImageSelectDialog(caption: String) extends OkCancelDialog(caption) with Resizable with CustomSizeDialog {

  val imageSelect = new ImageSelect

  mainComponent = imageSelect.view
}
