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

    val rand = new Random
    def randNum100 = rand.nextInt(100)

    //ovi linkovi idu kamo piše u linku
    def linkTest1 = SHtml.link("/pages/tests/test1", () => {println("Link1")}, Text(?("Link 1")))
    def linkTest2 = SHtml.link("/pages/tests/test2", () => {println("Link2")}, Text(?("Link 2")))
    //ovi linkovi ne idu nikamo
    def linkTest1b = SHtml.link("", () => {println("Link1")}, Text(?("Link 1")))
    def linkTest2b = SHtml.link("", () => {println("Link2")}, Text(?("Link 2")))
    //ovi linkovi idu kamo redirect šalje
    def linkTest1c = SHtml.link("", () => {println("Link1"); redirectTo("/pages/tests/test1")}, Text(?("Link 1")))
    def linkTest2c = SHtml.link("", () => {println("Link2"); redirectTo("/pages/tests/test2")}, Text(?("Link 2")))
    //ovi linkovi idu kamo radnom kaže
    def linkTestRand = SHtml.link("", () => {
            println("LinkRand");
            val x = randNum100;
            println("Random number == " + x)
            redirectTo(if(x<50) "/pages/tests/test1" else "/pages/tests/test2")
        }, Text(?("Link Rand")))
    def linkTestRandWithLinkToTest1 = SHtml.link("/pages/tests/test1", () => {
            println("LinkRand");
            val x = randNum100;
            println("Random number == " + x)
            redirectTo(if(x<50) "/pages/tests/test1" else "/pages/tests/test2")
        }, Text(?("Link Rand")))

}
