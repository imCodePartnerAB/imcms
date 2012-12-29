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
  private var visibleDocsFilterOpt: Option[Set[DocId]] = None  // todo: I18nDocRef, Doc
  ) extends Container
    with GenericContainer[Ix]
    with IndexedDocsContainerItem
    with ReadOnlyContainer
    with Container.Sortable
    with ContainerItemSetChangeNotifier
    with ImcmsServicesSupport {

  private val propertyIdToType = ListMap(
    "docs_projection.tbl_column.ix" -> classOf[Ix],
    "docs_projection.tbl_column.id" -> classOf[DocId],
    "docs_projection.tbl_column.type" -> classOf[JInteger],
    "docs_projection.tbl_column.language" -> classOf[I18nLanguage],  // todo: when multi-language support is enabled
    "docs_projection.tbl_column.phase" -> classOf[String],
    "docs_projection.tbl_column.alias" -> classOf[String],
    "docs_projection.tbl_column.parents" -> classOf[Component],
    "docs_projection.tbl_column.children" -> classOf[Component]
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
   * Returns inclusive range of visible docs ids.
   *
   * @return Some(range) or None if there are no visible docs in this container.
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
    "docs_projection.tbl_column.id",
    "docs_projection.tbl_column.language",
    "docs_projection.tbl_column.type",
    "docs_projection.tbl_column.status",
    "docs_projection.tbl_column.alias"
  )

  override def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef): Item = throw new UnsupportedOperationException

  override def addItemAfter(previousItemId: AnyRef): Item = throw new UnsupportedOperationException
}



trait IndexedDocsContainerItem { this: IndexedDocsContainer =>

  case class DocItem(ix: Ix, doc: DocumentDomainObject) extends Item with ReadOnlyItem {

    override val getItemPropertyIds: JCollection[_] = getContainerPropertyIds

    override def getItemProperty(id: AnyRef) = FunctionProperty(id match {
      case "docs_projection.tbl_column.ix" => () => ix + 1 : JInteger
      case "docs_projection.tbl_column.id" => () => doc.getId : JInteger
      case "docs_projection.tbl_column.type" => () => doc.getDocumentTypeId : JInteger
      case "docs_projection.tbl_column.language" => () => doc.getLanguage
      case "docs_projection.tbl_column.alias" => () => doc.getAlias
      case "docs_projection.tbl_column.phase" => () => "doc_publication_phase_name.%s".format(doc.getLifeCyclePhase).i

      case "docs_projection.tbl_column.parents" =>
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

      case "docs_projection.tbl_column.children" =>
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
