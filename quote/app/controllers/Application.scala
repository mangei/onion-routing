package controllers

import models.Quote
import play.api.mvc.{Controller, Action}
import play.api.libs.json._

object Application extends Controller {

  val getAQuote = Action { request =>
    val quote = Quote.getRandomQuote
    Ok(Json.toJson(JsObject(Seq("quote" -> JsString(quote.text + " - " + quote.author)))))
  }
}
