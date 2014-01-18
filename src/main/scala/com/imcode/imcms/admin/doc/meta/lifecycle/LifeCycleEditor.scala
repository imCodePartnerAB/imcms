package com.imcode
package imcms
package admin.doc.meta.lifecycle

import scala.collection.JavaConverters._
import com.imcode.imcms.admin.access.user.select.{UserSingleSelectView, UserSingleSelect}
import com.imcode.imcms.api.{DocumentVersion, Document, Meta}

import java.util.Date
import com.vaadin.ui._
import com.imcode.imcms.vaadin.component._
import com.imcode.imcms.vaadin.data._
import com.imcode.imcms.vaadin.event._
import _root_.imcode.server.document.LifeCyclePhase
import _root_.imcode.server.user.UserDomainObject
import _root_.imcode.server.document.textdocument.TextDocumentDomainObject
import com.imcode.imcms.vaadin.Editor


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


  override val view = new LifeCycleEditorView |>> { w =>
    for (phase <- LifeCyclePhase.ALL) {
      new Label with UndefinedSize |>> { lbl =>
        lbl.setCaption(s"doc_publication_phase.$phase".i)
        lbl.setIcon(Theme.Icon.Doc.phase(phase))
      } |> w.publication.lytPhase.addComponent
    }

    w.publication.chkEnd.addValueChangeHandler { _ =>
      w.publication.calEnd.setEnabled(w.publication.chkEnd.checked)

      updatePhase()
    }

    w.publication.chkArchive.addValueChangeHandler { _ =>
      w.publication.calArchive.setEnabled(w.publication.chkArchive.checked)

      updatePhase()
    }

    w.publication.sltStatus.addValueChangeHandler { _ =>
      updatePhase()
    }

    w.publication.calStart.addValueChangeHandler { _ =>
      updatePhase()
    }

    w.publication.calEnd.addValueChangeHandler { _ =>
      updatePhase()
    }

    w.publication.calArchive.addValueChangeHandler { _ =>
      updatePhase()
    }
  }

  resetValues()

  override def resetValues() {
    // version
    val (versionsNos, defaultVersionNo) = meta.getId match {
      case null =>
        Seq[JInteger](DocumentVersion.WORKING_VERSION_NO) -> DocumentVersion.WORKING_VERSION_NO

      case id =>
        val versionInfo = imcmsServices.getDocumentMapper.getDocumentVersionInfo(id)
        versionInfo.getVersions.asScala.map(_.getNo) -> versionInfo.getDefaultVersion.getNo
    }

    view.info.ussCreator.selection = meta.getCreatorId.asOption.map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))
    view.info.ussModifier.selection = None
    view.publication.ussPublisher.selection = meta.getPublisherId.asOption.map(imcmsServices.getImcmsAuthenticatorAndUserAndRoleMapper.getUser(_))

    view.publication.sltVersion.removeAllItems()
    versionsNos.foreach(no => view.publication.sltVersion.addItem(no, no.toString))
    view.publication.sltVersion.setItemCaption(DocumentVersion.WORKING_VERSION_NO, "doc.version.working".i)
    view.publication.sltVersion.select(defaultVersionNo)

    view.publication.sltStatus.select(meta.getPublicationStatus)

    view.publication.calStart.value = meta.getPublicationStartDatetime.asOption.getOrElse(new Date)
    view.publication.calEnd.value = meta.getPublicationEndDatetime
    view.publication.calArchive.value = meta.getArchivedDatetime

    view.publication.chkEnd.checked = meta.getPublicationEndDatetime != null
    view.publication.chkArchive.checked = meta.getPublicationEndDatetime != null

    updatePhase()
  }

  private def updatePhase() {
    val doc = new TextDocumentDomainObject() |>> { doc =>
      doc.setPublicationStartDatetime(view.publication.calStart.value)
      doc.setPublicationEndDatetime(if (view.publication.chkEnd.checked) view.publication.calEnd.value else null)
      doc.setArchivedDatetime(if (view.publication.chkArchive.checked) view.publication.calArchive.value else null)
      doc.setPublicationStatus(view.publication.sltStatus.selection)
    }

    val activePhase = doc.getLifeCyclePhase

    for ((phase, index) <- LifeCyclePhase.ALL.zipWithIndex) {
      view.publication.lytPhase.getComponent(index).setEnabled(phase == activePhase)
    }
  }

  override def collectValues(): ErrorsOrData = {
    val errors = scala.collection.mutable.Buffer.empty[String]

    if (view.publication.chkArchive.checked && view.publication.calArchive.valueOpt.isEmpty) {
      errors.append("Document archive is enabled but date is not specified")
    }

    if (view.publication.chkEnd.checked && view.publication.calEnd.valueOpt.isEmpty) {
      errors.append("Document expiration is enabled but date is not specified")
    }

    if (errors.nonEmpty) {
      Left(errors.toSeq)
    } else {
      Right(
        Data(
          view.publication.sltStatus.selection,
          view.publication.calStart.value,
          when(view.publication.chkArchive.checked)(view.publication.calArchive.value),
          when(view.publication.chkEnd.checked)(view.publication.calEnd.value),
          view.info.ussCreator.selection,
          view.publication.sltVersion.selection.intValue,
          view.info.dCreated.calDate.value,
          view.info.dModified.calDate.value,
          view.info.ussCreator.selection,
          view.info.ussModifier.selection
        )
      )
    }
  }
}
