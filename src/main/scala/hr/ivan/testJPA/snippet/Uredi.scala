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

class Uredi extends SimpleSifarnik[Ured] {

    def newInstance = new Ured

    override def list (implicit xhtml : NodeSeq) : NodeSeq = {
        createList[Ured](UredDAO.allUredi, "ured",
                         u => {
                "naziv" -> outputText(u.naziv) ::
                "uredNadredjeni" -> outputText(u.uredNadredjeni.naziv) ::
                "brojUsera" -> outputText(u.useri.size.toString) ::
                "edit" -> SHtml.link("/pages/sifarnici/uredi/uredi", () => entityVar(u), Text(?("Edit"))) ::
                "delete" -> deleteLink(classOf[Ured], u.id, "/pages/sifarnici/uredi/uredi", Text(?("Delete")), Some(doAfterDelete _), Model, statelessLink) ::
                Nil
            }
        )
    }

    def noviUred = SHtml.link("/pages/sifarnici/uredi/uredi", () => entityVar(new Ured), Text(?("New ured")))

    override def add (implicit xhtml : NodeSeq) : NodeSeq = {

        validation << ("naziv", _.naziv.length != 0, Some("Naziv ne može biti prazan"))

        def doAdd () = {
            validation.valid_?(entity) {
                trySavingEntity[Ured](entity, Some("Novi ured dodan"), Some("Spremljene promjene na uredu"))(Model)
                redirectTo("/pages/sifarnici/uredi/uredi")
            }
        }

        val current = entity
        val choicesUred = createSelectChoices(Some(?("Odaberite ured...")), UredDAO.allUredi, (ured : Ured) => (ured.id.toString -> ured.naziv))
        def selectedUredId = safeGet(entity.uredNadredjeni.id.toString, None)

        def doUredSelect(uredId : String) = {
            entity.uredNadredjeni = getFromEM(classOf[Ured], uredId, Model).getOrElse(null)
        }

        def bindLista = Nil +
        ("id" -> SHtml.hidden(() => entityVar(current))) ++
        createField("ured", "naziv", validation,
                    SHtml.text(entity.naziv, entity.naziv = _)) ++
        createField("ured", "uredNadredjeni", true,
                    SHtml.select(choicesUred, selectedUredId, doUredSelect)) +
        ("mode" -> Text(if(entity.id == 0) "Add ured" else "Edit ured")) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("ured", xhtml, bindLista:_*)
    }

}
