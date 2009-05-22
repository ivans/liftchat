package hr.ivan.testJPA.dao

import hr.ivan.testJPA.model.Model
import hr.ivan.testJPA.model._

import hr.ivan.util.GenericDAO

object UserDAO extends GenericDAO[User] {

    implicit val model = Model

    def allUseri = getListFromNamedQuery("findAllUsers")

    def allUseriCount = getSingleResultFromNamedQuery[Long]("findAllUsersCount")

}
