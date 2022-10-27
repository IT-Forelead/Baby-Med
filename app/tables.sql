CREATE TYPE ROLE AS ENUM ('super_manager', 'tech_admin', 'admin');

CREATE TABLE users
(
    id        UUID PRIMARY KEY,
    firstname VARCHAR NOT NULL,
    lastname  VARCHAR NOT NULL,
    phone     VARCHAR NOT NULL,
    role      ROLE    NOT NULL,
    password  VARCHAR NOT NULL,
    deleted   BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE regions
(
    id      UUID PRIMARY KEY,
    name  VARCHAR NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE towns
(
    id        UUID PRIMARY KEY,
    name      VARCHAR NOT NULL,
    region_id UUID    NOT NULL
        CONSTRAINT fk_region_id REFERENCES regions (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    deleted   BOOLEAN NOT NULL DEFAULT false
);

INSERT INTO "regions" ("id", "name") VALUES ('4fcb3bc7-8459-45dc-a380-10f995e15ad8', 'Andijon viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('122a0d83-fb8e-4dbf-a65d-3ee6a0688037', 'Buxoro viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('d51b9830-7cb6-4420-a07e-c8df78d90447', 'Farg`ona viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf', 'Jizzax viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('2d27b575-f952-4c93-8f9e-02c89758cbc7', 'Namangan viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('51b00d57-1b99-47c5-b89c-8d1fab5825f6', 'Navoiy viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('f4bbb8aa-680f-4220-9079-b460e9f2e573', 'Qashqadaryo viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('425ff71e-57dd-459f-a831-cf57b30a7345', 'Samarqand viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('3acfc29c-3e14-4beb-96f6-20f025e431ab', 'Sirdaryo viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('54b834ee-0df9-465e-ad34-be1834b491d0', 'Surxondaryo viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('3b316182-e55c-4e03-8811-052fcd888236', 'Toshkent viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('ad514b71-3096-4be5-a455-d87abbb081b2', 'Xorazm viloyati');
INSERT INTO "regions" ("id", "name") VALUES ('8b88eb6c-24e1-4ecd-b944-8605d28da975', 'Qoraqalpog`iston Respublikasi');
INSERT INTO "regions" ("id", "name") VALUES ('dac35ec3-a904-42d7-af20-5d7e853fe1f6', 'Toshkent shahri');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('ec155420-df46-403f-aae9-50f35c9f0a1e', 'Andijon shahri', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('1586486b-916a-40a3-a669-eb0043796edb', 'Xonabod shahri', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('eacbc31e-7205-40cc-bf2f-88695c9428af', 'Andijon tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2a4368c5-f154-49ba-96ac-bbeade152290', 'Asaka tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('adfc219e-d100-40af-ad6c-5be8be9162e5', 'Baliqchi tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('7ff5febe-ed07-41e7-9edc-571fda55bd4c', 'Bo`z tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b70f16cb-e568-403b-8497-90bf05c7f150', 'Buloqboshi tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3cc87d02-43eb-404c-b6ba-b6b30454771e', 'Isboskan tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2a98edee-2aac-4b5b-872a-9ba72f2a17ae', 'Jalaquduq tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('edc09abe-5e6d-463a-b3e3-5ff92457f49b', 'Marxamat tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('dde0568b-9323-4db5-a041-89c3101f0d3d', 'Oltinko`l tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d8881d2d-b42a-4da2-9d8e-9004af6c3f42', 'Paxtaobod tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('87307c3c-2cc1-4c63-b3db-6d1a3678cae0', 'Qo`rg`ontepa tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f22c4224-700d-439d-91fe-2ac4619ca110', 'Shahrixon tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('25d742c5-9fac-4de2-8a70-a571f5c5f733', 'Ulug`nor tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6593c021-3e62-4a4c-bfbb-205ec25654b2', 'Xo`jaobod tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('1dd36b80-2de5-43a8-b6fb-64c885cc0f83', 'Buxoro shahri', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('a5aceae7-7db7-4dc0-85cf-9f9cbf428dd1', 'Kogon shahri', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('1ae1962c-9e1d-4d10-a2d8-ee609a17d7c9', 'Buxoro tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f30e629d-ddec-4aba-b171-453d00ed263a', 'G`ijduvon tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d786cc60-eeaa-4faa-b6d4-1a29f4a04073', 'Jondor tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('11ab68ca-0ae0-4e73-8df4-a0d4eac9252f', 'Kogon tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('77a927da-7f35-4420-a976-21d6ceaedf12', 'Olot tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('41213082-e942-4b2b-a656-ee98b23dfca3', 'Peshku tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e3219b08-d583-415c-87b4-43bb6ba98ead', 'Qorovulbozor tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('90b2e1d5-3bb2-402a-b0de-c324bfc1ab6d', 'Qorako`l tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('01c1b0ac-79ec-4651-8106-c5a7f3f88e19', 'Romitan tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('47623faf-e9c1-4bd7-896b-bb6d38436dc7', 'Shofirkon tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('09d2e6e2-d939-4b71-a090-afdd4963127c', 'Vobkent tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('72c98b46-fe79-45af-9c12-17efe99f2894', 'Farg`ona shahri', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('bc5cff63-2c08-45e4-bd1b-5c31a1cad7e3', 'Marg`ilon shahri', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('1647aa14-e7a4-420e-b61a-7e81df60b1af', 'Quvasoy shahri', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('4d322e9f-f6f9-48b0-b1b4-cb4d9c22d623', 'Qo`qon shahri', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2d793cc0-c0d8-4faa-8b78-aad7ee9dbea3', 'Beshariq tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('14ca96da-3873-4812-9d62-e6f4b6c58c3b', 'Bag`dod tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b9fc31c2-6dfd-4631-a20d-c17ff52f37a7', 'Buvayda tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('74eb3eda-f168-484e-94f9-c7c71ed9d079', 'Dang`ara tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('35839ab8-fc21-4ae2-b9ef-dc5d8c7fdc33', 'Farg`ona tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e707ff5b-89a8-4d5b-89e4-bb6ff9accefb', 'Furqat tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('5b36fada-10c4-4faf-8ada-a71a2092d003', 'Oltiariq tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('1734b4b8-df3d-4448-84b5-2a9578a0b384', 'O`zbekiston tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('28d36344-e871-4a41-9f0a-6a0947506273', 'Quva tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3c0db081-cd99-4e71-997b-2c959dcd081e', 'Qo`shtepa tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3517f4f6-9d48-4436-9b22-b6b79fb0c62e', 'Rishton tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3ffe792f-7898-467e-ad12-f21df9419e83', 'So`x tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('54a0244a-3170-467e-8a9c-89f2b740db16', 'Toshloq tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b3fd17f9-9a56-4e66-83ce-90cb6c3527eb', 'Uchko`prik tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('eb3f2cb2-d4a1-4b46-a98c-ba0babfcf6aa', 'Yozyovon tumani', 'd51b9830-7cb6-4420-a07e-c8df78d90447');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('5ac4134e-99df-4eb7-9b45-bf1fec9e72a1', 'Jizzax shahri', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('162fbef2-a649-494d-ae7b-f203c6c2adec', 'Arnasoy tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e8ee1464-83ea-49b9-b3f2-846b53488c3a', 'Baxmal tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2ece210b-2b0c-4127-870c-c92bd3fb73dc', 'Do`stlik tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('bde2e401-a69c-4e80-9214-4e484f329699', 'Forish tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('35086816-20c0-4852-8edf-8cf128ae49b4', 'G`allaorol tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2c3cdf3c-2ea1-47d1-a56d-48579b78defa', 'Mirzacho`l tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('da28d5ad-ff91-460c-a751-f1c2a59c55b0', 'Paxtakor tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('5eba6593-d3eb-4eef-89ae-a80497083a0e', 'Sharof tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('620ddf52-cabe-43e3-bbf8-532a76343b73', 'Yangiobod tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('76791c74-377f-4618-a981-6bec0495ad52', 'Zafarobod tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('acd7d90e-a98e-4f9e-93bb-8c422cd20938', 'Zarbdor tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d07a0d66-3c71-4126-b1b9-deb1be6c7357', 'Zomin tumani', 'a4ec39b1-dfad-45e1-a12c-7986ffa4e4bf');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('04144c07-1763-4411-8a9d-504d48a45831', 'Namangan shahri', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('5fbe1cbc-cf14-470d-93e8-63019ad0742b', 'Chortoq tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('80ca813b-4fb5-4d0d-b630-ff79d6da4bb0', 'Chust tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('a172d567-02d5-4e7c-b86b-33a939e357ea', 'Davlatobod tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('79cd6a06-77cf-43d9-8aa4-f8c1ca0f2f2d', 'Kosonsoy tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('163ff3be-9646-43ae-af38-00c7fbc24599', 'Mingbuloq tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e48846f0-2200-4c9a-8cde-20fd3e75a89e', 'Namangan tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('99a1ade4-124f-4357-83e4-5e3364a02e63', 'Norin tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('20b45a0d-bec1-4682-bdf0-826f419a824f', 'Pop tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('09c9d3ae-7a5a-4c90-92ea-6f0509f83c78', 'To`raqo`rg`on tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('dcf79a2d-0965-4e7c-9f97-758565c30036', 'Uychi tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b813f21e-b53e-451c-95a4-0f9a7b39969e', 'Uchqo`rg`on tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('ba37467e-81b1-49ac-8eb8-2a41e916fbd9', 'Yangiqo`rg`on tumani', '2d27b575-f952-4c93-8f9e-02c89758cbc7');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('84a7b22c-6281-497c-b66b-a47b994bd586', 'Navoiy shahri', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d96698bd-3867-440c-a03d-0e25c061d7a1', 'Karmana tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('07a367c6-5604-4b37-b260-ba600285bc2a', 'Konimex tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('561592bc-96da-47fb-bb76-6a11102f9472', 'Navbahor tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('48919ea1-efe1-4334-b1a4-48c0cf2d962e', 'Nurota tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f736ff4e-b4bc-4967-bddf-ac35f8008f2e', 'Qiziltepa tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('c94f8c07-81f2-47f5-9307-f2ef262263a8', 'Tomdi tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('fc5e308c-5f06-424c-8b1d-70b72d924577', 'Uchquduq tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('7e265df2-5f64-435e-9d0e-1b91ddddd453', 'Xatirchi tumani', '51b00d57-1b99-47c5-b89c-8d1fab5825f6');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('134b2a47-da6a-45d4-bcb1-b33452c4e014', 'Qarshi shahri', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('57ed67b7-7a3b-435f-9e86-7645f6f7942a', 'Shahrisabz shahri', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('fc87afb0-3801-4d96-b491-88ac99ef3fca', 'Chiroqchi tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('5def9a44-d5cc-47da-98fc-8e16c58be4c5', 'Dehqonobod tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f90276c3-a7d5-4c4b-93eb-f3cd5307823b', 'G`uzor tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('06c55c5c-53ec-42ad-8c22-f1ab2546b1cc', 'Kasbi tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('ec186063-daf9-40ca-9e27-d3d2a845663a', 'Kitob tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('008d887d-765c-4192-9923-2d41d6d1f1ea', 'Koson tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2b45d291-5c72-443d-8e88-7ff52bb1c9bd', 'Ko`kdala tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6146c8dd-84ad-4309-83c2-2e2695cddf27', 'Mirishkor tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f3f82221-f5c8-4f59-8d35-a5d90b26229c', 'Muborak tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('700fd7d9-3401-4e47-87b9-e635e7905508', 'Nishon tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('8f00642d-af19-4daf-9d44-a2388b480e78', 'Qamashi tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('51d68dcd-9182-4d68-92fa-a3c3bb4b2ed4', 'Qarshi tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d35f78b8-a35b-4ccb-a908-6a91b3cd3151', 'Shahrisabz tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('293de22c-7984-4334-9719-18fdfc1e7e7a', 'Yakkabog` tumani', 'f4bbb8aa-680f-4220-9079-b460e9f2e573');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('fc1dbc69-fad1-41ea-857c-3e6710ac452c', 'Samarqand shahri', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('c1a9c4ea-19ca-4b2a-be6c-cbf13b42bd92', 'Kattaqo`rg`on shahri', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('245866b1-7c1a-4815-ba87-78ce6b1051a9', 'Bulung`ur tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('24ab8522-72fd-470b-9875-dc839caca6f2', 'Ishtixon tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('417e9060-c925-4dcf-9e6a-971e6e54ecee', 'Jomboy tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2bf97072-5601-409c-9c62-27ab1acef0d7', 'Kattaqo`rg`on tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('75216b73-dd35-439c-90ac-bb3cd78e6ea1', 'Natpay tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('ce8dbab2-e6b5-4729-861f-93e350eac5a9', 'Nurobod tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('23ab66bc-2a3a-4c4a-924f-b9d29e5d09e6', 'Oqdaryo tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('481f84ce-920e-4cdb-8ab4-2a601cce5719', 'Payariq tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f7dcbe52-40c1-4688-91b3-5631f9bd7f25', 'Pastdarg`on tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('50f3ff8e-0cbb-4740-ae8f-2929edd8431f', 'Paxtachi tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('47145446-9cd1-45dc-b75d-7f021fac6ea4', 'Qo`shrabot tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2ba230fb-bdfd-426a-b130-b9c1ca440350', 'Samarqand tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d0b9bfb1-571e-470c-a573-2dd5547b6ba2', 'Toyloq tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('0e55fe03-1156-489d-8a8e-63776cea3332', 'Urgut tumani', '425ff71e-57dd-459f-a831-cf57b30a7345');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f7458d21-7b7e-4a8f-bed1-284be01953b9', 'Guliston shahri', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('dcf4df7a-5552-46b0-8a72-130895eecaec', 'Yangiyer shahri', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('c9153187-b809-4688-b2be-c3f1d7ace980', 'Shirin shahri', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('56c51268-8495-45b1-857d-0d763b2aad4f', 'Boyovut tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e3132ca8-0368-416b-8854-281ab07f983c', 'Guliston tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('9b2371da-932f-4f42-97c9-6e5ee958b5e9', 'Mirzaobod tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('62fd3685-d169-491c-a3cc-d17dd87e988c', 'Oqoltin tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3b912bee-4af5-4455-a942-0c63deb387e0', 'Sardoba tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('63f2b724-a8ed-444f-afa1-01b61f1e9c9c', 'Sayxunobod tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('1ae82afd-5262-40df-b125-de03e08d0bb7', 'Sirdaryo tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('ec843a8f-7e73-456f-9316-f998cb80cf54', 'Xovos tumani', '3acfc29c-3e14-4beb-96f6-20f025e431ab');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2ec7867e-1742-428c-adac-4fb2edab5832', 'Termiz shahri', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('83190a4a-c3c4-4b92-8251-60a926068985', 'Angor tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('953d7390-be13-4f82-a75a-c83d00886eab', 'Bandixon tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('c5e0dab3-07e5-4cf8-94e5-5c4d425cd4d9', 'Boysun tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('9f518188-d0a1-4e9c-aa42-00d851b8c86f', 'Denov tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('c09e5e5c-8130-446d-bc3e-88d7f645c37c', 'Jarqo`rg`on tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d61af4f2-98a7-4ad4-ace1-030446eaadd1', 'Muzrobod tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('8c298579-135a-481c-960b-eedc7e3eacfd', 'Oltinsoy tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('7b5c855f-457f-415f-854a-3a2e97011156', 'Qiziriq tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f8ccb9c6-0c2b-493b-8bbf-adc16c3917e7', 'Qumqo`rg`on tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('39ef163c-a4cd-43a8-b970-9f526bb3dbdc', 'Sariosiyo tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e4e7ec38-db92-431c-8b90-65c9a07a6a47', 'Sherobod tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('5eed0df6-7c19-4755-bb91-e1dfeb62e63f', 'Sho`rchi tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('68a6f6bb-d230-49a1-9946-9ffe3320107d', 'Termiz tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('240709f5-bc90-4f9c-8e03-2c2bcfc45eb7', 'Uzun tumani', '54b834ee-0df9-465e-ad34-be1834b491d0');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('bff3c53c-3518-48b1-bc53-7845e3e46573', 'Angren shahri', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b07ad341-0877-4366-99a4-c03bcd887690', 'Bekobod shahri', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3a6b9840-0380-4c3f-94ab-3337aedb0d1b', 'Chirchiq shahri', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3e68f094-c92a-4f8c-aaf1-d00ab56465f8', 'Nurafshon shahri', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6b01f868-f1ec-4ee9-8248-0070969968a5', 'Olmaliq shahri', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('831e8f45-6ceb-40bf-8665-f8e142039bc7', 'Ohangaron shahri', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e07e76af-0002-47fa-886c-48112f1448b2', 'Yangiyo`l shahri', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('242db548-09b5-4907-99a7-8754e0407d2f', 'Bekobod tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('a0140f88-de6c-403b-8115-3f05735a03ba', 'Bo`ka tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('fc9f291b-b5dc-40b5-b1ca-bc4fcea329e0', 'Bo`stonliq tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('0a3b5b89-a3f4-4c12-84b1-1d6006a9ece2', 'Chinoz tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6be683e1-7faa-47b5-8346-afe143f4300c', 'Ohangaron tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2a724e13-9efd-4954-ac50-a5dbe2d38385', 'Oqqo`rg`on tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b08b9921-51bc-441c-b8d1-ad984f3fbdfd', 'O`rta chirchiq tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6635bc05-fe70-430d-a505-415756f46adb', 'Parkent tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('8ee11d3c-7130-48c3-afa1-30d62505df2d', 'Piskent tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('11533f55-f6a0-48d2-9ee6-7a46d7cea03a', 'Qibray tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('cbc8b791-03f8-4294-a938-347b601bb15b', 'Quyi chirchiq tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('53692b3c-526b-47f5-bd1a-a7e3e3310ef8', 'Toshkent tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('ca01ba59-8d5e-467b-b160-ca31034d18b3', 'Yangiyo`l tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('aa8e5e71-ac61-4c94-8102-20c52bf94a58', 'Yuqori chirchiq tumani', '3b316182-e55c-4e03-8811-052fcd888236');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('9b7efce1-6292-43b8-9319-9fa961a1cc6a', 'Zangiota tumani', '3b316182-e55c-4e03-8811-052fcd888236');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('0d073b76-08ce-4b78-a88c-a0cb6f80eaf9', 'Urganch shahri', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b272f8fe-e0a1-4157-903f-91d1b22b6770', 'Xiva shahri', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('8c20152c-881f-4b87-8119-213cf7288167', 'Bog`ot tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('bc0bd8b6-7657-465e-806f-a31765c5b461', 'Gurlan tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('c9e2cbef-381b-4158-91ce-39c77e7a41dd', 'Hazorasp tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e24b2941-ac1b-44d3-9a82-0ff5885f47ef', 'Qo`shko`pir tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('cd3b192c-64b1-415f-991b-0142f62fde98', 'Shovot tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('9f6b96c9-fe1c-4257-8b1c-f4b072ac84dd', 'Tuproqqal`a tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('8a2120f7-2ae3-4c9b-8a03-fcad3aef6d0f', 'Urganch tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('00314c6b-9962-408d-b195-2bfce1cc77d5', 'Xiva tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('acc47ab3-47c4-4d3c-977f-bde5fcfeb9cf', 'Xonqa tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('66558fa5-7a1c-45eb-8f96-186d4296d70e', 'Yangiariq tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('86645a69-5e92-405d-9d08-30d5b91d9a47', 'Yangibozor tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('d2863709-1e78-4b0c-9825-4f54e67e7d27', 'Nukus shahri', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6dd0a4fb-6ca1-4d31-886e-b398f89ba4c1', 'Amudaryo tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('837535e0-87f3-4f83-9d2f-dc623e5a38a2', 'Beruniy tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6b09acff-0043-433e-8ea1-ee31defc9669', 'Chimboy tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('87171fdc-d9e4-45c2-93de-f9f30d34726a', 'Ellikqala tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('1a92f818-e4f0-4f94-ba50-f09da044ac28', 'Kegeyli tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('53ebbdfd-632f-4df7-89ba-eb49fc326cd7', 'Mo`ynoq tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('01797bfb-e4e6-4a87-9a7b-6d66cfb8ac49', 'Nukus tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('a727673c-ef94-4292-ac8e-e54f3748c282', 'Qonliko`l tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f3b48b02-7e12-434d-921e-b3d630e2c6df', 'Qorao`zak tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('06103498-400d-4e20-9c70-97c087c659e2', 'Qo`ng`irot tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('fa2fe502-7ca1-4e97-8e65-958d9db6d2cc', 'Taxiatosh tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('b09af35a-6d1c-46b9-992c-adbbd84c94d7', 'Taxtako`prik tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('c915a23f-a85b-4818-8182-665692663b33', 'To`rtko`l tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('6cb72cf9-e60d-4d56-a268-d44edaeba5db', 'Sho`manoy tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('398848e5-1233-4b6e-bcee-f6dbf29cae8f', 'Xo`jayli tumani', '8b88eb6c-24e1-4ecd-b944-8605d28da975');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('94532c86-0c48-4d22-9969-11dd38414504', 'Bektemir tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('407ec75a-d1de-4799-a3db-854bcc14e71a', 'Chilonzor tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('852772ff-6149-4f20-8269-1d7cdb130dcd', 'Mirzo Ulug`bek tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('08454c0b-f298-4708-ba6c-a533250c2156', 'Mirobod tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('798dbd32-d288-446a-896e-10693a65e149', 'Olmazor tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('71d41132-7ce7-4274-a815-660296099500', 'Sirgali tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('3d5f97c9-258b-4a73-b7f0-3a5745502a1c', 'Shayxontohur tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('47d94c82-ddeb-425f-a1e4-1a12d45cc9cc', 'Uchtepa tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('2372b0b3-878c-4105-a61c-a87d2a99d086', 'Yakkasaroy tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('a144cd37-c743-47fc-88ec-c670b4fde1e7', 'Yashnobod tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('f4989b83-fc64-4844-bd57-e721a7f0e4aa', 'Yunusobod tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');

CREATE TABLE clients
(
    id        UUID PRIMARY KEY,
    firstname VARCHAR NOT NULL,
    lastname  VARCHAR NOT NULL,
    region_id UUID    NOT NULL
        CONSTRAINT fk_region_id REFERENCES regions (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    town_id   UUID    NOT NULL
        CONSTRAINT fk_town_id REFERENCES towns (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    address   VARCHAR NOT NULL,
    birthday  DATE    NOT NULL,
    phone     VARCHAR UNIQUE,
    deleted   BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE payments
(
    id         UUID PRIMARY KEY,
    client_id  UUID      NOT NULL
        CONSTRAINT fk_client_id REFERENCES clients (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    price      NUMERIC   NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deleted    BOOLEAN   NOT NULL DEFAULT false
);