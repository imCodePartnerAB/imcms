package com.imcode
package imcms.dao.hibernate

import scala.reflect.ClassTag
import org.hibernate.transform.ResultTransformer

class ArrayResultTransformerProvider[E <: AnyRef : ClassTag] extends ResultTransformerProvider[Array[E]] {
  override val resultTransformer = new ResultTransformer with IdentityResultTransformer {
    override def transformTuple(tuple: Array[AnyRef], aliases: Array[String]): Array[E] = Array.ofDim[E](tuple.size) |>> { array =>
      for ((element, i) <- tuple.zipWithIndex) array(i) = element.asInstanceOf[E]
    }
  }
}
