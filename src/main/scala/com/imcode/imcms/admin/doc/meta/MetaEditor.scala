package com.imcode
package imcms
package admin.doc.meta

import scala.collection.JavaConverters._

import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.document.{DocumentDomainObject}

import com.imcode.imcms.api._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.admin.doc.meta.access.AccessEditor
import com.imcode.imcms.admin.doc.meta.search.SearchSettingsEditor
import com.imcode.imcms.admin.doc.meta.profile.ProfileEditor
import com.imcode.imcms.admin.doc.meta.appearance.AppearanceEditor
import com.imcode.imcms.admin.doc.meta.lifecycle.LifeCycleEditor
import com.imcode.imcms.admin.doc.meta.category.CategoryEditor
import com.vaadin.ui.UI
import com.vaadin.server.Sizeable
import com.imcode.imcms.vaadin.Editor

/**
 * Doc's meta editor.
 *
 * @param doc used to initialize editor's values. It is never modified.
 */
// todo: i18n editors
// todo: appearance: alias prefix should be set to context path
// todo: appearance: alias check unique while typing
// todo: appearance: I18nMetaEditorUI link image instead of text
// todo: appearance:
//   add custom case class Target(id: String, boolean: Custom), so can check on override
//   legacy target support: up to v 6.x it was possible to define custom target for a doc
//   if this doc has custom target, then adds this target to the targets combo-box as a last item
class MetaEditor(doc: DocumentDomainObject) extends Editor with ImcmsServicesSupport {

  type Data = (DocumentDomainObject, Map[DocumentLanguage, I18nMeta])

  private var appearanceEditorOpt = Option.empty[AppearanceEditor]
  private var lifeCycleEditorOpt = Option.empty[LifeCycleEditor]
  private var accessEditorOpt = Option.empty[AccessEditor]
  private var searchSettingsEditorOpt = Option.empty[SearchSettingsEditor]
  private var categoryEditorOpt = Option.empty[CategoryEditor]
  private var profileEditorOpt = Option.empty[ProfileEditor]

  val ui = new MetaEditorUI |>> { ui =>
    ui.treeEditors.addItem("doc_meta_editor.menu_item.life_cycle", "doc_meta_editor.menu_item.life_cycle".i)
    ui.treeEditors.addItem("doc_meta_editor.menu_item.appearance", "doc_meta_editor.menu_item.appearance".i)
    ui.treeEditors.addItem("doc_meta_editor.menu_item.access", "doc_meta_editor.menu_item.access".i)
    ui.treeEditors.addItem("doc_meta_editor.menu_item.search", "doc_meta_editor.menu_item.search".i)
    ui.treeEditors.addItem("doc_meta_editor.menu_item.categories", "doc_meta_editor.menu_item.categories".i)

    // According to v.4.x.x may be defined for text docs only
    // todo: disable profile tag =or= add lable =not supported/available =or= show empty page instead of editor
    if (doc.isInstanceOf[TextDocumentDomainObject]) ui.treeEditors.addItem("doc_meta_editor.menu_item.profile", "doc_meta_editor.menu_item.profile".i)

    ui.treeEditors.addValueChangeHandler {
      ui.treeEditors.value match {
        case "doc_meta_editor.menu_item.life_cycle" =>
          if (lifeCycleEditorOpt.isEmpty) lifeCycleEditorOpt = Some(new LifeCycleEditor(doc.getMeta))

          ui.pnlCurrentEditor.setContent(lifeCycleEditorOpt.get.ui)

        case "doc_meta_editor.menu_item.appearance" =>
          if (appearanceEditorOpt.isEmpty) {
            val i18nMetas: Map[DocumentLanguage, I18nMeta] = doc.getMetaId.asOption match {
              case Some(id) =>
                imcmsServices.getDocumentMapper.getI18nMetas(id).asScala.map(m => m.getLanguage -> m).toMap
              case _ =>
                Map.empty
            }

            appearanceEditorOpt = Some(
              new AppearanceEditor(doc.getMeta, i18nMetas)
            )
          }

          ui.pnlCurrentEditor.setContent(appearanceEditorOpt.get.ui)

        case "doc_meta_editor.menu_item.access" =>
          if (accessEditorOpt.isEmpty) accessEditorOpt = Some(new AccessEditor(doc, UI.getCurrent.imcmsUser))

          ui.pnlCurrentEditor.setContent(accessEditorOpt.get.ui)

        case "doc_meta_editor.menu_item.search" =>
          if (searchSettingsEditorOpt.isEmpty) searchSettingsEditorOpt = Some(new SearchSettingsEditor(doc.getMeta))

          ui.pnlCurrentEditor.setContent(searchSettingsEditorOpt.get.ui)

        case "doc_meta_editor.menu_item.categories" =>
          if (categoryEditorOpt.isEmpty) categoryEditorOpt = Some(new CategoryEditor(doc.getMeta))

          ui.pnlCurrentEditor.setContent(categoryEditorOpt.get.ui)

        case "doc_meta_editor.menu_item.profile" =>
          if (profileEditorOpt.isEmpty) profileEditorOpt = Some(new ProfileEditor(doc.asInstanceOf[TextDocumentDomainObject], UI.getCurrent.imcmsUser))

          ui.pnlCurrentEditor.setContent(profileEditorOpt.get.ui)

        case _ =>
      }
    }

    ui.sp.setSplitPosition(20, Sizeable.UNITS_PERCENTAGE)
  } // ui

