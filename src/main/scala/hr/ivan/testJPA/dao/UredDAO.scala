package hr.ivan.testJPA.dao

import hr.ivan.testJPA.model.Model

import hr.ivan.testJPA.model._

object UredDAO {

    def allUredi = Model.createNamedQuery[Ured]("findAllUredi") getResultList()

}
