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

    /** Add / edit naselje
     */
    override def add (implicit xhtml : NodeSeq) : NodeSeq = {

        tryLoadingEntityByIdFromParam("id")

        def doAdd () = {
            validation << ("naziv", _.naziv.length != 0, Some("Naziv ne može biti prazan"))
            validation << ("sifra", _.sifra.length != 0, Some("Šifra ne može biti prazan"))

            validation.valid_?(entity) {
                trySavingEntity(entity, Some("Novo naselje dodano"), Some("Spremljene promjene"))(Model)
                redirectTo("/pages/sifarnici/naselja/naseljaList")
            }
        }

        val forma = new Form("naselje", xhtml)
        forma id("id")
        forma inputText("naziv", entity.naziv, entity.naziv = _)
        forma inputText("sifra", entity.sifra, entity.sifra = _)
        forma inputText("mbr", entity.mbr, entity.mbr = _)
        forma inputCheckBox("aktivan", entity.aktivan.getOrElse(false), entity.aktivan = _)
        forma submit("submit", "Save", doAdd)

        forma !!
    }

    /** Search forma
     */
    var searchNaziv = ""

    override def search(implicit xhtml : NodeSeq) : NodeSeq = {
        val forma = new Form("s", xhtml)
        forma inputText("naziv", searchNaziv, searchNaziv = _)
        forma submit("submit", "Traži", () => {})
        forma !!
    }
}