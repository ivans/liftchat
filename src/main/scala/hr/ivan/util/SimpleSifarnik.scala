package hr.ivan.util

import scala.xml.{NodeSeq,Text}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

trait SimpleSifarnik[T] {

    def newT : T

    var pageSize : Int = 10
    var first : Int = 0

    object entityVar extends RequestVar(newT)
    def entity = entityVar.is

    def getList() : List[T] = Nil
    def getListCount() : Int = 0

    def doAfterDelete(success : Boolean, obj : Option[T]) = success match {
        case true => notice("Entitet " + obj.getOrElse(null) + " je uspješno obrisan!")
        case false => notice("Entitet nije obrisan!")
    }

    def pager(xhtml : NodeSeq) : NodeSeq = {
        println ("creating pager...................")
        bind("page", xhtml,
             "first" -> SHtml.link("", () => {println("first")}, chooseTemplate("page", "first", xhtml)),
             "previous" -> SHtml.link("", () => {println("previous")}, chooseTemplate("page", "previous", xhtml)),
             "next" -> SHtml.link("", () => {println("next")}, chooseTemplate("page", "next", xhtml)),
             "last" -> SHtml.link("", () => {println("last")}, chooseTemplate("page", "last", xhtml)),
        )
    }

}
