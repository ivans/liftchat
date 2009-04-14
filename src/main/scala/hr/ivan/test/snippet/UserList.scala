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
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.http.js.JsCmds.SetHtml

import hr.ivan.test.model.User

class UserList {

    def userCount = <span>{User.count}</span>

    def list(xhtml : NodeSeq) = 
    User.findAll match {
        case List() => <span>{S.?("msgNoUsers")}</span>
        case useri : List[User] => {
                Log.info("USerList.list");
                S.notice("Prikazujem listu usera")
                val entries : NodeSeq = useri.flatMap({user =>
                        bind("user", chooseTemplate("list", "entry", xhtml),
                             "firstName" -> <span>{user.firstName}</span>,
                             "lastName" -> <span>{user.lastName}</span>,
                             "delete" -> SHtml.submit("Obriši", () => {user.delete_!}),
                             "delete2" -> SHtml.ajaxButton("Obriši", () => {
                                    Log.info("About to delete user")
                                    user.delete_!
                                    Log.info("About to return")
                                    DisplayMessage("message1", <lift:embed what="porukaObrisanKorisnik"/>, 10 seconds, 2 seconds) &
                                    SetHtml("userList", <lift:userList.list></lift:userList.list>)
                                })
                        )
                    })
                Log.info("entries:: " + entries)
                bind("list", xhtml, "entry" -> entries)
            }
    }

}
