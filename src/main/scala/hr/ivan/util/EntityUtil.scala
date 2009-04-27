package hr.ivan.util

import javax.persistence.{Id, GeneratedValue, GenerationType}
import org.scala_libs.jpa.{LocalEMF, RequestVarEM}

object EntityUtil {

    trait PrimaryKeyId {
        @Id
        @GeneratedValue(){val strategy = GenerationType.AUTO}
        var id : Long = 0
    }

    def createSelectChoices[T](emptyChoice : Option[String], lista : Seq[T], mapping : T => (String, String)) = {
        def list = lista.map(mapping)
        emptyChoice match {
            case Some(str) => ("" -> str) :: list.toList
            case None => list
        }
    }

    def getFromEM[T <: AnyRef with PrimaryKeyId](x : T, model : LocalEMF with RequestVarEM) : T = {
        if(x.id == 0) {
            x
        } else {
            model.getReference(x.getClass.asInstanceOf[Class[T]], x.id)
        }
    }

    def getFromEM[T <: AnyRef](klasa : Class[T], x : Long, model : LocalEMF with RequestVarEM) : Option[T] = {
        x match {
            case 0 => None
            case x => Some(model.getReference(klasa, x))
        }
    }

    def getFromEM[T <: AnyRef](klasa : Class[T], x : String, model : LocalEMF with RequestVarEM) : Option[T] = {
        if(x == 0 || x.length == 0) {
            None
        } else {
            Some(model.getReference(klasa, new java.lang.Long(x)))
        }
    }
}
