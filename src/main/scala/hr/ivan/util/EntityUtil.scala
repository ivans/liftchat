package hr.ivan.util

import net.liftweb.util.Log

import javax.persistence.{Id, GeneratedValue, GenerationType, Column, Transient}
import javax.persistence.{Embeddable, Temporal, TemporalType, PreUpdate, PrePersist}
import org.scala_libs.jpa.{LocalEMF, RequestVarEM}

import java.util.Date

object EntityUtil {

    trait PrimaryKeyId {
        @Id
        @GeneratedValue(){val strategy = GenerationType.AUTO}
        var id : Long = 0
    }

    trait RecordInfo {
        var _recordInfo = new RecordInfoImpl
        def recordInfo = {
            if(_recordInfo == null) {
                _recordInfo = new RecordInfoImpl
            }
            _recordInfo
        }
        def recordInfo_= (x : RecordInfoImpl) = _recordInfo = x
    }

    class RecordInfoListener {
        @PreUpdate
        def preUpdate(entity : Object with RecordInfo) = {
            Log.info(" ===> preUpdate")
            entity.recordInfo.userUpdate = "TEST"
            val now = new Date
            entity.recordInfo.dateUpdate = now
        }

        @PrePersist
        def prePersist(entity : Object with RecordInfo) = {
            Log.info(" ===> prePersist")
            entity.recordInfo.userInsert = "TEST"
            entity.recordInfo.userUpdate = "TEST"
            val now = new Date
            entity.recordInfo.dateInsert = now
            entity.recordInfo.dateUpdate = now
        }
    }

    @Embeddable
    class RecordInfoImpl {
        @Column {val name = "USER_INSERT", val unique = false, val nullable = false, val insertable = true, val updatable = true, val length = 30}
        var userInsert : String = null

        @Temporal {val value = TemporalType.TIMESTAMP}
        @Column {val name = "DATE_INSERT", val unique = false, val nullable = false, val insertable = true, val updatable = true}
        var dateInsert : Date = null

        @Column {val name = "USER_UPDATE", val unique = false, val nullable = false, val insertable = true, val updatable = true, val length = 30}
        var userUpdate : String = null

        @Temporal(TemporalType.TIMESTAMP)
        @Column {val name = "DATE_UPDATE", val unique = false, val nullable = false, val insertable = true, val updatable = true}
        var dateUpdate : Date = null
    }

    trait Aktivan {
        @Column {val name="AKTIVAN"}
        var _aktivan : java.lang.Boolean = null
        @Transient
        def aktivan = if(_aktivan == null) None else Some(_aktivan.booleanValue)
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
