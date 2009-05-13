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
                     "firstName" -> outputText(user.firstName),
                     "lastName" -> outputText(user.lastName),
                     "ured" -> outputText(user.ured.naziv),
                     "edit" -> SHtml.link("/pages/sifarnici/users/users", () => entityVar(user), Text(?("Edit"))),
                     "listRole" -> user.listRoleUsera.flatMap(rk =>
                        bind("rola", chooseTemplate("user", "listRole", xhtml),
                             "naziv" -> rk.rola.naziv)
                    ),
                     "delete" -> deleteLink(classOf[User], user.id, "/pages/sifarnici/users/users", Text(?("Delete")), Some(doAfterDelete _), Model),
                )
            }
        )
    }

    def add (implicit xhtml : NodeSeq) : NodeSeq = {

        object validation extends Validations[User] {
            addValidator("lastName", _.lastName.length != 0, Some("The users last name cannot be blank"))
            addValidator("listRole", _.listRoleUsera.size > 0, Some("Potrebno je odabrati bar jednu rolu"))
        }

        def doAdd () = {
            if(validation.doValidation(entity) == true) {
                trySavingEntity[User](entity, Some("Korisnik je uspješno dodan"), Some("Promjene na korsiniku su uspješno spremljene."))(Model)
                redirectTo("/pages/sifarnici/users/users")
            }
        }

        def doDeleteRole(ru : RolaUser) = {
            Log.info("doDeleteRole ru = " + ru)
            Log.info("doDeleteRole user = " + entity)
            entity._listRoleUsera.remove(ru)
        }

        val currentUser = entity
        val choicesUredi = createSelectChoices(Some(?("Odaberite ured...")), UredDAO.allUredi, (ured : Ured) => (ured.id.toString -> ured.naziv))
        val choicesRole = createSelectChoices(Some(?("Odaberite rolu...")), RolaDAO.allRoleAktivne, (rola : Rola) => (rola.id.toString -> rola.naziv))
        val selectedUredId = safeGet(entity.ured.id.toString, None)
        var selectedRola : Option[Rola] = None

        def bindLista = Nil +
        ("id" -> SHtml.hidden(() => entityVar(currentUser))) ++
        createField("user", "firstName", true, None, SHtml.text(entity.firstName, entity.firstName = _)) ++
        createField("user", "lastName", validation.is("lastName"), Some("validationError"), SHtml.text(entity.lastName, entity.lastName = _)) ++
        createField("user", "ured", validation.is("ured"), Some("validationError"),
                    SHtml.select(choicesUredi,  selectedUredId,
                                 uredId => {
                    entity.ured = getFromEM(classOf[Ured], uredId, Model).getOrElse(null)
                })
        ) ++
        createField("user", "listRole", validation.is("listRole"), Some("validationError"),
                    entity.listRoleUsera.flatMap(rk =>
                bind("rola", chooseTemplate("user", "listRole", xhtml),
                     "naziv" -> rk.rola.naziv,
                     "delete" -> SHtml.submit("Delete", () => {
                            doDeleteRole(rk)
                        })
                ))
        ) ++
        createField("user", "rolaDodaj", validation.is("rolaDodaj"), Some("validationError"),
                    {SHtml.select(choicesRole, Some(""),
                                  rolaId => { selectedRola = getFromEM(classOf[Rola], rolaId, Model) }) ++
                     SHtml.submit(?("Dodaj rolu"), () => selectedRola match {
                        case Some(rola) => entity.addRola(rola)
                        case None =>
                    } )}
        ) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("user", xhtml, bindLista:_*)
    }

}
