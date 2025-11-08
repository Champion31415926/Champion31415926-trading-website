-- users
CREATE TABLE "user"
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    useraccount VARCHAR(50),
    username    VARCHAR(50),
    userpwd     VARCHAR(200),
    useremail   VARCHAR(100),
    useravatar  VARCHAR(255),
    userrole    INTEGER,
    islogin     BOOLEAN DEFAULT TRUE,
    createtime  TIMESTAMP
);

-- product (示例)
CREATE TABLE product
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255),
    detail     TEXT,
    coverlist  TEXT,
    oldlevel   INTEGER,
    categoryid BIGINT,
    userid     BIGINT,
    inventory  INTEGER,
    price      DECIMAL,
    isbargain  BOOLEAN,
    createtime TIMESTAMP
);

-- interaction (示例)
CREATE TABLE interaction
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    userid     BIGINT,
    productid  BIGINT,
    type       INTEGER,
    createtime TIMESTAMP
);

-- orders 示例
CREATE TABLE "orders"
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(100),
    detail          VARCHAR(255),
    userid          BIGINT,
    productid       BIGINT,
    buyprice        DECIMAL,
    buynumber       INTEGER,
    tradestatus     INTEGER,
    tradetime       TIMESTAMP,
    refundstatus    INTEGER,
    refundtime      TIMESTAMP,
    isrefundconfirm BOOLEAN,
    isconfirmtime   TIMESTAMP,
    isconfirm       BOOLEAN,
    addressid       BIGINT,
    isdeliver       BOOLEAN,
    delivertime     TIMESTAMP,
    deliveradrid    BIGINT,
    createtime      TIMESTAMP
);

-- address 示例
CREATE TABLE address
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    userid        BIGINT,
    contactperson VARCHAR(100),
    addressdetail TEXT,
    contactphone  VARCHAR(50),
    isdefault     BOOLEAN
);

-- category
CREATE TABLE category
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(100),
    isuse BOOLEAN
);
