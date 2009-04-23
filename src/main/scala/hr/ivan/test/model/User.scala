package hr.ivan.test.model

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._

class User extends MegaProtoUser[User] {
    def getSingleton = User // what's the "meta" server

    object ured extends MappedLongForeignKey(this, Ured)

    object textArea extends MappedTextarea(this, 2048) {
        override def textareaRows  = 10
        override def textareaCols = 50
        override def displayName = "Personal Essay"
    }
}

object User extends User with MetaMegaProtoUser[User] {
    override def dbTableName = "users" // define the DB table name
    override def screenWrap = Full(<lift:surround with="default" at="content">
            <lift:bind /></lift:surround>)
    // define the order fields will appear in forms and output
    override def fieldOrder = List(id, firstName, lastName, email,
                                   locale, timezone, password, textArea)

    // comment this line out to require email validations
    override def skipEmailValidation = true
}


