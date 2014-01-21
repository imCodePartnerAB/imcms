package com.imcode
package imcms
package admin.instance.settings.language

import com.imcode.imcms.vaadin.data.util.converter.TableCellStringToBooleanConverter
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.vaadin.ui.themes.Reindeer
import scala.collection.JavaConverters._


class LanguageManagerView extends VerticalLayout with FullSize {

  val mb = new MenuBar with FullWidth |>> { _.addStyleName("manager") }
  val miNew = mb.addItem("mi.new".i)
  val miEdit = mb.addItem("mi.edit".i)
  val miDelete = mb.addItem("mi.delete".i)
  val miSetAsDefault = mb.addItem("doc_lng_mgr.mi.set_as_default".i)
  val miReload = mb.addItem("mi.reload".i)
  val miHelp = mb.addItem("mi.help".i)
  val tblLanguages = new Table with SingleSelect[JInteger] with Immediate with FullSize |>> { tbl =>
    addContainerProperties(tbl,
      PropertyDescriptor[JInteger]("doc_lng_mgr.lng_property.id"),
      PropertyDescriptor[String]("doc_lng_mgr.lng_property.code"),
      PropertyDescriptor[String]("doc_lng_mgr.lng_property.name"),
      PropertyDescriptor[String]("doc_lng_mgr.lng_property.native_name"),
      PropertyDescriptor[JBoolean]("doc_lng_mgr.lng_property.is_disabled"),
      PropertyDescriptor[JBoolean]("doc_lng_mgr.lng_property.is_default"),
      PropertyDescriptor[Void]("")
    )

    tbl.getContainerPropertyIds.asScala.foreach { id =>
      tbl.setColumnHeader(id, id.toString.i)
    }

    tbl.setStyleName(Reindeer.TABLE_BORDERLESS)
    tbl.setColumnExpandRatio("", 1f)
    tbl.setColumnAlignment("doc_lng_mgr.lng_property.id", Table.Align.RIGHT)
    tbl.setColumnAlignment("doc_lng_mgr.lng_property.is_disabled", Table.Align.CENTER)
    tbl.setColumnAlignment("doc_lng_mgr.lng_property.is_default", Table.Align.CENTER)

    val converter = TableCellStringToBooleanConverter.falseAsEmptyString

    tbl.setConverter("doc_lng_mgr.lng_property.is_disabled", converter)
    tbl.setConverter("doc_lng_mgr.lng_property.is_default", converter)
  }


  addComponents(mb, tblLanguages)
  setExpandRatio(tblLanguages, 1f)
}