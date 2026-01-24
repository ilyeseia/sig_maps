
--------
 -------- Author:  Ameur LAMOUR
-------- Created: 28 janvier. 2021 a 11:30
--------
--------scripts ::  add share and archive permission  to permissions TABLE
--------
--

--

INSERT INTO sig.permissions VALUES ('0a59bcc6-5c8c-4bd6-9d38-ef2f17ca641f', '2021-01-28 11:31:05.632', 'System', false, '2021-01-28 11:31:05.632', 'System', 'Partager une map', 'MAP_SHARE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('ef0c6575-f6c4-4b3a-87d5-6a2d0925aee3', '2021-01-28 11:31:05.634', 'System', false, '2021-01-28 11:31:05.634', 'System', 'Archiver une map', 'MAP_ARCHIVE_AUTHORITY')ON CONFLICT DO NOTHING;
INSERT INTO sig.permissions VALUES ('fffbb684-dc37-4395-8264-ffbb1799db59', '2021-01-28 11:31:05.636', 'System', false, '2021-01-28 11:31:05.636', 'System', 'Partager une couche', 'LAYER_SHARE_AUTHORITY')ON CONFLICT DO NOTHING;



--
-- PostgreSQL database dump complete
--



