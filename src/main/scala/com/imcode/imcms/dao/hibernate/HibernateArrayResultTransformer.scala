package com.imcode
package imcms.dao.hibernate

import scala.reflect.ClassTag
import org.hibernate.transform.ResultTransformer

class HibernateArrayResultTransformer[E <: AnyRef : ClassTag] extends HibernateResultTransformer[Array[E]] {
  override def transformer = new ResultTransformer with IdentityResultTransformer {
    def transformTuple(tuple: Array[AnyRef], aliases: Array[String]): Array[E] = Array.ofDim[E](tuple.size) |>> { arr =>
      for ((element, i) <- tuple.zipWithIndex) arr(i) = element.asInstanceOf[E]
    }
  }
}
