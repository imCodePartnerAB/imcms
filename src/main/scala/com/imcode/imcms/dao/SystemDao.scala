package com.imcode
package imcms.dao

import com.imcode.imcms.mapping.orm.SystemProperty
import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.dao.hibernate.HibernateSupport


@Transactional(rollbackFor = Array(classOf[Throwable]))
class SystemDao extends HibernateSupport {

  def getProperties(): JList[SystemProperty] = hibernate.listAll()

  def getProperty(name: String): SystemProperty = hibernate.getByQuery(
    "SELECT p FROM SystemProperty p WHERE p.name = ?1", 1 -> name)

  def saveProperty(property: SystemProperty) = hibernate.saveOrUpdate(property)
}





