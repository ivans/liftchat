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

class Uredi extends PageUtil {

    def allUredi = Model.createNamedQuery[Ured]("findAllUredi") getResultList()

    def list (xhtml : NodeSeq) : NodeSeq = {
        val uredi = allUredi
        uredi.flatMap(ured =>
            bind("ured", xhtml,
                 "naziv" -> Text(ured.naziv),
                 "uredNadredjeni" -> Text(if (ured.uredNadredjeni != null) ured.uredNadredjeni.naziv else ""),
                 "edit" -> SHtml.link("/uredi/uredi", () => uredVar(ured), Text(?("Edit")))
            ))
    }

    object uredVar extends RequestVar(new Ured())
    def ured = uredVar.is

    def add (xhtml : NodeSeq) : NodeSeq = {
        def doAdd () = {
            if (ured.naziv.length == 0) {
                error("naziv", "Naziv ureda ne može biti prazan!")
            } else {
                try {
                    Model.persistAndFlush(ured)
                    //Model.mergeAndFlush(ured)
                    redirectTo("/uredi/uredi")
                } catch {
                    case ee : EntityExistsException => error("Author already exists")
                    case pe : PersistenceException => logAndError("Error adding user")
                }
            }
        }

        val current = ured
        val choices = allUredi.map(ured => (ured.id.toString -> ured.naziv))
        val default = if (ured.uredNadredjeni != null) { Full(ured.uredNadredjeni.id.toString) } else { Empty }

        bind("ured", xhtml,
             "id" -> SHtml.hidden(() => uredVar(current)),
             "naziv" -> SHtml.text(ured.naziv, ured.naziv = _),
             "id2" -> Text(ured.id.toString),
             "uredNadredjeni" -> SHtml.select(choices,
                                              {Log.info("urednadredjeni default = " + default); default},
                                              uredId => {
                    Log.info("Selecting ured : id = " + uredId)
                    ured.uredNadredjeni = Model.getReference(classOf[Ured], uredId)
                    Log.info("Odabrani ured = " + ured.uredNadredjeni)
                }
            ),
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }

}
