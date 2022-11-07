ALTER TABLE users
    ADD CONSTRAINT UQ_users_phone UNIQUE (phone);
ALTER TABLE users
    DROP COLUMN deleted;
INSERT INTO "users" ("id", "created_at", "firstname", "lastname", "phone", "role", "password")
VALUES ('72a911c8-ad24-4e2d-8930-9c3ba51741df', '2022-11-07T06:43:01.089Z', 'Admin', 'Adminov', '+998901234567',
        'super_manager',
        '$s0$e0801$5JK3Ogs35C2h5htbXQoeEQ==$N7HgNieSnOajn1FuEB7l4PhC6puBSq+e1E8WUaSJcGY=');