package hr.ivan.util

import org.scala_libs.jpa._

class GenericDAO[T] {

    def getListFromNamedQuery(namedQuery : String)(implicit model : LocalEMF with RequestVarEM) = {
        model.createNamedQuery[T](namedQuery).getResultList
    }

    def getListFromNamedQuery(namedQuery : String, first : Int, count : Int)(implicit model : LocalEMF with RequestVarEM) = {
        val query = model.createNamedQuery[T](namedQuery)
        query.setFirstResult(first).setMaxResults(count)
        query.getResultList
    }

    def getSingleResultFromNamedQuery[SRT](namedQuery : String)(implicit model : LocalEMF with RequestVarEM) : SRT = {
        val query = model.createNamedQuery[T](namedQuery)
        query.getSingleResult.asInstanceOf[SRT]
    }

    def getSingleResultFromNamedQueryByParams[SRT]
    (namedQuery : String,
     params : Seq[Pair[String, Object]])
    (implicit model : LocalEMF with RequestVarEM) : SRT = {
        val query = model.createNamedQuery[T](namedQuery)
        params.foreach(_ match { case Pair(parName, obj) => query.setParameter(parName,obj) })
        query.getSingleResult.asInstanceOf[SRT]
    }

    def getListFromNamedQueryByParams
    (namedQuery : String,
     first : Int, count : Int,
     params : Seq[Pair[String, Object]])
    (implicit model : LocalEMF with RequestVarEM) = {
        val query = model.createNamedQuery[T](namedQuery)
        query.setFirstResult(first).setMaxResults(count)
        params.foreach(_ match { case Pair(parName, obj) => query.setParameter(parName,obj) })
        query.getResultList
    }

}
