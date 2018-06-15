package com.tomre.revolut.models

import io.circe._

case class Wallet(id: Long, custId: Long, name: String, currency: String, balance: BigDecimal)

object Wallet {
  implicit final val bigDecimalAsPlainStringEncoder: Encoder[BigDecimal] = new Encoder[BigDecimal] {
    final def apply(value: BigDecimal): Json = Json.fromJsonNumber(JsonNumber.fromDecimalStringUnsafe(value.bigDecimal.toPlainString))
  }

  implicit val encoder: Encoder[Wallet] = Encoder.forProduct5("id", "custId", "name", "currency", "balance")(w => (w.id, w.custId, w.name, w.currency, w.balance))

}