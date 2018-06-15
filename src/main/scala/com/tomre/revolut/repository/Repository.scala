package com.tomre.revolut.repository

import com.tomre.revolut.database.RevolutDatabaseContext

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

trait Repository {

	val ctx: RevolutDatabaseContext

	def await[T](f: Future[T]): T = Await.result(f, Duration.Inf)
}

