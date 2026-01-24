--
-- PostgreSQL database dump
--

--
-- Data for Name: group; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig."group" VALUES ('a320761f-8e42-4b9b-8e09-106d378c0f93', '2020-06-18 17:51:05.829', 'System', false, '2020-06-18 17:51:05.829', 'System', 'The Admin group', 'GROUP ADMIN', 'ROLE_ADMIN')ON CONFLICT DO NOTHING;


-- Data for Name: user; Type: TABLE DATA; Schema: sig; Owner: postgres
--


INSERT INTO sig."user" VALUES ('3ce7a395-1072-4783-9f17-a6405ad074c1', '2020-06-18 14:49:46', 'System', false, '2020-11-29 13:27:48.677', 'admin', '2020-06-18 14:49:46', NULL, NULL, 'admin@eadn.dz', true, NULL, 'admin', NULL, 'admin', NULL, '$2a$10$Zi.o0VHUWYfres0VHugqSeC7OTexynW7h19gkEBNfvsV1fmBnvKZW', 'admin')ON CONFLICT DO NOTHING;


--
-- Data for Name: group_users; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.group_users VALUES ('a320761f-8e42-4b9b-8e09-106d378c0f93','3ce7a395-1072-4783-9f17-a6405ad074c1')ON CONFLICT DO NOTHING;

