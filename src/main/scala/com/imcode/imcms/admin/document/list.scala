package com.imcode
package imcms
package admin.document

import scala.collection.JavaConversions._
import com.imcode.imcms.vaadin.{ContainerProperty => CP, _}

import vaadin.{ImcmsApplication, FullSize}
import com.vaadin.ui.Table.CellStyleGenerator
import com.vaadin.ui._
import imcode.server.Imcms
import imcode.server.document.textdocument.TextDocumentDomainObject
import com.vaadin.terminal.{ExternalResource, Resource}
import imcode.server.document.{LifeCyclePhase, DocumentDomainObject}
import com.vaadin.event.Action
import com.vaadin.data.{Property, Item, Container}
import java.lang.Class
import collection.immutable.{SortedSet, ListMap, ListSet}
import api.Document


case class FunctionProperty[A](propertyFn: Function0[A])(implicit mf: Manifest[A]) extends Property {

  def setReadOnly(newStatus: Boolean) = throw new UnsupportedOperationException

  val isReadOnly = true

  val getType = mf.erasure

  def setValue(newValue: AnyRef) = throw new UnsupportedOperationException

  def getValue = propertyFn().asInstanceOf[AnyRef]

  override def toString = getValue.toString
}

/**
 * Doc table container
 */
class DocTableContainer(private var itemIds: SortedSet[DocId] = SortedSet.empty) extends Container {

  private val propertyIdToType = ListMap(
      "doc.tbl.col.id" -> classOf[DocId],
      "doc.tbl.col.type" -> classOf[JInteger],
      "doc.tbl.col.status" -> classOf[String],
      "doc.tbl.col.alias" -> classOf[String])

  private val propertyIds = propertyIdToType.keys.toList

  case class DocItem(docId: DocId) extends Item {

    lazy val doc = Imcms.getServices.getDocumentMapper.getDocument(docId)

    def removeItemProperty(id: AnyRef) = throw new UnsupportedOperationException

    def addItemProperty(id: AnyRef, property: Property) = throw new UnsupportedOperationException

    def getItemPropertyIds = propertyIds

    def getItemProperty(id: AnyRef) = FunctionProperty(id match {
      case "doc.tbl.col.id" => doc.getId
      case "doc.tbl.col.type" => doc.getDocumentTypeId
      case "doc.tbl.col.alias" => () => ?(doc.getAlias).getOrElse("")
      case "doc.tbl.col.status" =>
        () => doc.getPublicationStatus match {
          case Document.PublicationStatus.NEW => "New"
          case Document.PublicationStatus.APPROVED => "Approved"
          case Document.PublicationStatus.DISAPPROVED => "Disapproved"
        }
    })
  }

  def getContainerPropertyIds = propertyIds

  def addItem() = throw new UnsupportedOperationException

  def addItem(itemId: AnyRef) = let(itemId.asInstanceOf[JInteger]) { id =>
    letret(DocItem(id)) { _ => itemIds += id }
  }

  def getType(propertyId: AnyRef) = propertyIdToType(propertyId.asInstanceOf[String])

  def getItem(itemId: AnyRef) = DocItem(itemId.asInstanceOf[JInteger])

  def getContainerProperty(itemId: AnyRef, propertyId: AnyRef) = getItem(itemId).getItemProperty(propertyId)

  def size = itemIds.size

  def removeItem(itemId: AnyRef) = letret(true) { _ => itemIds -= itemId.asInstanceOf[JInteger] }

  def removeAllItems = letret(true) { _ => itemIds = SortedSet.empty }

  def containsId(itemId: AnyRef) = itemIds.contains(itemId.asInstanceOf[JInteger])

  def addContainerProperty(propertyId: AnyRef, `type` : Class[_], defaultValue: AnyRef) = throw new UnsupportedOperationException

  def removeContainerProperty(propertyId: AnyRef) = throw new UnsupportedOperationException

  def getItemIds = itemIds
}

object DocTableUI {
  def apply(fullSize: Boolean = false) = new Table("", new DocTableContainer) with MultiSelect2[DocId] with Selectable { table =>
    if (fullSize) table.setSizeFull
  }

