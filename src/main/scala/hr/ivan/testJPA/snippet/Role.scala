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

        def createField(name : String, valid : Boolean, invalidClass : Option[String], field : NodeSeq) : Seq[BindParam] = {
            val clazz = ("class" -> {if(valid) invalidClass.getOrElse("invalid") })
            val nameLabel = name + "Label"
            val nameMsg = name + "Msg"
            List(
                nameLabel ->
                <label for={name}>
                    {chooseTemplate("rola", "nazivLabel", xhtml)}
                </label>,
                  nameMsg -> <lift:Msg id={nameMsg}/> % clazz,
                  name -> field
            )
        }

        def bindLista : Seq[BindParam] = Nil +
        ("id" -> SHtml.hidden(() => rolaVar(current))) ++
        createField("naziv", nazivGreska.is, Some("validationError"), SHtml.text(rola.naziv, rola.naziv = _)
                    % ("id" -> "naziv")) +
        ("submit" -> SHtml.submit(?("Save"), doAdd))

        bind("rola", xhtml, bindLista:_*)
    }
}
