CREATE TABLE ServiceWorker (

  serviceKey      CHAR(10) NOT NULL,
  workerFistName  VARCHAR(100),
  workerLastName  VARCHAR(100),
  
  PRIMARY KEY (serviceKey)
);

CREATE TABLE BankAccount (

  accountID           SMALLSERIAL NOT NULL,  
  customerFirstName   VARCHAR(100),
  customerLastName    VARCHAR(100),
  customerMiddleName  VARCHAR(100),
  customerAge         SMALLINT CHECK (customerAge > 0),
  customerAddress     VARCHAR(1024),
  
  PRIMARY KEY(accountID)
);

CREATE TABLE CreditCard (

  cardID    CHAR(16) NOT NULL,
  pin       CHAR(4) NOT NULL DEFAULT '0000',
  balance   MONEY NOT NULL DEFAULT 0,
  isLocked  BOOLEAN NOT NULL DEFAULT FALSE,
  accountID SMALLSERIAL NOT NULL REFERENCES BankAccount(accountID),
  
  PRIMARY KEY(cardID)
);
