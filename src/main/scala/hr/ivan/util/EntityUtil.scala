package hr.ivan.util

import javax.persistence.{Id, GeneratedValue, GenerationType}

object EntityUtil {

    trait PrimaryKeyId {
        @Id
        @GeneratedValue(){val strategy = GenerationType.AUTO}
        var id : Long = _
    }

}
