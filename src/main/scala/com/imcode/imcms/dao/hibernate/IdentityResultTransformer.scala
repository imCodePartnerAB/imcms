package com.imcode
package imcms.dao.hibernate

import org.hibernate.transform.ResultTransformer

trait IdentityResultTransformer { this: ResultTransformer =>
  override def transformList(collection: JList[_]): JList[_] = collection
}
