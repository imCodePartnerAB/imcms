package com.imcode
package imcms
package admin.doc.projection.filter


case class Relationship(withParents: Relationship.Type, withChildren: Relationship.Type)

object Relationship {
  sealed trait Type
  case object Unspecified extends Type
  case class Logical(value: Boolean) extends Type
  case class Exact(docId: DocId) extends Type
}
