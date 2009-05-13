package hr.ivan.testJPA.snippet

import scala.xml.{NodeSeq,Text}
import scala.collection.mutable.HashMap

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

import javax.persistence.{Entity, EntityExistsException,PersistenceException}

import hr.ivan.testJPA.model._
import hr.ivan.testJPA.dao._
import hr.ivan.util.{PageUtil, EntityUtil}
import EntityUtil._
import PageUtil._
import Model._

class Role {

    def list (implicit xhtml : NodeSeq) : NodeSeq = {

        def doAfterDelete(success : Boolean, obj : Option[Rola]) = success match {
            case true => notice("Rola " + obj.map(_.naziv).getOrElse("??") + " je obrisana")
            case false => notice("Rola nije obrisana")
        }

        createList[Rola](RolaDAO.allRole, "rola",
                         r => {
                "naziv" -> outputText(r.naziv) ::
                "aktivan" -> SHtml.checkbox(r.aktivan.getOrElse(false), _ => Nil, ("disabled" -> "true")) ::
                "edit" -> SHtml.link("/pages/role/addEdit", () => rolaVar(r), Text(?("Edit"))) ::
                "delete" -> deleteLink(classOf[Rola], r.id, "/pages/role/list", Text(?("Delete")), Some(doAfterDelete _), Model) ::
                Nil
            }
        )
    }

    object rolaVar extends RequestVar(new Rola())
    def rola = rolaVar.is

    def add (implicit xhtml : NodeSeq) : NodeSeq = {

        val current = rola

        object validation extends Validations[Rola] {
            addValidator("naziv", _.naziv.length != 0, Some("Naziv ne može biti prazan"))
            addValidator("naziv", _.naziv.length > 3, Some("Naziv mora biti duži od 3 znaka"))
        }

        def doAdd () = {
            if(validation.doValidation(rola) == true) {
                trySavingEntity[Rola](rola, Some("Nova rola dodana"), Some("Updatana rola"))(Model)
                redirectTo("/pages/role/list")
            }
        }

        def bindLista = Nil +
        ("id" -> SHtml.hidden(() => rolaVar(current))) ++
        createField("rola", "naziv", validation, Some("validationError"),
                    SHtml.text(rola.naziv, rola.naziv = _) % ("id" -> "naziv")) ++
        createField("rola", "aktivan", true, None,
                    SHtml.checkbox(rola.aktivan.getOrElse(false), rola.aktivan = _)) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("rola", xhtml, bindLista:_*)
    }
}
