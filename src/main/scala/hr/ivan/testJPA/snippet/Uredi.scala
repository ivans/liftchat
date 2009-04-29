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

class Uredi {

    def list (xhtml : NodeSeq) : NodeSeq = {
        val uredi = UredDAO.allUredi
        uredi.flatMap(ured =>
            bind("ured", xhtml,
                 "naziv" -> Text(ured.naziv),
                 "uredNadredjeni" -> Text(if (ured.uredNadredjeni != null) ured.uredNadredjeni.naziv else ""),
                 "brojUsera" -> Text(ured.useri.size.toString),
                 "edit" -> SHtml.link("/uredi/uredi", () => uredVar(ured), Text(?("Edit"))),
                 "delete" -> deleteLink(classOf[Ured], ured.id, "/uredi/uredi", Text(?("Delete")), Model),
            ))
    }

    object uredVar extends RequestVar(new Ured())
    def ured = uredVar.is

    def noviUred = SHtml.link("/uredi/uredi", () => uredVar(new Ured), Text(?("New ured")))

    def add (xhtml : NodeSeq) : NodeSeq = {
        def doAdd () = {
            if (ured.naziv.length == 0) {
                error("naziv", "Naziv ureda ne moze biti prazan!")
            } else {
                try {
                    //Model.persistAndFlush(ured)
                    Model.mergeAndFlush(ured)
                    redirectTo("/uredi/uredi")
                } catch {
                    case ee : EntityExistsException => logAndError("Author already exists", ee)
                    case pe : PersistenceException => logAndError("Error adding user", pe)
                }
            }
        }

        val current = ured
        val choices = createSelectChoices(Some(?("Odaberite ured...")), UredDAO.allUredi, (ured : Ured) => (ured.id.toString -> ured.naziv))
        val default = if (ured.uredNadredjeni != null) { Full(ured.uredNadredjeni.id.toString) } else { Empty }

        def doUredSelect(uredId : String) = {
            ured.uredNadredjeni = getFromEM(classOf[Ured], uredId, Model).getOrElse(null)
        }

        bind("ured", xhtml,
             "id" -> SHtml.hidden(() => uredVar(current)),
             "naziv" -> SHtml.text(ured.naziv, ured.naziv = _),
             "id2" -> Text(ured.id.toString),
             "uredNadredjeni" -> SHtml.select(choices, default, doUredSelect),
             "mode" -> Text(if(ured.id == 0) "Add ured" else "Edit ured"),
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }

}
