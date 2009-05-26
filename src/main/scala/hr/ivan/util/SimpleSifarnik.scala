package hr.ivan.util

import scala.xml.{NodeSeq, Text, Elem}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

import PageUtil._
import EntityUtil._

import hr.ivan.testJPA.model.Model

trait SimpleSifarnik[T <: AnyRef] extends StatefulSnippet {

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

    def tryLoadingEntityByIdFromParam(param : String) = {
        val id = S.param(param)
        try {
            id match {
                case Full(strId) => tryLoadingEntityById(strId.toLong)
                case Empty =>
            }
        }
    }

    def tryLoadingEntityById(id : Long) = {
        lazy val newObj = newInstance
        entityVar(getFromEM(newObj.getClass.asInstanceOf[Class[T]], id, Model).getOrElse(newObj))
    }

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
        println ("creating pager. lastPage = " + lastPage + ", currentPage = " + currentPage)

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
            println("actionNext: first = " + first_)
        }

        def actionLast = {
            first_ = lastFirst
            println("actionLast: first = " + first_)
        }

        def lastPage = entityListCount.is.get / pageSize
        def currentPage = first / pageSize
        def lastFirst = lastPage * pageSize
        def onLastPage_? = lastPage == currentPage
        def onFirstPage_? = first < pageSize
        def onlyOnePage_? : Boolean = entityListCount.is match {
            case Some(x) => x <= pageSize
            case None => true
        }

        // ako smo iza zadnjeg odi stranicu manje
        println("Prije while petlje", first, entityListCount.is.get)
        while(first >= entityListCount.is.get) {
            first_ = first_ - pageSize
            println("U petlji smanjem first na", first_)
        }
        if(first < 0) first_ = 0

        bind("page", xhtml,
             "first" -> ((onlyOnePage_? || onFirstPage_?) match {
                    case true => chooseTemplate("page", "first", xhtml)
                    case false => this.link("", () => {actionFirst}, chooseTemplate("page", "first", xhtml))
                }),
             "previous" -> ((onlyOnePage_? || onFirstPage_?) match {
                    case true => chooseTemplate("page", "previous", xhtml)
                    case false => this.link("", () => {actionPrevious}, chooseTemplate("page", "previous", xhtml))
                }),
             "next" -> ((onlyOnePage_? || onLastPage_?) match {
                    case true => chooseTemplate("page", "next", xhtml)
                    case false => this.link("", () => {actionNext}, chooseTemplate("page", "next", xhtml))
                }),
             "last" -> ((onlyOnePage_? || onLastPage_?) match {
                    case true => chooseTemplate("page", "last", xhtml)
                    case false => this.link("", () => {actionLast}, chooseTemplate("page", "last", xhtml))
                }),
             "currentPage" -> Text(currentPage.toString),
             "currentFirst" -> Text(first.toString),
             "count" ->  (entityListCount.is match {
                    case Some(x) => Text(x.toString)
                    case None => Text("?")
                })
        )
    }

    /* Linkovi
     */
    def statelessLink(to : String, func : ()=>Any, body : NodeSeq) : NodeSeq = SHtml.link(to, func, body)
    def statefullLink(to : String, func : ()=>Any, body : NodeSeq) : NodeSeq = this.link(to, func, body)

    /** Validacija
     */
    implicit object validation extends Validators[T]

    /** Kreiranje polja i sl
     */
    def createInputTextField[T](parentName : String, name : String,
                                value : String, setter : (String) => Any)
    (implicit xhtml : NodeSeq): Seq[BindParam] = {
        createField(parentName, name, validation, SHtml.text(value, setter))
    }

    /** Komponente, forme, i slično
     */
    abstract class Component {
        def toNodeSeq : Seq[BindParam]
    }
    case class Id(name : String) extends Component {
        override def toNodeSeq = List(name -> SHtml.hidden(() => entityVar(entity)))
    }
    case class InputText(parent : String, name : String, value : String, setter : (String) => Any)(implicit xhtml : NodeSeq) extends Component {
        override def toNodeSeq =
        createField(parent, name, validation,
                    SHtml.text(safeGet(value, ""), setter))
    }
    case class InputCheckBox(parent : String, name : String, value : Boolean, setter : (Boolean) => Any)(implicit xhtml : NodeSeq) extends Component {
        override def toNodeSeq = createField(parent, name, validation,
                                             SHtml.checkbox(value, setter))
    }
    case class Submit(name : String, label : String, method : () => Any) extends Component {
        override def toNodeSeq = List(name -> SHtml.submit(?(label), method))
    }
    case class Form(formName : String, xhtml : NodeSeq, var comp : List[Component]) {

        def this(formName : String, xhtml : NodeSeq) = this(formName, xhtml, Nil)

        def !! = bind(formName, xhtml, comp.flatMap(x => x.toNodeSeq):_*)

        def << (c : Component) = comp = c :: comp

        def id(name : String) = Id(name)

        def inputText(name : String, value : String, setter : (String) => Any)(implicit xhtml : NodeSeq) = 
        InputText(formName, name, value, setter)(xhtml)

        def inputCheckBox(name : String, value : Boolean, setter : (Boolean) => Any)(implicit xhtml : NodeSeq) =
        InputCheckBox(formName, name, value, setter)(xhtml)

        def submit(name : String, label : String, method : () => Any) = Submit(name, label, method)
    }

}
