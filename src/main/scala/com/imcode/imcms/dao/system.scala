package com.imcode
package imcms.dao

import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.api.{I18nLanguage, SystemProperty, IPAccess}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class SystemDao extends HibernateSupport {

  def getProperties(): JList[SystemProperty] = hibernate.listAll()

  def getProperty(name: String): SystemProperty = hibernate.getByQuery(
    "SELECT p FROM SystemProperty p WHERE p.name = ?", name)

  def saveProperty(property: SystemProperty) = hibernate.saveOrUpdate(property)
}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class IPAccessDao extends HibernateSupport {

    def getAll(): JList[IPAccess] = hibernate.listAll()

    def delete(id: JInteger) = hibernate.bulkUpdateByNamedParams(
        "DELETE FROM IPAccess i WHERE i.id = :id", "id" -> id
    )

    def save(ipAccess: IPAccess ) = hibernate.saveOrUpdate(ipAccess)

    def get(id: JInteger): IPAccess = hibernate.get(id)
}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class LanguageDao extends HibernateSupport {


  def getAllLanguages(): JList[I18nLanguage] = hibernate.listAll()


  def getById(id: JInteger): I18nLanguage = hibernate.getByNamedQueryAndNamedParams("I18nLanguage.getById", "id" -> id)


  def getByCode(code: String): I18nLanguage =
    hibernate.getByNamedQueryAndNamedParams("I18nLanguage.getByCode", "code" -> code)


  def saveLanguage(language: I18nLanguage) = hibernate.saveOrUpdate(language.clone())


  def deleteLanguage(id: JInteger) =
    hibernate.bulkUpdateByNamedParams("DELETE FROM I18nLanguage l WHERE l.id = :id", "id" -> id)
}