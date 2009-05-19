package hr.ivan.util

import scala.xml.{NodeSeq,Text}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

trait SimpleSifarnik[T] extends StatefulSnippet {

    def newInstance : T

    object entityVar extends RequestVar(newInstance)
    def entity = entityVar.is

    def doAfterDelete(success : Boolean, obj : Option[T]) = success match {
        case true => notice("Entitet " + obj.getOrElse(null) + " je uspješno obrisan!")
        case false => notice("Entitet nije obrisan!")
    }

    /** Basic dispatch dispatches to list, add, pager and search methods
     */
    def dispatch: DispatchIt = {
        case "list" => println("::: dispatch to list"); list(_)
        case "add" => println("::: dispatch to add"); add(_)
        case "pager" => println("::: dispatch to pager"); pager(_)
        case "search" => println("::: dispatch to search"); search(_)
    }

    /** Osnovne metode (list, add...)
     */
    def list (implicit xhtml : NodeSeq) : NodeSeq = Nil
    def add (implicit xhtml : NodeSeq) : NodeSeq = Nil
    def search(implicit xhtml : NodeSeq) : NodeSeq = Nil

    /** Stvari vezane uz pager
     */
    def fetchEntityList() : Seq[T] = Nil
    def fetchEntityListCount() : Option[Int] = None

    object entityList extends RequestVar(fetchEntityList())
    object entityListCount extends RequestVar[Option[Int]](fetchEntityListCount())

    def pageSize = pageSize_
    def first = first_

    var pageSize_ : Int = 10
    var first_ : Int = 0

    def pager(xhtml : NodeSeq) : NodeSeq = {
        println ("creating pager. first = " + first_ + ", pageSize = " + pageSize_)

        def actionFirst = {
            first_ = 0
            println("actionFirst: first = " + first_)
        }

        def actionPrevious = {
            first_ = first_ - pageSize_
            if(first_ < 0) first_ = 0
            println("actionPrevious: first = " + first_)
        }

        def actionNext = {
            println("actionNext: first = " + first_ + " pageSize = " + pageSize_)
            first_ = first_ + pageSize_
            if(first_ >= lastFirst) first_ = lastFirst
            println("actionNext: first = " + first_)
        }

        def lastFirst = (entityListCount.is.get / pageSize) * pageSize

        def actionLast = {
            first_ = lastFirst
            println("actionLast: first = " + first_)
        }

        bind("page", xhtml,
             "first" -> this.link("", () => {actionFirst}, chooseTemplate("page", "first", xhtml)),
             "previous" -> this.link("", () => {actionPrevious}, chooseTemplate("page", "previous", xhtml)),
             "next" -> this.link("", () => {actionNext}, chooseTemplate("page", "next", xhtml)),
             "last" -> (entityListCount.is match {
                    case Some(x) => this.link("", () => {actionLast}, chooseTemplate("page", "last", xhtml))
                    case None => chooseTemplate("page", "last", xhtml)
                }),
             "current" -> Text((first / pageSize).toString),
             "count" ->  (entityListCount.is match {
                    case Some(x) => Text(x.toString)
                    case None => Text("?")
                })
        )
    }

}
