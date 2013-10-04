package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.data.{Property, Item, Container}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.Theme

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.Imcms
import scala.collection.immutable.{ListMap}
import scala.collection.JavaConverters._
import java.util.Arrays
import java.util.Date
import org.apache.solr.client.solrj.SolrQuery

class IndexedDocsContainer(
  user: UserDomainObject,
  private var solrQueryOpt: Option[SolrQuery] = None,
  private var visibleDocsFilterOpt: Option[Set[DocId]] = None  // todo: I18nDocRef, Doc
  ) extends Container
    with ContainerWithTypedItemId[Ix]
    with IndexedDocsContainerItem
    with ReadOnlyContainer
    with Container.Sortable
    with ContainerItemSetChangeNotifier
    with ImcmsServicesSupport {

  private implicit class JListOps(jList: JList[_]) {
    def nonEmpty: Boolean = !jList.isEmpty
  }

  private val propertyIdToType = ListMap(
    "docs_projection.container_property.index" -> classOf[Ix],
    "docs_projection.container_property.meta_id" -> classOf[Component],
    "docs_projection.container_property.phase" -> classOf[String],
    "docs_projection.container_property.type" -> classOf[String],
    "docs_projection.container_property.language" -> classOf[Component],
    "docs_projection.container_property.alias" -> classOf[String],
    "docs_projection.container_property.headline" -> classOf[String],

    "docs_projection.container_property.created_dt" -> classOf[String],
    "docs_projection.container_property.modified_dt" -> classOf[String],

    "docs_projection.container_property.publish_dt" -> classOf[String],
    "docs_projection.container_property.archive_dt" -> classOf[String],
    "docs_projection.container_property.expire_dt" -> classOf[String],

    "docs_projection.container_property.parents" -> classOf[Component],
    "docs_projection.container_property.children" -> classOf[Component]
    // todo: version when version support is enabled
    // todo: parents, children <-> referenced, references
  )

  private val propertyIds = propertyIdToType.keys.toList

  private var visibleDocs = java.util.Collections.emptyList[DocumentDomainObject]

  def getVisibleDocsFilterOpt: Option[Set[DocId]] = visibleDocsFilterOpt

  def setVisibleDocsFilterOpt(visibleItemsFilterOpt: Option[Set[DocId]]) {
    if (this.visibleDocsFilterOpt != visibleItemsFilterOpt) {
      this.visibleDocsFilterOpt = visibleItemsFilterOpt
      updateVisibleDocsIds()
    }
  }

  def getSolrQueryOpt: Option[SolrQuery] = solrQueryOpt

  def setSolrQueryOpt(solrQueryOpt: Option[SolrQuery]) {
    if (this.solrQueryOpt.isDefined || solrQueryOpt.isDefined) {
      this.solrQueryOpt = solrQueryOpt
      updateVisibleDocsIds()
    }
  }

  private def updateVisibleDocsIds() {
    visibleDocs = (solrQueryOpt, visibleDocsFilterOpt) match {
      case (None, _) => java.util.Collections.emptyList()
      case (_, Some(ids)) if ids.isEmpty => java.util.Collections.emptyList()
      case (Some(solrQuery), None) =>
        imcmsServices.getDocumentMapper.getDocumentIndex.getService.search(solrQuery, user).get
      case (Some(solrQuery), Some(ids)) =>
        // todo: apply visible docs filter
        imcmsServices.getDocumentMapper.getDocumentIndex.getService.search(solrQuery, user).get
    }

    notifyItemSetChanged()
  }


  /**
   * Returns inclusive range of visible docs ids.
   *
   * @return Some(range) or None if there are no visible docs in this container.
   */
  def visibleDocsRange(): Option[(DocId, DocId)] = visibleDocsFilterOpt match {
    case Some(ids) => if (ids.isEmpty) None else Some(ids.min, ids.max)
    case _ => imcmsServices.getDocumentMapper.getDocumentIdRange |> { idsRange =>
      Some(idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
    }
  }

  override val getContainerPropertyIds: JList[String] = propertyIds.asJava

  override def getType(propertyId: AnyRef): Class[_] = propertyIdToType(propertyId.asInstanceOf[String])

  override def getContainerProperty(itemId: AnyRef, propertyId: AnyRef): Property[AnyRef] = getItem(itemId).getItemProperty(propertyId)

  @transient
  override def size: Int = visibleDocs.size

  override def getItemIds: JCollection[_] = new java.util.AbstractList[Ix] {
    def get(index: Int): Ix = index
    def size(): Int = IndexedDocsContainer.this.size
  }

  override def containsId(itemId: AnyRef): Boolean = itemId match {
    case ix: Ix if visibleDocs.nonEmpty => ix >= 0 && ix < size
    case _ => false
  }

  override def isFirstId(itemId: AnyRef): Boolean = itemId match {
    case ix: Ix if visibleDocs.nonEmpty => ix == 0
    case _ => false
  }

  override def isLastId(itemId: AnyRef): Boolean = itemId match {
    case ix: Ix if visibleDocs.nonEmpty => ix == visibleDocs.size - 1
    case _ => false
  }

  override def firstItemId: Ix = if (visibleDocs.isEmpty) null else 0

  override def lastItemId: Ix = if (visibleDocs.isEmpty) null else visibleDocs.size - 1

  override def prevItemId(itemId: AnyRef): Ix = itemId match {
    case ix: Ix if containsId(ix - 1 : JInteger) => ix - 1
    case _ => null
  }

  override def nextItemId(itemId: AnyRef): Ix = itemId match {
    case ix: Ix if containsId(ix + 1 : JInteger) => ix + 1
    case _ => null
  }

  override def getItem(itemId: AnyRef): DocItem = itemId match {
      case ix: Ix if containsId(ix) => DocItem(ix, visibleDocs.get(ix))
    case _ => null
  }

  // todo implement
  override def sort(propertyId: Array[AnyRef], ascending: Array[Boolean]) {}

  // todo implement
  override val getSortableContainerPropertyIds: JCollection[_] = Arrays.asList(
    "docs_projection.container_property.meta_id",
    "docs_projection.container_property.language",
    "docs_projection.container_property.type",
    "docs_projection.container_property.status",
    "docs_projection.container_property.alias",
    "docs_projection.container_property.headline"
  )

  override def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef): Item = throw new UnsupportedOperationException

  override def addItemAfter(previousItemId: AnyRef): Item = throw new UnsupportedOperationException
}



