package hr.ivan.test.snippet

import _root_.net.liftweb._
import http._
import S._
import SHtml._
import util._
import Helpers._
import js._
import JsCmds._

import scala.xml. _

class AjaxTest {

    object counter extends SessionVar(0)

    var cnt = 0;

    def test = {
        counter
    }

    def ajaxCounterInc1 = a(() =>
        {cnt = cnt + 1;
         counter(counter.is+1);
         SetHtml("cnt1_id", Text( cnt.toString)) &
         SetHtml("cnt2_id", Text( counter.is.toString ))
        },
        <span>Click me to increase the count</span>)

    def ajaxCounterInc2 = a(() =>
        {counter(counter.is+1);
         SetHtml("cnt2_id", Text( counter.is.toString ))
        },
        <span>Click me to increase the count</span>)

    def ajaxCounterShow1 = <span>Trenutna vrijednost countera1 je <span id="cnt1_id">{cnt}</span></span>
    def ajaxCounterShow2 = <span>Trenutna vrijednost countera2 je <span id="cnt2_id">{counter.is}</span></span>

}
