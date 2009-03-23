/*
 * UserList.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package hr.ivan.test.snippet

import scala.xml.{NodeSeq, Text, Group, Node}
import net.liftweb.http._
import net.liftweb.http.S
import net.liftweb.mapper._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.util.Helpers._
import net.liftweb.util._

import hr.ivan.test.model.User

class UserList {

    def userCount = <span>{User.count}</span>

    def list(xhtml : NodeSeq) = 
    User.findAll match {
        case List() => <span>No users</span>
        case useri : List[User] => {
                val entries : NodeSeq = useri.flatMap({user =>
                        bind("user", chooseTemplate("list", "entry", xhtml),
                             "firstName" -> <span>{user.firstName}</span>,
                             "lastName" -> <span>{user.lastName}</span>
                        )
                    })
                bind("list", xhtml, "entry" -> entries)
            }
    }


}
