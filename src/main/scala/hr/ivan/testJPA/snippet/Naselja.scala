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

        val id = Id("id")
        val naziv = InputText("naselje", "naziv", entity.naziv, entity.naziv = _)
        val sifra = InputText("naselje", "sifra", entity.sifra, entity.sifra = _)
        val mbr = InputText("naselje", "mbr", entity.mbr, entity.mbr = _)
        val aktivan = InputCheckBox("naselje", "aktivan", entity.aktivan.getOrElse(false), entity.aktivan = _)
        val submit = Submit("submit", "Save", doAdd)

        val forma = Form("naselje", xhtml, List(id, naziv, sifra, mbr, aktivan, submit))
        forma()
    }

    /** Search forma
     */
    var searchNaziv = ""

    override def search(implicit xhtml : NodeSeq) : NodeSeq = {
        bind("s", xhtml,
             "naziv" -> SHtml.text(searchNaziv, searchNaziv = _),
             "submit" -> SHtml.submit(?("Traži"), () => {})
        )
    }

}