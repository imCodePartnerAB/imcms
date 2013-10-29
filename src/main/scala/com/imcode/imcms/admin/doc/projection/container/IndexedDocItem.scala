package com.imcode
package imcms
package admin.doc.projection.container

import _root_.imcode.server.Imcms
import _root_.imcode.server.document.index.DocumentStoredFields

import com.vaadin.data.{Property, Item}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.data.{LazyProperty, ReadOnlyItem}
import com.imcode.imcms.vaadin.ui.{Theme, UndefinedSize, NoMargin, Spacing}

import java.util.Date

case class IndexedDocItem(
  index: Index,
  fields: DocumentStoredFields,
  parentsRenderer: ((DocId, JCollection[DocId]) => Component) = (_, _) => null,
  childrenRenderer: ((DocId, JCollection[DocId]) => Component) = (_, _) => null
) extends Item with ReadOnlyItem {

  private val properties = scala.collection.mutable.Map.empty[AnyRef, Property[AnyRef]]

  private def formatDt(dt: Date): String = dt.asOption.map(dt => "%1$td.%1$tm.%1$tY %1$tH:%1$tM".format(dt)).getOrElse("")

  override def getItemPropertyIds(): JCollection[_] = PropertyId.valuesCollection()

  override def getItemProperty(id: AnyRef): Property[AnyRef] = properties.getOrElseUpdate(id, id match {
    case PropertyId.INDEX => LazyProperty(index + 1 : JInteger)
    case PropertyId.META_ID => LazyProperty(
      new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
        val icon = new Image(null, Theme.Icon.Doc.phase(fields))
        val label = new Label(fields.getId.toString)

        lyt.addComponent(icon)
        lyt.addComponent(label)

        lyt.setComponentAlignment(icon, Alignment.MIDDLE_LEFT)
        lyt.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
      }
    )

    case PropertyId.HEADLINE => LazyProperty(fields.getHeadline)
    case PropertyId.TYPE => LazyProperty(fields.getDocumentType.getName.toLocalizedString(Imcms.getUser))
    case PropertyId.LANGUAGE => LazyProperty(
      new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
        val language = fields.getLanguage
        val icon = new Image(null, Theme.Icon.Language.flag(language))
        val label = new Label(language.getNativeName)

        lyt.addComponent(icon)
        lyt.addComponent(label)

        lyt.setComponentAlignment(icon, Alignment.MIDDLE_LEFT)
        lyt.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
      }
    )
    case PropertyId.ALIAS => LazyProperty(fields.getAlias)
    case PropertyId.PHASE => LazyProperty("doc_publication_phase.%s".format(fields.getLifeCyclePhase).i)
    case PropertyId.CREATED_DT => LazyProperty(formatDt(fields.getCreatedDatetime))
    case PropertyId.MODIFIED_DT => LazyProperty(formatDt(fields.getModifiedDatetime))

    case PropertyId.PUBLICATION_START_DT => LazyProperty(formatDt(fields.getPublicationStartDatetime))
    case PropertyId.ARCHIVING_DT => LazyProperty(formatDt(fields.getArchivedDatetime))
    case PropertyId.EXPIRATION_DT => LazyProperty(formatDt(fields.getPublicationEndDatetime))

    case PropertyId.PARENTS => LazyProperty(parentsRenderer(fields))
    case PropertyId.CHILDREN => LazyProperty(childrenRenderer(fields))
  })
}