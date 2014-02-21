package com.imcode.imcms.mapping.dao

import com.imcode._
import com.imcode.imcms.mapping.orm.IPAccess
import org.springframework.transaction.annotation.Transactional
import com.imcode.imcms.dao.hibernate.HibernateSupport

@Transactional(rollbackFor = Array(classOf[Throwable]))
class IPAccessDao extends HibernateSupport {

    def getAll(): JList[IPAccess] = hibernate.listAll()

    def delete(id: JInteger) = hibernate.bulkUpdateByNamedParams(
        "DELETE FROM IPAccess i WHERE i.id = :id", "id" -> id
    )

    def save(ipAccess: IPAccess) = hibernate.saveOrUpdate(ipAccess)

    def get(id: JInteger) = hibernate.get[IPAccess](id)
}
