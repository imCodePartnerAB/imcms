package com.imcode
package imcms
package admin.doc.projection.filter

// todo: I18n is not properly implemented - should not be static
object DateRangeType extends Enumeration {
  val Undefined = Value("date_range.cb_type.item.undefined".i)
  val Custom = Value("date_range.cb_type.item.custom".i)
  val Day = Value("date_range.cb_type.item.day".i)
  val Week = Value("date_range.cb_type.item.week".i)
  val Month = Value("date_range.cb_type.item.month".i)
  val Quarter = Value("date_range.cb_type.item.quarter".i)
  val Year = Value("date_range.cb_type.item.year".i)
}
