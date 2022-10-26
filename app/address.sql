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
VALUES ('', 'Andijon shahri', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Xonabod shahri', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Andijon tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Asaka tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Baliqchi tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Bo`z tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Buloqboshi tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Isboskan tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Jalaquduq tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Marxamat tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Oltinko`l tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Paxtaobod tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Qo`rg`ontepa tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Shahrixon tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Ulug`nor tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Xo`jaobod tumani', '4fcb3bc7-8459-45dc-a380-10f995e15ad8');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Buxoro shahri', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Kogon shahri', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Buxoro tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'G`ijduvon tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Jondor tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Kogon tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Olot tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Peshku tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Qorovulbozor tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Qorako`l tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Romitan tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Shofirkon tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Vobkent tumani', '122a0d83-fb8e-4dbf-a65d-3ee6a0688037');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('', 'Farg`ona shahri', 'd51b9830-7cb6-4420-a07e-c8df78d90447');

INSERT INTO towns (town, region_id)
VALUES ('', 3),
       ('Marg`ilon shahri', 3),
       ('Quvasoy shahri', 3),
       ('Qo`qon shahri', 3),
       ('Beshariq tumani', 3),
       ('Bag`dod tumani', 3),
       ('Buvayda tumani', 3),
       ('Dang`ara tumani', 3),
       ('Farg`ona tumani', 3),
       ('Furqat tumani', 3),
       ('Oltiariq tumani', 3),
       ('O`zbekiston tumani', 3),
       ('Quva tumani', 3),
       ('Qo`shtepa tumani', 3),
       ('Rishton tumani', 3),
       ('So`x tumani', 3),
       ('Toshloq tumani', 3),
       ('Uchko`prik tumani', 3),
       ('Yozyovon tumani', 3);

INSERT INTO towns (town, region_id)
VALUES ('Jizzax shahri', 4),
       ('Arnasoy tumani', 4),
       ('Baxmal tumani', 4),
       ('Do`stlik tumani', 4),
       ('Forish tumani', 4),
       ('G`allaorol tumani', 4),
       ('Mirzacho`l tumani', 4),
       ('Paxtakor tumani', 4),
       ('Sharof tumani', 4),
       ('Yangiobod tumani', 4),
       ('Zafarobod tumani', 4),
       ('Zarbdor tumani', 4),
       ('Zomin tumani', 4);

INSERT INTO towns (town, region_id)
VALUES ('Namangan shahri', 5),
       ('Chortoq tumani', 5),
       ('Chust tumani', 5),
       ('Davlatobod tumani', 5),
       ('Kosonsoy tumani', 5),
       ('Mingbuloq tumani', 5),
       ('Namangan tumani', 5),
       ('Norin tumani', 5),
       ('Pop tumani', 5),
       ('To`raqo`rg`on tumani', 5),
       ('Uychi tumani', 5),
       ('Uchqo`rg`on tumani', 5),
       ('Yangiqo`rg`on tumani', 5);

INSERT INTO towns (town, region_id)
VALUES ('Navoiy shahri', 6),
       ('Karmana tumani', 6),
       ('Konimex tumani', 6),
       ('Navbahor tumani', 6),
       ('Nurota tumani', 6),
       ('Qiziltepa tumani', 6),
       ('Tomdi tumani', 6),
       ('Uchquduq tumani', 6),
       ('Xatirchi tumani', 6);

INSERT INTO towns (town, region_id)
VALUES ('Qarshi shahri', 7),
       ('Shahrisabz shahri', 7),
       ('Chiroqchi tumani', 7),
       ('Dehqonobod tumani', 7),
       ('G`uzor tumani', 7),
       ('Kasbi tumani', 7),
       ('Kitob tumani', 7),
       ('Koson tumani', 7),
       ('Ko`kdala tumani', 7),
       ('Mirishkor tumani', 7),
       ('Muborak tumani', 7),
       ('Nishon tumani', 7),
       ('Qamashi tumani', 7),
       ('Qarshi tumani', 7),
       ('Shahrisabz tumani', 7),
       ('Yakkabog` tumani', 7);

