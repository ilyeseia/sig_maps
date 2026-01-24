
--------
-------- Author:  Ameur LAMOUR
-------- Created: 21 Juin, 2021  à 11:05
--------
--------scripts ::  add DEFAULT_ADMINISTRATIVE_LIMIT settings
--------
--

--

INSERT INTO sig.settings(id, create_date, created_by, deleted, last_modified_date, modified_by, code, value, default_value, enabled, type_id) VALUES ('c51fb984-fa39-40ee-a8ca-f878a2672380', '2021-06-20 09:32:16.739', 'System', false, '2021-06-21 10:52:29.329', 'System', 'SIG_DEFAULT_ADMINISTRATIVE_LIMIT', 'wilaya', true, true, 'aa2303de-f71c-4831-9aa4-226a910921fd')ON CONFLICT DO NOTHING;