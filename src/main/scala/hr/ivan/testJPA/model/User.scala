package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient}
import javax.persistence.{GeneratedValue, GenerationType, Table, EntityListeners}
import org.hibernate.annotations.{Cascade, CascadeType}

import _root_.hr.ivan.util.EntityUtil._

@Entity
@Table {val name="TST_USERS"}
@EntityListeners {val value = { Array(classOf[RecordInfoListener]) }}
class User extends PrimaryKeyId with AktivanDefaultTrue with RecordInfo {

    @Column{val nullable = true}
    var firstName : String = ""

    @Column{val nullable = false}
    var lastName : String = ""

    @ManyToOne{val optional = true}
    var ured : Ured = _

}
