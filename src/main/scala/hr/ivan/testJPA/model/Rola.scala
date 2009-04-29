package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient}
import javax.persistence.{GeneratedValue, GenerationType, Table, EntityListeners}
import org.hibernate.annotations.{Cascade, CascadeType, Cache, CacheConcurrencyStrategy}

import _root_.hr.ivan.util.EntityUtil._

@Entity
@Table {val name = "TST_ROLE"}
@Cache {val usage = CacheConcurrencyStrategy.READ_WRITE}
@EntityListeners {val value = { Array(classOf[RecordInfoListener]) }}
class Rola extends PrimaryKeyId with AktivanDefaultTrue with RecordInfo {

    @Column{val nullable = false, val unique = true}
    var naziv : String = ""

}
