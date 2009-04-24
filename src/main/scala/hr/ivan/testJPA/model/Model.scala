package hr.ivan.testJPA.model

import org.scala_libs.jpa._

object Model extends LocalEMF("jpaweb", /*userTx=*/ false) with RequestVarEM

