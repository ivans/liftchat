package hr.ivan.util

import javax.persistence.{Id, GeneratedValue, GenerationType, Column, Transient}
import org.scala_libs.jpa.{LocalEMF, RequestVarEM}

object EntityUtil {

    trait PrimaryKeyId {
        @Id
        @GeneratedValue(){val strategy = GenerationType.AUTO}
        var id : Long = 0
    }

    trait Aktivan {
        @Column {val name="AKTIVAN"}
        var _aktivan : java.lang.Boolean = null
        @Transient
        def aktivan = if(_aktivan == null) None else Some(_aktivan)
        @Transient
        def aktivan_=(b : Boolean) = _aktivan = b
        @Transient
        def aktivan_=(b : Option[Boolean]) = {
            b match {
                case Some(x) => _aktivan = x
                case None => _aktivan = null
            }
        }
    }

    trait AktivanDefaultTrue extends Aktivan {
        this.aktivan = true
    }

    trait AktivanDefaultFalse extends Aktivan {
        this.aktivan = false
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
