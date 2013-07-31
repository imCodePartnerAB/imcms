package com.imcode.imcms.dao.hibernate

import scala.reflect.ClassTag
import org.hibernate.transform.ResultTransformer

class SingleColumnTransformerProvider[A <: AnyRef : ClassTag] extends ResultTransformerProvider[A] {
  override val resultTransformer = new ResultTransformer with IdentityResultTransformer {
    override def transformTuple(tuple: Array[AnyRef], aliases: Array[String]): A = tuple(0).asInstanceOf[A]
  }
}