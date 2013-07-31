package com.imcode.imcms.dao.hibernate

import org.hibernate.transform.ResultTransformer
import scala.reflect.ClassTag

abstract class ResultTransformerProvider[+A <: AnyRef : ClassTag] {

  def resultTransformer: ResultTransformer

  override def toString = "ResultTransformerProvider[%s]".format(scala.reflect.classTag[A].runtimeClass)
}


object ResultTransformerProvider extends LowLevelImplicitResultTransformerProviders {
  implicit val defaultResultTransformerProvider = new ResultTransformerProvider[Array[AnyRef]] {
    override val resultTransformer = null
  }
}
