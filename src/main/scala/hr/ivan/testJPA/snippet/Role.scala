package hr.ivan.testJPA.snippet

import scala.xml.{NodeSeq,Text}

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

    def list (xhtml : NodeSeq) : NodeSeq = {
        val role = RolaDAO.allRoleAktivne
        role.flatMap(rola =>
            bind("rola", xhtml,
                 "naziv" -> Text(rola.naziv),
                 "aktivan" -> SHtml.checkbox(rola.aktivan.getOrElse(false), _ => Nil, ("disabled" -> "true")),
                 "edit" -> SHtml.link("/pages/role/addEdit", () => rolaVar(rola), Text(?("Edit"))),
                 "delete" -> deleteLink(classOf[Rola], rola.id, "/pages/role/list", Text(?("Delete")), Model),
            ))
    }

    object rolaVar extends RequestVar(new Rola())
    def rola = rolaVar.is

    def add (xhtml : NodeSeq) : NodeSeq = {
        def doAdd () = {
            if (rola.naziv.length == 0) {
                error("naziv", "Naziv ne moze biti prazan")
            } else {
                try {
                    val nova = rola.id != 0
                    Model.mergeAndFlush(rola)
                    notice(rola.id match { 
                            case 0 => "Nova rola dodana!"
                            case _ => "Updatana postojeća rola"
                        })
                    redirectTo("/pages/role/list")
                } catch {
                    case ee : EntityExistsException => logAndError("Rola vec postoji ", ee)
                    case pe : PersistenceException => logAndError("Error adding Rola ", pe)
                }
            }
        }

        val current = rola

        bind("rola", xhtml,
             "id" -> SHtml.hidden(() => rolaVar(current)),
             "naziv" -> SHtml.text(rola.naziv, rola.naziv = _),
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }
}
