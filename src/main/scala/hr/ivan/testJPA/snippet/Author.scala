package hr.ivan.testJPA.snippet

import scala.xml.{NodeSeq,Text}

import _root_.net.liftweb._
import http._
import S._
import util._
import Helpers._

import javax.persistence.{EntityExistsException,PersistenceException}

import hr.ivan.testJPA.model._
import hr.ivan.util.{PageUtil}
import Model._

class AuthorOps extends PageUtil {
    def list (xhtml : NodeSeq) : NodeSeq = {
        val authors = Model.createNamedQuery[Author]("findAllAuthors") getResultList

        def logAndError(e : String) = { error(e); Log.error(e) }

        def getAllCauses(e : Throwable) : String = if (e == null) {
            " END"
        } else {
            e.getMessage + " :: " + getAllCauses(e.getCause)
        }
  
        authors.flatMap(author =>
            bind("author", xhtml,
                 "name" -> Text(author.firstName + " " + author.lastName),
                 "count" -> SHtml.link("/books/search.html", {() =>
                        BookOps.resultVar(Model.createNamedQuery[Book]("findBooksByAuthor", "id" ->author.id).getResultList.toList)
                    }, Text(author.books.size().toString)),
                 "edit" -> SHtml.link("add.html", () => authorVar(author), Text(?("Edit"))),
                 "delete" -> SHtml.link("", () => {
                        Log.info("deleting instance Author ", author.id, author.firstName, author.lastName)
                        try {
                            Model.removeAndFlush(Model.getReference(classOf[Author], author.id))
                        } catch {
                            case ee : EntityExistsException =>
                                logAndError("Entity exists! Maybe object has children?")
                            case pe : PersistenceException =>
                                logAndError("Persistence exception")
                            case _ =>
                                logAndError("Some strange exception happened")
                        } finally {
                            redirectTo("/authors/list.html")
                        }
                    }, Text(?("Delete!")))
            ))
    }

    // Set up a requestVar to track the author object for edits and adds
    object authorVar extends RequestVar(new Author())
    def author = authorVar.is

    def add (xhtml : NodeSeq) : NodeSeq = {
        def doAdd () = {
            if (author.lastName.length == 0) {
                error("emptyAuthor", "The author's name cannot be blank")
            } else {
                try {
                    Model.mergeAndFlush(author)
                    redirectTo("list.html")
                } catch {
                    case ee : EntityExistsException => error("Author already exists")
                    case pe : PersistenceException => error("Error adding author"); Log.error("Error adding author", pe)
                }
            }
        }

        // Hold a val here so that the "id" closure holds it when we re-enter this method
        val currentId = author.id

        bind("author", xhtml,
             "id" -> SHtml.hidden(() => author.id = currentId),
             "firstName" -> SHtml.text(author.firstName, author.firstName = _),
             "lastName" -> SHtml.text(author.lastName, author.lastName = _),
             "submit" -> SHtml.submit(?("Save"), doAdd))
    }
}
