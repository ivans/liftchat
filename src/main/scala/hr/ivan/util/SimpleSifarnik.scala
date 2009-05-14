package hr.ivan.util

import scala.xml.{NodeSeq,Text}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

class SimpleSifarnik[T](newT : => T) {

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



}
