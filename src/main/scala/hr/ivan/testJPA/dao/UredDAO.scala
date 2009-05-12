package hr.ivan.testJPA.dao

import hr.ivan.testJPA.model.Model
import hr.ivan.testJPA.model._

import hr.ivan.util.GenericDAO

object UredDAO extends GenericDAO[Ured] {

    implicit val model = Model

    def allUredi = getListFromNamedQuery("findAllUredi")

}
