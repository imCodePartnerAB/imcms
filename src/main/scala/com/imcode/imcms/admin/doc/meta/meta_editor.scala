package com.imcode
package imcms
package admin.doc.meta

import scala.collection.JavaConverters._

import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import _root_.imcode.server.document.{DocumentDomainObject}

import com.imcode.imcms.api._
import com.imcode.imcms.vaadin._
import com.imcode.imcms.admin.doc.meta.access.AccessEditor
import com.imcode.imcms.admin.doc.meta.search.SearchSettingsEditor
import com.imcode.imcms.admin.doc.meta.profile.ProfileEditor
import com.imcode.imcms.admin.doc.meta.appearance.AppearanceEditor
import com.imcode.imcms.admin.doc.meta.lifecycle.LifeCycleEditor
import com.imcode.imcms.admin.doc.meta.category.CategoryEditor

import com.vaadin.terminal.{Sizeable, ExternalResource}
import com.vaadin.data.Validator
import com.vaadin.data.Validator.InvalidValueException
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.ui.dialog._


/**
 * Doc's meta editor.
 *
 * @param doc used to initialize editor's values. It is never modified.
 */
// todo: i18n
// todo: appearance: alias prefix should be set to context path
// todo: appearance: alias check unique while typing
// todo: appearance: I18nMetaEditorUI link image instead of text
// todo: appearance:
//   add custom case class Target(id: String, boolean: Custom), so can check on override
//   legacy target support: up to v 6.x it was possible to define custom target for a doc
//   if this doc has custom target, then adds this target to the targets combo-box as a last item
class MetaEditor(doc: DocumentDomainObject) extends Editor with ImcmsServicesSupport {

  type Data = (DocumentDomainObject, Map[I18nLanguage, I18nMeta])

  private var appearanceEditorOpt = Option.empty[AppearanceEditor]
  private var lifeCycleEditorOpt = Option.empty[LifeCycleEditor]
  private var accessEditorOpt = Option.empty[AccessEditor]
  private var searchSettingsEditorOpt = Option.empty[SearchSettingsEditor]
  private var categoryEditorOpt = Option.empty[CategoryEditor]
  private var profileEditorOpt = Option.empty[ProfileEditor]

  val ui = new MetaEditorUI |>> { ui =>
    ui.treeMenu.addItem("Appearance")
    ui.treeMenu.addItem("Life cycle")
    ui.treeMenu.addItem("Access")
    ui.treeMenu.addItem("Search")
    ui.treeMenu.addItem("Categories")

    // According to v.4.x.x may be defined for text docs only
    // todo: disable profile tag =or= add lable =not supported/available =or= show empty page instead of editor
    if (doc.isInstanceOf[TextDocumentDomainObject]) ui.treeMenu.addItem("Profile")

    ui.treeMenu.addValueChangeHandler {
      ui.treeMenu.getValue match {
        case "Appearance" =>
          if (appearanceEditorOpt.isEmpty) {
            val i18nMetas: Map[I18nLanguage, I18nMeta] = Option(doc.getIdValue) match {
              case Some(id) =>
                imcmsServices.getDocumentMapper.getI18nMetas(id).asScala.map(m => m.getLanguage -> m).toMap
              case _ =>
                Map.empty
            }

            appearanceEditorOpt = Some(
              new AppearanceEditor(doc.getMeta, i18nMetas)
            )
          }

          ui.pnlMenuItem.setContent(appearanceEditorOpt.get.ui)

        case "Life cycle" =>
          if (lifeCycleEditorOpt.isEmpty) lifeCycleEditorOpt = Some(new LifeCycleEditor(doc.getMeta))

          ui.pnlMenuItem.setContent(lifeCycleEditorOpt.get.ui)

        case "Access" =>
          if (accessEditorOpt.isEmpty) accessEditorOpt =
            Some(new AccessEditor(doc, ui.getApplication.user))

          ui.pnlMenuItem.setContent(accessEditorOpt.get.ui)

        case "Search" =>
          if (searchSettingsEditorOpt.isEmpty) searchSettingsEditorOpt = Some(new SearchSettingsEditor(doc.getMeta))

          ui.pnlMenuItem.setContent(searchSettingsEditorOpt.get.ui)

        case "Categories" =>
          if (categoryEditorOpt.isEmpty) categoryEditorOpt = Some(new CategoryEditor(doc.getMeta))

          ui.pnlMenuItem.setContent(categoryEditorOpt.get.ui)

        case "Profile" =>
          if (profileEditorOpt.isEmpty) profileEditorOpt = Some(new ProfileEditor(doc.asInstanceOf[TextDocumentDomainObject], ui.getApplication.user))

          ui.pnlMenuItem.setContent(profileEditorOpt.get.ui)

        case _ =>
      }
    }

    ui.sp.setSplitPosition(25, Sizeable.UNITS_PERCENTAGE)
    ui.treeMenu.select("Appearance")
  } // ui

