
--------
-------- Author:  LOKBANI Chouaib
-------- Created: 07 Juillet, 2021  à 10:58
--------
--------scripts ::  insert auditing permission in Permission table
--------
--

--

INSERT INTO sig.permissions (id, create_date, created_by, deleted, last_modified_date, modified_by, label, name) VALUES ('122a90df-87ac-445e-2c10-5e5b97164222', '2021-07-04 11:00:05.705000', 'System', false, '2021-07-04 11:00:05.705000', 'System', 'Tracabilité', 'AUDITING') ON CONFLICT DO NOTHING;