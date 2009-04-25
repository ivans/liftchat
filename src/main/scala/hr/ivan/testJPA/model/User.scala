package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, Transient, GeneratedValue, GenerationType, Table}
import org.hibernate.annotations.{Cascade, CascadeType}

@Entity
@Table {val name="TST_USERS"}
class User {

    @Id
    @GeneratedValue(){val strategy = GenerationType.AUTO}
    var id : Long = _

    @Column{val nullable = true}
    var firstName : String = ""

    @Column{val nullable = false}
    var lastName : String = ""

}
