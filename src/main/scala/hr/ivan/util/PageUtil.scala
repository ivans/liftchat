package hr.ivan.util

import net.liftweb.util.{Log}

trait PageUtil {

    def logAndError(e : String) = { error(e); Log.error(e) }

    def getAllCauses(e : Throwable) : String = if (e == null) {
        " END"
    } else {
        e.getMessage + " :: " + getAllCauses(e.getCause)
    }

}
