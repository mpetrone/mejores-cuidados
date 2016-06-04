package migrator

import models.Product
import play.api.Logger

import scala.io.Source

object CsvMigrator {

  def migrate(fileName: String): Long = {
    Logger.debug(s"Starting to process the file $fileName")
    val home = System.getProperty("app.home")
    val bufferedSource = Source.fromFile(s"csv/$fileName")
    var count = 0L
    for (line <- bufferedSource.getLines) {
      line.split(",").map(_.trim.replace("\"","")).toList match {
        case List(location, category, description, brand, unitQuantity, ean, naturalPart, decimalPart) =>
          Logger.debug(s"Inserting location $location, category $category, description $description, unitQuantity $unitQuantity, ean $ean, price $naturalPart,$decimalPart")
          Product.insert(location, category, description, brand, unitQuantity, ean, naturalPart + "," + decimalPart)
          count = count + 1
        case a => Logger.warn(s"${a.size} the line $line could not be parsed")

      }
    }
    count
  }
}
