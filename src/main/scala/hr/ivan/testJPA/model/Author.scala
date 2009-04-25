package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, Transient, GeneratedValue, GenerationType}
import org.hibernate.annotations._

@Entity
class Author {
    @Id
    @GeneratedValue(){val strategy = GenerationType.AUTO}
    var id : Long = _

    @Column{val nullable = true}
    var firstName : String = ""

    @Column{val nullable = false}
    var lastName : String = ""

    @OneToMany(){val mappedBy = "author", val targetEntity = classOf[Book]}
    @Cascade(){val value = Array(org.hibernate.annotations.CascadeType.ALL)}
    var books : java.util.Set[Book] = new java.util.HashSet[Book]()

    @Transient
    def name = firstName + " " + lastName
}
