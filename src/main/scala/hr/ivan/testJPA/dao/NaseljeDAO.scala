package hr.ivan.testJPA.dao

import hr.ivan.testJPA.model.Model
import hr.ivan.testJPA.model._

import hr.ivan.util.GenericDAO

object NaseljeDAO extends GenericDAO[Naselje] {

    implicit val model = Model

    def allNaselja = getListFromNamedQuery("findAllNaselja")

    def findNaseljaByNaziv(first : Int, count : Int, naziv : String) = {
        getListFromNamedQueryByParams("findNaseljaByNaziv", first, count, Pair("naziv", naziv) :: Nil)
    }

    def naseljaCountByNaziv(naziv : String) = {
        getSingleResultFromNamedQueryByParams[Long]("findNaseljaCountByNaziv", Pair("naziv", naziv) :: Nil)
    }

    def allNaseljaCount = getSingleResultFromNamedQuery[Long]("findAllNaseljaCount")

    def allNaseljaPaged(first : Int, count : Int) = getListFromNamedQuery("findAllNaselja", first, count)

}
