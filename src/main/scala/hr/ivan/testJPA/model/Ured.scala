package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient}
import javax.persistence.{GeneratedValue, GenerationType, Table, EntityListeners}
import org.hibernate.annotations.{Cascade, CascadeType, Cache, CacheConcurrencyStrategy}

import _root_.hr.ivan.util.EntityUtil._

@Entity
@Table {val name = "TST_UREDI"}
@Cache {val usage = CacheConcurrencyStrategy.READ_WRITE}
@EntityListeners {val value = { Array(classOf[RecordInfoListener]) }}
class Ured extends PrimaryKeyId with AktivanDefaultTrue with RecordInfo {

    @Column{val nullable = false}
    var naziv : String = ""

    @ManyToOne{val optional = true}
    var uredNadredjeni : Ured = _

    @OneToMany() {val mappedBy = "ured", val targetEntity = classOf[User]}
    var useri : java.util.List[User] = new java.util.ArrayList[User]()

    override def toString = "Ured[" + id + ", " + naziv + ", " + uredNadredjeni + "]"
}
