package com.imcode
package imcms
package admin.doc.projection.container

import _root_.imcode.server.Imcms
import _root_.imcode.server.document.DocumentDomainObject

import com.vaadin.data.{Property, Item}
import com.vaadin.ui._

import com.imcode.imcms.vaadin.data.{LazyProperty, ReadOnlyItem}
import com.imcode.imcms.vaadin.ui.{Theme, UndefinedSize, NoMargin, Spacing}

import java.util.Date

case class DocItem(
  index: Index,
  doc: DocumentDomainObject,
  parentsRenderer: (DocumentDomainObject => Component) = _ => null,
  childrenRenderer: (DocumentDomainObject => Component) = _ => null
) extends Item with ReadOnlyItem {

  private val properties = scala.collection.mutable.Map.empty[AnyRef, Property[AnyRef]]

  private def formatDt(dt: Date): String = dt.asOption.map(dt => "%1$td.%1$tm.%1$tY %1$tH:%1$tM".format(dt)).getOrElse("")

  override def getItemPropertyIds(): JCollection[_] = PropertyId.valuesCollection()

  override def getItemProperty(id: AnyRef): Property[AnyRef] = properties.getOrElseUpdate(id, id match {
    case PropertyId.INDEX => LazyProperty(index + 1 : JInteger)
    case PropertyId.META_ID => LazyProperty(
      new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
        val icon = new Image(null, Theme.Icon.Doc.phase(doc))
        val label = new Label(doc.getId.toString)

        lyt.addComponent(icon)
        lyt.addComponent(label)

        lyt.setComponentAlignment(icon, Alignment.MIDDLE_LEFT)
        lyt.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
      }
    )

    case PropertyId.HEADLINE => LazyProperty(doc.getHeadline)
    case PropertyId.TYPE => LazyProperty(doc.getDocumentType.getName.toLocalizedString(Imcms.getUser))
    case PropertyId.LANGUAGE => LazyProperty(
      new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
        val language = doc.getLanguage
        val icon = new Image(null, Theme.Icon.Language.flag(language))
        val label = new Label(language.getNativeName)

        lyt.addComponent(icon)
        lyt.addComponent(label)

        lyt.setComponentAlignment(icon, Alignment.MIDDLE_LEFT)
        lyt.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
      }
    )
    case PropertyId.ALIAS => LazyProperty(doc.getAlias)
    case PropertyId.PHASE => LazyProperty("doc_publication_phase.%s".format(doc.getLifeCyclePhase).i)
    case PropertyId.CREATED_DT => LazyProperty(formatDt(doc.getCreatedDatetime))
    case PropertyId.MODIFIED_DT => LazyProperty(formatDt(doc.getModifiedDatetime))

    case PropertyId.PUBLISH_DT => LazyProperty(formatDt(doc.getPublicationStartDatetime))
    case PropertyId.ARCHIVE_DT => LazyProperty(formatDt(doc.getArchivedDatetime))
    case PropertyId.EXPIRE_DT => LazyProperty(formatDt(doc.getPublicationEndDatetime))

    case PropertyId.PARENTS => LazyProperty(parentsRenderer(doc))
    case PropertyId.CHILDREN => LazyProperty(childrenRenderer(doc))
  })
}