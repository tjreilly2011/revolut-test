package com.tomre.revolut.database

import com.tomre.revolut.utils.{TimeStampDecoder, TimeStampEncoder}
import io.getquill._

class RevolutTestSqlMirrorContext(config: String) extends SqlMirrorContext(H2Dialect, SnakeCase) with TimeStampEncoder with TimeStampDecoder