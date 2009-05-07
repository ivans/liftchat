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

    def deleteLink[T <: AnyRef](clazz : Class[T], id : Long, dest : String, link : NodeSeq, model : LocalEMF with RequestVarEM) = {
        SHtml.link(dest, () => {
                var success = false;
                try {
                    println("a")
                    model.removeAndFlush(model.getReference(clazz, id))
                    println("b")
                    success = true
                    notice("Succesfully deleted!")
                } catch {
                    case ee : EntityExistsException =>
                        println("e3")
                        logAndError("Entity exists! Maybe object has children?", ee)
                    case pe : PersistenceException =>
                        println("e2")
                        logAndError("Persistence exception", pe)
                    case e : Throwable =>
                        println("e1")
                        logAndError("Some strange exception happened", e)
                } finally {
                    println("c")
                    S.redirectTo(dest)
                    println("d")
                }
            }, link)
    }

}
