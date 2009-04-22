package hr.ivan.test.snippet

import _root_.net.liftweb._
import http._
import S._
import SHtml._
import util._
import Helpers._
import js._
import JsCmds._

import scala.xml. _

import hr.ivan.test.model._

class DBTests {

    def test0 = <span>DB Tests are here:</span> ++ <div id="msgTest1"></div>

    def test1 = SHtml.ajaxButton("Test1", () => {
            Log info ("Saving user with ured")

            var user = User.create
            user firstName "Ivan - 1"
            user lastName "Senji - 1"

            var uredMin = Ured.create
            uredMin naziv "Ministarstvo"

            user ured uredMin

            uredMin.save()
            user.save()

            Log info "All saved"

            SetHtml("msgTest1", Text("Method test1 done..."))
        })

}
