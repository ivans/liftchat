package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient, GeneratedValue, GenerationType, Table}
import org.hibernate.annotations.{Cascade, CascadeType, Cache, CacheConcurrencyStrategy}

import _root_.hr.ivan.util.EntityUtil._

@Entity
@Table {val name = "TST_ROLE"}
@Cache {val usage = CacheConcurrencyStrategy.READ_WRITE}
class Rola extends PrimaryKeyId with AktivanDefaultTrue {

    @Column{val nullable = false}
    var naziv : String = ""

}
