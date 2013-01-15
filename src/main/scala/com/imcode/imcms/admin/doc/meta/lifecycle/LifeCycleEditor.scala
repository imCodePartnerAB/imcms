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
import _root_.imcode.server.document.LifeCyclePhase
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject


class LifeCycleEditor(meta: Meta) extends Editor with ImcmsServicesSupport {

  case class Data(
    publicationStatus: Document.PublicationStatus,
    publicationStartDt: Date,
    archiveDt: Option[Date],
    publicationEndDt: Option[Date],
    publisher: Option[UserDomainObject],
    versionNo: Int,
    createdDt: Date,
    modifiedDt: Date,
    creator: Option[UserDomainObject],
    modifier: Option[UserDomainObject]
  )


  val ui = new LifeCycleEditorUI |>> { ui =>
    for (phase <- LifeCyclePhase.ALL) {
      new Label with UndefinedSize |>> { lbl =>
        lbl.setCaption(s"doc_publication_phase.$phase".i)
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

  resetValues()

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
      doc.setPublicationEndDatetime(ui.publication.chkEnd.checked ? ui.publication.calEnd.value | null)
      doc.setArchivedDatetime(ui.publication.chkArchive.checked ? ui.publication.calArchive.value | null)
      doc.setPublicationStatus(ui.publication.sltStatus.value)
    }

    val activePhase = doc.getLifeCyclePhase

    for ((phase, index) <- LifeCyclePhase.ALL.zipWithIndex) {
      ui.publication.lytPhase.getComponent(index).setEnabled(phase == activePhase)
    }
  }

  def collectValues(): ErrorsOrData = {
    val errors = scala.collection.mutable.Buffer.empty[String]

    if (ui.publication.chkArchive.checked && ui.publication.calArchive.valueOpt.isEmpty) {
      errors.append("Document archive is enabled but date is not specified")
    }

    if (ui.publication.chkEnd.checked && ui.publication.calEnd.valueOpt.isEmpty) {
      errors.append("Document expiration is enabled but date is not specified")
    }

    if (errors.nonEmpty) {
      Left(errors.toSeq)
    } else {
      Right(
        Data(
          ui.publication.sltStatus.value,
          ui.publication.calStart.value,
          ui.publication.chkArchive.checked ? Some(ui.publication.calArchive.value) | None,
          ui.publication.chkEnd.checked ? Some(ui.publication.calEnd.value) | None,
          ui.info.ussCreator.selection,
          ui.publication.sltVersion.value.intValue,
          ui.info.dCreated.calDate.value,
          ui.info.dModified.calDate.value,
          ui.info.ussCreator.selection,
          ui.info.ussModifier.selection
        )
      )
    }
  }
}
