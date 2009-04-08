package hr.ivan.test.snippet

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

import scala.xml._

class HelloWorld extends StatefulSnippet {

    val dispatch: DispatchIt = {
        case "howdy" => howdy _
    }

    var cnt = new SessionVar[Int](0) {}

    private var counter = 0

    //stateless :: counter će se uvijek ispisivati da je 1
    def howdy(xhtml : NodeSeq) =
    <span>
        Welcome to liftchat at {new _root_.java.util.Date},
        counter is at
        {counter += 1; 
         cnt.set(cnt.is + 1);
         counter + cnt.is}
    </span>
}

