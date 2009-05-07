package hr.ivan.util

import scala.xml.{NodeSeq,Text}
import org.scala_libs.jpa._

import net.liftweb.util.{Log, BindHelpers, Helpers}
import net.liftweb.http.SHtml
import _root_.net.liftweb._
import http._
import S._
import Helpers._

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

    def deleteLink[T <: AnyRef](clazz : Class[T], 
                                id : Long,
                                dest : String,
                                link : NodeSeq,
                                postDelete : Option[(Boolean, Option[T]) => Any],
                                model : LocalEMF with RequestVarEM) = {
        SHtml.link(dest, () => {
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
