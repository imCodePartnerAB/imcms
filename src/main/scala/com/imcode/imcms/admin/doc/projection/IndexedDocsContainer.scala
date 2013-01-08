package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.data.{Item, Container}
import com.imcode.imcms.api.{I18nLanguage, Document}
import com.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.ui._

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import scala.collection.immutable.{ListSet, ListMap}
import scala.collection.JavaConverters._
import java.{util => ju}
import org.apache.solr.client.solrj.SolrQuery
import com.imcode.imcms.vaadin.Theme
import java.util.Date
import scala.Some
import com.imcode.imcms.vaadin.data.FunctionProperty
import imcode.server.Imcms

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
    "docs_projection.container_property.ix" -> classOf[Ix],
    "docs_projection.container_property.id" -> classOf[Component],
    "docs_projection.container_property.language" -> classOf[Component],
    "docs_projection.container_property.phase" -> classOf[String],
    "docs_projection.container_property.type" -> classOf[String],
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
    "docs_projection.container_property.id",
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

    private def formatDt(dt: Date) = Option(dt).map(dt => "%1$td.%1$tm.%1$tY %1$tH:%1$tM".format(dt)).getOrElse("")

    override val getItemPropertyIds: JCollection[_] = getContainerPropertyIds

    override def getItemProperty(id: AnyRef) = FunctionProperty(id match {
      case "docs_projection.container_property.ix" => () => ix + 1 : JInteger
      case "docs_projection.container_property.id" => () => {
        val label = new Label with UndefinedSize |>> { lbl =>
          lbl.setCaption(doc.getId.toString)
          lbl.setIcon(Theme.Icon.Doc.phase(doc))
        }

        new HorizontalLayout with UndefinedSize |>> { _.addComponent(label) }
      }

      case "docs_projection.container_property.headline" => () => doc.getHeadline
      case "docs_projection.container_property.type" => () => doc.getDocumentType.getName.toLocalizedString(Imcms.getUser)
      case "docs_projection.container_property.language" => () => {
        val label = new Label with UndefinedSize |>> { lbl =>
          val language = doc.getLanguage
          lbl.setCaption(language.getNativeName)
          lbl.setIcon(Theme.Icon.Language.flag(language))
        }

        new HorizontalLayout with UndefinedSize |>> { _.addComponent(label) }
      }

      case "docs_projection.container_property.alias" => () => doc.getAlias
      case "docs_projection.container_property.phase" => () => "doc_publication_phase.%s".format(doc.getLifeCyclePhase).i
      case "docs_projection.container_property.created_dt" => () => formatDt(doc.getCreatedDatetime)
      case "docs_projection.container_property.modified_dt" => () => formatDt(doc.getModifiedDatetime)

      case "docs_projection.container_property.publish_dt" => () => formatDt(doc.getPublicationStartDatetime)
      case "docs_projection.container_property.archive_dt" => () => formatDt(doc.getArchivedDatetime)
      case "docs_projection.container_property.expire_dt" => () => formatDt(doc.getPublicationEndDatetime)

      case "docs_projection.container_property.parents" =>
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

      case "docs_projection.container_property.children" =>
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
