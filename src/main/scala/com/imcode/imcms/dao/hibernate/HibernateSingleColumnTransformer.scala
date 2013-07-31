package com.imcode.imcms.dao.hibernate

import scala.reflect.ClassTag
import org.hibernate.transform.ResultTransformer

class HibernateSingleColumnTransformer[A <: AnyRef : ClassTag] extends HibernateResultTransformer[A] {
  override def transformer = new ResultTransformer with IdentityResultTransformer {
    def transformTuple(tuple: Array[AnyRef], aliases: Array[String]): A = tuple(0).asInstanceOf[A]
  }
}