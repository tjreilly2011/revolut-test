package com.tomre.revolut.repository

import com.tomre.revolut.database.RevolutDatabaseContext

trait Repository {

  val ctx: RevolutDatabaseContext
}

