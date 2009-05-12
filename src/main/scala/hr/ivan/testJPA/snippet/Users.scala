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
import hr.ivan.util.{PageUtil, EntityUtil, SimpleSifarnik}
import EntityUtil._
import PageUtil._
import Model._

class Users extends SimpleSifarnik[User](new User) {

    def list (xhtml : NodeSeq) : NodeSeq = {
        val users = Model.createNamedQuery[User]("findAllUsers") getResultList()
        users.flatMap(user => {
                bind("user", xhtml,
                     "firstName" -> Text(user.firstName),
                     "lastName" -> Text(user.lastName),
                     "ured" -> (if(user.ured != null) Text(user.ured.naziv) else Text("")),
                     "edit" -> SHtml.link("/users/users", () => entityVar(user), Text(?("Edit"))),
                     "listRole" -> user.listRoleUsera.flatMap(rk =>
                        bind("rola", chooseTemplate("user", "listRole", xhtml),
                             "naziv" -> rk.rola.naziv)
                    ),
                     "delete" -> deleteLink(classOf[User], user.id, "/users/users", Text(?("Delete")), None, Model),
                )
            }
        )
    }

    def add (xhtml : NodeSeq) : NodeSeq = {
        def doAdd () = {
            if (entity.lastName.length == 0) {
                error("lastName", "The users last name cannot be blank")
            } else {
                try {
                    Model.mergeAndFlush(entity)
                    redirectTo("/users/users")
                } catch {
                    case ee : EntityExistsException => logAndError("Author already exists ", ee)
                    case pe : PersistenceException => logAndError("Error adding user ", pe)
                }
            }
        }

        def doDeleteRole(ru : RolaUser) = {
            Log.info("doDeleteRole ru = " + ru)
            Log.info("doDeleteRole user = " + entity)
            entity._listRoleUsera.remove(ru)
        }

        val currentUser = entity
        val choices = createSelectChoices(Some(?("Odaberite ured...")), UredDAO.allUredi, (ured : Ured) => (ured.id.toString -> ured.naziv))
        val default = if (entity.ured != null) { Full(entity.ured.id.toString) } else { Empty }
        val choicesRole = createSelectChoices(Some(?("Odaberite rolu...")), RolaDAO.allRoleAktivne, (rola : Rola) => (rola.id.toString -> rola.naziv))
        var odabranaRola : Option[Rola] = None

        bind("user", xhtml,
             "id" -> SHtml.hidden(() => entityVar(currentUser)),
             "firstName" -> SHtml.text(entity.firstName, entity.firstName = _),
             "lastName" -> SHtml.text(entity.lastName, entity.lastName = _),
             "ured" -> SHtml.select(choices, default,
                                    uredId => {
                    entity.ured = getFromEM(classOf[Ured], uredId, Model).getOrElse(null)
                }),
             "listRole" -> entity.listRoleUsera.flatMap(rk =>
                bind("rola", chooseTemplate("user", "listRole", xhtml),
                     "naziv" -> rk.rola.naziv,
                     "delete" -> SHtml.submit("Delete", () => {
                            doDeleteRole(rk)
                        })
                )),
             "rolaDodaj" -> {SHtml.select(choicesRole, Some(""),
                                          rolaId => { odabranaRola = getFromEM(classOf[Rola], rolaId, Model) }) ++
                             SHtml.submit(?("Dodaj rolu"), () => odabranaRola match {
                        case Some(rola) => entity.addRola(rola)
                        case None =>
                    } )},
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }

}
