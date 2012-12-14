package com.imcode.imcms.vaadin.data

import com.vaadin.data.Property

case class FunctionProperty[A <: AnyRef](valueFn: () => A)(implicit mf: Manifest[A]) extends Property {
    val isReadOnly: Boolean = true
    val getType: Class[_] = mf.erasure
    def setValue(newValue: AnyRef): Unit = throw new UnsupportedOperationException
    def setReadOnly(newStatus: Boolean): Unit = throw new UnsupportedOperationException
    def getValue: AnyRef = valueFn().asInstanceOf[AnyRef]
    override def toString: String = Option(getValue).map(_.toString).getOrElse("")
  }
