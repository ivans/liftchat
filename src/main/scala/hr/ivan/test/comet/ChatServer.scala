package hr.ivan.test.comet

import scala.actors.Actor
import scala.actors.Actor._
import net.liftweb.util.Helpers._
import net.liftweb.util._
import scala.xml.{NodeSeq}
import scala.collection.immutable.TreeMap
import net.liftweb.textile.TextileParser
import scala.xml.Text
import java.util.Date

object ChatServer extends Actor {
    private var chats: List[ChatLine] = Nil
    private var listeners: List[Actor] = Nil
  
    def act = loop {
        react {
            case ChatServerMsg(user, msg) if msg.length > 0 =>
                chats = ChatLine(user, toHtml(msg), timeNow) :: chats
                val toDistribute = chats.take(15)
                listeners.foreach (_ ! ChatServerUpdate(toDistribute))
      
            case ChatServerAdd(me) =>
                me ! ChatServerUpdate(chats.take(15))
                listeners = me :: listeners
            
            case ChatServerRemove(me) =>
                listeners = listeners.remove(_ == me)

            case _ =>
        }
    }

    def toHtml(msg: String): NodeSeq = TextileParser.parse(msg, Empty). // parse it
    map(_.toHtml.toList match {case Nil => Nil case x :: xs => x.child}).  // convert to html and get the first child (to avoid things being wrapped in <p>)
    getOrElse(Text(msg)) // if it wasn't parsable, then just return a Text node of the message
  
    this.start
}

case class ChatLine(user: String, msg: NodeSeq, when: Date)
case class ChatServerMsg(user: String, msg: String)
case class ChatServerUpdate(msgs: List[ChatLine])
case class ChatServerAdd(me: Actor)
case class ChatServerRemove(me: Actor)

