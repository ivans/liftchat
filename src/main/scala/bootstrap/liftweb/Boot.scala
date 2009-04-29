package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import _root_.java.sql.{Connection, DriverManager}
import _root_.javax.servlet.http.{HttpServletRequest}

import S.?

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
    def boot {

        LogBoot.defaultProps =
        """<?xml version="1.0" encoding="UTF-8" ?>
          <!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
          <log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
          <appender name="appender" class="org.apache.log4j.ConsoleAppender">
          <layout class="org.apache.log4j.SimpleLayout"/>
          </appender>
          <root>
          <priority value ="DEBUG"/>
          <appender-ref ref="appender"/>
          </root>
          </log4j:configuration>
          """

        if (!DB.jndiJdbcConnAvailable_?)
        DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

        // where to search snippet
        LiftRules.addToPackages("hr.ivan.testJPA")
        LiftRules.addToPackages("hr.ivan.test")

        val entries =
        Menu(Loc("Home", List("index"), "Home")) ::
        Menu(Loc("Chat", List("chat"), "Chat")) ::
        Menu(Loc("Authors", List("authors", "list"), ?("Author List"))) ::
        Menu(Loc("Add Author", List("authors","add"), ?("Add Author"), Hidden)) ::
        Menu(Loc("Books", List("books","list"), ?("Book List"))) ::
        Menu(Loc("Add Book", List("books" , "add"), ?("Add Book"), Hidden)) ::
        Menu(Loc("BookSearch", List("books" , "search" ), ?("Book Search"))) ::
        Menu(Loc("Users JPA", List("users" , "users" ), ?("Users JPA"))) ::
        Menu(Loc("Uredi JPA", List("uredi" , "uredi" ), ?("Uredi JPA"))) ::
        Menu(Loc("Role JPA - list", List("pages", "role" , "list" ), ?("Role JPA - list"))) ::
        Menu(Loc("Role JPA - add", List("pages", "role" , "addEdit" ), ?("Role JPA - add"))) ::
        Nil

        LiftRules.setSiteMap(SiteMap(entries:_*))

        /*
         * Show the spinny image when an Ajax call starts
         */
        LiftRules.ajaxStart =
        Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

        /*
         * Make the spinny image go away when it ends
         */
        LiftRules.ajaxEnd =
        Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

        LiftRules.early.append(makeUtf8)

        LiftRules.resourceNames = "messages" :: Nil

        S.addAround(DB.buildLoanWrapper)
    }

    /**
     * Force the request to be UTF-8
     */
    private def makeUtf8(req: HttpServletRequest) {
        req.setCharacterEncoding("UTF-8")
    }

}

/**
 * Database connection calculation
 */
object DBVendor extends ConnectionManager {
    private var pool: List[Connection] = Nil
    private var poolSize = 0
    private val maxPoolSize = 4

    private def createOne: Box[Connection] = try {
        val driverName: String = Props.get("db.driver") openOr
        "org.apache.derby.jdbc.EmbeddedDriver"

        val dbUrl: String = Props.get("db.url") openOr
        "jdbc:derby:lift_example;create=true"

        Class.forName(driverName)

        val dm = (Props.get("db.user"), Props.get("db.password")) match {
            case (Full(user), Full(pwd)) =>
                DriverManager.getConnection(dbUrl, user, pwd)

            case _ => DriverManager.getConnection(dbUrl)
        }

        Full(dm)
    } catch {
        case e: Exception => e.printStackTrace; Empty
    }

    def newConnection(name: ConnectionIdentifier): Box[Connection] =
    synchronized {
        pool match {
            case Nil if poolSize < maxPoolSize =>
                val ret = createOne
                poolSize = poolSize + 1
                ret.foreach(c => pool = c :: pool)
                ret

            case Nil => wait(1000L); newConnection(name)
            case x :: xs => try {
                    x.setAutoCommit(false)
                    Full(x)
                } catch {
                    case e => try {
                            pool = xs
                            poolSize = poolSize - 1
                            x.close
                            newConnection(name)
                        } catch {
                            case e => newConnection(name)
                        }
                }
        }
    }

    def releaseConnection(conn: Connection): Unit = synchronized {
        pool = conn :: pool
        notify
    }
}


