package hr.ivan.testJPA.snippet

import scala.xml.{NodeSeq,Text}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

class Test {

    var brojevi = <span>{
            (1 to 100).flatMap(x => <span>{x}::</span>)
        }</span>

    def brojevi2(xhtml : NodeSeq) = (1 to 100).flatMap(broj =>
        bind("broj", xhtml,
             "broj" -> Text(broj.toString)))

}
