-- src/test/resources/data.sql
-- Minimal seed data for tests (from your dump)

INSERT INTO "user"(id, useraccount, username, userpwd, useravatar, useremail, userrole, islogin, isword, lastlogintime,
                   createtime)
VALUES (1, 'adminfalse', 'Mr. Black Pineapple', '123456',
        'http://localhost:21090/api/campus-product-sys/v1.0/file/getFile?filename=49bf8cepic_6.jpg', '1343243@qq.com',
        1, FALSE, FALSE, TIMESTAMP '2025-10-18 21:00:37', TIMESTAMP '2025-10-04 12:53:05'),
       (3, 'user2', 'Hello', '14e1b600b1fd579f47433b88e8d85291',
        'http://localhost:21090/api/trading-website/v1.0/file/getFile?fileName=645f631屏幕截图', NULL, 2, FALSE, FALSE,
        TIMESTAMP '2025-10-27 13:12:19', TIMESTAMP '2025-10-20 20:03:19'),
       (7, 'user', 'user', '14e1b600b1fd579f47433b88e8d85291', NULL, '2470865290@qq.com', 2, FALSE, FALSE,
        TIMESTAMP '2025-10-27 13:02:42', TIMESTAMP '2025-10-24 22:54:04');

INSERT INTO category(id, name, isuse)
VALUES (1, '电子产品', TRUE),
       (2, '家用电器', TRUE),
       (11, '水杯', TRUE);

INSERT INTO product(id, name, detail, coverlist, oldlevel, categoryid, userid, inventory, price, isbargain, createtime)
VALUES (1, '123', '<p>123</p>',
        'http://localhost:21090/api/trading-website/v1.0/file/getFile?fileName=e55ea9da69c9639457da4bda8537504d6ce66aa.png',
        9, 10, 2, 0, 123.00, FALSE, TIMESTAMP '2025-10-21 23:03:01'),
       (2, '水杯', '<p>非常好看！</p>',
        'http://localhost:21090/api/trading-website/v1.0/file/getFile?fileName=dfa1198WIN_20240607_14_15_38_Pro.jpg', 9,
        11, NULL, 348, 540.00, TRUE, TIMESTAMP '2025-10-24 22:53:12');

INSERT INTO address(id, userid, contactperson, addressdetail, contactphone, isdefault)
VALUES (1, NULL, '潘雨馨', '福州大学梅努斯国际工程学院', '15965379641', TRUE),
       (2, 7, 'user', 'fuzhoudaxue1', '18359046396', TRUE),
       (3, 3, '陈品言', '浮渣偶多谢', '18359046396', TRUE);

INSERT INTO orders(id, code, detail, userid, productid, buyprice, buynumber, tradestatus, tradetime, refundstatus,
                   isrefundconfirm, isconfirm, addressid, isdeliver, createtime)
VALUES (1, '1761529855844', '我真的想要', 7, 1, 123.00, 1, 2, TIMESTAMP '2025-10-27 11:07:08', 1, FALSE, FALSE, 2,
        FALSE, TIMESTAMP '2025-10-27 09:50:55');
