package controllers

import migrator.CsvMigrator
import models.Product
import models.Product._
import play.api.mvc._
import play.api.libs.json.Json

class Application extends Controller {

  def dashboard = Action {
    //CsvMigrator.migrate("PC_amba.csv")
    Ok(views.html.dashboard("Your new application is ready."))
  }

  def search(q: Option[String]) = Action {
    val query = q.map(_.trim) match {
      case None => "%"
      case Some("") => "%"
      case Some(a) => a

    }
    Ok(Json.toJson(Product.list(filter = query.toUpperCase)))
  }

  def migrate(filename: String) = Action { implicit request =>
    val count = CsvMigrator.migrate(filename)
    Ok(s"$count rows where migrated")
  }

}
