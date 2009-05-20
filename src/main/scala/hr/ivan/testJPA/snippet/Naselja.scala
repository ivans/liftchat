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

class Naselja extends SimpleSifarnik[Naselje] {

    override def dispatch : DispatchIt =  {
        case "searchNaziv" => _ => Text(searchNaziv)
        case other => super.dispatch(other)
    }

    def newInstance = new Naselje

    override def fetchEntityList = NaseljeDAO.findNaseljaByNaziv(first, pageSize, searchNaziv)
    override def fetchEntityListCount = Some(NaseljeDAO.naseljaCountByNaziv(searchNaziv).toInt)

    override def list (implicit xhtml : NodeSeq) : NodeSeq = {
        createList[Naselje](entityList.get, "naselje",
                            n => {
                "naziv" -> outputText(n.naziv) ::
                "sifra" -> outputText(n.sifra) ::
                "mbr" -> outputText(n.mbr) ::
                "aktivan" -> SHtml.checkbox(n.aktivan.getOrElse(false), _ => Nil, ("disabled" -> "true")) ::
                "edit" -> SHtml.link("/pages/sifarnici/naselja/naseljaEdit", () => entityVar(n), Text(?("Edit"))) ::
                "delete" -> deleteLink(classOf[Naselje], n.id, "/pages/sifarnici/naselja/naseljaList", Text(?("Delete")), Some(doAfterDelete _), Model, statefullLink) ::
                Nil
            }
        )
    }

    override def add (implicit xhtml : NodeSeq) : NodeSeq = {

        def doAdd () = {
            validation << ("naziv", _.naziv.length != 0, Some("Naziv ne može biti prazan"))
            validation << ("sifra", _.sifra.length != 0, Some("Šifra ne može biti prazan"))

            validation.valid_?(entity) {
                trySavingEntity(entity, Some("Novo naselje dodano"), Some("Spremljene promjene"))(Model)
                redirectTo("/pages/sifarnici/naselja/naseljaList")
            }
        }

        def bindLista = Nil +
        ("id" -> SHtml.hidden(() => entityVar(entity))) ++
        createField("naselje", "naziv", validation, Some("validationError"),
                    SHtml.text(entity.naziv, entity.naziv = _)) ++
        createField("naselje", "sifra", validation, Some("validationError"),
                    SHtml.text(entity.sifra, entity.sifra = _)) ++
        createField("naselje", "mbr", true, None,
                    SHtml.text(safeGet(entity.mbr, ""), entity.mbr = _)) ++
        createField("naselje", "aktivan", true, None,
                    SHtml.checkbox(entity.aktivan.getOrElse(false), entity.aktivan = _)) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("naselje", xhtml, bindLista:_*)
    }

    /** Search dio
     */
    var searchNaziv = ""

    override def search(implicit xhtml : NodeSeq) : NodeSeq = {
        bind("s", xhtml,
             "naziv" -> SHtml.text(searchNaziv, searchNaziv = _),
             "submit" -> SHtml.submit(?("Traži"), () => {})
        )
    }

}