  def apply2(fullSize: Boolean = false) = new Table with DocStatusItemIcon with MultiSelect2[DocumentDomainObject] with Selectable { table =>
    addContainerProperties(table,
      CP[JInteger]("doc.tbl.col.id"),
      CP[JInteger]("doc.tbl.col.type"),
      CP[String]("doc.tbl.col.status"),
      CP[String]("doc.tbl.col.alias"))


    if (fullSize) table.setSizeFull

//    table.setCellStyleGenerator(new CellStyleGenerator {
//      def getStyle(itemId: AnyRef, propertyId: AnyRef) {
//        if (propertyId == null) {
//            // no propertyId, styling row
//            return (markedRows.contains(itemId) ? "marked" : null);
//        } else if (ExampleUtil.iso3166_PROPERTY_NAME.equals(propertyId)) {
//            return "bold";
//        } else {
//            // no style
//            return null;
//        }
//      }
//    })

    // alias VIEW -> 1003
    // status EDIT META -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1003&flags=1
    // admin: VIWE + ADMIN PANEL 1009 - Start page swe(Copy/Kopia) -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1009
    // ref -> DocumentReferences! 3 -> http://imcms.dev.imcode.com/servlet/DocumentReferences?returnurl=ListDocuments%3Fstart%3D1001%26end%3D1031%26showspan%3D%2BLista%2B&id=1001
    // children LIST DOCS -> 1023 - Testdoc-swe -> http://imcms.dev.imcode.com/servlet/ListDocuments?start=1023&end=1023

    // >>> Html.getLinkedStatusIconTemplate( document, user, request )

    val docMapper = Imcms.getServices.getDocumentMapper


    trait TreeActionHandler extends Tree {
      addActionHandler(new Action.Handler {
        import Actions._

        def getActions(target: AnyRef, sender: AnyRef) = target match {
          case doc: DocumentDomainObject => Array(AddToSelection, View)
          case _ => Array.empty[Action]
        }

        def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
          action match {
            case AddToSelection => //docSelection.ui.tblDocs.addItem(target)
            case _ =>
          }
      })
    }

    table.addGeneratedColumn("doc.tbl.col.parents", new Table.ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) =
        docMapper.getDocumentMenuPairsContainingDocument(itemId.asInstanceOf[DocumentDomainObject]).toList match {
          case List() => null
          case List(pair) =>
            letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
              val parentDoc = pair.getDocument
              tree.addItem(parentDoc)
              tree.setChildrenAllowed(parentDoc, false)
              tree.setItemCaption(parentDoc, "%s - %s" format (parentDoc.getId, parentDoc.getHeadline))
            }

          case pairs => letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
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
    })

    table.addGeneratedColumn("doc.tbl.col.children", new Table.ColumnGenerator {
      def generateCell(source: Table, itemId: AnyRef, columnId: AnyRef) =
        itemId match {
          case textDoc: TextDocumentDomainObject =>
            docMapper.getDocuments(textDoc.getChildDocumentIds).toList match {
              case List() => null
              case List(childDoc) =>
                letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                }

              case childDocs =>letret(new Tree with TreeActionHandler with ItemIdType[DocumentDomainObject] with DocStatusItemIcon) { tree =>
                val root = new {}
                tree.addItem(root)
                tree.setItemCaption(root, childDocs.size.toString)
                for (childDoc <- childDocs) {
                  tree.addItem(childDoc)
                  tree.setChildrenAllowed(childDoc, false)
                  tree.setItemCaption(childDoc, "%s - %s" format (childDoc.getId, childDoc.getHeadline))
                  tree.setParent(childDoc, root)
                  // >>> link to list documents
                }
              }
            }

          case _ => null
        }
    })

    table.setColumnHeaders(Array("doc.tbl.col.id".i, "doc.tbl.col.type".i, "doc.tbl.col.status".i,
      "doc.tbl.col.alias".i, "doc.tbl.col.parents".i, "doc.tbl.col.children".i))

    table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY)
  }
}

trait DocStatusItemIcon extends AbstractSelect {
  override def getItemIcon(itemId: AnyRef) = itemId match {
    case doc: DocumentDomainObject => new ExternalResource("imcms/eng/images/admin/status/%s.gif" format
      itemId.asInstanceOf[DocumentDomainObject].getLifeCyclePhase.toString)

    case _ => null
  }
}