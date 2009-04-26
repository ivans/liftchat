package hr.ivan.testJPA.snippet

import scala.xml.{NodeSeq,Text}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

import javax.persistence.{EntityExistsException,PersistenceException}

import hr.ivan.testJPA.model._
import hr.ivan.util.{PageUtil}
import Model._

class Users extends PageUtil {

    def list (xhtml : NodeSeq) : NodeSeq = {
        val users = Model.createNamedQuery[User]("findAllUsers") getResultList()
        users.flatMap(user =>
            bind("user", xhtml,
                 "firstName" -> Text(user.firstName),
                 "lastName" -> Text(user.lastName),
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
                    case ee : EntityExistsException => error("Author already exists")
                    case pe : PersistenceException => logAndError("Error adding user")
                }
            }
        }

        val currentId = user.id

        bind("user", xhtml,
             "id" -> SHtml.hidden(() => user.id = currentId),
             "firstName" -> SHtml.text(user.firstName, user.firstName = _),
             "lastName" -> SHtml.text(user.lastName, user.lastName = _),
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }

}
