package hr.ivan.util

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

class SimpleSifarnik[T](newT : => T) {

    object entityVar extends RequestVar(newT)
    def entity = entityVar.is

    def doAfterDelete(success : Boolean, obj : Option[T]) = success match {
        case true => notice("Entitet " + obj.getOrElse(null) + " je uspješno obrisan!")
        case false => notice("Entitet nije obrisan!")
    }

}
