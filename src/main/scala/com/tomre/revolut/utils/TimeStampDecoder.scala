package com.tomre.revolut.utils

import java.time.Instant
import java.util.Date

import io.getquill.MappedEncoding

trait TimeStampDecoder {
	implicit val instantDecoder = MappedEncoding[Date, Instant](d => d.toInstant)
}
