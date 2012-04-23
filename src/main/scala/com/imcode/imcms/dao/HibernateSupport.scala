package com.imcode
package imcms.dao

import org.hibernate.{SQLQuery, Query, SessionFactory, Session}
import java.lang.String
import org.hibernate.transform.ResultTransformer


trait HibernateSupport {

  @scala.reflect.BeanProperty
  var sessionFactory: SessionFactory = _

  @deprecated
  def flush() = hibernate.flush()

  object hibernate {

    import HibernateSupport.ResultTransformerFactory

    type NamedParam = (String, Any)

    def withSession[T](f: Session => T) =  f(sessionFactory.getCurrentSession)

    def flush() = withSession { _.flush() }


    private def setParams[Q <: Query](ps: Any*)(query: Q) = query |<< {
      for ((param, position) <- ps.zipWithIndex) query.setParameter(position, param.asInstanceOf[AnyRef])
    }

    private def setNamedParams[Q <: Query](namedParam: NamedParam, namedParams: NamedParam*)(query: Q) = query |<< {
      for ((name, value) <- namedParam +: namedParams) query.setParameter(name, value.asInstanceOf[AnyRef])
    }


    def runSqlQuery[A](queryString: String, ps: Any*)(f: SQLQuery => A) = withSession {
      _.createSQLQuery(queryString) |> setParams(ps: _*) |> f
    }

    def runQuery[A](queryString: String, ps: Any*)(f: Query => A) = withSession {
      _.createQuery(queryString) |> setParams(ps: _*) |> f
    }

    def runQueryWithNamedParams[A](queryString: String, nameParam: NamedParam, namedParams: NamedParam*)(f: Query => A) = withSession {
      _.createQuery(queryString) |> setNamedParams(nameParam, namedParams: _*) |> f
    }

    def runNamedQuery[A](queryName: String, ps: Any*)(f: Query => A) = withSession {
      _.getNamedQuery(queryName) |> setParams(ps: _*) |> f
    }

    def runNamedQueryWithNamedParams[A](queryName: String, namedParam: NamedParam, namedParams: NamedParam*)(f: Query => A) = withSession {
      _.getNamedQuery(queryName) |> setNamedParams(namedParam, namedParams: _*) |> f
    }


    def getByQuery[A](queryString: String, ps: Any*): A =
      runQuery(queryString, ps: _*)(_.uniqueResult().asInstanceOf[A])

    def getByNamedQuery[A](queryName: String, ps: Any*): A =
      runNamedQuery(queryName, ps: _*)(_.uniqueResult().asInstanceOf[A])

    def getByNamedQueryAndNamedParams[A](queryName: String, namedParam: NamedParam, namedParams: NamedParam*): A =
      runNamedQueryWithNamedParams(queryName, namedParam, namedParams: _*)(_.uniqueResult().asInstanceOf[A])


    def listAll[A <: AnyRef : ClassManifest]() = withSession {
      _.createCriteria(classManifest[A].erasure).list().asInstanceOf[JList[A]]
    }

    def list[A <: AnyRef](queryString: String, ps: Any*): JList[A] =
      runQuery(queryString, ps: _*)(_.list().asInstanceOf[JList[A]])

    def listByNamedParams[A <: AnyRef](queryString: String, namedParam: NamedParam, namedParams: NamedParam*): JList[A] =
      runQueryWithNamedParams(queryString, namedParam, namedParams: _*)(_.list().asInstanceOf[JList[A]])

    def listByNamedQuery[A <: AnyRef](queryName: String, ps: Any*): JList[A] =
      runNamedQuery(queryName, ps: _*)(_.list().asInstanceOf[JList[A]])

    def listByNamedQueryAndNamedParams[A <: AnyRef](queryName: String, namedParam: NamedParam, namedParams: NamedParam*): JList[A] =
      runNamedQueryWithNamedParams(queryName, namedParam, namedParams: _*)(_.list().asInstanceOf[JList[A]])

    def listBySqlQuery[A <: AnyRef : ResultTransformerFactory](queryString: String, ps: Any*): JList[A] =
      runSqlQuery(queryString, ps: _*) { query =>
        println(">>>>>RSF>>>>> " + implicitly[ResultTransformerFactory[A]])
        query.setResultTransformer(implicitly[ResultTransformerFactory[A]].transformer)
        query.list().asInstanceOf[JList[A]]
      }

