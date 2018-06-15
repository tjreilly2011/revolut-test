package com.tomre.revolut.services

import com.tomre.revolut.models.{Customer, Wallet}
import com.tomre.revolut.database.RevolutDatabaseContext
import com.tomre.revolut.repository.RevolutRepository
import com.tomre.revolut.utils.TransferException
import com.twitter.util.Future

class RevolutService(val ctx: RevolutDatabaseContext) extends RevolutRepository {

  def loadDataIntoDb() = {
    loadTestData
  }

  def getCustomers = {
    Future.value(getAllCusts)
  }

  def getWallets = {
    Future.value(getAllWallets)
  }

  def getWalletTxs = {
    Future.value(getAllWalletTxs)
  }

	def getCustomerById(id:Long) = {
		Future.value(getCustById(id))
	}

  def getWalletById(id:Long) = {
    Future.value(getWallById(id))
  }

  def getWalletTxById(id:Long) = {
    Future.value(getWallTxById(id))
  }

//
	def getCustomerWalletById(custId:Long,walletId:Long) = {

		Future.value(getCustWalletById(custId,walletId))
	}

  def getCustomerWalletTxById(custId:Long,walletId:Long,walletTxId:Long) = {

    Future.value(getCustWalletTxById(custId,walletId,walletTxId))
  }

    def getCustomerWalletTxs(custId:Long,walletId:Long) = {
      Future.value(getCustWalletTxs(custId:Long,walletId:Long))
    }

//
	def getCustomerWalletTransById(id:Long) = {

		Future.value(getWallTxById(id))
	}

	def createCustomer(customer: Customer) = {
		Future.value(createCust(customer))
	}

	def getCustomersWithWallets = {
		Future.value(getAllCustsAndWallets)
	}

  def getCustomerWallets(custId:Long) = {
    Future.value(getCustWallets(custId:Long))
  }

//  def getCustomerWallet(custId:Long,walletId:Long) = {
//    Future.value(getCustWallet(custId:Long,walletId:Long))
//  }
//
//  def getCustomerTxs(custId:Long) = {
//    Future.value(getCustTxs(custId:Long))
//  }


//
	def getCustomersWalletsAndTxs = {
		Future.value(getAllCustsWalletsandTransactions)
	}

	def updateCustomer(customer: Customer) = {
		Future.value(updateCust(customer))
	}

	def updateCustomerBatch(customerList:List[Customer]) = {
		Future.value(updateCustBatch(customerList))
	}

	def updateCustomerWalletBalance(wallet:Wallet,balance:BigDecimal) = {
		Future.value(updateWalletBalance(wallet,balance))
	}
	def deleteCustomer(customer: Customer) = {
		Future.value(deleteCust(customer))
	}

	def transfer(currency:String,debitAccId:Long, creditAccId:Long, amount:BigDecimal) = {
		if(amount <= BigDecimal("0.00"))
      throw new TransferException("Transfer Amount can be less than or equal to 0")
    Future.value(transferMoney(currency,debitAccId,creditAccId,amount))
	}


}
