package hr.ivan.util

import org.scala_libs.jpa._

class GenericDAO[T] {

    def getListFromNamedQuery(namedQuery : String)(implicit model : LocalEMF with RequestVarEM) = {
        model.createNamedQuery[T](namedQuery).getResultList
    }

}