  def collectValues(): ErrorsOrData = {
    case class UberData(uberData: ErrorsOrData) {
      def merge[B](childDataOpt: => Option[Either[Seq[ErrorMsg], B]])(fn: (Data, B) => Data): UberData =
        childDataOpt match {
          case None => this
          case Some(Right(_)) if uberData.isLeft => this
          case Some(Right(childValue)) => UberData(Right(fn(uberData.right.get, childValue)))
          case Some(Left(childErrorMsgs)) if uberData.isRight => UberData(Left(childErrorMsgs))
          case Some(Left(childErrorMsgs)) => UberData(Left(uberData.left.get ++ childErrorMsgs))
        }
    }

    UberData(
      Right(doc.clone, Map.empty[I18nLanguage, I18nMeta])
    ).merge(appearanceEditorOpt.map(_.collectValues())) {
      case ((dc, _), appearance) => (dc, appearance.i18nMetas) |>> { _ =>
        dc.getMeta.setEnabledLanguages(appearance.enabledLanguages.asJava)
        dc.getMeta.setI18nShowMode(appearance.disabledLanguageShowSetting)
        dc.getMeta.setAlias(appearance.alias.orNull)
        dc.getMeta.setTarget(appearance.target)
      }
    }.merge(lifeCycleEditorOpt.map(_.collectValues())) {
      case (uberData @ (dc, _), lifeCycle) => uberData |>> { _ =>
        dc.getMeta.setPublicationStatus(lifeCycle.publicationStatus)
        dc.getMeta.setPublicationStartDatetime(lifeCycle.publicationStart)
        dc.getMeta.setPublicationEndDatetime(lifeCycle.publicationEnd.orNull)
        dc.getMeta.setPublicationEndDatetime(lifeCycle.publicationEnd.orNull)
        dc.getMeta.setPublisherId(lifeCycle.publisher.map(p => Int box p.getId).orNull)
        //???dc.setVersion(new DocumentVersion() state.versionNo)
        dc.getMeta.setCreatedDatetime(lifeCycle.created)
        dc.getMeta.setModifiedDatetime(lifeCycle.modified)
        dc.getMeta.setCreatorId(lifeCycle.creator.map(c => Int box c.getId).orNull)
        //???dc.getMeta.setModifierId
      }
    }.merge(accessEditorOpt.map(_.collectValues)) {
      case (uberData @ (dc, _), permissions) => uberData |>> { _ =>
        dc.setRoleIdsMappedToDocumentPermissionSetTypes(permissions.rolesPermissions)
        dc.getPermissionSets.setRestricted1(permissions.restrictedOnePermSet)
        dc.getPermissionSets.setRestricted2(permissions.restrictedTwoPermSet)
        dc.setRestrictedOneMorePrivilegedThanRestrictedTwo(permissions.isRestrictedOneMorePrivilegedThanRestrictedTwo)
        dc.setLinkedForUnauthorizedUsers(permissions.isLinkedForUnauthorizedUsers)
        dc.setLinkableByOtherUsers(permissions.isLinkableByOtherUsers)
      }
    }.merge(categoryEditorOpt.map(_.collectValues)) {
      case (uberData @ (dc, _), categories) => uberData |>> { _ =>
        dc.setCategoryIds(categories.categoriesIds.asJava)
      }
    }.merge(profileEditorOpt.map(_.collectValues)) {
      case (uberData @ (tdc: TextDocumentDomainObject, _), profile) => uberData |>> { _ =>
        tdc.setDefaultTemplateId(profile.defaultTemplate)
        tdc.getPermissionSetsForNewDocuments.setRestricted1(profile.restrictedOnePermSet)
        tdc.getPermissionSetsForNewDocuments.setRestricted2(profile.restrictedTwoPermSet)
        tdc.setDefaultTemplateIdForRestricted1(profile.restrictedOneTemplate)
        tdc.setDefaultTemplateIdForRestricted2(profile.restrictedTwoTemplate)
      }

      case (uberData, _) => uberData
    }.uberData
    //      //// ?????????????????????????????????????
    //      ////    ui.cbDefaultTemplate.value,
    //      ////    restrictedOnePermSet, // ??? clone
    //      ////    restrictedTwoPermSet, // ??? clone
    //      ////    ui.cbRestrictedOneDefaultTemplate,
    //      ////    ui.cbRestrictedTwoDefaultTemplate
  } // data

  def resetValues() {}
}


/**
 * Editor UI's main component is a horizontal split panel.
 * -Left component - navigation tree.
 * -Right component - scrollable panel.
 */
class MetaEditorUI extends VerticalLayout with FullSize with NoMargin {

  val sp = new HorizontalSplitPanel with FullSize
  val treeMenu = new Tree with SingleSelect[MenuItemId] with NoChildrenAllowed with Immediate
  val pnlMenuItem = new Panel with LightStyle with FullSize

  sp.setFirstComponent(treeMenu)
  sp.setSecondComponent(pnlMenuItem)

  addComponent(sp)
}