package hr.ivan.testJPA.snippet

import scala.xml.{NodeSeq,Text}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

import javax.persistence.{EntityExistsException,PersistenceException}

import hr.ivan.testJPA.model._
import hr.ivan.testJPA.dao._
import hr.ivan.util.{PageUtil, EntityUtil}
import EntityUtil._
import PageUtil._
import Model._

class Users {

    def list (xhtml : NodeSeq) : NodeSeq = {
        val users = Model.createNamedQuery[User]("findAllUsers") getResultList()
        users.flatMap(user => {
                bind("user", xhtml,
                     "firstName" -> Text(user.firstName),
                     "lastName" -> Text(user.lastName),
                     "ured" -> (if(user.ured != null) Text(user.ured.naziv) else Text("")),
                     "edit" -> SHtml.link("/users/users", () => userVar(user), Text(?("Edit"))),
                     "listRole" -> user.listRoleUsera.flatMap(rk =>
                        bind("rola", chooseTemplate("user", "listRole", xhtml),
                             "naziv" -> rk.rola.naziv)
                    ),
                     "delete" -> deleteLink(classOf[User], user.id, "/users/users", Text(?("Delete")), None, Model),
                )
            }
        )
    }

    object userVar extends RequestVar(new User())
    def user = userVar.is

    def add (xhtml : NodeSeq) : NodeSeq = {
        def doAdd () = {
            if (user.lastName.length == 0) {
                error("lastName", "The users last name cannot be blank")
            } else {
                try {
                    Model.mergeAndFlush(user)
                    redirectTo("/users/users")
                } catch {
                    case ee : EntityExistsException => logAndError("Author already exists ", ee)
                    case pe : PersistenceException => logAndError("Error adding user ", pe)
                }
            }
        }

        def doDeleteRole(ru : RolaUser) = {
            Log.info("doDeleteRole ru = " + ru)
            Log.info("doDeleteRole user = " + user)
            user._listRoleUsera.remove(ru)
        }

        val currentUser = user
        val choices = createSelectChoices(Some(?("Odaberite ured...")), UredDAO.allUredi, (ured : Ured) => (ured.id.toString -> ured.naziv))
        val default = if (user.ured != null) { Full(user.ured.id.toString) } else { Empty }
        val choicesRole = createSelectChoices(Some(?("Odaberite rolu...")), RolaDAO.allRoleAktivne, (rola : Rola) => (rola.id.toString -> rola.naziv))
        var odabranaRola : Option[Rola] = None

        bind("user", xhtml,
             "id" -> SHtml.hidden(() => userVar(currentUser)),
             "firstName" -> SHtml.text(user.firstName, user.firstName = _),
             "lastName" -> SHtml.text(user.lastName, user.lastName = _),
             "ured" -> SHtml.select(choices, default,
                                    uredId => {
                    user.ured = getFromEM(classOf[Ured], uredId, Model).getOrElse(null)
                }),
             "listRole" -> user.listRoleUsera.flatMap(rk =>
                bind("rola", chooseTemplate("user", "listRole", xhtml),
                     "naziv" -> rk.rola.naziv,
                     "delete" -> SHtml.submit("Delete", () => {
                            doDeleteRole(rk)
                        })
                )),
             "rolaDodaj" -> {SHtml.select(choicesRole, Some(""),
                                          rolaId => { odabranaRola = getFromEM(classOf[Rola], rolaId, Model) }) ++
                             SHtml.submit(?("Dodaj rolu"), () => odabranaRola match {
                        case Some(rola) => user.addRola(rola)
                        case None =>
                    } )},
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }

}
