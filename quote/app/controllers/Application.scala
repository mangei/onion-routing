package controllers

import models.Quote
import play.api.mvc.{Controller, Action}
import play.api.libs.json._
import views.html.index

object Application extends Controller {

  val getIndex = Action { request =>
    Ok(index.render())
  }

  val getAQuote = Action { request =>
    val quote = Quote.getRandomQuote
    Ok(Json.toJson(JsObject(Seq("quote" -> JsString(quote.text + " - " + quote.author)))))
  }
}
