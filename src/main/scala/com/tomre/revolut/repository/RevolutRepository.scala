package com.tomre.revolut.repository

import com.tomre.revolut.models.{Currency, Customer, Wallet, WalletTrans}
import java.time.Instant

import com.tomre.revolut.utils.TransferException
import org.slf4j.LoggerFactory

trait RevolutRepository extends Repository  {

import ctx._

  val logger = LoggerFactory.getLogger("com.tomre.revolut.repository.RevolutRepository")

	val customers = quote(query[Customer])
	val wallets = quote(query[Wallet])
	val walletTrans = quote(query[WalletTrans])

	def loadTestData() = {

    val listCustomers = List(
      Customer(1L, "treilly", "Tom", "Reilly", 2),
      Customer(2L, "jsoap", "Joe", "Soap", 3),
      Customer(3L, "bnomates", "Billy", "Nomates", 4)
    )

    val insertCustomerList = quote {
      liftQuery(listCustomers).foreach(e => query[Customer].insert(e))
    }

    val listWallets = List(
      Wallet(1L, 1L, "treilly_gbp_wallet", Currency.GBP.toString, BigDecimal("100.00")),
      Wallet(2L, 1L, "treilly_eur_wallet", Currency.EUR.toString, BigDecimal("200.00")),
      Wallet(3L, 2L, "jsoap_gbp_wallet", Currency.GBP.toString, BigDecimal("300.00")),
      Wallet(4L, 3L, "bnomates_gbp_wallet", Currency.GBP.toString, BigDecimal("400.00"))
    )

    val insertWalletList = quote {
      liftQuery(listWallets).foreach(e => query[Wallet].insert(e))
    }

    val listWalletTrans = List(
      WalletTrans(1L, 1L, 4L, BigDecimal("100"), Instant.now()),
      WalletTrans(2L, 3L, 1L, BigDecimal("50"), Instant.now()),
      WalletTrans(3L, 3L, 1L, BigDecimal("50"), Instant.now()),
      WalletTrans(4L, 1L, 3L, BigDecimal("50"), Instant.now())
    )

    val insertWalletTrans = quote {
      liftQuery(listWalletTrans).foreach(e => query[WalletTrans].insert(e))
    }

    ctx.transaction {
      ctx.run(insertCustomerList)
      ctx.run(insertWalletList)
      ctx.run(insertWalletTrans)
    }
    ()

    logger.info("Finished adding test customer, wallet and transaction data to database.")
    getAllWalletTxs.foreach(println(_))
	}

  def getAllCusts = {
    val q = quote {
      for {
        c <- customers
      } yield {
        (c)
      }
    }
    ctx.run(q)
  }

  def getAllWallets = {
    val q = quote {
      for {
        w <- wallets
      } yield {
        (w)
      }
    }
    ctx.run(q)
  }

  def getAllWalletTxs = {
    val q = quote {
      for {
        wtx <- walletTrans
      } yield {
        (wtx)
      }
    }
    ctx.run(q)
  }

	def custById = quote {
		(id: Long) => customers.filter(_.id == id)
	}

	def walletById = quote {
		(id: Long) => wallets.filter(_.id == id)
	}

	def walletTransById = quote {
		(id: Long) => walletTrans.filter(_.id == id)
	}

  def getCustById(id:Long) = {

    ctx.run(custById(lift(id))).head
  }

  def getWallById(id:Long) = {
    ctx.run(walletById(lift(id))).head
  }

  def getWallTxById(id:Long) = {
    ctx.run(walletTransById(lift(id))).head
  }

	def createCust(customer: Customer) = {

		val customerInsert = quote {
			(c: Customer) => customers.insert(c).returning(_.id)
		}

		val inserted = ctx.run(customerInsert(lift(customer)))
		ctx.run(custById(lift(inserted))).head
	}

  def getCustWalletById(custId:Long,walletId:Long) = {

    val q = quote {
      for {
        c <- customers if(c.id == lift(custId))
        w <- wallets.filter(_.id == lift(walletId)) if(c.id == w.custId)
      } yield {
        (c, w)
      }
    }

    ctx.run(q)
  }

