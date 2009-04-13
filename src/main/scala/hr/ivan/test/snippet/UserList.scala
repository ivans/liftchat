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
                                    user.delete_!
                                    DisplayMessage("message1", <lift:embed what="porukaObrisanKorisnik"/>, 10000, 2000)
                                })
                        )
                    })
                bind("list", xhtml, "entry" -> entries)
            }
    }

}
