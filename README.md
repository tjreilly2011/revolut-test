# RevolutTest

Used following libs etc for this project:
* Scala 2.11.12
* Quill 2.5.4
* Sbt 0.13.17
* h2database 1.4.197
* Twitter Finch 0.20.0
* Twitter Server 18.5.0
* Scalatest 3.0.5


Build instructions:
* Install Scala version 2.11.12
* Install Sbt version 0.13.17
* Clone|Download zip of the project to local machine from github https://github.com/tjreilly2011/revolut-test
* cd to the project main directory revolut-test
* sbt clean test run
```log
[info] RevolutServiceTest:
.
[info] - Get All Customers
.
[info] - Get All Wallets
.
[info] - Get All Transactions
.
[info] - Retrieve Customer by Id
.
[info] - Retrieve wallet by Id
.
[info] - Retrieve wallet transaction by Id
.
[info] - Retrieve Customer's Wallet
.
[info] - Retrieve Customer's Wallet transaction
.
[info] - Get All Txs for a Single Customer Wallet
.
[info] - Single Customer Creation
.
[info] - Get All Customers Wallets
.
[info] - Get All Customers Wallet Transactions
.
[info] - Update a Single Customer
.
[info] - Update Single Wallet Balance
.
[info] - Batch Update a Customer
.
[info] - Delete a Customer
.
[info] - Transfer between Wallets
.
[info] - Transfer not allowed if insufficient funds in debit account
.
[info] - Transfer not allowed if debit and credit account are the same
Closing DB connection
```

# Model

1 Customer can have many wallets
1 Wallet can have many Wallet transactions

* customer table stores customer info
* wallet table stores customer wallet info
* wallet_trans table stores information about transactions

REST API URLS:

Base app url is http://localhost:8081

all customers:

	curl -v localhost:8081/customers

```log
[
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
},
{
"id": 2,
"loginName": "jsoap",
"firstName": "Joe",
"lastName": "Soap",
"noOfAccounts": 3
},
{
"id": 3,
"loginName": "bnomates",
"firstName": "Billy",
"lastName": "Nomates",
"noOfAccounts": 4
}
]
```
all wallets

	curl -v localhost:8081/wallets

```log
[
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
},
{
"id": 2,
"custId": 1,
"name": "treilly_eur_wallet",
"currency": "EUR",
"balance": 200
},
{
"id": 3,
"custId": 2,
"name": "jsoap_gbp_wallet",
"currency": "GBP",
"balance": 300
},
{
"id": 4,
"custId": 3,
"name": "bnomates_gbp_wallet",
"currency": "GBP",
"balance": 400
}
]
```
all transactions

	curl -v localhost:8081/transactions
```log
[
{
"id": 1,
"walletIdToCredit": 1,
"walletIdToDebit": 4,
"amount": 100,
"time": "2018-06-15T15:41:40.271Z"
},
{
"id": 2,
"walletIdToCredit": 3,
"walletIdToDebit": 1,
"amount": 50,
"time": "2018-06-15T15:41:40.271Z"
},
{
"id": 3,
"walletIdToCredit": 3,
"walletIdToDebit": 1,
"amount": 50,
"time": "2018-06-15T15:41:40.271Z"
},
{
"id": 4,
"walletIdToCredit": 1,
"walletIdToDebit": 3,
"amount": 50,
"time": "2018-06-15T15:41:40.271Z"
}
]
```
single customer

	curl -v localhost:8081/customer/1
```log
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
}
```
single wallet

	curl -v localhost:8081/wallet/1
```log
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
}
```
single wallet tx

	curl -v localhost:8081/transaction/1
```log
{
"id": 1,
"walletIdToCredit": 1,
"walletIdToDebit": 4,
"amount": 100,
"time": "2018-06-15T15:41:40.271Z"
}
```

single customer wallet

	curl -v localhost:8081/customer/1/wallet/1
```log
[
[
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
},
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
}
]
]
```
single customer wallet transaction

	curl -v localhost:8081/customer/1/wallet/1/transaction/1
```log
[
[
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
},
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
},
{
"id": 1,
"walletIdToCredit": 1,
"walletIdToDebit": 4,
"amount": 100,
"time": "2018-06-15T15:41:40.271Z"
}
]
]
```

all customer wallet transactions
	curl -v localhost:8081/customer/1/wallet/1/transactions
```log
[
[
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
},
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
},
{
"id": 1,
"walletIdToCredit": 1,
"walletIdToDebit": 4,
"amount": 100,
"time": "2018-06-15T15:41:40.271Z"
}
],
[
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
},
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
},
{
"id": 2,
"walletIdToCredit": 3,
"walletIdToDebit": 1,
"amount": 50,
"time": "2018-06-15T15:41:40.271Z"
}
],
[
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
},
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
},
{
"id": 3,
"walletIdToCredit": 3,
"walletIdToDebit": 1,
"amount": 50,
"time": "2018-06-15T15:41:40.271Z"
}
],
[
{
"id": 1,
"loginName": "treilly",
"firstName": "Tom",
"lastName": "Reilly",
"noOfAccounts": 2
},
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
},
{
"id": 4,
"walletIdToCredit": 1,
"walletIdToDebit": 3,
"amount": 50,
"time": "2018-06-15T15:41:40.271Z"
}
]
]
```
transfer between wallets

Example to debit £20 from wallet id=1 and credit wallet id = 3

curl -v localhost:8081/wallet/1
```log
{
"id": 1,
"custId": 1,
"name": "treilly_gbp_wallet",
"currency": "GBP",
"balance": 100
}
```
curl -v localhost:8081/wallet/3
{
"id": 3,
"custId": 2,
"name": "jsoap_gbp_wallet",
"currency": "GBP",
"balance": 300
}

	Do transfer:

	curl --request PATCH localhost:8081/transfer/debit/1/credit/3?amount=20

```log
[
   {
      "id":1,
      "custId":1,
      "name":"treilly_gbp_wallet",
      "currency":"GBP",
      "balance":80.00
   },
   {
      "id":3,
      "custId":2,
      "name":"jsoap_gbp_wallet",
      "currency":"GBP",
      "balance":320.00
   },
   {
      "id":5,
      "walletIdToCredit":3,
      "walletIdToDebit":1,
      "amount":20.00,
      "time":"2018-06-15T16:16:51.772Z"
   }
```

Twitter Server admin url:

http://localhost:9990/admin