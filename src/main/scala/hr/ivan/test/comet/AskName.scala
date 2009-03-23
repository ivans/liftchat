package hr.ivan.test.comet

import net.liftweb.http._
import S._
import SHtml._
import net.liftweb.util._
import scala.xml._

class AskName extends CometActor {
    override def defaultPrefix = Some("ask_name")

    def render = ajaxForm(<div>What is your username?</div> 
                          ++ text("",name => answer(name.trim))
                          ++ <input type="submit" value="Enter"/>)

}
