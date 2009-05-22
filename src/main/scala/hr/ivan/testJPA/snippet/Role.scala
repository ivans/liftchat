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
import hr.ivan.util.{PageUtil, EntityUtil, SimpleSifarnik}
import EntityUtil._
import PageUtil._
import Model._

class Role extends SimpleSifarnik[Rola] {

    def newInstance = new Rola

    override def list (implicit xhtml : NodeSeq) : NodeSeq = {

        def doAfterDelete(success : Boolean, obj : Option[Rola]) = success match {
            case true => notice("Rola " + obj.map(_.naziv).getOrElse("??") + " je obrisana")
            case false => notice("Rola nije obrisana")
        }

        createList[Rola](RolaDAO.allRole, "rola",
                         r => {
                "naziv" -> outputText(r.naziv) ::
                "aktivan" -> SHtml.checkbox(r.aktivan.getOrElse(false), _ => Nil, ("disabled" -> "true")) ::
                "edit" -> SHtml.link("/pages/role/addEdit", () => entityVar(r), Text(?("Edit"))) ::
                "delete" -> deleteLink(classOf[Rola], r.id, "/pages/role/list", Text(?("Delete")), Some(doAfterDelete _), Model, statelessLink) ::
                Nil
            }
        )
    }

    override def add (implicit xhtml : NodeSeq) : NodeSeq = {

        val current = entity

        validation << ("naziv", _.naziv.length != 0, Some("Naziv ne može biti prazan"))
        validation << ("naziv", _.naziv.length > 3, Some("Naziv mora biti duži od 3 znaka"))

        def doAdd () = {
            validation.valid_?(entity) {
                trySavingEntity[Rola](entity, Some("Nova rola dodana"), Some("Updatana rola"))(Model)
                redirectTo("/pages/role/list")
            }
        }

        def bindLista = Nil +
        ("id" -> SHtml.hidden(() => entityVar(current))) ++
        createField("rola", "naziv", validation,
                    SHtml.text(entity.naziv, entity.naziv = _) % ("id" -> "naziv")) ++
        createField("rola", "aktivan", true,
                    SHtml.checkbox(entity.aktivan.getOrElse(false), entity.aktivan = _)) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("rola", xhtml, bindLista:_*)
    }
}
