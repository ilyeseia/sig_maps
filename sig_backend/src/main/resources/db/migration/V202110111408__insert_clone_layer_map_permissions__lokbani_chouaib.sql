
--------
-------- Author:  Chouaib LOKBANI
-------- Created: 11 October, 2021  à 14:09
--------
--------scripts ::  insert clone [Layer, Map] permission
--------
--

--
INSERT INTO sig.permissions (id, create_date, created_by, deleted, last_modified_date, modified_by, label, name) VALUES ('901a90df-17ab-445a-8f49-5e6b97164062', '2021-10-11 14:08:05.705000', 'System', false, '2021-10-11 14:08:05.705000', 'System', 'Cloner une couche', 'LAYER_CLONE_AUTHORITY') ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions (id, create_date, created_by, deleted, last_modified_date, modified_by, label, name) VALUES ('901a90af-15ab-443a-7f49-5e6b97164052', '2021-10-11 14:08:05.705000', 'System', false, '2021-10-11 14:08:05.705000', 'System', 'Cloner une carte', 'MAP_CLONE_AUTHORITY') ON CONFLICT DO NOTHING;