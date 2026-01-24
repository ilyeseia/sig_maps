
--------
-------- Author:  LOKBANI Chouaib
-------- Created: 13 Semptembre, 2021  à 10:40
--------
--------scripts ::  insert geo-processing permission in Permission table
--------
--

--

INSERT INTO sig.permissions (id, create_date, created_by, deleted, last_modified_date, modified_by, label, name) VALUES ('122a90df-871c-425e-2a10-5e5b87164222', '2021-09-13 10:40:05.705000', 'System', false, '2021-09-13 10:40:05.705000', 'System', 'Géotraitement', 'GEOPROCESSING') ON CONFLICT DO NOTHING;