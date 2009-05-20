package hr.ivan.testJPA.model

import java.util.Date

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient}
import javax.persistence.{GeneratedValue, GenerationType, Table, EntityListeners}
import javax.persistence.{CascadeType, FetchType}
import org.hibernate.annotations.{Cascade, CascadeType => HibernateCascadeType}

import _root_.hr.ivan.util.EntityUtil._

@Entity
@Table {val name="TST_USERS"}
@EntityListeners {val value = { Array(classOf[RecordInfoListener]) }}
class User extends PrimaryKeyId with AktivanDefaultTrue with RecordInfo {

    @Column{val nullable = true}
    var firstName : String = ""

    @Column{val nullable = false}
    var lastName : String = ""

    @Column{val nullable = true, val name="DATE_OF_BIRTH"}
    var dateOfBirth : Date = null

    @ManyToOne{val optional = true}
    var ured : Ured = _

  	@OneToMany {val cascade = Array(CascadeType.ALL), val fetch = FetchType.LAZY, 
                val mappedBy = "user", val targetEntity = classOf[RolaUser]}
    @Cascade {val value=Array(HibernateCascadeType.DELETE_ORPHAN)}
    var _listRoleUsera : java.util.List[RolaUser] = new java.util.ArrayList[RolaUser]()

    @Transient
    def listRoleUsera = List.fromArray(this._listRoleUsera.toArray).asInstanceOf[List[RolaUser]]

    def addRola(r : Rola) = {
        val ru = new RolaUser
        ru.user = this
        ru.rola = r
        this._listRoleUsera.add(ru)
    }

    override def toString = "User[" + firstName + " " + lastName + " " + ured + "]"

}
