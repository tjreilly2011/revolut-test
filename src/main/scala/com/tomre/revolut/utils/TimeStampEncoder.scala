/*
 * Created by IntelliJ IDEA.
 * User: tomreilly
 * Date: 10/06/2018
 * Time: 17:04
 */
package com.tomre.revolut.utils

import java.time.Instant
import java.util.Date

import io.getquill.MappedEncoding


trait TimeStampEncoder {
	implicit val instantEncoder = MappedEncoding[Instant, Date](i => Date.from(i))
}
