package com.imcode
package imcms
package vaadin.data.util.converter

import com.vaadin.data.util.converter.StringToBooleanConverter

object TableCellStringToBooleanConverter {

  val falseAsEmptyString = new StringToBooleanConverter {
    override def getTrueString: String = "boolean_value.true_as_yes".i
    override def getFalseString: String = ""
  }

  val trueAsEmptyString = new StringToBooleanConverter {
    override def getTrueString: String = "boolean_value.true_as_yes".i
    override def getFalseString: String = ""
  }
}
