package hr.ivan.test.snippet

import scala.xml.{NodeSeq, Text, Group, Node}
import net.liftweb.http._
import net.liftweb.http.S
import net.liftweb.mapper._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.util.Helpers._
import net.liftweb.util._

import hr.ivan.test.model._

class FormTest {

    def addUser(xhtml : Group) : NodeSeq = {
        object firstName extends RequestVar[String]("")
        object lastName extends RequestVar[String]("")

        def processEntryAdd() {
            Log.info("processEntryAdd: " + firstName + ", " + lastName)
            var user = new User
            user.firstName(firstName)
            user.lastName(lastName)
            user.save
            firstName("")
            lastName("")
            S.notice("User added to database.")
        }

        bind("entry", xhtml,
             "firstName" -> SHtml.text(firstName, firstName(_)),
             "lastName" -> SHtml.text("", lastName(_)),
             "submit" -> SHtml.submit("Add user", processEntryAdd))
    }

}