INSERT INTO towns (town, region_id)
VALUES ('Samarqand shahri', 8),
       ('Kattaqo`rg`on shahri', 8),
       ('Bulung`ur tumani', 8),
       ('Ishtixon tumani', 8),
       ('Jomboy tumani', 8),
       ('Kattaqo`rg`on tumani', 8),
       ('Natpay tumani', 8),
       ('Nurobod tumani', 8),
       ('Oqdaryo tumani', 8),
       ('Payariq tumani', 8),
       ('Pastdarg`on tumani', 8),
       ('Paxtachi tumani', 8),
       ('Qo`shrabot tumani', 8),
       ('Samarqand tumani', 8),
       ('Toyloq tumani', 8),
       ('Urgut tumani', 8);

INSERT INTO towns (town, region_id)
VALUES ('Guliston shahri', 9),
       ('Yangiyer shahri', 9),
       ('Shirin shahri', 9),
       ('Boyovut tumani', 9),
       ('Guliston tumani', 9),
       ('Mirzaobod tumani', 9),
       ('Oqoltin tumani', 9),
       ('Sardoba tumani', 9),
       ('Sayxunobod tumani', 9),
       ('Sirdaryo tumani', 9),
       ('Xovos tumani', 9);

INSERT INTO towns (town, region_id)
VALUES ('Termiz shahri', 10),
       ('Angor tumani', 10),
       ('Bandixon tumani', 10),
       ('Boysun tumani', 10),
       ('Denov tumani', 10),
       ('Jarqo`rg`on tumani', 10),
       ('Muzrobod tumani', 10),
       ('Oltinsoy tumani', 10),
       ('Qiziriq tumani', 10),
       ('Qumqo`rg`on tumani', 10),
       ('Sariosiyo tumani', 10),
       ('Sherobod tumani', 10),
       ('Sho`rchi tumani', 10),
       ('Termiz tumani', 10),
       ('Uzun tumani', 10);

INSERT INTO towns (town, region_id)
VALUES ('Angren shahri', 11),
       ('Bekobod shahri', 11),
       ('Chirchiq shahri', 11),
       ('Nurafshon shahri', 11),
       ('Olmaliq shahri', 11),
       ('Ohangaron shahri', 11),
       ('Yangiyo`l shahri', 11),
       ('Bekobod tumani', 11),
       ('Bo`ka tumani', 11),
       ('Bo`stonliq tumani', 11),
       ('Chinoz tumani', 11),
       ('Ohangaron tumani', 11),
       ('Oqqo`rg`on tumani', 11),
       ('O`rta chirchiq tumani', 11),
       ('Parkent tumani', 11),
       ('Piskent tumani', 11),
       ('Qibray tumani', 11),
       ('Quyi chirchiq tumani', 11),
       ('Toshkent tumani', 11),
       ('Yangiyo`l tumani', 11),
       ('Yuqori chirchiq tumani', 11),
       ('Zangiota tumani', 11);

INSERT INTO towns (town, region_id)
VALUES ('Urganch shahri', 12),
       ('Xiva shahri', 12),
       ('Bog`ot tumani', 12),
       ('Gurlan tumani', 12),
       ('Hazorasp tumani', 12),
       ('Qo`shko`pir tumani', 12),
       ('Shovot tumani', 12),
       ('Tuproqqal`a tumani', 12),
       ('Urganch tumani', 12),
       ('Xiva tumani', 12),
       ('Xonqa tumani', 12),
       ('Yangiariq tumani', 12),
       ('Yangibozor tumani', 12);

INSERT INTO towns(town, region_id)
VALUES ('Nukus shahri', 13),
       ('Amudaryo tumani', 13),
       ('Beruniy tumani', 13),
       ('Chimboy tumani', 13),
       ('Ellikqala tumani', 13),
       ('Kegeyli tumani', 13),
       ('Mo`ynoq tumani', 13),
       ('Nukus tumani', 13),
       ('Qonliko`l tumani', 13),
       ('Qorao`zak tumani', 13),
       ('Qo`ng`irot tumani', 13),
       ('Taxiatosh tumani', 13),
       ('Taxtako`prik tumani', 13),
       ('To`rtko`l tumani', 13),
       ('Sho`manoy tumani', 13),
       ('Xo`jayli tumani', 13);

INSERT INTO towns (town, region_id)
VALUES ('Bektemir tumani', 14),
       ('Chilonzor tumani', 14),
       ('Mirzo Ulug`bek tumani', 14),
       ('Mirobod tumani', 14),
       ('Olmazor tumani', 14),
       ('Sirgali tumani', 14),
       ('Shayxontohur tumani', 14),
       ('Uchtepa tumani', 14),
       ('Yakkasaroy tumani', 14),
       ('Yashnobod tumani', 14),
       ('Yunusobod tumani', 14);