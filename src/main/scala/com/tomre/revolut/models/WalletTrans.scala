package com.tomre.revolut.models

import java.time.Instant

import io.circe.{Encoder, Json, JsonNumber}

case class WalletTrans(id: Long, walletIdToCredit: Long, walletIdToDebit: Long, amount: BigDecimal, time: Instant)

object WalletTrans {
  implicit final val bigDecimalAsPlainStringEncoder: Encoder[BigDecimal] = new Encoder[BigDecimal] {
    final def apply(value: BigDecimal): Json = Json.fromJsonNumber(JsonNumber.fromDecimalStringUnsafe(value.bigDecimal.toPlainString))
  }

  implicit final val encodeInstant: Encoder[Instant] = Encoder.instance(time => Json.fromString(time.toString))

  implicit val encoder: Encoder[WalletTrans] = Encoder.forProduct5("id", "walletIdToCredit", "walletIdToDebit", "amount", "time")(wtx => (wtx.id, wtx.walletIdToCredit, wtx.walletIdToDebit, wtx.amount, wtx.time))

}