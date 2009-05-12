package hr.ivan.testJPA.dao

import hr.ivan.testJPA.model.Model
import hr.ivan.testJPA.model._

import hr.ivan.util.GenericDAO

object RolaDAO extends GenericDAO[Rola] {

    implicit val model = Model

    def allRole = getListFromNamedQuery("findAllRole")

    def allRoleAktivne = getListFromNamedQuery("findAllRoleAktivne")

}
