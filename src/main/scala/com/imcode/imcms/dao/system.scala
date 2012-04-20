package com.imcode
package imcms.dao

import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.api.{I18nLanguage, SystemProperty, IPAccess}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class SystemDao extends HibernateSupport {

  def getProperties() = hibernate.listAll[SystemProperty]()

  def getProperty(name: String) = hibernate.find[SystemProperty](
    "SELECT p FROM SystemProperty p WHERE p.name = ?", name)

  def saveProperty(property: SystemProperty) = hibernate.saveOrUpdate(property)
}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class IPAccessDao extends HibernateSupport {

    def getAll() = hibernate.listAll[IPAccess]()

    def delete(id: JInteger) = hibernate.bulkUpdateByNamedParams(
        "DELETE FROM IPAccess i WHERE i.id = :id", "id" -> id
    )

    def save(ipAccess: IPAccess ) = hibernate.saveOrUpdate(ipAccess)

    def get(id: JInteger) = hibernate.get[IPAccess](id)
}


@Transactional(rollbackFor = Array(classOf[Throwable]))
class LanguageDao extends HibernateSupport {

  // @Transactional
  def getAllLanguages() = hibernate.listAll[I18nLanguage]()

  //@Transactional
  def getById(id: JInteger) = hibernate.findByNamedQueryAndNamedParams[I18nLanguage]("I18nLanguage.getById", "id" -> id)

  //@Transactional
  def getByCode(code: String) = hibernate.findByNamedQueryAndNamedParams[I18nLanguage]("I18nLanguage.getByCode", "code" -> code)

  //@Transactional
  def saveLanguage(language: I18nLanguage) = hibernate.saveOrUpdate(language.clone())

  //@Transactional
  def deleteLanguage(id: JInteger) = hibernate.bulkUpdateByNamedParams("DELETE FROM I18nLanguage l WHERE l.id = :id", "id" -> id)
}