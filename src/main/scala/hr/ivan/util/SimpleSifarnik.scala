package hr.ivan.util

import net.liftweb.http.RequestVar

class SimpleSifarnik[T](newT : => T) {

    object entityVar extends RequestVar(newT)
    def entity = entityVar.is

}
