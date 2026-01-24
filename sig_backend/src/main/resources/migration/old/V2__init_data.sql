--
-- Data for Name: group; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig."group" VALUES ('a320761f-8e42-4b9b-8e09-106d378c0f93', '2020-06-18 17:51:05.829', 'System', false, '2020-06-18 17:51:05.829', 'System', 'The Admin group', 'Admingroup');

--
-- Data for Name: user; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig."user" VALUES ('3ce7a395-1072-4783-9f17-a6405ad074c1', '2020-06-18 14:49:46', 'System', false, '2020-06-18 14:49:46', 'System', NULL, 'admin@eadn.dz', true, '$2a$10$Zi.o0VHUWYfres0VHugqSeC7OTexynW7h19gkEBNfvsV1fmBnvKZW', 'admin', 'a320761f-8e42-4b9b-8e09-106d378c0f93');

--
-- Data for Name: permissions; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.permissions VALUES ('50efc4d1-a498-438e-be1e-c5022aa238f4', '2020-06-18 17:51:05.632', 'System', false, '2020-06-18 17:51:05.632', 'System', 'Creer un layer', 'LAYER_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('dbe7aab1-8f33-48cc-b72f-e644a9d59403', '2020-06-18 17:51:05.643', 'System', false, '2020-06-18 17:51:05.643', 'System', 'Mettre a jour un layer', 'LAYER_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('a7a902e2-01b0-4557-a823-968da0e4142a', '2020-06-18 17:51:05.643', 'System', false, '2020-06-18 17:51:05.643', 'System', 'Afficher un layer', 'LAYER_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('c16aa318-23a0-4c77-ab26-6d214a02edfd', '2020-06-18 17:51:05.644', 'System', false, '2020-06-18 17:51:05.644', 'System', 'Supprimer un layer', 'LAYER_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('3ee68f1f-1819-449f-b2f5-993b5c585953', '2020-06-18 17:51:05.688', 'System', false, '2020-06-18 17:51:05.688', 'System', 'Creer un Champ', 'FIELD_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('e662dc14-04b5-4bd2-b3bb-5509c450e3c7', '2020-06-18 17:51:05.689', 'System', false, '2020-06-18 17:51:05.689', 'System', 'Mettre a jour un Champ', 'FIELD_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('0618f76b-706c-4ba1-957b-255c5ee94136', '2020-06-18 17:51:05.689', 'System', false, '2020-06-18 17:51:05.689', 'System', 'Afficher un Champ', 'FIELD_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('f28dba6a-01b5-44b4-aead-9c498a928ac9', '2020-06-18 17:51:05.69', 'System', false, '2020-06-18 17:51:05.69', 'System', 'Supprimer un Champ', 'FIELD_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('85d575d5-b520-4200-8220-f2df16357997', '2020-06-18 17:51:05.704', 'System', false, '2020-06-18 17:51:05.704', 'System', 'Creer une resource', 'RESOURCE_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('232a90df-87ab-445e-8c49-5e5b97164062', '2020-06-18 17:51:05.705', 'System', false, '2020-06-18 17:51:05.705', 'System', 'Mettre a jour une resource', 'RESOURCE_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('e24cdbb1-4f21-4b4a-84e7-ccb62d7205f4', '2020-06-18 17:51:05.705', 'System', false, '2020-06-18 17:51:05.705', 'System', 'Afficher une resource', 'RESOURCE_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('804febf4-072d-4c44-9cd1-04e91204fa8f', '2020-06-18 17:51:05.705', 'System', false, '2020-06-18 17:51:05.705', 'System', 'Supprimer une resource', 'RESOURCE_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('d6658abd-f714-471d-93e5-17f03155e9e9', '2020-06-18 17:51:05.706', 'System', false, '2020-06-18 17:51:05.706', 'System', 'Importer une resource', 'RESOURCE_IMPORT_AUTHORITY');
INSERT INTO sig.permissions VALUES ('6595b47a-9054-4a7b-9a8e-5bbdd6a95cd5', '2020-06-18 17:51:05.718', 'System', false, '2020-06-18 17:51:05.718', 'System', 'Creer un parametre', 'SETTINGS_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('4e034732-9e54-48d1-a59b-885100833b5e', '2020-06-18 17:51:05.719', 'System', false, '2020-06-18 17:51:05.719', 'System', 'Mettre a jour un parametre', 'SETTINGS_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('09f2f66b-95dc-412c-924f-38e7606dbca1', '2020-06-18 17:51:05.72', 'System', false, '2020-06-18 17:51:05.72', 'System', 'Afficher un parametre', 'SETTINGS_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('9e0ac787-4b9b-40e9-876c-d177a2af4591', '2020-06-18 17:51:05.72', 'System', false, '2020-06-18 17:51:05.72', 'System', 'Supprimer un parametre', 'SETTINGS_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('32e9c58d-1f55-4950-b066-013e236f2750', '2020-06-18 17:51:05.73', 'System', false, '2020-06-18 17:51:05.73', 'System', 'Creer une tag', 'TAG_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('4793a041-6f45-429b-8d5c-bc0275cdbfd9', '2020-06-18 17:51:05.731', 'System', false, '2020-06-18 17:51:05.731', 'System', 'Mettre a jour une tag', 'TAG_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('a9bd07dd-04c9-4a60-8cd9-5c526434fea7', '2020-06-18 17:51:05.731', 'System', false, '2020-06-18 17:51:05.731', 'System', 'Afficher une tag', 'TAG_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('d5f27327-10b9-4e4f-aed5-21446cf0c4a4', '2020-06-18 17:51:05.731', 'System', false, '2020-06-18 17:51:05.731', 'System', 'Supprimer une tag', 'TAG_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('c2ecce90-7126-4480-a622-7fac21d76dea', '2020-06-18 17:51:05.742', 'System', false, '2020-06-18 17:51:05.742', 'System', 'Creer un utilisateur', 'USER_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('00e98dc2-4538-4004-a449-c50cc76edfcc', '2020-06-18 17:51:05.743', 'System', false, '2020-06-18 17:51:05.743', 'System', 'Mettre a jour un utilisateur', 'USER_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('ef091636-6b94-4a9f-ac8d-c2ed3cfb7ee8', '2020-06-18 17:51:05.743', 'System', false, '2020-06-18 17:51:05.743', 'System', 'Afficher un utilisateur', 'USER_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('39f5bf32-4f5b-4014-9647-e43ff9564e16', '2020-06-18 17:51:05.744', 'System', false, '2020-06-18 17:51:05.744', 'System', 'Supprimer un utilisateur', 'USER_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('2fb73996-061c-45a7-88d7-71b1dcb1d12b', '2020-06-18 17:51:05.754', 'System', false, '2020-06-18 17:51:05.754', 'System', 'Creer un groupe', 'GROUP_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('9d027bbc-6a5a-4689-8cec-b0aa8e658921', '2020-06-18 17:51:05.754', 'System', false, '2020-06-18 17:51:05.754', 'System', 'Mettre a jour un groupe', 'GROUP_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('ba83b693-f26a-4648-9443-3bd80744263c', '2020-06-18 17:51:05.755', 'System', false, '2020-06-18 17:51:05.755', 'System', 'Afficher un group', 'GROUP_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('4756ef43-2488-4d05-b87e-c3ab80aadf39', '2020-06-18 17:51:05.755', 'System', false, '2020-06-18 17:51:05.755', 'System', 'Supprimer un group', 'GROUP_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('cea8a0c2-eb32-4159-b3dd-a1afcd2252aa', '2020-06-18 17:51:05.765', 'System', false, '2020-06-18 17:51:05.765', 'System', 'Creer un point d''interet', 'ENTITY_ELEMENT_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('a87f373d-43d9-4e47-8030-2572fb3937e2', '2020-06-18 17:51:05.766', 'System', false, '2020-06-18 17:51:05.766', 'System', 'Mettre a jour un point d''interet', 'ENTITY_ELEMENT_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('6682ea51-38c8-4657-8f29-610379934068', '2020-06-18 17:51:05.766', 'System', false, '2020-06-18 17:51:05.766', 'System', 'Afficher un point d''interet', 'ENTITY_ELEMENT_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('4bb980c0-ebae-483d-95c8-3614b710cf86', '2020-06-18 17:51:05.767', 'System', false, '2020-06-18 17:51:05.767', 'System', 'Supprimer un point d''interet', 'ENTITY_ELEMENT_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('5310e96f-d088-48c9-8916-a9e2ea96d7d7', '2020-06-18 17:51:05.767', 'System', false, '2020-06-18 17:51:05.767', 'System', 'Exporter un point d''interet', 'ENTITY_ELEMENT_EXPORT_AUTHORITY');
INSERT INTO sig.permissions VALUES ('9e662277-2032-4d8c-bcae-600837a19087', '2020-06-18 17:51:05.767', 'System', false, '2020-06-18 17:51:05.767', 'System', 'Importer un point d''interet', 'ENTITY_ELEMENT_IMPORT_AUTHORITY');
INSERT INTO sig.permissions VALUES ('d5487c1c-c693-4ee9-9cfc-1e9ad9df11e0', '2020-06-18 17:51:05.777', 'System', false, '2020-06-18 17:51:05.777', 'System', 'Mettre un fichier sur le serveur', 'FILE_UPLOAD_AUTHORITY');
INSERT INTO sig.permissions VALUES ('e7a8261f-21cf-44f1-a749-fec940f3ff44', '2020-06-18 17:51:05.778', 'System', false, '2020-06-18 17:51:05.778', 'System', 'Telecharge un fichier depuis le serveur', 'FILE_LOAD_AUTHORITY');
INSERT INTO sig.permissions VALUES ('3a0b3163-5b1d-4843-986b-555de5192af4', '2020-06-25 14:19:28.444', 'System', false, '2020-06-25 14:19:28.444', 'System', 'Supprimer un fichier', 'FILE_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('4c24d81f-9e2f-4b98-b0a5-d429be7371b5', '2020-06-18 17:51:05.783', 'System', false, '2020-06-18 17:51:05.783', 'System', 'Afficher les sessions ouverts', 'SESSION_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('99e71e8d-c381-43c8-b774-ae5fb6dab7a3', '2020-06-18 17:51:05.784', 'System', false, '2020-06-18 17:51:05.784', 'System', 'supprimer une session ouvert', 'SESSION_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('9962b620-b9d6-4b9b-a48b-d240d8e5c20c', '2020-06-18 17:51:05.791', 'System', false, '2020-06-18 17:51:05.791', 'System', 'Creer une map', 'MAP_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('93aae8d3-d92e-45f9-ae38-e8219b47d165', '2020-06-18 17:51:05.792', 'System', false, '2020-06-18 17:51:05.792', 'System', 'Mettre a jour une map', 'MAP_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('402f25bb-c747-48fd-b74b-d9fed39e4308', '2020-06-18 17:51:05.792', 'System', false, '2020-06-18 17:51:05.792', 'System', 'Afficher une map', 'MAP_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('2228ddf9-abe0-454b-abe8-e472e8b61ded', '2020-06-18 17:51:05.792', 'System', false, '2020-06-18 17:51:05.792', 'System', 'Supprimer une map', 'MAP_DELETE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('ba463214-7d12-46e2-b085-6e748d1f176f', '2020-06-25 14:26:40.281', 'System', false, '2020-06-25 14:26:40.281', 'System', 'Creer une permission', 'PERMISSIONS_CREATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('52dbd7e9-0e79-477c-8df5-7a504859d651', '2020-06-25 14:26:59.17', 'System', false, '2020-06-25 14:26:59.17', 'System', 'Mettre a jour une permission', 'PERMISSIONS_UPDATE_AUTHORITY');
INSERT INTO sig.permissions VALUES ('e5b749cb-9f67-4eb6-b8ee-6d331c883d64', '2020-06-25 14:25:43.101', 'System', false, '2020-06-25 14:25:43.101', 'System', 'Afficher une permission', 'PERMISSIONS_READ_AUTHORITY');
INSERT INTO sig.permissions VALUES ('eaad3892-4fef-4d4f-a612-52fd33af55b2', '2020-06-25 14:24:54.197', 'System', false, '2020-06-25 14:24:54.197', 'System', 'Supprimer une permission', 'PERMISSIONS_DELETE_AUTHORITY');

--
-- Data for Name: roles; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.roles VALUES ('76385517-e1dd-4085-8680-d4efd1b7c688', '2020-06-18 17:51:05.816', 'System', false,'2020-06-18 17:51:05.816', 'System', 'Module Admin', true, 'ROLE_ADMIN');
INSERT INTO sig.roles VALUES ('8c3774af-75e6-4ac3-87c9-ed8128c90e76', '2020-06-25 11:26:36.757', 'System', false, '2020-06-25 11:26:36.757', 'System', 'Module Resource', true, 'ROLE_RESOURCE_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('25895d3a-11e1-4075-979a-1ae93ab3a704', '2020-06-25 11:27:20.544', 'System', false, '2020-06-25 11:27:20.544', 'System', 'Module Resource', true, 'ROLE_RESOURCE_WRITE');
INSERT INTO sig.roles VALUES ('e66f8b17-36ac-433b-8dad-5847f60edc1b', '2020-06-25 11:27:41.212', 'System', false, '2020-06-25 11:27:41.212', 'System', 'Module Resource', true, 'ROLE_RESOURCE_READ');
INSERT INTO sig.roles VALUES ('8cb4e534-fede-4ece-9a30-183be460db7c', '2020-06-25 11:30:54.273', 'System', false, '2020-06-25 11:30:54.273', 'System', 'Module Settings', true, 'ROLE_SETTINGS_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('142841f2-bbb0-415f-bb68-6d066d0bbcbf', '2020-06-25 11:31:19.987', 'System', false, '2020-06-25 11:31:19.987', 'System', 'Module Settings', true, 'ROLE_SETTINGS_WRITE');
INSERT INTO sig.roles VALUES ('9954cceb-70ad-47a0-a53d-e2868555ef8f', '2020-06-25 11:31:31.354', 'System', false, '2020-06-25 11:31:31.354', 'System', 'Module Settings', true, 'ROLE_SETTINGS_READ');
INSERT INTO sig.roles VALUES ('ef519da5-4bf4-4631-9850-770de7f4fef5', '2020-06-25 11:35:51.342', 'System', false, '2020-06-25 11:35:51.342', 'System', 'Module User', true, 'ROLE_USER_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('2629c978-ff6e-4773-8074-4f2885c9a2f7', '2020-06-25 11:36:12.359', 'System', false, '2020-06-25 11:36:12.359', 'System', 'Module User', true, 'ROLE_USER_WRITE');
INSERT INTO sig.roles VALUES ('c030bc08-c435-4761-858a-b3769eb5f19a', '2020-06-25 11:36:47.294', 'System', false, '2020-06-25 11:36:47.294', 'System', 'Module User', true, 'ROLE_USER_READ');
INSERT INTO sig.roles VALUES ('aa564dd7-e430-42ab-9ae9-c5f749c382a2', '2020-06-25 11:38:43.609', 'System', false, '2020-06-25 11:38:43.609', 'System', 'Module Group', true, 'ROLE_GROUP_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('3cceaf3d-634c-48af-bdde-2faafdbe87b5', '2020-06-25 11:38:58.428', 'System', false, '2020-06-25 11:38:58.428', 'System', 'Module Group', true, 'ROLE_GROUP_WRITE');
INSERT INTO sig.roles VALUES ('58c52f5d-8779-4c5f-9d32-be2d1d176467', '2020-06-25 11:39:11.511', 'System', false, '2020-06-25 11:39:11.511', 'System', 'Module Group', true, 'ROLE_GROUP_READ');
INSERT INTO sig.roles VALUES ('511e8e76-511e-41b3-bf02-5d705e07cb18', '2020-06-25 11:42:32.895', 'System', false, '2020-06-25 11:42:32.895', 'System', 'Module EntityElement', true, 'ROLE_ENTITY_ELEMENT_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('c0db09de-99b3-4dd8-b020-d438aa7589e9', '2020-06-25 11:42:58.924', 'System', false, '2020-06-25 11:42:58.924', 'System', 'Module EntityElement', true, 'ROLE_ENTITY_ELEMENT_WRITE');
INSERT INTO sig.roles VALUES ('a0bd2485-f881-4608-a9df-18218069b84e', '2020-06-25 11:43:19.584', 'System', false, '2020-06-25 11:43:19.584', 'System', 'Module EntityElement', true, 'ROLE_ENTITY_ELEMENT_READ');
INSERT INTO sig.roles VALUES ('67567848-c2c0-4874-b66d-630c800ddb86', '2020-06-25 14:22:01.036', 'System', false, '2020-06-25 14:22:01.036', 'System', 'Module File', true, 'ROLE_FILE_ALL_PRIVILAGE');
INSERT INTO sig.roles VALUES ('d95c7c60-d2c5-41bd-9402-6922196a231b', '2020-06-25 11:48:24.102', 'System', false, '2020-06-25 11:48:24.102', 'System', 'Module File', true, 'ROLE_FILE_WRITE');
INSERT INTO sig.roles VALUES ('e0eedba8-675d-4f4b-ac01-62b573c3399b', '2020-06-25 11:48:46.849', 'System', false, '2020-06-25 11:48:46.849', 'System', 'Module File', true, 'ROLE_FILE_READ');
INSERT INTO sig.roles VALUES ('fc01092c-cd15-4e3e-8959-01b9d5f1caaa', '2020-06-25 11:50:52.197', 'System', false, '2020-06-25 11:50:52.197', 'System', 'Module Session', true, 'ROLE_SESSION_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('c5ce63af-887b-4a47-91d5-cba451fd804c', '2020-06-25 11:51:10.068', 'System', false, '2020-06-25 11:51:10.068', 'System', 'Module Session', true, 'ROLE_SESSION_READ');
INSERT INTO sig.roles VALUES ('5ce9ed32-c81d-4f09-8de4-de7322612552', '2020-06-25 11:52:02.687', 'System', false, '2020-06-25 11:52:52.687', 'System', 'Module Map', true, 'ROLE_MAP_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('331de3c7-3148-475f-860b-5a3eb689b30e', '2020-06-25 11:53:08.702', 'System', false, '2020-06-25 11:53:08.702', 'System', 'Module Map', true, 'ROLE_MAP_WRITE');
INSERT INTO sig.roles VALUES ('a4113f78-5e87-40a7-938f-ea4bddc9eb31', '2020-06-25 11:53:21.315', 'System', false, '2020-06-25 11:53:21.315', 'System', 'Module Map', true, 'ROLE_MAP_READ');
INSERT INTO sig.roles VALUES ('b0acfee0-7dc9-44c2-a031-f87c91084fec', '2020-06-25 11:00:36.968', 'System', false, '2020-06-25 11:07:26.968', 'System', 'Module Layer', true, 'ROLE_LAYER_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('57f956db-8815-4052-a1a0-c4d5bffb185d', '2020-06-25 11:02:18.412', 'System', false, '2020-06-25 11:07:48.412', 'System', 'Module Layer', true, 'ROLE_LAYER_WRITE');
INSERT INTO sig.roles VALUES ('210783a3-2b0b-457d-afa1-4b094792b581', '2020-06-25 11:03:39.353', 'System', false, '2020-06-25 11:07:59.353', 'System', 'Module Layer', true, 'ROLE_LAYER_READ');
INSERT INTO sig.roles VALUES ('a2da7577-77a8-4d07-9032-86f91d00695a', '2020-06-25 11:19:04.636', 'System', false, '2020-06-25 11:19:04.636', 'System', 'Module Field', true, 'ROLE_FIELD_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('9e20ca4c-e487-4810-aff8-e0d76190b866', '2020-06-25 11:20:40.718', 'System', false, '2020-06-25 11:20:40.718', 'System', 'Module Field', true, 'ROLE_FIELD_WRITE');
INSERT INTO sig.roles VALUES ('9bfbf08c-5d60-4135-987a-2abb590c36d0', '2020-06-25 11:20:56.005', 'System', false, '2020-06-25 11:20:56.005', 'System', 'Module Field', true, 'ROLE_FIELD_READ');
INSERT INTO sig.roles VALUES ('4905afd8-3f09-43a2-a54f-02f7de9a699a', '2020-06-25 11:33:41.15', 'System', false, '2020-06-25 11:33:41.15', 'System', 'Module Tag', true, 'ROLE_TAG_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('893b13a7-4e7a-4563-b942-c9de0592b7e3', '2020-06-25 11:33:55.189', 'System', false, '2020-06-25 11:33:55.189', 'System', 'Module Tag', true, 'ROLE_TAG_WRITE');
INSERT INTO sig.roles VALUES ('d2d59a6d-4862-45ed-9a0e-99e7684fd17c', '2020-06-25 11:34:06.424', 'System', false, '2020-06-25 11:34:06.424', 'System', 'Module Tag', true, 'ROLE_TAG_READ');
INSERT INTO sig.roles VALUES ('bf09d78a-f6ed-4598-a912-53fcbc9f1e06', '2020-06-25 14:30:26.823', 'System', false, '2020-06-25 14:30:26.823', 'System', 'Module Permissions', true, 'ROLE_PERMISSIONS_ALL_PRIVILEGE');
INSERT INTO sig.roles VALUES ('464ec4b3-f83c-46f7-949e-a241d9118880', '2020-06-25 14:30:48.592', 'System', false, '2020-06-25 14:30:48.592', 'System', 'Module Permissions', true, 'ROLE_PERMISSIONS_WRITE');
INSERT INTO sig.roles VALUES ('21dbd3b9-fce4-4b21-a455-7582e083c819', '2020-06-25 14:30:59.027', 'System', false, '2020-06-25 14:30:59.027', 'System', 'Module Permissions', true, 'ROLE_PERMISSIONS_READ');


--
-- Data for Name: roles_permissions; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.roles_permissions VALUES ('210783a3-2b0b-457d-afa1-4b094792b581', 'a7a902e2-01b0-4557-a823-968da0e4142a');
INSERT INTO sig.roles_permissions VALUES ('57f956db-8815-4052-a1a0-c4d5bffb185d', 'a7a902e2-01b0-4557-a823-968da0e4142a');
INSERT INTO sig.roles_permissions VALUES ('57f956db-8815-4052-a1a0-c4d5bffb185d', '50efc4d1-a498-438e-be1e-c5022aa238f4');
INSERT INTO sig.roles_permissions VALUES ('57f956db-8815-4052-a1a0-c4d5bffb185d', 'dbe7aab1-8f33-48cc-b72f-e644a9d59403');

INSERT INTO sig.roles_permissions VALUES ('b0acfee0-7dc9-44c2-a031-f87c91084fec', 'a7a902e2-01b0-4557-a823-968da0e4142a');
INSERT INTO sig.roles_permissions VALUES ('b0acfee0-7dc9-44c2-a031-f87c91084fec', '50efc4d1-a498-438e-be1e-c5022aa238f4');
INSERT INTO sig.roles_permissions VALUES ('b0acfee0-7dc9-44c2-a031-f87c91084fec', 'dbe7aab1-8f33-48cc-b72f-e644a9d59403');
INSERT INTO sig.roles_permissions VALUES ('b0acfee0-7dc9-44c2-a031-f87c91084fec', 'c16aa318-23a0-4c77-ab26-6d214a02edfd');

INSERT INTO sig.roles_permissions VALUES ('a2da7577-77a8-4d07-9032-86f91d00695a', '3ee68f1f-1819-449f-b2f5-993b5c585953');
INSERT INTO sig.roles_permissions VALUES ('a2da7577-77a8-4d07-9032-86f91d00695a', 'e662dc14-04b5-4bd2-b3bb-5509c450e3c7');
INSERT INTO sig.roles_permissions VALUES ('a2da7577-77a8-4d07-9032-86f91d00695a', 'f28dba6a-01b5-44b4-aead-9c498a928ac9');
INSERT INTO sig.roles_permissions VALUES ('a2da7577-77a8-4d07-9032-86f91d00695a', '0618f76b-706c-4ba1-957b-255c5ee94136');
INSERT INTO sig.roles_permissions VALUES ('9e20ca4c-e487-4810-aff8-e0d76190b866', '3ee68f1f-1819-449f-b2f5-993b5c585953');
INSERT INTO sig.roles_permissions VALUES ('9e20ca4c-e487-4810-aff8-e0d76190b866', 'e662dc14-04b5-4bd2-b3bb-5509c450e3c7');
INSERT INTO sig.roles_permissions VALUES ('9e20ca4c-e487-4810-aff8-e0d76190b866', '0618f76b-706c-4ba1-957b-255c5ee94136');
INSERT INTO sig.roles_permissions VALUES ('9bfbf08c-5d60-4135-987a-2abb590c36d0', '0618f76b-706c-4ba1-957b-255c5ee94136');
INSERT INTO sig.roles_permissions VALUES ('8c3774af-75e6-4ac3-87c9-ed8128c90e76', '85d575d5-b520-4200-8220-f2df16357997');
INSERT INTO sig.roles_permissions VALUES ('8c3774af-75e6-4ac3-87c9-ed8128c90e76', '232a90df-87ab-445e-8c49-5e5b97164062');
INSERT INTO sig.roles_permissions VALUES ('8c3774af-75e6-4ac3-87c9-ed8128c90e76', '804febf4-072d-4c44-9cd1-04e91204fa8f');
INSERT INTO sig.roles_permissions VALUES ('8c3774af-75e6-4ac3-87c9-ed8128c90e76', 'e24cdbb1-4f21-4b4a-84e7-ccb62d7205f4');
INSERT INTO sig.roles_permissions VALUES ('8c3774af-75e6-4ac3-87c9-ed8128c90e76', 'd6658abd-f714-471d-93e5-17f03155e9e9');
INSERT INTO sig.roles_permissions VALUES ('25895d3a-11e1-4075-979a-1ae93ab3a704', '85d575d5-b520-4200-8220-f2df16357997');
INSERT INTO sig.roles_permissions VALUES ('25895d3a-11e1-4075-979a-1ae93ab3a704', '232a90df-87ab-445e-8c49-5e5b97164062');
INSERT INTO sig.roles_permissions VALUES ('25895d3a-11e1-4075-979a-1ae93ab3a704', 'e24cdbb1-4f21-4b4a-84e7-ccb62d7205f4');
INSERT INTO sig.roles_permissions VALUES ('25895d3a-11e1-4075-979a-1ae93ab3a704', 'd6658abd-f714-471d-93e5-17f03155e9e9');
INSERT INTO sig.roles_permissions VALUES ('e66f8b17-36ac-433b-8dad-5847f60edc1b', 'e24cdbb1-4f21-4b4a-84e7-ccb62d7205f4');
INSERT INTO sig.roles_permissions VALUES ('8cb4e534-fede-4ece-9a30-183be460db7c', '6595b47a-9054-4a7b-9a8e-5bbdd6a95cd5');
INSERT INTO sig.roles_permissions VALUES ('8cb4e534-fede-4ece-9a30-183be460db7c', '4e034732-9e54-48d1-a59b-885100833b5e');
INSERT INTO sig.roles_permissions VALUES ('8cb4e534-fede-4ece-9a30-183be460db7c', '9e0ac787-4b9b-40e9-876c-d177a2af4591');
INSERT INTO sig.roles_permissions VALUES ('8cb4e534-fede-4ece-9a30-183be460db7c', '09f2f66b-95dc-412c-924f-38e7606dbca1');
INSERT INTO sig.roles_permissions VALUES ('142841f2-bbb0-415f-bb68-6d066d0bbcbf', '6595b47a-9054-4a7b-9a8e-5bbdd6a95cd5');
INSERT INTO sig.roles_permissions VALUES ('142841f2-bbb0-415f-bb68-6d066d0bbcbf', '4e034732-9e54-48d1-a59b-885100833b5e');
INSERT INTO sig.roles_permissions VALUES ('142841f2-bbb0-415f-bb68-6d066d0bbcbf', '09f2f66b-95dc-412c-924f-38e7606dbca1');
INSERT INTO sig.roles_permissions VALUES ('9954cceb-70ad-47a0-a53d-e2868555ef8f', '09f2f66b-95dc-412c-924f-38e7606dbca1');
INSERT INTO sig.roles_permissions VALUES ('4905afd8-3f09-43a2-a54f-02f7de9a699a', '32e9c58d-1f55-4950-b066-013e236f2750');
INSERT INTO sig.roles_permissions VALUES ('4905afd8-3f09-43a2-a54f-02f7de9a699a', '4793a041-6f45-429b-8d5c-bc0275cdbfd9');
INSERT INTO sig.roles_permissions VALUES ('4905afd8-3f09-43a2-a54f-02f7de9a699a', 'a9bd07dd-04c9-4a60-8cd9-5c526434fea7');
INSERT INTO sig.roles_permissions VALUES ('4905afd8-3f09-43a2-a54f-02f7de9a699a', 'd5f27327-10b9-4e4f-aed5-21446cf0c4a4');
INSERT INTO sig.roles_permissions VALUES ('893b13a7-4e7a-4563-b942-c9de0592b7e3', '32e9c58d-1f55-4950-b066-013e236f2750');
INSERT INTO sig.roles_permissions VALUES ('893b13a7-4e7a-4563-b942-c9de0592b7e3', '4793a041-6f45-429b-8d5c-bc0275cdbfd9');
INSERT INTO sig.roles_permissions VALUES ('893b13a7-4e7a-4563-b942-c9de0592b7e3', 'a9bd07dd-04c9-4a60-8cd9-5c526434fea7');
INSERT INTO sig.roles_permissions VALUES ('d2d59a6d-4862-45ed-9a0e-99e7684fd17c', 'a9bd07dd-04c9-4a60-8cd9-5c526434fea7');
INSERT INTO sig.roles_permissions VALUES ('ef519da5-4bf4-4631-9850-770de7f4fef5', 'c2ecce90-7126-4480-a622-7fac21d76dea');
INSERT INTO sig.roles_permissions VALUES ('ef519da5-4bf4-4631-9850-770de7f4fef5', '00e98dc2-4538-4004-a449-c50cc76edfcc');
INSERT INTO sig.roles_permissions VALUES ('ef519da5-4bf4-4631-9850-770de7f4fef5', 'ef091636-6b94-4a9f-ac8d-c2ed3cfb7ee8');
INSERT INTO sig.roles_permissions VALUES ('ef519da5-4bf4-4631-9850-770de7f4fef5', '39f5bf32-4f5b-4014-9647-e43ff9564e16');
INSERT INTO sig.roles_permissions VALUES ('2629c978-ff6e-4773-8074-4f2885c9a2f7', 'c2ecce90-7126-4480-a622-7fac21d76dea');
INSERT INTO sig.roles_permissions VALUES ('2629c978-ff6e-4773-8074-4f2885c9a2f7', '00e98dc2-4538-4004-a449-c50cc76edfcc');
INSERT INTO sig.roles_permissions VALUES ('2629c978-ff6e-4773-8074-4f2885c9a2f7', 'ef091636-6b94-4a9f-ac8d-c2ed3cfb7ee8');
INSERT INTO sig.roles_permissions VALUES ('c030bc08-c435-4761-858a-b3769eb5f19a', 'ef091636-6b94-4a9f-ac8d-c2ed3cfb7ee8');
INSERT INTO sig.roles_permissions VALUES ('aa564dd7-e430-42ab-9ae9-c5f749c382a2', '2fb73996-061c-45a7-88d7-71b1dcb1d12b');
INSERT INTO sig.roles_permissions VALUES ('aa564dd7-e430-42ab-9ae9-c5f749c382a2', '9d027bbc-6a5a-4689-8cec-b0aa8e658921');
INSERT INTO sig.roles_permissions VALUES ('aa564dd7-e430-42ab-9ae9-c5f749c382a2', 'ba83b693-f26a-4648-9443-3bd80744263c');
INSERT INTO sig.roles_permissions VALUES ('aa564dd7-e430-42ab-9ae9-c5f749c382a2', '4756ef43-2488-4d05-b87e-c3ab80aadf39');
INSERT INTO sig.roles_permissions VALUES ('3cceaf3d-634c-48af-bdde-2faafdbe87b5', '2fb73996-061c-45a7-88d7-71b1dcb1d12b');
INSERT INTO sig.roles_permissions VALUES ('3cceaf3d-634c-48af-bdde-2faafdbe87b5', '9d027bbc-6a5a-4689-8cec-b0aa8e658921');
INSERT INTO sig.roles_permissions VALUES ('3cceaf3d-634c-48af-bdde-2faafdbe87b5', 'ba83b693-f26a-4648-9443-3bd80744263c');
INSERT INTO sig.roles_permissions VALUES ('58c52f5d-8779-4c5f-9d32-be2d1d176467', 'ba83b693-f26a-4648-9443-3bd80744263c');
INSERT INTO sig.roles_permissions VALUES ('511e8e76-511e-41b3-bf02-5d705e07cb18', 'cea8a0c2-eb32-4159-b3dd-a1afcd2252aa');
INSERT INTO sig.roles_permissions VALUES ('511e8e76-511e-41b3-bf02-5d705e07cb18', 'a87f373d-43d9-4e47-8030-2572fb3937e2');
INSERT INTO sig.roles_permissions VALUES ('511e8e76-511e-41b3-bf02-5d705e07cb18', '6682ea51-38c8-4657-8f29-610379934068');
INSERT INTO sig.roles_permissions VALUES ('511e8e76-511e-41b3-bf02-5d705e07cb18', '4bb980c0-ebae-483d-95c8-3614b710cf86');
INSERT INTO sig.roles_permissions VALUES ('511e8e76-511e-41b3-bf02-5d705e07cb18', '5310e96f-d088-48c9-8916-a9e2ea96d7d7');
INSERT INTO sig.roles_permissions VALUES ('511e8e76-511e-41b3-bf02-5d705e07cb18', '9e662277-2032-4d8c-bcae-600837a19087');
INSERT INTO sig.roles_permissions VALUES ('c0db09de-99b3-4dd8-b020-d438aa7589e9', 'cea8a0c2-eb32-4159-b3dd-a1afcd2252aa');
INSERT INTO sig.roles_permissions VALUES ('c0db09de-99b3-4dd8-b020-d438aa7589e9', 'a87f373d-43d9-4e47-8030-2572fb3937e2');
INSERT INTO sig.roles_permissions VALUES ('c0db09de-99b3-4dd8-b020-d438aa7589e9', '6682ea51-38c8-4657-8f29-610379934068');
INSERT INTO sig.roles_permissions VALUES ('c0db09de-99b3-4dd8-b020-d438aa7589e9', '5310e96f-d088-48c9-8916-a9e2ea96d7d7');
INSERT INTO sig.roles_permissions VALUES ('c0db09de-99b3-4dd8-b020-d438aa7589e9', '9e662277-2032-4d8c-bcae-600837a19087');
INSERT INTO sig.roles_permissions VALUES ('a0bd2485-f881-4608-a9df-18218069b84e', '6682ea51-38c8-4657-8f29-610379934068');
INSERT INTO sig.roles_permissions VALUES ('d95c7c60-d2c5-41bd-9402-6922196a231b', 'd5487c1c-c693-4ee9-9cfc-1e9ad9df11e0');
INSERT INTO sig.roles_permissions VALUES ('d95c7c60-d2c5-41bd-9402-6922196a231b', 'e7a8261f-21cf-44f1-a749-fec940f3ff44');
INSERT INTO sig.roles_permissions VALUES ('e0eedba8-675d-4f4b-ac01-62b573c3399b', 'e7a8261f-21cf-44f1-a749-fec940f3ff44');
INSERT INTO sig.roles_permissions VALUES ('fc01092c-cd15-4e3e-8959-01b9d5f1caaa', '4c24d81f-9e2f-4b98-b0a5-d429be7371b5');
INSERT INTO sig.roles_permissions VALUES ('fc01092c-cd15-4e3e-8959-01b9d5f1caaa', '99e71e8d-c381-43c8-b774-ae5fb6dab7a3');
INSERT INTO sig.roles_permissions VALUES ('c5ce63af-887b-4a47-91d5-cba451fd804c', '4c24d81f-9e2f-4b98-b0a5-d429be7371b5');
INSERT INTO sig.roles_permissions VALUES ('5ce9ed32-c81d-4f09-8de4-de7322612552', '9962b620-b9d6-4b9b-a48b-d240d8e5c20c');
INSERT INTO sig.roles_permissions VALUES ('5ce9ed32-c81d-4f09-8de4-de7322612552', '93aae8d3-d92e-45f9-ae38-e8219b47d165');
INSERT INTO sig.roles_permissions VALUES ('5ce9ed32-c81d-4f09-8de4-de7322612552', '402f25bb-c747-48fd-b74b-d9fed39e4308');
INSERT INTO sig.roles_permissions VALUES ('5ce9ed32-c81d-4f09-8de4-de7322612552', '2228ddf9-abe0-454b-abe8-e472e8b61ded');
INSERT INTO sig.roles_permissions VALUES ('331de3c7-3148-475f-860b-5a3eb689b30e', '9962b620-b9d6-4b9b-a48b-d240d8e5c20c');
INSERT INTO sig.roles_permissions VALUES ('331de3c7-3148-475f-860b-5a3eb689b30e', '93aae8d3-d92e-45f9-ae38-e8219b47d165');
INSERT INTO sig.roles_permissions VALUES ('331de3c7-3148-475f-860b-5a3eb689b30e', '402f25bb-c747-48fd-b74b-d9fed39e4308');
INSERT INTO sig.roles_permissions VALUES ('a4113f78-5e87-40a7-938f-ea4bddc9eb31', '402f25bb-c747-48fd-b74b-d9fed39e4308');
INSERT INTO sig.roles_permissions VALUES ('67567848-c2c0-4874-b66d-630c800ddb86', 'd5487c1c-c693-4ee9-9cfc-1e9ad9df11e0');
INSERT INTO sig.roles_permissions VALUES ('67567848-c2c0-4874-b66d-630c800ddb86', 'e7a8261f-21cf-44f1-a749-fec940f3ff44');
INSERT INTO sig.roles_permissions VALUES ('67567848-c2c0-4874-b66d-630c800ddb86', '3a0b3163-5b1d-4843-986b-555de5192af4');
INSERT INTO sig.roles_permissions VALUES ('bf09d78a-f6ed-4598-a912-53fcbc9f1e06', 'ba463214-7d12-46e2-b085-6e748d1f176f');
INSERT INTO sig.roles_permissions VALUES ('bf09d78a-f6ed-4598-a912-53fcbc9f1e06', '52dbd7e9-0e79-477c-8df5-7a504859d651');
INSERT INTO sig.roles_permissions VALUES ('bf09d78a-f6ed-4598-a912-53fcbc9f1e06', 'eaad3892-4fef-4d4f-a612-52fd33af55b2');
INSERT INTO sig.roles_permissions VALUES ('bf09d78a-f6ed-4598-a912-53fcbc9f1e06', 'e5b749cb-9f67-4eb6-b8ee-6d331c883d64');
INSERT INTO sig.roles_permissions VALUES ('464ec4b3-f83c-46f7-949e-a241d9118880', 'ba463214-7d12-46e2-b085-6e748d1f176f');
INSERT INTO sig.roles_permissions VALUES ('464ec4b3-f83c-46f7-949e-a241d9118880', '52dbd7e9-0e79-477c-8df5-7a504859d651');
INSERT INTO sig.roles_permissions VALUES ('464ec4b3-f83c-46f7-949e-a241d9118880', 'e5b749cb-9f67-4eb6-b8ee-6d331c883d64');
INSERT INTO sig.roles_permissions VALUES ('21dbd3b9-fce4-4b21-a455-7582e083c819', 'e5b749cb-9f67-4eb6-b8ee-6d331c883d64');

--
-- Data for Name: settings; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.settings VALUES ('18346110-83bb-4f3c-b18c-13df9a5331a7', '2020-06-18 17:51:05.81', 'System', false, '2020-06-18 17:51:05.81', 'System', 'SIG_DEFAULT_DATE_FORMAT', 'GENERAL', 'yyyy-MM-dd HH:mm:ss');
INSERT INTO sig.settings VALUES ('64822f7b-699e-4dea-ad3c-bde54d32dc60', '2020-06-18 17:51:05.81', 'System', false, '2020-06-18 17:51:05.81', 'System', 'SIG_DEFAULT_PATH_FOLDER_TEMP', 'GENERAL', 'C:\Users\A.LAMOUR/sigeadn/temp/');
INSERT INTO sig.settings VALUES ('254a7384-7aa0-4273-92b2-eb6dbb360429', '2020-06-18 17:51:05.811', 'System', false, '2020-06-18 17:51:05.811', 'System', 'SIG_DEFAULT_PATH_FOLDER_IMAGE', 'GENERAL', 'C:\Users\A.LAMOUR/sigeadn/images/');
INSERT INTO sig.settings VALUES ('5a2d0d75-301f-41d1-8567-064cac172cfb', '2020-06-18 17:51:05.811', 'System', false, '2020-06-18 17:51:05.811', 'System', 'SIG_DEFAULT_LANGUAGE', 'GENERAL', 'fr');
INSERT INTO sig.settings VALUES ('af014cf2-9801-4650-854c-f5bd3c70ebc2', '2020-06-18 17:51:05.811', 'System', false, '2020-06-18 17:51:05.811', 'System', 'SERVER_ADDRESS', 'GENERAL', 'http://localhost:8080');


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: sig; Owner: postgres
--

INSERT INTO sig.user_roles VALUES ('3ce7a395-1072-4783-9f17-a6405ad074c1', '76385517-e1dd-4085-8680-d4efd1b7c688');

