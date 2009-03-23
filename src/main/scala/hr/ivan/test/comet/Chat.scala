package hr.ivan.test.comet

import scala.actors._
import scala.actors.Actor._
import net.liftweb.http._
import net.liftweb.util.Helpers._
import net.liftweb.util.{Helpers, Box, Full, Empty, Failure, Log}
import scala.collection.immutable.TreeMap
import scala.xml._
import S._
import SHtml._
import net.liftweb.util._
import net.liftweb.http.js._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._

class Chat extends CometActor {
    private var userName = ""
    private var currentData: List[ChatLine] = Nil
    override def defaultPrefix = Some("chat")

    private lazy val infoId = uniqueId + "_info"

    private val server = {
        val ret = ChatServer
        ret ! ChatServerAdd(this)
        ret
    }

    override def lowPriority = {
        case ChatServerUpdate(value) =>
            (value diff currentData) match {
                case Nil =>
                case diff => partialUpdate(diff.reverse.foldLeft(Noop)((a,b) => a & AppendHtml(infoId, line(b))))
            }

            currentData = value
    }

    override lazy val fixedRender: Box[NodeSeq] = {
        val n = "id"+randomString(10)
        ajaxForm(After(100, SetValueAndFocus(n, "")),
                 (text("", sendMessage _) % ("id" -> n)) ++ <input type="submit" value="Chat"/> )
    }

    def line(cl: ChatLine) = (<li>{hourFormat(cl.when)} {cl.user}: {cl.msg}</li>)

    override def render = (<span>Hello "{userName}"
            <ul id={infoId}>{currentData.reverse.flatMap(line)}</ul>
                           </span>)

    override def localSetup {
        if (userName.length == 0) {
            ask(new AskName, "what's your username") {
                case s: String if (s.trim.length > 2) => userName = s.trim; reRender(true)
                case s => localSetup; reRender(false)
            }
        }
    }

    // def waitForUpdate : Option[List[ChatLine]] = receiveWithin(100) {case ChatServerUpdate(l) => Some(l) case TIMEOUT => None}

    def sendMessage(msg: String) = server ! ChatServerMsg(userName, msg.trim)


}
