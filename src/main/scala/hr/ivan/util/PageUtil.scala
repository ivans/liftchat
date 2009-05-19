package hr.ivan.util

import scala.xml.{NodeSeq, Text, Elem}
import scala.collection.mutable.HashMap
import org.scala_libs.jpa._

import net.liftweb.util.{Log, BindHelpers, Helpers}
import net.liftweb.http.SHtml
import _root_.net.liftweb._
import http._
import S._
import Helpers._

import hr.ivan.util.EntityUtil._

import javax.persistence.{EntityExistsException,PersistenceException}

object PageUtil {

    def logAndError(e : String, ex : Throwable) = {
        error(e);
        Log.error(e + " " + (if(ex != null) ex.getMessage else ".."))
        //Log.error(ex.getStackTrace.map(x=>x.getClassName).foldLeft(""){(x,y) => x+y+"\n"})
        ex.printStackTrace
    }

    def getAllCauses(e : Throwable) : String = if (e == null) {
        " END"
    } else {
        e.getMessage + " :: " + getAllCauses(e.getCause)
    }

    def safeGet[T](value : => T, default : Option[T]) : Option[T] = {
        try {
            if(value != null) return Some(value)
            else return default
        } catch {
            case e : NullPointerException => return default
        }
    }

    def outputText(text : => String) : Text = {
        Text(safeGet(text, ""))
    }

    def safeGet[T](value : => T, default : T) : T = {
        try {
            if(value != null) return value
            else return default
        } catch {
            case e : NullPointerException => return default
        }
    }

    def createSelectChoices[T](emptyChoice : Option[String], lista : Seq[T], mapping : T => (String, String)) = {
        def list = lista.map(mapping)
        emptyChoice match {
            case Some(str) => ("" -> str) :: list.toList
            case None => list
        }
    }

    def createList[T](lista : Seq[T], itemName : String, params : T => Seq[BindParam])(implicit xhtml : NodeSeq) : NodeSeq = {
        lista.flatMap(obj =>
            bind(itemName, xhtml, params(obj):_*)
        )
    }

    /* TODO: popraviti kad Scala dobije default vrijednosti
     */
    def createField(parentName : String, name : String, valid : Boolean, invalidClass : Option[String], field : NodeSeq)(implicit xhtml : NodeSeq) : Seq[BindParam] = {
        val clazz = if(!valid) invalidClass.getOrElse("invalid") else ""
        val nameLabel = name + "Label"
        val nameMsg = name + "Msg"
        Log.info("createField " + parentName + "." + name + " valid = " + valid)
        List(
            nameLabel -> <label for={name}>{chooseTemplate(parentName, nameLabel, xhtml)}</label> % ("class" -> clazz),
              nameMsg -> <lift:Msg id={nameMsg}/> % ("class" -> clazz),
              name -> field
        )
    }

    def createField[T](parentName : String, name : String, validations : Validations[T], invalidClass : Option[String], field : NodeSeq)(implicit xhtml : NodeSeq) : Seq[BindParam] = {
        createField(parentName, name, validations.is(name), invalidClass, field)(xhtml)
    }

    class Validations[T]
    extends RequestVar[HashMap[String, Boolean]] (
        new HashMap[String, Boolean] {
            override def default(key: String): Boolean = true
        }
    ) {
        case class ValidatorDesc(validator : T => Boolean, 
                                 component : String,
                                 msg : Option[String]);
        var validators : List[ValidatorDesc] = Nil

        def addValidator(component : String, v : T => Boolean, msg : Option[String]) = {
            validators = validators + ValidatorDesc(v, component, msg)
        }

        def doValidation(obj : T) : Boolean = {
            Log.info("Processing validations for " + obj)
            var valid = true
            for(validator <- validators) {
                if(validator.validator(obj) == false) {
                    createErrorNotification(validator.component, Some("validationError"), validator.msg.getOrElse("Pogreška u unosu"))
                    this.is.put(validator.component, false)
                    valid = false
                }
            }
            valid
        }
    }

    def trySavingEntity[T <: PrimaryKeyId](obj : T, msgInsert : Option[String], msgUpdate : Option[String])(implicit model : LocalEMF with RequestVarEM) {
        try {
            val nova = obj.id != 0
            model.mergeAndFlush(obj)
            notice(obj.id match {
                    case 0 => msgInsert.getOrElse("Uspješno dodano")
                    case _ => msgUpdate.getOrElse("Uspješno updatano")
                })
        } catch {
            case ee : EntityExistsException => logAndError("Entitet već postoji ", ee)
            case pe : PersistenceException => logAndError("Greška kod spremanja ", pe)
        }
    }

    def createErrorNotification(name : String, invalidClass : Option[String], message : String) =
    error(name + "Msg", <span class={invalidClass.getOrElse("invalid")}>{message}</span>)

    type LinkFuncType = (String, ()=>Any, NodeSeq) => NodeSeq
    /* TODO kad scala dobije defaultne vrijednosti ovo postaje SHtml.link
     */
    def deleteLink[T <: AnyRef](clazz : Class[T],
                                id : Long,
                                dest : String,
                                link : NodeSeq,
                                postDelete : Option[(Boolean, Option[T]) => Any],
                                model : LocalEMF with RequestVarEM,
                                linkGenerator : LinkFuncType) = {
        linkGenerator(dest, () => {
                var success = false
                var obj : Option[T] = None
                try {
                    obj = Some(model.getReference(clazz, id).asInstanceOf[T])
                    obj match {
                        case Some(rola) =>
                            model.removeAndFlush(rola);
                            success = true
                        case None =>
                    }
                } catch {
                    case ee : EntityExistsException =>
                        success = false
                        logAndError("Entity exists! Maybe object has children?", ee)
                    case pe : PersistenceException =>
                        success = false
                        logAndError("Persistence exception", pe)
                    case e : Throwable if !e.isInstanceOf[net.liftweb.http.ResponseShortcutException] =>
                        success = false
                        logAndError("Some unexpected exception happened", e)
                } finally {
                    def doFinallyBlock = postDelete match {
                        case Some(func) => func(success, obj)
                        case None => redirectTo(dest)
                    }
                    doFinallyBlock
                }
            }, link)
    }

}
