# Spring cloud microservices

This project is realise simple bank system. It's possible create accounts with multiple bills.  
Three main operations with balance of bills:
- deposit;
- payment;
- transfer.
In the project uses 9 microservices (more details below). 

## Tags
- Microservices;
- Gradle;
- Docker;  
- Spring;
- Spring Boot;
- Spring Cloud;
- PostgreSQL;
- REST;
- Eureka;
- Zuul;
- Feign;
- Ribbon;
- RabbitMQ;
- AMQP.

## Architecture of project
![Image alt](https://github.com/miihe/spring-cloud-microservices/blob/master/image/Architecture.png?raw=true)

Note:
1. Configuration service (***Config***) - in this service is stored configurations of other services. 
   Realized by the Spring Cloud Config.
2. Discovery service (***Discovery***) -  execute function of detection other services and also 
   registration their addresses of instances.
3. Gateway service (***Gateway***) - single entry point to application.
4. Account service (***Account***) - is a service for working with accounts. 
   Performs operations of receiving, creating, updating, deleting accounts.
5. Bill service (***Bill***) - is a service for working with bills.
   Performs operations of receiving, creating, updating, deleting accounts.    
6. Deposit service (***Deposit***) - service for make deposit to bill. 
7. Payment service (***Payment***) - service for make payment from some bill.
8. Transfer service (***Transfer***) - service for make transfer "bill-to-bill".
9. Notification service (***Notification***) - service which responds for notification about executed 
   operations deposit, payment or transfer. For this purpose using the RabbitMQ (message-broker software).
   
## Requests table
| HTTP METHOD | PATH | OPTION | NECESSARY FIELDS |
|---------|---------|:----------------:|:-------:|
| account-service (port 8081) |
| **GET** | /accounts/{accountId} | get the account by id | account_id in the request URI mapping |
| **POST** | /accounts/ | create account and one default bill | name, email, phone |
| **PUT** | /accounts/{accountId} | update account by id | name, email, phone |
| **DELETE** | /accounts/{accountId} | delete account by id | account_id in the request URI mapping |
| bill-service (port 8082) |
| **GET** | /bills/{billId} | get the bill by id | bill_id in the request URI mapping |
| **POST** | /bills/ | create bill | account_id, amount, is_default, is_overdraft_enable |
| **PUT** | /bills/{billId} | update bill by id | bill_id in the request URI mapping and JSON: is_default, is_overdraft_enable |
| **DELETE** | /bills/{billId} | delete bill by id | bill_id in the request URI mapping |
| deposit-service (port 9991) |
| **POST** | /deposits/ | make a deposit to bill | bill_id or account_id (bill_id at priority), amount |
| **GET** | /deposits/{billId} | get all deposits to bill by id | bill_id in the request URI mapping |
| payment-service (port 8888) |
| **POST** | /payments/ | make a payment to bill | bill_id or account_id (bill_id at priority), amount |
| **GET** | /payments/{billId} | get all payments from bill by id | bill_id in the request URI mapping |
| transfer-service (port 8889) |
| **POST** | /transfers/ | make a transfer bill-to-bill | bill_id or account_id of sender (bill_id at priority), bill_id or account_id of payee (bill_id at priority), amount |
| **GET** | /transfers/{transferId} | get transfer by id | transfer_id in the request URI mapping |
