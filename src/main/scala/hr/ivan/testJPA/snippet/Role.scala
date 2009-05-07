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

    type BindParamsGenerator[T] = T => Seq[BindParam]

    def createList[T](lista : Seq[T], itemName : String, params : BindParamsGenerator[T]) (implicit xhtml : NodeSeq) : NodeSeq = {
        lista.flatMap(obj =>
            bind(itemName, xhtml, params(obj):_*)
        )
    }

    def list (implicit xhtml : NodeSeq) : NodeSeq = {
        val role = RolaDAO.allRoleAktivne
        createList[Rola](RolaDAO.allRoleAktivne, "rola",
                         r => {
                "naziv" -> Text(r.naziv) ::
                "aktivan" -> SHtml.checkbox(r.aktivan.getOrElse(false), _ => Nil, ("disabled" -> "true")) ::
                "edit" -> SHtml.link("/pages/role/addEdit", () => rolaVar(r), Text(?("Edit"))) ::
                "delete" -> deleteLink(classOf[Rola], r.id, "/pages/role/list", Text(?("Delete")), Model) ::
                Nil
            }
        )
    }

    object rolaVar extends RequestVar(new Rola())
    def rola = rolaVar.is

    def add (xhtml : NodeSeq) : NodeSeq = {

        object nazivGreska extends RequestVar(false)

        def doAdd () = {
            if (rola.naziv.length == 0) {
                error("nazivMsg", <span class="validationError">Naziv ne moze biti prazan</span>)
                nazivGreska(true)
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
             "nazivLabel" -> <label for="naziv"><lift:loc>Naziv</lift:loc></label>
             % ("class" -> {println(nazivGreska); if(nazivGreska.is) "validationError" else ""}),
             "naziv" -> (
                SHtml.text(rola.naziv, rola.naziv = _)
                % ("id" -> "naziv")
                % ("class" -> {println(nazivGreska); if(nazivGreska.is) "validationError" else ""})
                ++ <lift:Msg id="nazivMsg" />
            ),
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }
}
