package com.imcode
package imcms
package admin.doc.meta.lifecycle

import scala.collection.JavaConverters._
import com.imcode.imcms.admin.access.user.{UserSingleSelectUI, UserSingleSelect}
import com.imcode.imcms.api.{DocumentVersion, Document, Meta}
import com.imcode.imcms.vaadin._
import java.util.Date
import com.vaadin.ui._
import com.imcode.imcms.vaadin.ui._
import com.imcode.imcms.vaadin.data._
import com.vaadin.terminal.ThemeResource
import _root_.imcode.server.document.LifeCyclePhase
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject

// todo: ??? remember lytDate.chkEnd date when uncheked ???
class LifeCycleEditor(meta: Meta) extends Editor with ImcmsServicesSupport {

  case class Data(
    publicationStatus: Document.PublicationStatus,
    publicationStart: Date,
    publicationEnd: Option[Date],
    publisher: Option[UserDomainObject],
    versionNo: Int,
    created: Date,
    modified: Date,
    creator: Option[UserDomainObject],
    modifier: Option[UserDomainObject]
  )

  val ui = new LifeCycleEditorUI |>> { ui =>
    for (phase <- LifeCyclePhase.ALL) {
      new Label with UndefinedSize |>> { lbl =>
        lbl.setCaption("doc_publication_phase.%s".format(phase).i)
        lbl.setIcon(Theme.Icon.Doc.phase(phase))
      } |> ui.publication.lytPhase.addComponent
    }

    ui.publication.chkEnd.addValueChangeHandler {
      ui.publication.calEnd.setEnabled(ui.publication.chkEnd.checked)
      updatePhase()
    }

    ui.publication.chkArchive.addValueChangeHandler {
      ui.publication.calArchive.setEnabled(ui.publication.chkArchive.checked)
      updatePhase()
    }

    ui.publication.sltStatus.addValueChangeHandler {
      updatePhase()
    }

    ui.publication.calStart.addValueChangeHandler {
      updatePhase()
    }

    ui.publication.calEnd.addValueChangeHandler {
      updatePhase()
    }

    ui.publication.calArchive.addValueChangeHandler {
      updatePhase()
    }
  }

  def resetValues() {
    // version
    val (versionsNos, defaultVersionNo) = meta.getId match {
      case null =>
        Seq[JInteger](DocumentVersion.WORKING_VERSION_NO) -> DocumentVersion.WORKING_VERSION_NO

      case id =>
        val versionInfo = imcmsServices.getDocumentMapper.getDocumentVersionInfo(id)
        versionInfo.getVersions.asScala.map(_.getNo) -> versionInfo.getDefaultVersion.getNo
    }

    ui.info.ussCreator.selection = Option(meta.getCreatorId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    ui.info.ussModifier.selection = None
    ui.publication.ussPublisher.selection = Option(meta.getPublisherId).map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))

    ui.publication.sltVersion.removeAllItems()
    versionsNos.foreach(no => ui.publication.sltVersion.addItem(no, no.toString))
    ui.publication.sltVersion.setItemCaption(DocumentVersion.WORKING_VERSION_NO, "doc.version.working".i)
    ui.publication.sltVersion.select(defaultVersionNo)

    ui.publication.sltStatus.select(meta.getPublicationStatus)

    ui.publication.calStart.value = meta.getPublicationStartDatetime |> opt getOrElse new Date
    ui.publication.calEnd.value = meta.getPublicationEndDatetime
    ui.publication.calArchive.value = meta.getArchivedDatetime

    ui.publication.chkEnd.checked = meta.getPublicationEndDatetime != null
    ui.publication.chkArchive.checked = meta.getPublicationEndDatetime != null

    updatePhase()
  }

  private def updatePhase() {
    val doc = new TextDocumentDomainObject() |>> { doc =>
      doc.setPublicationStartDatetime(ui.publication.calStart.value)
      doc.setPublicationEndDatetime(if (ui.publication.chkEnd.checked) ui.publication.calEnd.value else null)
      doc.setArchivedDatetime(if (ui.publication.chkArchive.checked) ui.publication.calArchive.value else null)
      doc.setPublicationStatus(ui.publication.sltStatus.value)
    }

    val activePhase = doc.getLifeCyclePhase

    for ((phase, index) <- LifeCyclePhase.ALL.zipWithIndex) {
      ui.publication.lytPhase.getComponent(index).setEnabled(phase == activePhase)
    }
  }

  def collectValues(): ErrorsOrData = Right(
    Data(
      ui.publication.sltStatus.value,
      ui.publication.calStart.value,
      ui.publication.calEnd.valueOpt,
      ui.info.ussCreator.selection,
      ui.publication.sltVersion.value.intValue,
      ui.info.dCreated.calDate.value,
      ui.info.dModified.calDate.value,
      ui.info.ussCreator.selection,
      ui.info.ussModifier.selection
    )
  )

  resetValues()
}
