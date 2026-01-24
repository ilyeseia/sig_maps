
--------
-------- Author:  Chouaib LOKBANI
-------- Created: 20 Mai, 2021  à 12:21
--------
--------scripts ::  insert reporting permission in Permission table
--------
--

--

INSERT INTO sig.permissions (id, create_date, created_by, deleted, last_modified_date, modified_by, label, name) VALUES ('992a90df-87ab-445e-8c10-5e5b97164262', '2021-05-20 12:16:05.705000', 'System', false, '2021-05-20 12:16:05.705000', 'System', 'Reporting', 'REPORTING') ON CONFLICT DO NOTHING;