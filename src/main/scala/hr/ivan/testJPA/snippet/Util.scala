package hr.ivan.testJPA.snippet

import scala.xml.{NodeSeq, Text, Elem}

import net.liftweb.util.{Log, BindHelpers, Helpers}
import net.liftweb.http.SHtml
import _root_.net.liftweb._
import http._
import S._
import Helpers._

class Util {

    val style = """a.dp-choose-date {
                float: left;
                width: 16px;
                height: 16px;
                padding: 0;
                margin: 5px 3px 0;
                display: block;
                text-indent: -2000px;
                overflow: hidden;
                background: url(/scripts/calendar.png) no-repeat;
            }
            a.dp-choose-date.dp-disabled {
                background-position: 0 -20px;
                cursor: default;
            }
            input.dp-applied {
                float: left;
            }
    """

    def jQueryDatePickerIncludes =
    <head>
        <title><lift:loc>Users</lift:loc></title>
        <!-- jQuery datePicker -->
        <script type="text/javascript" src="/scripts/date.js"></script>
        <script type="text/javascript" src="/scripts/jquery.datePicker.js"></script>
        <link rel="stylesheet" type="text/css" href="/css/datePicker.css" />
        <style>
            {style}
        </style>
    </head>

}
