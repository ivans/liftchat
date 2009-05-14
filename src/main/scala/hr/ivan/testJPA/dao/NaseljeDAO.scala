package hr.ivan.testJPA.dao

import hr.ivan.testJPA.model.Model
import hr.ivan.testJPA.model._

import hr.ivan.util.GenericDAO

object NaseljeDAO extends GenericDAO[Naselje] {

    implicit val model = Model

    def allNaselja = getListFromNamedQuery("findAllNaselja")

    def allNaseljaPaged(first : Int, count : Int) = getListFromNamedQuery("findAllNaselja", first, count)

}
