package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.data.{Item, Container}
import com.imcode.imcms.api.Document
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
  private var solrQuery: Option[String] = None,
  private var visibleDocsFilter: Option[Set[DocId]] = None
  ) extends Container
    with GenericContainer[DocId]
    with IndexedDocsContainerItem
    with ReadOnlyContainer
    with Container.Ordered
    with Container.Sortable
    with ContainerItemSetChangeNotifier
    with ImcmsServicesSupport {

  private val propertyIdToType = ListMap(
    "doc.tbl.col.id" -> classOf[DocId],
    "doc.tbl.col.type" -> classOf[JInteger],
    "doc.tbl.col.status" -> classOf[String],
    "doc.tbl.col.alias" -> classOf[String],
    "doc.tbl.col.parents" -> classOf[Component],
    "doc.tbl.col.children" -> classOf[Component]
  )

  private val propertyIds = propertyIdToType.keys.toList

  private var docsIds: ListSet[DocId] = ListSet.empty

  private var visibleDocsIds: JCollection[DocId] = ju.Collections.emptyList[DocId]

  def getVisibleDocsFilter: Option[Set[DocId]] = visibleDocsFilter

  def setVisibleDocsFilter(visibleItemsFilter: Option[Set[DocId]]) {
    if (this.visibleDocsFilter != visibleItemsFilter) {
      this.visibleDocsFilter = visibleItemsFilter
      updateVisibleDocsIds()
    }
  }

  def getSolrQuery: Option[String] = solrQuery

  def setSolrQuery(solrQuery: Option[String]) {
    if (solrQuery != this.solrQuery) {
      this.solrQuery = solrQuery
      updateVisibleDocsIds()
    }
  }

  // todo: fix
  private def updateVisibleDocsIds() {
    visibleDocsIds = (solrQuery, visibleDocsFilter) match {
      case (None, _) => ju.Collections.emptyList()
      case (_, Some(ids)) if ids.isEmpty => ju.Collections.emptyList()
      case (Some(query), None) => imcmsServices.getDocumentMapper.getDocumentIndex.service().search(new SolrQuery(query), user).asScala.map(_.getMeta.getId).asJavaCollection
      case (Some(query), Some(ids)) => imcmsServices.getDocumentMapper.getDocumentIndex.service().search(new SolrQuery(query), user).asScala.map(_.getMeta.getId).asJavaCollection
    }

    notifyItemSetChanged()
  }


  /**
   * Returns full (non filtered) inclusive docs range of this container.
   *
   * @return Some(range) or None if there is no docs in this container.
   */
  def visibleDocsRange: Option[(DocId, DocId)] = visibleDocsFilter match {
    case Some(ids) => if (ids.isEmpty) None else Some(ids.min, ids.max)
    case _ => imcmsServices.getDocumentMapper.getDocumentIdRange |> { idsRange =>
      Some(idsRange.getMinimumInteger: DocId, idsRange.getMaximumInteger: DocId)
    }
  }

  override val getContainerPropertyIds = propertyIds.asJava

  override def getType(propertyId: AnyRef) = propertyIdToType(propertyId.asInstanceOf[String])

  override def getItem(itemId: AnyRef): DocItem = DocItem(itemId.asInstanceOf[DocId])

  override def getContainerProperty(itemId: AnyRef, propertyId: AnyRef) = getItem(itemId).getItemProperty(propertyId)

  override def containsId(itemId: AnyRef) = getItemIds.contains(itemId)

  override def size = getItemIds.size

  override def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef) = null

  override def addItemAfter(previousItemId: AnyRef) = null

  override def isLastId(itemId: AnyRef) = itemId == lastItemId

  override def isFirstId(itemId: AnyRef) = itemId == firstItemId

  // extremely ineffective prototype
  override def lastItemId = itemIds.asScala.lastOption.orNull

  // extremely ineffective prototype
  override def firstItemId = itemIds.asScala.headOption.orNull

  // extremely ineffective prototype
  override def prevItemId(itemId: AnyRef) = itemIds.asScala.toIndexedSeq |> { seq =>
    seq.indexOf(itemId.asInstanceOf[DocId]) match {
      case index if index > 0 => seq(index - 1)
      case _ => null
    }
  }

  // extremely ineffective prototype
  override def nextItemId(itemId: AnyRef) = itemIds.asScala.toIndexedSeq |> { seq =>
    seq.indexOf(itemId.asInstanceOf[DocId]) match {
      case index if index < (size - 1) => seq(index + 1)
      case _ => null
    }
  }

  override def sort(propertyId: Array[AnyRef], ascending: Array[Boolean]) {}

  override val getSortableContainerPropertyIds: JCollection[_] = ju.Arrays.asList(
    "doc.tbl.col.id",
    "doc.tbl.col.type",
    "doc.tbl.col.status",
    "doc.tbl.col.alias"
  )

  override def getItemIds: JCollection[_] = visibleDocsIds
}



trait IndexedDocsContainerItem { this: IndexedDocsContainer =>

  case class DocItem(docId: DocId) extends Item with ReadOnlyItem {

    lazy val doc = imcmsServices.getDocumentMapper.getDocument(docId)
    //def doc() = imcmsServices.getDocumentMapper.getDocument(docId)

    override val getItemPropertyIds: JCollection[_] = getContainerPropertyIds

    override def getItemProperty(id: AnyRef) = FunctionProperty(id match {
      case "doc.tbl.col.id" => () => doc.getId : JInteger
      case "doc.tbl.col.type" => () => doc.getDocumentTypeId : JInteger
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
