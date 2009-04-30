package hr.ivan.testJPA.model

import javax.persistence.{Entity, Id, Column, OneToMany, ManyToOne, Transient}
import javax.persistence.{GeneratedValue, GenerationType, Table, EntityListeners}
import javax.persistence.{FetchType, JoinColumn}
import org.hibernate.annotations.{Cascade, CascadeType, Cache, CacheConcurrencyStrategy}

import _root_.hr.ivan.util.EntityUtil._

@Entity
@Table {val name = "TST_ROLE_USERA"}
@EntityListeners {val value = { Array(classOf[RecordInfoListener]) }}
class RolaUser extends PrimaryKeyId with AktivanDefaultTrue with RecordInfo {

    @ManyToOne {val fetch = FetchType.LAZY}
	@JoinColumn {val name = "USER_ID", val unique = false, val nullable = false}
	var user : User = _

	@ManyToOne {val fetch = FetchType.LAZY}
	@JoinColumn {val name = "ROLA_ID", val unique = false, val nullable = false}
    var rola : Rola = _

    override def toString = "RolaUser[" + user + ", " + rola + "]"
}
