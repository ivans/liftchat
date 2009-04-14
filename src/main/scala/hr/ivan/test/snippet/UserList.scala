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
                             "delete" -> SHtml.submit("Obrisi", () => {user.delete_!}),
                             "delete2" -> SHtml.ajaxButton("Obrisi", () => {
                                    Log.info("About to delete user")
                                    user.delete_!
                                    Log.info("About to return")
                                    DisplayMessage("message1", <lift:embed what="porukaObrisanKorisnik"/>, 10 seconds, 2 seconds) &
                                    SetHtml("userList", <lift:embed what="listaKorisnika"/>)
                                })
                        )
                    })
                bind("list", xhtml, "entry" -> entries)
            }
    }

}
