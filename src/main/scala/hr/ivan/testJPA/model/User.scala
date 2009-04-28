package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient, GeneratedValue, GenerationType, Table}
import org.hibernate.annotations.{Cascade, CascadeType}

import _root_.hr.ivan.util.EntityUtil._

@Entity
@Table {val name="TST_USERS"}
class User extends PrimaryKeyId with AktivanDefaultTrue {

    @Column{val nullable = true}
    var firstName : String = ""

    @Column{val nullable = false}
    var lastName : String = ""

    @ManyToOne{val optional = true}
    var ured : Ured = _

}
