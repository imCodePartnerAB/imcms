package com.imcode.imcms.dao.hibernate

import org.hibernate.transform.ResultTransformer
import scala.reflect.ClassTag

abstract class HibernateResultTransformer[+A <: AnyRef : ClassTag] {

  def transformer: ResultTransformer

  override def toString = "HibernateResultTransformer[%s]".format(scala.reflect.classTag[A].runtimeClass)
}

object HibernateResultTransformer extends LowLevelHibernateResultTransformerImplicits {
  implicit object defaultResultTransformerFactory extends HibernateResultTransformer[Array[AnyRef]] {
    def transformer = null
  }
}
