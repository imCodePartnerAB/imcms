package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.orm.DocLanguage
import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.dao.hibernate.HibernateSupport

@Transactional(rollbackFor = Array(classOf[Throwable]))
class LanguageDao extends HibernateSupport {


  def getAllLanguages(): JList[DocLanguage] = hibernate.listAll()


  def getById(id: JInteger): DocLanguage = hibernate.getByNamedQueryAndNamedParams("I18nLanguage.getById", "id" -> id)


  def getByCode(code: String): DocLanguage =
    hibernate.getByNamedQueryAndNamedParams("I18nLanguage.getByCode", "code" -> code)


  def saveLanguage(language: DocLanguage): DocLanguage = language.clone() |> hibernate.saveOrUpdate


  def deleteLanguage(id: JInteger) =
    hibernate.bulkUpdateByNamedParams("DELETE FROM I18nLanguage l WHERE l.id = :id", "id" -> id)
}