  resetValues()

  def collectValues(): ErrorsOrData = {
    case class Collector(errorsOrData: ErrorsOrData) {
      def merge[A](subEditorErrorsOrDataOpt: => Option[Seq[ErrorMsg] Either A])(fn: (Data, A) => Data): Collector = {
        subEditorErrorsOrDataOpt match {
          case None => this
          case Some(subEditorErrorsOrData) => subEditorErrorsOrData match {
            case Right(_) if errorsOrData.isLeft => this
            case Right(subEditorData) => Collector(Right(fn(errorsOrData.right.get, subEditorData)))
            case Left(subEditorErrors) if errorsOrData.isRight => Collector(Left(subEditorErrors))
            case Left(subEditorErrors) => Collector(Left(errorsOrData.left.get ++ subEditorErrors))
          }
        }
      }
    }

    val dc = doc.clone()
    val i18nMetas = Map(dc.getLanguage -> dc.getI18nMeta)

    Collector(
      Right((dc, i18nMetas))
    ).merge(appearanceEditorOpt.map(_.collectValues)) {
      case ((dc, _), appearance) => (dc, appearance.i18nMetas) |>> { _ =>
        dc.getMeta.setEnabledLanguages(appearance.enabledLanguages.asJava)
        dc.getMeta.setI18nShowMode(appearance.disabledLanguageShowSetting)
        dc.getMeta.setAlias(appearance.alias.orNull)
        dc.getMeta.setTarget(appearance.target)
      }
    }.merge(lifeCycleEditorOpt.map(_.collectValues())) {
      case (data@(dc, _), lifeCycle) => data |>> { _ =>
        dc.getMeta.setPublicationStatus(lifeCycle.publicationStatus)
        dc.getMeta.setPublicationStartDatetime(lifeCycle.publicationStartDt)
        dc.getMeta.setPublicationEndDatetime(lifeCycle.publicationEndDt.orNull)
        dc.getMeta.setArchivedDatetime(lifeCycle.archiveDt.orNull)
        dc.getMeta.setPublisherId(lifeCycle.publisher.map(p => p.getId : JInteger).orNull)
        //???dc.setVersion(new DocumentVersion() state.versionNo)
        dc.getMeta.setCreatedDatetime(lifeCycle.createdDt)
        dc.getMeta.setModifiedDatetime(lifeCycle.modifiedDt)
        dc.getMeta.setCreatorId(lifeCycle.creator.map(c => c.getId : JInteger).orNull)
        //???dc.getMeta.setModifierId
      }
    }.merge(accessEditorOpt.map(_.collectValues)) {
      case (data@(dc, _), permissions) => data |>> { _ =>
        dc.setRoleIdsMappedToDocumentPermissionSetTypes(permissions.rolesPermissions)
        dc.getPermissionSets.setRestricted1(permissions.restrictedOnePermSet)
        dc.getPermissionSets.setRestricted2(permissions.restrictedTwoPermSet)
        dc.setRestrictedOneMorePrivilegedThanRestrictedTwo(permissions.isRestrictedOneMorePrivilegedThanRestrictedTwo)
        dc.setLinkedForUnauthorizedUsers(permissions.isLinkedForUnauthorizedUsers)
        dc.setLinkableByOtherUsers(permissions.isLinkableByOtherUsers)
      }
    }.merge(categoryEditorOpt.map(_.collectValues)) {
      case (data@(dc, _), categories) => data |>> { _ =>
        dc.setCategoryIds(categories.categoriesIds.asJava)
      }
    }.merge(profileEditorOpt.map(_.collectValues)) {
      case (data@(tdc: TextDocumentDomainObject, _), profile) => data |>> { _ =>
        tdc.setDefaultTemplateId(profile.defaultTemplate)
        tdc.getPermissionSetsForNewDocuments.setRestricted1(profile.restrictedOnePermSet)
        tdc.getPermissionSetsForNewDocuments.setRestricted2(profile.restrictedTwoPermSet)
        tdc.setDefaultTemplateIdForRestricted1(profile.restrictedOneTemplate)
        tdc.setDefaultTemplateIdForRestricted2(profile.restrictedTwoTemplate)
      }

      case (data, _) => data
    }.errorsOrData
    //      //// ?????????????????????????????????????
    //      ////    ui.cbDefaultTemplate.value,
    //      ////    restrictedOnePermSet, // ??? clone
    //      ////    restrictedTwoPermSet, // ??? clone
    //      ////    ui.cbRestrictedOneDefaultTemplate,
    //      ////    ui.cbRestrictedTwoDefaultTemplate
  } // data

  def resetValues() {
    appearanceEditorOpt = None
    lifeCycleEditorOpt = None
    accessEditorOpt = None
    searchSettingsEditorOpt = None
    categoryEditorOpt = None
    profileEditorOpt = None

    ui.treeEditors.selection = "doc_meta_editor.menu_item.life_cycle"
  }
}
