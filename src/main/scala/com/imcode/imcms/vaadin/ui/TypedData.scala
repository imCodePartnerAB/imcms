package com.imcode.imcms.vaadin.ui

import com.vaadin.ui.AbstractComponent

/**
 * Component data type.
 *
 * Adds type-checked access to data.
 */
trait TypedData[A <: AnyRef] { this: AbstractComponent =>
  def data: A = getData.asInstanceOf[A]
  def data_=(d: A) { setData(d) }
}
