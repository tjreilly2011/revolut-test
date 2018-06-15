package com.tomre.revolut.repository

import com.tomre.revolut.database.RevolutTestSqlMirrorContext

trait SqlMirrorRepository {
	implicit lazy val ctx:RevolutTestSqlMirrorContext = new RevolutTestSqlMirrorContext("testH2DB")
}
