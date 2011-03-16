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

class DocSelection(app: ImcmsApplication) {
  val ui = new DocSelectionUI

  ui.tblDocs.addActionHandler(new Action.Handler {
    import Actions._

    def getActions(target: AnyRef, sender: AnyRef) = Array(Exclude, View, Edit, Delete)

    def handleAction(action: Action, sender: AnyRef, target: AnyRef) =
      action match {
        case Exclude => sender.asInstanceOf[Table].removeItem(target)
        case _ =>
      }
  })
}


class DocSelectionUI extends VerticalLayout with Spacing with FullSize {
  val tblDocs = DocTableUI(fullSize = true)
  val mb = new MenuBar
  val miDoc = mb.addItem("Document")
  val miView = mb.addItem("Filter") // -> search in selection

  addComponents(this, mb, tblDocs)
  setExpandRatio(tblDocs, 1.0f)
}


object DocTableUI {
  def apply(fullSize: Boolean = false) = new Table with DocStatusItemIcon with MultiSelect2[DocumentDomainObject] with Selectable { table =>
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
