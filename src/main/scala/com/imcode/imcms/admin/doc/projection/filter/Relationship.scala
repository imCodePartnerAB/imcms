package com.imcode
package imcms
package admin.doc.projection.filter


case class Relationship(parents: Relationship.Type = Relationship.Unspecified, children: Relationship.Type = Relationship.Unspecified)

object Relationship {

  sealed trait Type

  case object Unspecified extends Type

  case class Logical(value: Boolean) extends Type

  case class Exact(docId: DocId) extends Type

}