package com.imcode
package imcms
package vaadin.component.dialog

trait OKCaptionIsSave { this: OKButton =>
  btnOk.setCaption("btn_caption.save".i)
}