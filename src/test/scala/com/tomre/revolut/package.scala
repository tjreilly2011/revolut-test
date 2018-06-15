package com.tomre

import com.tomre.revolut.database.RevolutDatabaseContext
import com.tomre.revolut.utils.{TimeStampDecoder, TimeStampEncoder}

package object revolut {

	object testContext extends RevolutDatabaseContext("testH2DB") with TimeStampEncoder with TimeStampDecoder

}
