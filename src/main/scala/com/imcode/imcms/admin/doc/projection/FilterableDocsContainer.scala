package com.imcode
package imcms
package admin.doc.projection

import com.vaadin.data.{Property, Item, Container}
import com.imcode.imcms.api.Document
import com.vaadin.ui.{Tree, Component}
import com.imcode.imcms.vaadin.data.{GenericContainer}
import com.imcode.imcms.vaadin.ui._

import _root_.imcode.server.document.DocumentDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.user.UserDomainObject
import scala.collection.immutable.ListMap
import scala.collection.JavaConverters._
import com.imcode.imcms.vaadin.data.FunctionProperty
import com.imcode.imcms.vaadin.data.ContainerItemSetChangeNotifier

/**
 * Docs container with filtering support.
 */
abstract class FilterableDocsContainer extends Container
    with ContainerItemSetChangeNotifier
    with Container.Ordered
    with GenericContainer[DocId]
    with ImcmsServicesSupport {

  private val propertyIdToType = ListMap(
      "doc.tbl.col.id" -> classOf[DocId],
      "doc.tbl.col.type" -> classOf[JInteger],
      "doc.tbl.col.status" -> classOf[String],
      "doc.tbl.col.alias" -> classOf[String],
      "doc.tbl.col.parents" -> classOf[Component],
      "doc.tbl.col.children" -> classOf[Component])

  private val propertyIds = propertyIdToType.keys.toList

  case class DocItem(docId: DocId) extends Item {

    lazy val doc = imcmsServices.getDocumentMapper.getDocument(docId)
    //def doc() = imcmsServices.getDocumentMapper.getDocument(docId)

    def removeItemProperty(id: AnyRef) = throw new UnsupportedOperationException

    def addItemProperty(id: AnyRef, property: Property) = throw new UnsupportedOperationException

    def getItemPropertyIds = propertyIds.asJava

    def getItemProperty(id: AnyRef) = FunctionProperty(id match {
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
              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
            }

          case pairs => new Tree with GenericContainer[DocumentDomainObject] with NotSelectable with DocSelectWithLifeCycleIcon |>> { tree =>
            val root = new {}
            tree.addItem(root)
            tree.setItemCaption(root, pairs.size.toString)
            for (pair <- pairs; parentDoc = pair.getDocument) {
              tree.addItem(parentDoc)
              tree.setChildrenAllowed(parentDoc, false)
              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
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

              case childDocs => new Tree with GenericContainer[DocumentDomainObject] with DocSelectWithLifeCycleIcon with NotSelectable |>> { tree =>
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

  /**
   * Filter docs in this container using SOLr query.
   *
   * @param Some(query) to restrict accessible docs set in this container or None to access all docs.
   */
  def filter(solrQuery: Option[String], user: UserDomainObject) {
    innerFilter(solrQuery, user)
    notifyItemSetChanged()
  }

  protected def innerFilter(solrQuery: Option[String], user: UserDomainObject): Unit

  /**
   * Returns full (non filtered) inclusive docs range of this container.
   *
   * @return Some(range) or None if there is no docs in this container.
   */
  def idRange: Option[(DocId, DocId)]

  def getContainerPropertyIds = propertyIds.asJava

  def addItem() = throw new UnsupportedOperationException

  def getType(propertyId: AnyRef) = propertyIdToType(propertyId.asInstanceOf[String])

  def getItem(itemId: AnyRef) = {
    DocItem(itemId.asInstanceOf[DocId])
  }

  def getContainerProperty(itemId: AnyRef, propertyId: AnyRef) = getItem(itemId).getItemProperty(propertyId)

  def containsId(itemId: AnyRef) = getItemIds.contains(itemId)

  def addContainerProperty(propertyId: AnyRef, `type`: Class[_], defaultValue: AnyRef) = throw new UnsupportedOperationException

  def removeContainerProperty(propertyId: AnyRef) = throw new UnsupportedOperationException

  def size = getItemIds.size

  def addItemAfter(previousItemId: AnyRef, newItemId: AnyRef) = null

  def addItemAfter(previousItemId: AnyRef) = null

  def isLastId(itemId: AnyRef) = itemId == lastItemId

  def isFirstId(itemId: AnyRef) = itemId == firstItemId

  def lastItemId = itemIds.asScala.lastOption.orNull

  def firstItemId = itemIds.asScala.headOption.orNull

  // extremely ineffective prototype
  def prevItemId(itemId: AnyRef) = itemIds.asScala.toIndexedSeq |> { seq =>
    seq.indexOf(itemId.asInstanceOf[DocId]) match {
      case index if index > 0 => seq(index - 1)
      case _ => null
    }
  }

  // extremely ineffective prototype
  def nextItemId(itemId: AnyRef) = itemIds.asScala.toIndexedSeq |> { seq =>
    seq.indexOf(itemId.asInstanceOf[DocId]) match {
      case index if index < (size - 1) => seq(index + 1)
      case _ => null
    }
  }
}
