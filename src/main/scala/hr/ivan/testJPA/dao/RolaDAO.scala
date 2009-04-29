package hr.ivan.testJPA.dao

import hr.ivan.testJPA.model.Model

import hr.ivan.testJPA.model._

object RolaDAO {

    def allRoleAktivne = Model.createNamedQuery[Rola]("findAllRoleAktivne").getResultList

}
