package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient, GeneratedValue, GenerationType, Table}
import org.hibernate.annotations.{Cascade, CascadeType}

@Entity
@Table {val name = "TST_UREDI"}
class Ured {

    @Id
    @GeneratedValue(){val strategy = GenerationType.AUTO}
    var id : Long = _

    @Column{val nullable = false}
    var naziv : String = ""

    @ManyToOne{val optional = true}
    var uredNadredjeni : Ured = _

    override def toString = "Ured[" + id + ", " + naziv + ", " + uredNadredjeni + "]"
}
