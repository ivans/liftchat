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

        createList[Rola](RolaDAO.allRoleAktivne, "rola",
                         r => {
                "naziv" -> Text(r.naziv) ::
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

        object validations extends RequestVar[HashMap[String, Boolean]](new HashMap[String, Boolean] {
                override def default(key: String): Boolean = true
            })

        def doAdd () = {
            if (rola.naziv.length == 0) {
                createErrorNotification("naziv", Some("validationError"), "Naziv ne može biti prazan")
                validations.is.put("naziv", false)
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

        def bindLista : Seq[BindParam] = Nil +
        ("id" -> SHtml.hidden(() => rolaVar(current))) ++
        createField("rola", "naziv", validations.is("naziv"), Some("validationError"), SHtml.text(rola.naziv, rola.naziv = _)
                    % ("id" -> "naziv")) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("rola", xhtml, bindLista:_*)
    }
}
