package com.imcode
package imcms.dao

import org.springframework.transaction.annotation.Transactional
import java.util.TreeMap

/**
 * Native queries - moved from the DocumentMapper.
 * TODO: Rewrite native queries using HQL
 */
@Transactional(rollbackFor = Array(classOf[Throwable]))
class NativeQueriesDao extends HibernateSupport {

  def getAllMimeTypes(): JList[String] =
    hibernate.listBySqlQuery[Array[AnyRef]](
      "SELECT mime FROM mime_types WHERE mime_id > 0 ORDER BY mime_id"
    ) map (_(0).toString)

  def getAllMimeTypesWithDescriptions(languageIso639_2: String): JList[Array[String]] =
    hibernate.listBySqlQuery[Array[AnyRef]](
      "SELECT mime, mime_name FROM mime_types WHERE lang_prefix = ? AND mime_id > 0 ORDER BY mime_id", languageIso639_2
    ) map {
      _.map(_.toString)
    }

  def getParentDocumentAndMenuIdsForDocument(documentId: JInteger): JList[AnyRef] =
    hibernate.listBySqlQuery[Array[AnyRef]](
      """SELECT doc_id, no FROM imcms_text_doc_menu_items childs, imcms_text_doc_menus menus " +
         WHERE menus.id = childs.menu_id AND to_doc_id = ?""", documentId
    ) map (_(0))

  def getDocumentsWithPermissionsForRole(roleId: JInteger): JList[JInteger] =
    hibernate.listBySqlQuery[Array[AnyRef]](
      "SELECT meta_id FROM roles_rights WHERE role_id = ? ORDER BY meta_id", roleId
    ) map { case Array(metaId: JInteger) => metaId }


  def getAllDocumentTypeIdsAndNamesInUsersLanguage(languageIso639_2: String): JMap[JInteger, String] =
    hibernate.listBySqlQuery[Array[AnyRef]](
      "SELECT doc_type, type FROM doc_types WHERE lang_prefix = ? ORDER BY doc_type", languageIso639_2
    ) |> {
      rows =>
        new TreeMap[JInteger, String] |< { m =>
          for (Array(typeId: JInteger, name: String) <- rows) m.put(typeId, name)
        }
    }

  def getDocumentMenuPairsContainingDocument(documentId: JInteger): JList[Array[JInteger]] =
    hibernate.listBySqlQuery[Array[AnyRef]](
      """SELECT doc_id, no FROM imcms_text_doc_menus menus, imcms_text_doc_menu_items childs " +
          WHERE menus.id = childs.menu_id AND childs.to_doc_id = ? ORDER BY doc_id, no""",
      documentId
    ) map (_.map(_.asInstanceOf[JInteger]))
}