trait IndexedDocsContainerItem { this: IndexedDocsContainer =>

  case class DocItem(ix: Ix, doc: DocumentDomainObject) extends Item with ReadOnlyItem {

    private def formatDt(dt: Date): String = dt.asOption.map(dt => "%1$td.%1$tm.%1$tY %1$tH:%1$tM".format(dt)).getOrElse("")

    override val getItemPropertyIds: JCollection[_] = getContainerPropertyIds

    private val properties = scala.collection.mutable.Map[AnyRef, Property[AnyRef]]()

    override def getItemProperty(id: AnyRef): Property[AnyRef] = properties.getOrElseUpdate(id, id match {
      case "docs_projection.container_property.index" => LazyProperty(ix + 1 : JInteger)

      case "docs_projection.container_property.meta_id" => LazyProperty(
        new HorizontalLayout with Spacing with NoMargin with UndefinedSize |>> { lyt =>
          val icon = new Image(null, Theme.Icon.Doc.phase(doc))
          val label = new Label(doc.getId.toString)

          lyt.addComponent(icon)
          lyt.addComponent(label)

          lyt.setComponentAlignment(icon, Alignment.MIDDLE_LEFT)
          lyt.setComponentAlignment(label, Alignment.MIDDLE_LEFT)
        }
      )

      case "docs_projection.container_property.headline" => LazyProperty(doc.getHeadline)
      case "docs_projection.container_property.type" => LazyProperty(doc.getDocumentType.getName.toLocalizedString(Imcms.getUser))
      case "docs_projection.container_property.language" => LazyProperty(
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
      case "docs_projection.container_property.alias" => LazyProperty(doc.getAlias)
      case "docs_projection.container_property.phase" => LazyProperty("doc_publication_phase.%s".format(doc.getLifeCyclePhase).i)
      case "docs_projection.container_property.created_dt" => LazyProperty(formatDt(doc.getCreatedDatetime))
      case "docs_projection.container_property.modified_dt" => LazyProperty(formatDt(doc.getModifiedDatetime))

      case "docs_projection.container_property.publish_dt" => LazyProperty(formatDt(doc.getPublicationStartDatetime))
      case "docs_projection.container_property.archive_dt" => LazyProperty(formatDt(doc.getArchivedDatetime))
      case "docs_projection.container_property.expire_dt" => LazyProperty(formatDt(doc.getPublicationEndDatetime))

      case "docs_projection.container_property.parents" => LazyProperty(
        imcmsServices.getDocumentMapper.getDocumentMenuPairsContainingDocument(doc).toList match {
          case Nil => null
          case pair :: Nil =>
            new Tree with ContainerWithTypedItemId[DocumentDomainObject] with NotSelectable with DocSelectWithLifeCycleIcon |>> { tree =>
              val parentDoc = pair.getDocument
              tree.addItem(parentDoc)
              tree.setChildrenAllowed(parentDoc, false)
              tree.setItemCaption(parentDoc, "%s - %s".format(parentDoc.getId, parentDoc.getHeadline))
            }

          case pairs =>
            new Tree with ContainerWithTypedItemId[DocumentDomainObject] with NotSelectable with DocSelectWithLifeCycleIcon |>> { tree =>
              val root = new {}
              tree.addItem(root)
              tree.setItemCaption(root, pairs.size.toString)
              for (pair <- pairs; parentDoc = pair.getDocument) {
                tree.addItem(parentDoc)
                tree.setChildrenAllowed(parentDoc, false)
                tree.setItemCaption(parentDoc, "%s - %s".format(parentDoc.getId, parentDoc.getHeadline))
                tree.setParent(parentDoc, root)
              }
            }
        }
      )

      case "docs_projection.container_property.children" => LazyProperty(
        doc match {
          case textDoc: TextDocumentDomainObject =>
            imcmsServices.getDocumentMapper.getDocuments(textDoc.getChildDocumentIds).asScala.toList match {
              case List() => null
              case List(childDoc) =>
                new Tree with ContainerWithTypedItemId[DocumentDomainObject] with DocSelectWithLifeCycleIcon with NotSelectable |>> { tree =>
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                }

              case childDocs =>
                new Tree with ContainerWithTypedItemId[DocumentDomainObject] with DocSelectWithLifeCycleIcon with NotSelectable |>> { tree =>
                  val root = new {}
                  tree.addItem(root)
                  tree.setItemCaption(root, childDocs.size.toString)
                  for (childDoc <- childDocs) {
                    tree.addItem(childDoc)
                    tree.setChildrenAllowed(childDoc, false)
                    tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                    tree.setParent(childDoc, root)
                    // >>> link to listByNamedParams documents
                  }
                }
            }

          case _ => null
        }
      )
    })
  }
}
