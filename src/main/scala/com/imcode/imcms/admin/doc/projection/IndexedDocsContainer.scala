package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.data.{Item, Container}
import com.imcode.imcms.api.{I18nLanguage, Document}
import com.vaadin.ui.{Tree, Component}
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.ui._

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import scala.collection.immutable.{ListSet, ListMap}
import scala.collection.JavaConverters._
import java.{util => ju}
import com.imcode.imcms.vaadin.data.FunctionProperty
import org.apache.solr.client.solrj.SolrQuery

class IndexedDocsContainer(
  user: UserDomainObject,
  private var solrQueryOpt: Option[SolrQuery] = None,
  private var visibleDocsFilterOpt: Option[Set[DocId]] = None
  ) extends Container
    with GenericContainer[Ix]
    with IndexedDocsContainerItem
    with ReadOnlyContainer
    with Container.Sortable
    with ContainerItemSetChangeNotifier
    with ImcmsServicesSupport {

  private val propertyIdToType = ListMap(
    "doc.tbl.col.ix" -> classOf[Ix],
    "doc.tbl.col.id" -> classOf[DocId],
    "doc.tbl.col.type" -> classOf[JInteger],
    "doc.tbl.col.language" -> classOf[I18nLanguage],  // todo: when multi-language support is enabled
    "doc.tbl.col.status" -> classOf[String],
    "doc.tbl.col.alias" -> classOf[String],
    "doc.tbl.col.parents" -> classOf[Component],
    "doc.tbl.col.children" -> classOf[Component]
    // todo: version when version support is enabled
    // todo: parents, children <-> referenced, references
  )

  private val propertyIds = propertyIdToType.keys.toList

  private var visibleDocs = Array.empty[DocumentDomainObject]

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
      case (None, _) => Array.empty
      case (_, Some(ids)) if ids.isEmpty => Array.empty
      case (Some(solrQuery), None) =>
        imcmsServices.getDocumentMapper.getDocumentIndex.service().search(solrQuery, user).toArray
      case (Some(solrQuery), Some(ids)) =>
        // todo: apply visible docs filter
        imcmsServices.getDocumentMapper.getDocumentIndex.service().search(solrQuery, user).toArray
    }

    notifyItemSetChanged()
  }


  /**
   * Returns full (non filtered) inclusive docs range of this container.
   *
   * @return Some(range) or None if there is no docs in this container.
   */
  def visibleDocsRange: Option[(DocId, DocId)] = visibleDocsFilterOpt match {
    case Some(ids) => if (ids.isEmpty) None else Some(ids.min, ids.max)
    case _ => imcmsServices.getDocumentMapper.getDocumentIdRange |> { idsRange =>
      Some(idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
    }
  }

  override val getContainerPropertyIds = propertyIds.asJava

  override def getType(propertyId: AnyRef) = propertyIdToType(propertyId.asInstanceOf[String])

  override def getContainerProperty(itemId: AnyRef, propertyId: AnyRef) = getItem(itemId).getItemProperty(propertyId)

  override def size: Int = visibleDocs.size

  override def getItemIds: JCollection[_] = visibleDocs.indices.asJavaCollection

  override def containsId(itemId: AnyRef): Boolean = itemId match {
    case ix: Ix => visibleDocs.isDefinedAt(ix)
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

  override def lastItemId: Ix = if (visibleDocs.isEmpty) null else visibleDocs.length - 1

  override def prevItemId(itemId: AnyRef): Ix = itemId match {
    case ix: Ix if visibleDocs.indices.lift(ix - 1).isDefined => ix - 1
    case _ => null
  }

  override def nextItemId(itemId: AnyRef): Ix = itemId match {
    case ix: Ix if visibleDocs.indices.lift(ix + 1).isDefined => ix + 1
    case _ => null
  }

  override def getItem(itemId: AnyRef): DocItem = itemId match {
    case ix: Ix if visibleDocs.lift(ix).isDefined => DocItem(ix, visibleDocs(ix))
    case _ => null
  }

  // todo implement
  override def sort(propertyId: Array[AnyRef], ascending: Array[Boolean]) {}

  // todo implement
  override val getSortableContainerPropertyIds: JCollection[_] = ju.Arrays.asList(
    "doc.tbl.col.id",
    "doc.tbl.col.language",
    "doc.tbl.col.type",
    "doc.tbl.col.status",
    "doc.tbl.col.alias"
  )

  override def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef): Item = throw new UnsupportedOperationException

  override def addItemAfter(previousItemId: AnyRef): Item = throw new UnsupportedOperationException
}



trait IndexedDocsContainerItem { this: IndexedDocsContainer =>

  case class DocItem(ix: Ix, doc: DocumentDomainObject) extends Item with ReadOnlyItem {

    override val getItemPropertyIds: JCollection[_] = getContainerPropertyIds

    override def getItemProperty(id: AnyRef) = FunctionProperty(id match {
      case "doc.tbl.col.ix" => () => ix
      case "doc.tbl.col.id" => () => doc.getId : JInteger
      case "doc.tbl.col.type" => () => doc.getDocumentTypeId : JInteger
      case "doc.tbl.col.language" => () => doc.getLanguage
      case "doc.tbl.col.alias" => () => doc.getAlias
      case "doc.tbl.col.status" =>
        () => doc.getPublicationStatus match {
          case Document.PublicationStatus.NEW => "doc.publication_status.new".i
          case Document.PublicationStatus.APPROVED => "doc.publication_status.approved".i
          case Document.PublicationStatus.DISAPPROVED => "doc.publication_status.disapproved".i
        }

      case "doc.tbl.col.parents" =>
        () => imcmsServices.getDocumentMapper.getDocumentMenuPairsContainingDocument(doc).toList match {
          case Nil => null
          case pair :: Nil =>
            new Tree with GenericContainer[DocumentDomainObject] with NotSelectable with DocSelectWithLifeCycleIcon |>> { tree =>
              val parentDoc = pair.getDocument
              tree.addItem(parentDoc)
              tree.setChildrenAllowed(parentDoc, false)
              tree.setItemCaption(parentDoc, "%s - %s".format(parentDoc.getId, parentDoc.getHeadline))
            }

          case pairs =>
            new Tree with GenericContainer[DocumentDomainObject] with NotSelectable with DocSelectWithLifeCycleIcon |>> { tree =>
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

      case "doc.tbl.col.children" =>
        () => doc match {
          case textDoc: TextDocumentDomainObject =>
            imcmsServices.getDocumentMapper.getDocuments(textDoc.getChildDocumentIds).asScala.toList match {
              case List() => null
              case List(childDoc) =>
                new Tree with GenericContainer[DocumentDomainObject] with DocSelectWithLifeCycleIcon with NotSelectable |>> { tree =>
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                }

              case childDocs =>
                new Tree with GenericContainer[DocumentDomainObject] with DocSelectWithLifeCycleIcon with NotSelectable |>> { tree =>
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
    })
  }
}
