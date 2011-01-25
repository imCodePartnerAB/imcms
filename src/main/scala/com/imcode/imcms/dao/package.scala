package com.imcode
package imcms

import org.springframework.orm.hibernate3.{HibernateCallback, HibernateTemplate}
import org.hibernate.Session

package object dao {

  trait SpringHibernateTemplate {
    @scala.reflect.BeanProperty
    var hibernateTemplate: HibernateTemplate = _

    def withSession[T](callback: Session => T) = hibernateTemplate.execute(new HibernateCallback[T] {
      def doInHibernate(session: Session) = callback(session)
    })

    def flush() = withSession { _.flush() }
  }
}