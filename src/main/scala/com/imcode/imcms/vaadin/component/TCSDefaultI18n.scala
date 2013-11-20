package com.imcode
package imcms
package vaadin.component

import com.vaadin.ui.TwinColSelect

trait TCSDefaultI18n extends TwinColSelect {
  setLeftColumnCaption("tcs.col.available.caption".i)
  setRightColumnCaption("tcs.col.selected.caption".i)
}