    def bulkUpdate(queryString: String, ps: Any*): Int =
      runQuery(queryString, ps: _*)(_.executeUpdate())

    def bulkUpdateByNamedParams(queryString: String, namedParam: NamedParam, namedParams: NamedParam*): Int =
      runQueryWithNamedParams(queryString, namedParam, namedParams: _*)(_.executeUpdate())

    def bulkUpdateByNamedQuery(queryString: String, ps: Any*): Int =
      runNamedQuery(queryString, ps: _*)(_.executeUpdate())

    def bulkUpdateByNamedQueryAndNamedParams(queryString: String, namedParam: NamedParam, namedParams: NamedParam*): Int =
      runNamedQueryWithNamedParams(queryString, namedParam, namedParams: _*)(_.executeUpdate())


    def get[A: ClassManifest](id: java.io.Serializable): A = withSession {
      _.get(classManifest[A].erasure, id).asInstanceOf[A]
    }

    def save[A <: AnyRef](obj: A): A = withSession { session =>
      session.save(obj)
      obj
    }

    def saveOrUpdate[A <: AnyRef](obj: A): A = withSession { session =>
      session.saveOrUpdate(obj)
      obj
    }

    def delete[A <: AnyRef](obj: A): Unit = withSession { _.delete(obj) }

    def merge[A <: AnyRef](obj: A): A = withSession { _.merge(obj).asInstanceOf[A] }
  }
}


object HibernateSupport {

  abstract class ResultTransformerFactory[+A <: AnyRef : ClassManifest] {
    def transformer: ResultTransformer

    override def toString = "ResultTransformerFactory[%s]" format classManifest[A].erasure
  }


  trait ResultTransformerBase { this: ResultTransformer =>
    def transformList(collection: JList[_]) = collection
  }


  class ArrayResultTransformerFactory[E <: AnyRef : ClassManifest] extends ResultTransformerFactory[Array[E]] {
    def transformer = new ResultTransformer with ResultTransformerBase {
      def transformTuple(tuple: Array[AnyRef], aliases: Array[String]) = Array.ofDim[E](tuple.size) |< { arr =>
        for ((n, i) <- tuple.zipWithIndex) arr(i) = n.asInstanceOf[E]
      }
    }
  }


  class SingleValueTransformerFactory[A <: AnyRef : ClassManifest] extends ResultTransformerFactory[A] {
    def transformer = new ResultTransformer with ResultTransformerBase {
      def transformTuple(tuple: Array[AnyRef], aliases: Array[String]) = tuple(0)
    }
  }


  object ResultTransformerFactory extends LowLevelResultTransformerFactoryImplicits {
    implicit object anyRefArrayResultTransformerFactory extends ArrayResultTransformerFactory[AnyRef]
  }


  class LowLevelResultTransformerFactoryImplicits {
    implicit object anyRefSingleValueTransformerFactory extends SingleValueTransformerFactory[AnyRef]
    implicit object stringSingleValueTransformerFactory extends SingleValueTransformerFactory[String]
    implicit object jIntegerSingleValueTransformerFactory extends SingleValueTransformerFactory[JInteger]
    implicit object jDoubleSingleValueTransformerFactory extends SingleValueTransformerFactory[JDouble]
    implicit object jFloatSingleValueTransformerFactory extends SingleValueTransformerFactory[JFloat]
    implicit object jBooleanSingleValueTransformerFactory extends SingleValueTransformerFactory[JBoolean]
    implicit object jCharacterSingleValueTransformerFactory extends SingleValueTransformerFactory[JCharacter]
    implicit object jByteSingleValueTransformerFactory extends SingleValueTransformerFactory[JByte]

    implicit object stringArrayResultTransformerFactory extends ArrayResultTransformerFactory[String]
    implicit object jIntegerArrayResultTransformerFactory extends ArrayResultTransformerFactory[JInteger]
    implicit object jDoubleArrayResultTransformerFactory extends ArrayResultTransformerFactory[JDouble]
    implicit object jFloatArrayResultTransformerFactory extends ArrayResultTransformerFactory[JFloat]
    implicit object jBooleanArrayResultTransformerFactory extends ArrayResultTransformerFactory[JBoolean]
    implicit object jCharacterArrayResultTransformerFactory extends ArrayResultTransformerFactory[JCharacter]
    implicit object jByteArrayResultTransformerFactory extends ArrayResultTransformerFactory[JByte]
  }
}