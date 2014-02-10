package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.orm.DocLanguage
import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.dao.hibernate.HibernateSupport

@Transactional(rollbackFor = Array(classOf[Throwable]))
class DocLanguageDao extends HibernateSupport {


  def getAllLanguages(): JList[DocLanguage] = hibernate.listAll()


  def getById(id: Int): DocLanguage = hibernate.get[DocLanguage](id)


  def getByCode(code: String): DocLanguage =
    hibernate.getByNamedQueryAndNamedParams("DocLanguage.getByCode", "code" -> code)


  def saveLanguage(language: DocLanguage): DocLanguage = language |> hibernate.saveOrUpdate


  def deleteLanguage(id: Int) =
    hibernate.bulkUpdateByNamedParams("DELETE FROM DocLanguage l WHERE l.id = :id", "id" -> id)
}
