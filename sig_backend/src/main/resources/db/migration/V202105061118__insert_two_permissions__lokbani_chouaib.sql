
--------
-------- Author:  Chouaib LOKBANI
-------- Created: 06 Mai, 2021  à 11:18
--------
--------scripts ::  insert two permission in Permission table
--------
--

--
INSERT INTO sig.permissions (id, create_date, created_by, deleted, last_modified_date, modified_by, label, name) VALUES ('992a90df-87ab-445e-8f49-5e6b97164062', '2021-05-06 11:31:05.705000', 'System', false, '2021-05-06 11:31:05.705000', 'System', 'Parteget un point d''interet', 'ENTITY_ELEMENT_SHARE_AUTHORITY') ON CONFLICT DO NOTHING;;
INSERT INTO sig.permissions (id, create_date, created_by, deleted, last_modified_date, modified_by, label, name) VALUES ('753a90df-87ab-445e-8f48-5e6b97164052', '2021-05-06 11:31:05.805000', 'System', false, '2021-05-06 11:31:05.805000', 'System', 'Exporter des points d''interet', 'ENTITY_ELEMENT_MULTI_EXPORT_AUTHORITY') ON CONFLICT DO NOTHING;;
