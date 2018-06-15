package com.tomre.revolut

import com.tomre.revolut.models.{Currency, Customer, Wallet, WalletTrans}
import com.tomre.revolut.services.RevolutService
import com.twitter.app.Flag
import com.twitter.server.TwitterServer
import com.tomre.revolut.database.RevolutDatabaseContext
import com.twitter.finagle.Http
import com.twitter.finagle.tracing.DefaultTracer
import com.twitter.util.{Await, Future}
import io.finch.{Application, Endpoint, InternalServerError, Ok}
import io.finch._
import io.finch.syntax.{get,patch}
import io.circe.generic.auto._
import io.finch.circe._


object RevolutTestAppServer extends TwitterServer {

	val port: Flag[Int] = flag("port", 8081, "TCP port for HTTP server")

	implicit lazy val ctx = new RevolutDatabaseContext("testH2DB") //with QueryProbing

	val custServ = new RevolutService(ctx)

	logger.info("###### Inserting test data into database #####")
	// populate db
	custServ.loadDataIntoDb

  /**
    * A matching [[Endpoint]] that reads a long value from the current path segment.
    */
  val long: Endpoint[Long] = path[Long]

  /**
    * A matching [[Endpoint]] that reads a string value from the current path segment.
    */

  val string: Endpoint[String] = path[String]

  // uri : /customers
  def customers: Endpoint[List[Customer]] = get("customers" ) {
    custServ.getCustomers.map(Ok)
  }
  // uri : /wallets
  def wallets: Endpoint[List[Wallet]] = get("wallets" ) {
    custServ.getWallets.map(Ok)
  }
  // uri : /transactions
  def transactions: Endpoint[List[WalletTrans]] = get("transactions" ) {
    custServ.getWalletTxs.map(Ok)
  }

  // uri : /customer/{id}
  def customer: Endpoint[Customer] = get("customer" :: long ) { (id: Long) =>
    custServ.getCustomerById(id).map(Ok)
  }

  // uri : /wallet/{id}
  def wallet: Endpoint[Wallet] = get("wallet" :: long ) { (id: Long) =>
    custServ.getWalletById(id).map(Ok)
  }

  // uri : /transaction/{id}
  def transaction: Endpoint[WalletTrans] = get("transaction" :: long ) { (id: Long) =>
    custServ.getWalletTxById(id).map(Ok)
  }

  // uri : /customer/{id}/wallet/{id}
  val customerWallet:Endpoint[List[(Customer,Wallet)]] = get("customer" :: long :: "wallet" :: long ) {
    (custId:Long,walletId:Long) => custServ.getCustomerWalletById(custId,walletId).map(Ok)
  }

  // uri : /customer/{id}/wallet/{id}/transaction{id}
  val customerWalletTx:Endpoint[List[(Customer,Wallet,WalletTrans)]] = get("customer" :: long :: "wallet" :: long :: "transaction" :: long) {
    (custId:Long,walletId:Long,walletTxId: Long) => custServ.getCustomerWalletTxById(custId,walletId,walletTxId).map(Ok)
  }

  // uri : /customer/{id}/wallet/{id}/transactions
  val customerWalletTxs:Endpoint[List[(Customer,Wallet,WalletTrans)]] = get("customer" :: long :: "wallet" :: long :: "transactions") {
    (custId:Long,walletId:Long) => custServ.getCustomerWalletTxs(custId,walletId).map(Ok)
  }

  // uri : /transfer/debit/{id}/credit{id}/{amount}
  val transfer:Endpoint[Long] = patch("transfer" :: "debit" :: long :: "credit"::long :: paramOption("amount")) {
    (debitWalletId:Long,creditWalletId:Long,amount: Option[String]) => custServ.transfer("GBP",debitWalletId,creditWalletId,BigDecimal(amount.getOrElse("0.00"))).map(Ok)
  }

	val api = (
            customers :+:
            wallets :+:
            transactions :+:
            customer :+:
            wallet :+:
            transaction :+:
            customerWallet :+:
            customerWalletTx :+:
            customerWalletTxs :+:
            transfer
    ).handle {
		case e: Exception => InternalServerError(e)
	}

	def main(): Unit = {
		logger.info(s"Serving the application on port ${port()}")

		val server =
			Http.server
				.withStatsReceiver(statsReceiver)
      //.withTracer(DefaultTracer)
				.serve(s":${port()}", api.toServiceAs[Application.Json])
		closeOnExit(server)

		Await.ready(adminHttpServer)
		()
	}
}
