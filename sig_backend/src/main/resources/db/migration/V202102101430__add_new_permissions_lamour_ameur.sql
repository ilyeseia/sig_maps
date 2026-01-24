
--------
 -------- Author:  Ameur LAMOUR
-------- Created: 10 FEVRIER. 2021 a 14:31
--------
--------scripts ::  add share and archive permission  to permissions TABLE
--------
--

--

INSERT INTO sig.permissions VALUES ('797da019-af0d-4c82-9b3b-dcc23def19bc', '2021-02-10 14:31:05.632', 'System', false, '2021-02-10 14:31:05.632', 'System', 'Attacher une couche à une map', 'ATTACH_LAYER_MAP_AUTHORITY')ON CONFLICT DO NOTHING;

INSERT INTO sig.permissions VALUES ('b681ca5b-a7ff-4e71-be80-dc625b060261', '2021-02-10 14:31:05.654', 'System', false, '2021-02-10 14:31:05.654', 'System', 'Détacher une couche de map', 'DETACH_LAYER_MAP_AUTHORITY')ON CONFLICT DO NOTHING;

INSERT INTO sig.permissions VALUES ('14e79ea4-b3bc-4bc4-bc01-736b9407743c', '2021-02-10 14:31:05.659', 'System', false, '2021-02-10 14:31:05.659', 'System', 'Configurer le style de couche', 'CONFIGURE_LAYER_STYLE_AUTHORITY')ON CONFLICT DO NOTHING;


--
-- PostgreSQL database dump complete
--