--
-- Data for Name: permissions; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.permissions VALUES ('50efc4d1-a498-438e-be1e-c5022aa238f4', '2020-06-18 17:51:05.632', 'System', false, '2020-06-18 17:51:05.632', 'System', 'Creer un layer', 'LAYER_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('dbe7aab1-8f33-48cc-b72f-e644a9d59403', '2020-06-18 17:51:05.643', 'System', false, '2020-06-18 17:51:05.643', 'System', 'Mettre a jour un layer', 'LAYER_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('a7a902e2-01b0-4557-a823-968da0e4142a', '2020-06-18 17:51:05.643', 'System', false, '2020-06-18 17:51:05.643', 'System', 'Afficher un layer', 'LAYER_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('c16aa318-23a0-4c77-ab26-6d214a02edfd', '2020-06-18 17:51:05.644', 'System', false, '2020-06-18 17:51:05.644', 'System', 'Supprimer un layer', 'LAYER_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('3ee68f1f-1819-449f-b2f5-993b5c585953', '2020-06-18 17:51:05.688', 'System', false, '2020-06-18 17:51:05.688', 'System', 'Creer un Champ', 'FIELD_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('e662dc14-04b5-4bd2-b3bb-5509c450e3c7', '2020-06-18 17:51:05.689', 'System', false, '2020-06-18 17:51:05.689', 'System', 'Mettre a jour un Champ', 'FIELD_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('0618f76b-706c-4ba1-957b-255c5ee94136', '2020-06-18 17:51:05.689', 'System', false, '2020-06-18 17:51:05.689', 'System', 'Afficher un Champ', 'FIELD_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('f28dba6a-01b5-44b4-aead-9c498a928ac9', '2020-06-18 17:51:05.69', 'System', false, '2020-06-18 17:51:05.69', 'System', 'Supprimer un Champ', 'FIELD_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('85d575d5-b520-4200-8220-f2df16357997', '2020-06-18 17:51:05.704', 'System', false, '2020-06-18 17:51:05.704', 'System', 'Creer une resource', 'RESOURCE_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('232a90df-87ab-445e-8c49-5e5b97164062', '2020-06-18 17:51:05.705', 'System', false, '2020-06-18 17:51:05.705', 'System', 'Mettre a jour une resource', 'RESOURCE_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('e24cdbb1-4f21-4b4a-84e7-ccb62d7205f4', '2020-06-18 17:51:05.705', 'System', false, '2020-06-18 17:51:05.705', 'System', 'Afficher une resource', 'RESOURCE_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('804febf4-072d-4c44-9cd1-04e91204fa8f', '2020-06-18 17:51:05.705', 'System', false, '2020-06-18 17:51:05.705', 'System', 'Supprimer une resource', 'RESOURCE_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('d6658abd-f714-471d-93e5-17f03155e9e9', '2020-06-18 17:51:05.706', 'System', false, '2020-06-18 17:51:05.706', 'System', 'Importer une resource', 'RESOURCE_IMPORT_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('6595b47a-9054-4a7b-9a8e-5bbdd6a95cd5', '2020-06-18 17:51:05.718', 'System', false, '2020-06-18 17:51:05.718', 'System', 'Creer un parametre', 'SETTINGS_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('4e034732-9e54-48d1-a59b-885100833b5e', '2020-06-18 17:51:05.719', 'System', false, '2020-06-18 17:51:05.719', 'System', 'Mettre a jour un parametre', 'SETTINGS_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('09f2f66b-95dc-412c-924f-38e7606dbca1', '2020-06-18 17:51:05.72', 'System', false, '2020-06-18 17:51:05.72', 'System', 'Afficher un parametre', 'SETTINGS_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('9e0ac787-4b9b-40e9-876c-d177a2af4591', '2020-06-18 17:51:05.72', 'System', false, '2020-06-18 17:51:05.72', 'System', 'Supprimer un parametre', 'SETTINGS_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('32e9c58d-1f55-4950-b066-013e236f2750', '2020-06-18 17:51:05.73', 'System', false, '2020-06-18 17:51:05.73', 'System', 'Creer une tag', 'TAG_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('4793a041-6f45-429b-8d5c-bc0275cdbfd9', '2020-06-18 17:51:05.731', 'System', false, '2020-06-18 17:51:05.731', 'System', 'Mettre a jour une tag', 'TAG_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('a9bd07dd-04c9-4a60-8cd9-5c526434fea7', '2020-06-18 17:51:05.731', 'System', false, '2020-06-18 17:51:05.731', 'System', 'Afficher une tag', 'TAG_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('d5f27327-10b9-4e4f-aed5-21446cf0c4a4', '2020-06-18 17:51:05.731', 'System', false, '2020-06-18 17:51:05.731', 'System', 'Supprimer une tag', 'TAG_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('c2ecce90-7126-4480-a622-7fac21d76dea', '2020-06-18 17:51:05.742', 'System', false, '2020-06-18 17:51:05.742', 'System', 'Creer un utilisateur', 'USER_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('00e98dc2-4538-4004-a449-c50cc76edfcc', '2020-06-18 17:51:05.743', 'System', false, '2020-06-18 17:51:05.743', 'System', 'Mettre a jour un utilisateur', 'USER_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('ef091636-6b94-4a9f-ac8d-c2ed3cfb7ee8', '2020-06-18 17:51:05.743', 'System', false, '2020-06-18 17:51:05.743', 'System', 'Afficher un utilisateur', 'USER_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('39f5bf32-4f5b-4014-9647-e43ff9564e16', '2020-06-18 17:51:05.744', 'System', false, '2020-06-18 17:51:05.744', 'System', 'Supprimer un utilisateur', 'USER_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('2fb73996-061c-45a7-88d7-71b1dcb1d12b', '2020-06-18 17:51:05.754', 'System', false, '2020-06-18 17:51:05.754', 'System', 'Creer un groupe', 'GROUP_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('9d027bbc-6a5a-4689-8cec-b0aa8e658921', '2020-06-18 17:51:05.754', 'System', false, '2020-06-18 17:51:05.754', 'System', 'Mettre a jour un groupe', 'GROUP_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('ba83b693-f26a-4648-9443-3bd80744263c', '2020-06-18 17:51:05.755', 'System', false, '2020-06-18 17:51:05.755', 'System', 'Creer un group', 'GROUP_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('4756ef43-2488-4d05-b87e-c3ab80aadf39', '2020-06-18 17:51:05.755', 'System', false, '2020-06-18 17:51:05.755', 'System', 'Supprimer un group', 'GROUP_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('cea8a0c2-eb32-4159-b3dd-a1afcd2252aa', '2020-06-18 17:51:05.765', 'System', false, '2020-06-18 17:51:05.765', 'System', 'Creer un point d''interet', 'ENTITY_ELEMENT_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('a87f373d-43d9-4e47-8030-2572fb3937e2', '2020-06-18 17:51:05.766', 'System', false, '2020-06-18 17:51:05.766', 'System', 'Mettre a jour un point d''interet', 'ENTITY_ELEMENT_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('6682ea51-38c8-4657-8f29-610379934068', '2020-06-18 17:51:05.766', 'System', false, '2020-06-18 17:51:05.766', 'System', 'Afficher un point d''interet', 'ENTITY_ELEMENT_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('4bb980c0-ebae-483d-95c8-3614b710cf86', '2020-06-18 17:51:05.767', 'System', false, '2020-06-18 17:51:05.767', 'System', 'Supprimer un point d''interet', 'ENTITY_ELEMENT_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('5310e96f-d088-48c9-8916-a9e2ea96d7d7', '2020-06-18 17:51:05.767', 'System', false, '2020-06-18 17:51:05.767', 'System', 'Exporter un point d''interet', 'ENTITY_ELEMENT_EXPORT_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('9e662277-2032-4d8c-bcae-600837a19087', '2020-06-18 17:51:05.767', 'System', false, '2020-06-18 17:51:05.767', 'System', 'Importer un point d''interet', 'ENTITY_ELEMENT_IMPORT_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('d5487c1c-c693-4ee9-9cfc-1e9ad9df11e0', '2020-06-18 17:51:05.777', 'System', false, '2020-06-18 17:51:05.777', 'System', 'Mettre un fichier sur le serveur', 'FILE_UPLOAD_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('e7a8261f-21cf-44f1-a749-fec940f3ff44', '2020-06-18 17:51:05.778', 'System', false, '2020-06-18 17:51:05.778', 'System', 'Telecharge un fichier depuis le serveur', 'FILE_LOAD_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('3a0b3163-5b1d-4843-986b-555de5192af4', '2020-06-25 14:19:28.444', 'admin', false, '2020-06-25 14:19:28.444', 'admin', 'Supprimer un fichier', 'FILE_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('4c24d81f-9e2f-4b98-b0a5-d429be7371b5', '2020-06-18 17:51:05.783', 'System', false, '2020-06-18 17:51:05.783', 'System', 'Afficher les sessions ouverts', 'SESSION_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('99e71e8d-c381-43c8-b774-ae5fb6dab7a3', '2020-06-18 17:51:05.784', 'System', false, '2020-06-18 17:51:05.784', 'System', 'supprimer une session ouvert', 'SESSION_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('9962b620-b9d6-4b9b-a48b-d240d8e5c20c', '2020-06-18 17:51:05.791', 'System', false, '2020-06-18 17:51:05.791', 'System', 'Creer une map', 'MAP_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('93aae8d3-d92e-45f9-ae38-e8219b47d165', '2020-06-18 17:51:05.792', 'System', false, '2020-06-18 17:51:05.792', 'System', 'Mettre a jour une map', 'MAP_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('402f25bb-c747-48fd-b74b-d9fed39e4308', '2020-06-18 17:51:05.792', 'System', false, '2020-06-18 17:51:05.792', 'System', 'Afficher une map', 'MAP_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('2228ddf9-abe0-454b-abe8-e472e8b61ded', '2020-06-18 17:51:05.792', 'System', false, '2020-06-18 17:51:05.792', 'System', 'Supprimer une map', 'MAP_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('ba463214-7d12-46e2-b085-6e748d1f176f', '2020-06-25 14:26:40.281', 'admin', false, '2020-06-25 14:26:40.281', 'admin', 'Creer une permission', 'PERMISSIONS_CREATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('52dbd7e9-0e79-477c-8df5-7a504859d651', '2020-06-25 14:26:59.17', 'admin', false, '2020-06-25 14:26:59.17', 'admin', 'Mettre a jour une permission', 'PERMISSIONS_UPDATE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('e5b749cb-9f67-4eb6-b8ee-6d331c883d64', '2020-06-25 14:25:43.101', 'admin', false, '2020-06-25 14:25:43.101', 'admin', 'Afficher une permission', 'PERMISSIONS_READ_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('eaad3892-4fef-4d4f-a612-52fd33af55b2', '2020-06-25 14:24:54.197', 'admin', false, '2020-06-25 14:24:54.197', 'admin', 'Supprimer une permission', 'PERMISSIONS_DELETE_AUTHORITY')ON CONFLICT DO NOTHING;

--
-- Data for Name: settings; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.settings VALUES ('18346110-83bb-4f3c-b18c-13df9a5331a7', '2020-06-18 17:51:05.81', 'System', false, '2020-06-18 17:51:05.81', 'System', 'SIG_DEFAULT_DATE_FORMAT', 'GENERAL', 'yyyy-MM-dd HH:mm:ss')ON CONFLICT DO NOTHING;
INSERT INTO sig.settings VALUES ('64822f7b-699e-4dea-ad3c-bde54d32dc60', '2020-06-18 17:51:05.81', 'System', false, '2020-06-18 17:51:05.81', 'System', 'SIG_DEFAULT_PATH_FOLDER_TEMP', 'GENERAL', 'C:\Users\A.LAMOUR/sigeadn/temp/')ON CONFLICT DO NOTHING;
INSERT INTO sig.settings VALUES ('254a7384-7aa0-4273-92b2-eb6dbb360429', '2020-06-18 17:51:05.811', 'System', false, '2020-06-18 17:51:05.811', 'System', 'SIG_DEFAULT_PATH_FOLDER_IMAGE', 'GENERAL', 'C:\Users\A.LAMOUR/sigeadn/images/')ON CONFLICT DO NOTHING;
INSERT INTO sig.settings VALUES ('5a2d0d75-301f-41d1-8567-064cac172cfb', '2020-06-18 17:51:05.811', 'System', false, '2020-06-18 17:51:05.811', 'System', 'SIG_DEFAULT_LANGUAGE', 'GENERAL', 'fr')ON CONFLICT DO NOTHING;
INSERT INTO sig.settings VALUES ('af014cf2-9801-4650-854c-f5bd3c70ebc2', '2020-06-18 17:51:05.811', 'System', false, '2020-06-18 17:51:05.811', 'System', 'SERVER_ADDRESS', 'GENERAL', 'http://localhost:8080')ON CONFLICT DO NOTHING;


--
-- PostgreSQL database dump complete
--