  def getCustWalletTxById(custId:Long,walletId:Long,walletTxId:Long) = {

    val q = quote {
      for {
        c <- customers if(c.id == lift(custId))
        w <- wallets.filter(_.id == lift(walletId)) if(c.id == w.custId)
        wtx <- walletTrans.filter(_.id == lift(walletTxId)) if(w.id == wtx.walletIdToCredit || w.id == wtx.walletIdToDebit)
      } yield {
        (c, w, wtx)
      }
    }

    ctx.run(q)
  }

  def getCustWalletTxs(custId:Long,walletId:Long) = {

    val q = quote {
      for {
        c <- customers if(c.id == lift(custId))
        w <- wallets.filter(_.id == lift(walletId)) if(c.id == w.custId)
        wtx <- walletTrans if(w.id == wtx.walletIdToCredit || w.id == wtx.walletIdToDebit)
      } yield {
        (c, w, wtx)
      }
    }

    ctx.run(q)
  }

	def createWalletTrans(wtx: WalletTrans) = {

		val walletTransInsert = quote {
			(wt: WalletTrans) => walletTrans.insert(wt).returning(_.id)
		}

		val inserted = ctx.run(walletTransInsert(lift(wtx)))
		ctx.run(walletTransById(lift(inserted))).head
	}

	def getAllCustsAndWallets = {
		val q = quote {
			for {
				(c,w) <- customers.join(wallets).on((c, w) => c.id ==w.custId)
			} yield {
				(c,w)
			}
		}
		ctx.run(q)
	}

	def getCustWallets(custId:Long) = {
		val q = quote {
			for {
				(c,w) <- customers.filter(_.id == lift(custId)).join(wallets).on((c, w) => c.id ==w.custId)
			} yield {
				(c,w)
			}
		}
		ctx.run(q)
	}

  	def getAllCustsWalletsandTransactions = {
		val q = quote {
			for {

        c <- customers
        w <- wallets if(c.id == w.custId)
        wtxs <- walletTrans if(w.id == wtxs.walletIdToCredit || w.id == wtxs.walletIdToDebit)
			} yield {
				(c,w,wtxs)
			}
		}
		ctx.run(q)
	}

	def updateCust(customer:Customer) = {

		val custToUpdate = quote {
			(c: Customer) => customers.filter(_.id == lift(customer.id)).update(c)
		}

		ctx.run(custToUpdate(lift(customer)))
		ctx.run(custById(lift(customer.id))).head

	}

	def updateCustBatch(listCust:List[Customer]) = {

		val customersToUpdate = quote {
			liftQuery(listCust).foreach { customer =>
				customers.filter(_.id == customer.id)
  				.update(customer)
			}
		}
		ctx.run(customersToUpdate)
	}

	def updateWalletBalance(wallet:Wallet,balance:BigDecimal) = {

		val walletToUpdate = quote {
			(w: Wallet) => wallets.filter(_.id == lift(wallet.id)).update(_.balance -> lift(balance))
		}

		ctx.run(walletToUpdate(lift(wallet)))
		ctx.run(walletById(lift(wallet.id))).head

	}

	def deleteCust(customer:Customer) = {
		ctx.run(query[Customer].filter(_.id == lift(customer.id)).delete)
	}

	def transferMoney(currency:String,debitWalletId:Long, creditWalletId:Long, amount:BigDecimal) = {

		var txId = 0L

    if(debitWalletId == creditWalletId)
      throw new TransferException("Transfer to/from same account not allowed!")

		// The Quill async module provides transaction support based on a
    // custom implicit execution context

    // Run the transfer steps in a transaction

		ctx.transaction {
			// get debit wallet id
			val debWallet = getWallById(debitWalletId)

      if(debWallet.balance <= amount)
        throw new TransferException("Insufficent Funds for transfer!")

			// get credit wallet id
			val credWallet = getWallById(creditWalletId)

			// update debit wallet with balance subtracted
			updateWalletBalance(debWallet,debWallet.balance - amount)

			// update credit wallet with balance added
			updateWalletBalance(credWallet,credWallet.balance + amount)

			// create a new tx for the the transfer and return transaction Id
			createWalletTrans(WalletTrans(1L,credWallet.id,debWallet.id,amount,Instant.now())).id

		}

	}

}