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

}
