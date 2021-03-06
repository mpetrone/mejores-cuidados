package models

import play.api.db._
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import scala.language.postfixOps
import play.api.Logger

case class Product(id: Long,
                   location: String,
                   category: String,
                   description: String,
                   brand: String,
                   unitQuantity: String,
                   ean: String,
                   price: String)

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Int, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Product {

  // -- Json Formatters

  implicit val productFormatter = Json.format[Product]

  implicit def pageFormat[T: Format]: Format[Page[T]] =
    ( (__ \ "items").format[Seq[T]] ~
      (__ \ "pageIndex").format[Int] ~
      (__ \ "pageSize").format[Int] ~
      (__ \ "totalCount").format[Long])(Page.apply, unlift(Page.unapply))

  // -- Parsers

  val productParser = {
        get[Long]("products.id") ~
        get[String]("products.location") ~
        get[String]("products.category") ~
        get[String]("products.description") ~
        get[String]("products.brand") ~
        get[String]("products.unit_quantity") ~
        get[String]("products.ean") ~
        get[String]("products.price") map {
        case id ~ location ~ category ~ description ~ brand ~ unitQuatity ~ ean ~ price=>
          Product(id, location, category, description, brand, unitQuatity, ean, price)
      }
  }

  // -- Queries

  def findById(id: Long): Option[Product] = {
    DB.withConnection { implicit connection =>
      SQL("select * from products where id = {id}").on('id -> id).as(productParser.singleOpt)
    }
  }

  def list(page: Int = 0,
           pageSize: Int = 10,
           orderBy: Int = 1,
           filter: String): Page[Product] = {

    Logger.debug(s"list with filter $filter")

    val offset = pageSize * page

    DB.withConnection { implicit connection =>

      val employees = SQL(
        """
          select *
          from products
          where textsearchable @@ to_tsquery('spanish', {filter})
          order by {orderBy} nulls last
          limit {pageSize} offset {offset}
        """).on(
          'pageSize -> pageSize,
          'offset -> offset,
          'filter -> (filter + " | " + filter + ":*"),
          'orderBy -> orderBy).as(productParser *)

      val totalRows = SQL(
        """
          select count(*)
          from products
          where textsearchable @@ to_tsquery('spanish', {filter})
        """).on(
          'filter -> (filter + " | " + filter + ":*")).as(scalar[Long].single)

      Page(employees, page, offset, totalRows)
    }
  }

  def insert(location: String,
             category: String,
             description: String,
             brand: String,
             unitQuantity: String,
             ean: String,
             price: String): Option[Long] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into products (location, category, description, brand, unit_quantity, ean, price, textsearchable)
           values ( {location}, {category}, {description}, {brand}, {unitQuantity}, {ean}, {price},
           to_tsvector('spanish', coalesce({category},'') || ' ' || coalesce({description},'')))
        """).on(
          'location -> location,
          'category -> category,
          'description -> description,
          'brand -> brand,
          'unitQuantity -> unitQuantity,
          'ean -> ean,
          'price -> price).executeInsert()
    }
  }

  def delete(id: Long): Int = {
    DB.withConnection { implicit connection =>
      SQL("delete from products where id = {id}").on('id -> id).executeUpdate()
    }
  }

}
