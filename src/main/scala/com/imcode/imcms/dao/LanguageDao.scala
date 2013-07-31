package com.imcode
package imcms.dao

import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.api.DocumentLanguage
import com.imcode.imcms.dao.hibernate.HibernateSupport

@Transactional(rollbackFor = Array(classOf[Throwable]))
class LanguageDao extends HibernateSupport {


  def getAllLanguages(): JList[DocumentLanguage] = hibernate.listAll()


  def getById(id: JInteger): DocumentLanguage = hibernate.getByNamedQueryAndNamedParams("I18nLanguage.getById", "id" -> id)


  def getByCode(code: String): DocumentLanguage =
    hibernate.getByNamedQueryAndNamedParams("I18nLanguage.getByCode", "code" -> code)


  def saveLanguage(language: DocumentLanguage): DocumentLanguage = language.clone() |> hibernate.saveOrUpdate


  def deleteLanguage(id: JInteger) =
    hibernate.bulkUpdateByNamedParams("DELETE FROM I18nLanguage l WHERE l.id = :id", "id" -> id)
}
