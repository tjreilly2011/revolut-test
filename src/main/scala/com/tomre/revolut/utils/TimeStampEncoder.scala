package com.tomre.revolut.utils

import java.time.Instant
import java.util.Date

import io.getquill.MappedEncoding


trait TimeStampEncoder {
  implicit val instantEncoder = MappedEncoding[Instant, Date](i => Date.from(i))
}
