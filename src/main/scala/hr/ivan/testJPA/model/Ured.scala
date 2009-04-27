package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient, GeneratedValue, GenerationType, Table}
import org.hibernate.annotations.{Cascade, CascadeType, Cache, CacheConcurrencyStrategy}

import hr.ivan.util.EntityUtil._

@Entity
@Table {val name = "TST_UREDI"}
@Cache {val usage = CacheConcurrencyStrategy.READ_WRITE}
class Ured extends PrimaryKeyId {

    @Column{val nullable = false}
    var naziv : String = ""

    @ManyToOne{val optional = true}
    var uredNadredjeni : Ured = _

    override def toString = "Ured[" + id + ", " + naziv + ", " + uredNadredjeni + "]"
}
