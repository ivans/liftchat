package hr.ivan.test.model

import _root_.net.liftweb.mapper._

class Ured extends LongKeyedMapper[Ured] with IdPK {
    def getSingleton = Ured

    object naziv extends MappedString(this, 100)

    def users = User.findAll(By(User.ured, this.id))
}

object Ured extends Ured with LongKeyedMetaMapper[Ured] {
    override def dbTableName = "uredi"
    override def fieldOrder = List(naziv)
}
