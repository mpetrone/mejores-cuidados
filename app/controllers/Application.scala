package controllers

import migrator.CsvMigrator
import models.Product
import models.Product._
import play.api.mvc._
import play.api.libs.json.Json

class Application extends Controller {

  def dashboard = Action {
    Ok(views.html.dashboard("Your new application is ready."))
  }

  def list = Action { implicit request => Ok(Json.toJson(Product.list())) }

  def migrate(filename: String) = Action { implicit request =>
    val count = CsvMigrator.migrate(filename)
    Ok(s"$count rows where migrated")
  }

}
