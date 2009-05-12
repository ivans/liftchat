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

class Naselja {

    def list (implicit xhtml : NodeSeq) : NodeSeq = {

        def doAfterDelete(success : Boolean, obj : Option[Naselje]) = success match {
            case true => notice("Naselje " + obj.map(_.naziv).getOrElse("??") + " je obrisano")
            case false => notice("Naselje nije obrisana")
        }

        createList[Naselje](NaseljeDAO.allNaselja, "naselje",
                            n => {
                "naziv" -> Text(n.naziv) ::
                "sifra" -> Text(n.sifra) ::
                "mbr" -> Text(n.mbr) ::
                "aktivan" -> SHtml.checkbox(n.aktivan.getOrElse(false), _ => Nil, ("disabled" -> "true")) ::
                "edit" -> SHtml.link("/pages/sifarnici/naselja/naseljaEdit", () => naseljeVar(n), Text(?("Edit"))) ::
                "delete" -> deleteLink(classOf[Naselje], n.id, "/pages/sifarnici/naselja/naseljaList", Text(?("Delete")), Some(doAfterDelete _), Model) ::
                Nil
            }
        )
    }

    object naseljeVar extends RequestVar(new Naselje())
    def naselje = naseljeVar.is

    def add (implicit xhtml : NodeSeq) : NodeSeq = {

        val current = naselje

        object validation extends Validations[Naselje] {
            addValidator("naziv", _.naziv.length != 0, Some("Naziv ne može biti prazan"))
            addValidator("sifra", _.sifra.length != 0, Some("Šifra ne može biti prazan"))
        }

        def doAdd () = {
            if(validation.doValidation(naselje) == true) {
                trySavingEntity[Naselje](naselje, Some("Novo naselje dodano"), Some("Spremljene promjene"))(Model)
                redirectTo("/pages/sifarnici/naselja/naseljaList")
            }
        }

        def bindLista = Nil +
        ("id" -> SHtml.hidden(() => naseljeVar(current))) ++
        createField("naselje", "naziv",
                    validation.is("naziv"), Some("validationError"),
                    SHtml.text(naselje.naziv, naselje.naziv = _)) ++
        createField("naselje", "sifra",
                    validation.is("sifra"), Some("validationError"),
                    SHtml.text(naselje.sifra, naselje.sifra = _)) ++
        createField("naselje", "mbr", true, None,
                    SHtml.text(naselje.mbr, naselje.mbr = _)) ++
        createField("naselje", "aktivan", true, None,
                    SHtml.checkbox(naselje.aktivan.getOrElse(false), naselje.aktivan = _)) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("naselje", xhtml, bindLista:_*)
    }
}
