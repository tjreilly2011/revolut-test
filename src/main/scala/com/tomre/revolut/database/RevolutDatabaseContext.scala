package com.tomre.revolut.database

import com.tomre.revolut.utils.{TimeStampDecoder, TimeStampEncoder}
import io.getquill.{H2JdbcContext, SnakeCase}

class RevolutDatabaseContext(config: String) extends H2JdbcContext[SnakeCase](SnakeCase,config) with TimeStampEncoder with TimeStampDecoder
