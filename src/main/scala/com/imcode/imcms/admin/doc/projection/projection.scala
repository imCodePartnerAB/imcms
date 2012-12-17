package com.imcode
package imcms
package admin.doc.projection

//    // alias VIEW -> 1003
//    // status EDIT META -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1003&flags=1
//    // admin: VIWE + ADMIN PANEL 1009 - Start page swe(Copy/Kopia) -> http://imcms.dev.imcode.com/servlet/AdminDoc?meta_id=1009
//    // ref -> DocumentReferences! 3 -> http://imcms.dev.imcode.com/servlet/DocumentReferences?returnurl=ListDocuments%3Fstart%3D1001%26end%3D1031%26showspan%3D%2BLista%2B&id=1001
//    // children LIST DOCS -> 1023 - Testdoc-swe -> http://imcms.dev.imcode.com/servlet/ListDocuments?start=1023&end=1023
// >>> Html.getLinkedStatusIconTemplate(document, user, request )


//// todo: check doc is not deleted from container
//trait DocTableItemIcon extends AbstractSelect with GenericContainer[DocId] {
//  override def getItemIcon(itemId: AnyRef) = item(itemId.asInstanceOf[DocId]) match {
//    case docItem: DocsContainer#DocItem =>
//      docItem.doc match {
//        case null => null
//        case doc =>
////          val app = getApplication
//          val fileBaseName = doc.getLifeCyclePhase.toString
////          val file = new File(app.context.getBaseDirectory, "imcms/eng/images/admin/status/%s.gif".format(fileBaseName))
////
////          new FileResource(file, app)
////          new ExternalResource("imcms/eng/images/admin/status/%s.gif".format(fileBaseName))
//          new ThemeResource("icons/docstatus/%s.gif".format(fileBaseName))
//      }
//
//    case _ => null
//  }
//}


//trait DocSelectDialog extends CustomSizeDialog { this: OkCancelDialog =>
//  val search = new DocsProjection(new AllDocsContainer)
//
//  mainUI = search.ui
//
//  search.listen { btnOk setEnabled _.nonEmpty }
//  search.notifyListeners()
//}