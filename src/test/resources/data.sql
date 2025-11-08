INSERT INTO "user" (useraccount, username, userpwd, useremail, userrole, islogin, createtime)
VALUES ('admin', 'Admin', 'admin123', 'admin@test.com', 1, TRUE, CURRENT_TIMESTAMP);

INSERT INTO "user" (useraccount, username, userpwd, useremail, userrole, islogin, createtime)
VALUES ('exist_user', 'Exist', '123456', 'exist@test.com', 2, TRUE, CURRENT_TIMESTAMP);
