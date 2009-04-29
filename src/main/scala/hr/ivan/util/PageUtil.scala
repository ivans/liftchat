package hr.ivan.util

import scala.xml.{NodeSeq,Text}
import org.scala_libs.jpa._

import net.liftweb.util.{Log}
import net.liftweb.http.SHtml
import _root_.net.liftweb._
import http._
import S._

import javax.persistence.{EntityExistsException,PersistenceException}

object PageUtil {

    def logAndError(e : String, ex : Throwable) = { error(e); Log.error(e + " " + (if(ex != null) ex.getMessage else "..")) }

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

    def deleteLink[T <: AnyRef](clazz : Class[T], id : Long, redirectToDest : String, link : NodeSeq, model : LocalEMF with RequestVarEM) = {
        SHtml.link("", () => {
                try {
                    model.removeAndFlush(model.getReference(clazz, id))
                } catch {
                    case ee : EntityExistsException =>
                        logAndError("Entity exists! Maybe object has children?", ee)
                    case pe : PersistenceException =>
                        logAndError("Persistence exception", pe)
                    case _ =>
                        logAndError("Some strange exception happened", null)
                } finally {
                    redirectTo(redirectToDest)
                }
            }, link)
    }

}
