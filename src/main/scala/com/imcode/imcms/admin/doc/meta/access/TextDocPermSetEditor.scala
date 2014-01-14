package com.imcode
package imcms
package admin.doc.meta.access

import scala.collection.JavaConverters._
import imcode.server.user._
import imcms.ImcmsServicesSupport
import imcode.server.document._
import com.imcode.imcms.vaadin.component._

class TextDocPermSetEditor(
                            permSet: TextDocumentPermissionSetDomainObject,
                            doc: DocumentDomainObject,
                            user: UserDomainObject) extends DocPermSetEditor with ImcmsServicesSupport {

  override val view = new TextDocPermSetEditorView

  resetValues()

  override def resetValues() {
    // Authorized document types
    val selectedTypeIds = permSet.getAllowedDocumentTypeIds
    for ((typeId, typeName) <- imcmsServices.getDocumentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user).asScala) {
      view.tcsCreateDocsOfTypes.addItem(typeId, typeName)
      if (selectedTypeIds contains typeId) view.tcsCreateDocsOfTypes.select(typeId)
    }

    // template groups
    val selectedGroupIds = permSet.getAllowedTemplateGroupIds
    for (group <- imcmsServices.getTemplateMapper.getAllTemplateGroups) {
      view.tcsUseTemplatesFromTemplateGroups.addItem(group, group.getName)
      if (selectedGroupIds contains group.getId) view.tcsUseTemplatesFromTemplateGroups.select(group)
    }

    view.chkEditMeta.checked = permSet.getEditDocumentInformation
    view.chkEditPermissions.checked = permSet.getEditPermissions
    view.chkEditTexts.checked = permSet.getEdit

    view.chkEditTemplates.checked = permSet.getEditTemplates
    view.chkEditImages.checked = permSet.getEditImages
    view.chkEditMenus.checked = permSet.getEditMenus
    view.chkEditIncludes.checked = permSet.getEditIncludes
  }

  override def collectValues(): ErrorsOrData = new TextDocumentPermissionSetDomainObject(permSet.getType) |>> { ps =>
    ps.setEditDocumentInformation(view.chkEditMeta.checked)
    ps.setEditPermissions(view.chkEditPermissions.checked)
    ps.setEdit(view.chkEditTexts.checked)

    ps.setEditTemplates(view.chkEditTemplates.checked)
    ps.setEditImages(view.chkEditImages.checked)
    ps.setEditMenus(view.chkEditMenus.checked)
    ps.setEditIncludes(view.chkEditIncludes.checked)

    ps.setAllowedDocumentTypeIds(new java.util.HashSet(view.tcsCreateDocsOfTypes.itemIds))
    ps.setAllowedTemplateGroupIds(view.tcsUseTemplatesFromTemplateGroups.itemIds.asScala.map(_.getId: JInteger).toSet.asJava)
  } |> Right.apply
}
