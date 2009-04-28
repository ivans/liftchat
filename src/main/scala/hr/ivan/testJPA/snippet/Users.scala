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
        users.flatMap(user =>
            bind("user", xhtml,
                 "firstName" -> Text(user.firstName),
                 "lastName" -> Text(user.lastName),
                 "ured" -> (if(user.ured != null) Text(user.ured.naziv) else Text("")),
                 "edit" -> SHtml.link("/users/users", () => userVar(user), Text(?("Edit")))
            ))
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
                    case ee : EntityExistsException => error("Author already exists " + ee.getMessage)
                    case pe : PersistenceException => logAndError("Error adding user " + pe.getMessage)
                }
            }
        }

        val currentId = user.id
        val choices = createSelectChoices(Some(?("Odaberite ured...")), UredDAO.allUredi, (ured : Ured) => (ured.id.toString -> ured.naziv))
        val default = if (user.ured != null) { Full(user.ured.id.toString) } else { Empty }

        bind("user", xhtml,
             "id" -> SHtml.hidden(() => user.id = currentId),
             "firstName" -> SHtml.text(user.firstName, user.firstName = _),
             "lastName" -> SHtml.text(user.lastName, user.lastName = _),
             "ured" -> SHtml.select(choices, default,
                                    uredId => {
                    user.ured = getFromEM(classOf[Ured], uredId, Model).getOrElse(null)
                }),
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }

}
