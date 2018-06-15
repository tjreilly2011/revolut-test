package com.tomre.revolut

import org.slf4j.LoggerFactory
import com.tomre.revolut.models.{Currency, Customer}
import com.tomre.revolut.services.RevolutService
import com.tomre.revolut.utils.TransferException
import com.twitter.util.Await
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class RevolutServiceTest extends FunSuite with BeforeAndAfterAll {

  val ctx = testContext

  val logger = LoggerFactory.getLogger("com.tomre.revolut.RevolutServiceTest")

  val custServ = new RevolutService(ctx)

  // Load test data
  custServ.loadTestData()

  test("Get All Customers") {

    val customerList = Await.result(custServ.getCustomers)
    assert(customerList.length == 3)

  }

  test("Get All Wallets") {

    val walletList = Await.result(custServ.getWallets)
    assert(walletList.length == 4)

  }

  test("Get All Transactions") {

    val walletTxList = Await.result(custServ.getWalletTxs)
    assert(walletTxList.length == 4)

  }

  test("Retrieve Customer by Id") {

    val custID = 1L
    val returnedCust = Await.result(custServ.getCustomerById(custID))

    assert(returnedCust.loginName == "treilly")
    assert(returnedCust.firstName == "Tom")
    assert(returnedCust.lastName == "Reilly")
    assert(returnedCust.noOfAccounts == 2)

  }

  test("Retrieve wallet by Id") {
    val walletID = 1L
    val returnedWallet = Await.result(custServ.getWalletById(1L))

    assert(returnedWallet.name === "treilly_gbp_wallet")
    assert(returnedWallet.balance == 100)

  }

  test("Retrieve wallet transaction by Id") {
    val walletID = 1L
    val returnedWalletTx = Await.result(custServ.getWalletTxById(1L))

    assert(returnedWalletTx.amount == 100)
    assert(returnedWalletTx.walletIdToCredit == 1)
    assert(returnedWalletTx.walletIdToDebit == 4)

  }

  test("Retrieve Customer's Wallet") {
    val custId = 1L
    val walletId = 1L
    val custWallet = Await.result(custServ.getCustomerWalletById(custId, walletId))
    logger.info("Customer 1L's wallet is: " + custWallet)
    logger.info("custWallet len is: " + custWallet.length)
    assert(custWallet.length == 1)
  }

  test("Retrieve Customer's Wallet transaction") {
    val custId = 1L
    val walletId = 1L
    val walletTxId = 1L
    val custWalletTxs = Await.result(custServ.getCustomerWalletTxById(custId, walletId, walletTxId))
    logger.info("Customer 1L's wallet is: " + custWalletTxs)
    logger.info("custWallet len is: " + custWalletTxs.length)
    assert(custWalletTxs.length == 1)
  }


  test("Get All Txs for a Single Customer Wallet") {
    val custId = 1L
    val walletId = 1L
    val listCustWalTxs = Await.result(custServ.getCustomerWalletTxs(custId, walletId))
    // .map(cwt => (cwt._3, cwt._4))

    logger.info("listCustWalTxs len is: " + listCustWalTxs.length)

    assert(listCustWalTxs.length == 4)
  }


  test("Single Customer Creation") {
    val singleCustToInsert = Customer(4L, "treilly4", "Tom4", "Reilly4", 2)
    val newCustomer = Await.result(custServ.createCustomer(singleCustToInsert))

    assert(newCustomer.id == singleCustToInsert.id)
    assert(newCustomer.firstName == singleCustToInsert.firstName)
    assert(newCustomer.lastName == singleCustToInsert.lastName)

  }

  test("Get All Customers Wallets") {

    val walletList = Await.result(custServ.getCustomersWithWallets).map(cw => cw._2)
    assert(walletList.length == 4)

  }


  test("Get All Customers Wallet Transactions") {

    val listAllCreditAndDebitTxs = Await.result(custServ.getCustomersWalletsAndTxs)
    // .map(cwt => cwt._3)

    println(listAllCreditAndDebitTxs.length)

    listAllCreditAndDebitTxs.foreach(println(_))

    assert(listAllCreditAndDebitTxs.length == 8)
    assert(listAllCreditAndDebitTxs.length == 8)

  }


  test("Update a Single Customer") {

    val singleCustToUpdate = Customer(3, "bnomates", "William", "Nomates", 4)

    val singleCustUpdated = Await.result(custServ.updateCustomer(singleCustToUpdate))

    assert(singleCustUpdated.firstName === "William")

  }

  test("Update Single Wallet Balance") {

    val custId = 3L
    val walletId = 4L

    val walletBeforeUpdate = Await.result(custServ.getCustomerWalletById(custId, walletId))
      .map(_._2).head


    assert(walletBeforeUpdate.name === "bnomates_gbp_wallet")
    assert(walletBeforeUpdate.balance === 400)

    val walletAfterUpdate = Await.result(custServ.updateCustomerWalletBalance(walletBeforeUpdate, BigDecimal("500.00")))

    assert(walletAfterUpdate.balance === 500)
  }

  test("Batch Update a Customer") {

    val listCustsToUpdate = Customer(1L, "treilly", "Tomas", "Reilly", 4) :: Customer(2L, "jsoap", "Joseph", "Soap", 6) :: Nil

    val updatedCustomerList = Await.result(custServ.updateCustomerBatch(listCustsToUpdate))
    // should be 2 updated
    assert(updatedCustomerList.length == 2)

    val cust1Updated = Await.result(custServ.getCustomerById(1L))

    assert(cust1Updated.firstName === "Tomas")
    assert(cust1Updated.noOfAccounts == 4)

    val cust2Updated = Await.result(custServ.getCustomerById(2L))

    assert(cust2Updated.firstName === "Joseph")
    assert(cust2Updated.noOfAccounts == 6)

  }

  test("Delete a Customer") {

    val singleCustToDelete = Customer(4L, "treilly4", "Tom4", "Reilly4", 2)
    custServ.deleteCustomer(singleCustToDelete)

    val customerList = Await.result(custServ.getCustomers)

    assert(customerList.length == 3)
    assert(customerList.contains(singleCustToDelete) == false)

  }

  test("Transfer between Wallets") {

    val currency = Currency.GBP.toString
    val debitWalletId = 1L
    val creditWalletId = 3L
    val amount = BigDecimal("10")

    val credDebWallAndTx = Await.result(custServ.transfer(currency, debitWalletId, creditWalletId, amount))
    logger.info("transaction id is: " + credDebWallAndTx._3.id)

    val newWalletTx = Await.result(custServ.getCustomerWalletTransById(credDebWallAndTx._3.id))

    assert(newWalletTx.id > 0)
    assert(newWalletTx.amount == 10L)

    val debAcc = Await.result(custServ.getWalletById(debitWalletId))
    val credAcc = Await.result(custServ.getWalletById(creditWalletId))

    assert(debAcc.balance == 90)
    assert(credAcc.balance == 310)
  }

  test("Transfer not allowed if insufficient funds in debit account") {
    val currency = Currency.GBP.toString
    val debitWalletId = 1L
    val creditWalletId = 3L
    val amount = BigDecimal("200")

    assertThrows[TransferException] {
      Await.result(custServ.transfer(currency, debitWalletId, creditWalletId, amount))
    }

  }

  test("Transfer not allowed if debit and credit account are the same") {
    val currency = Currency.GBP.toString
    val debitWalletId = 1L
    val creditWalletId = 1L
    val amount = BigDecimal("50")

    assertThrows[TransferException] {
      Await.result(custServ.transfer(currency, debitWalletId, creditWalletId, amount))
    }

  }

  override def afterAll() = {
    println("Closing DB connection")
    ctx.close
  }

}
