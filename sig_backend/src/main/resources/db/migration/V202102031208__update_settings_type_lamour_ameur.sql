
--------
 -------- Author:  Ameur LAMOUR
-------- Created: 03 fevrier. 2021 a 12:08
--------
--------scripts ::  update settings table 
--------
--

--

INSERT INTO sig.settings_type VALUES ('aa2303de-f71c-4831-9aa4-226a910921fd', '2021-01-03 11:33:40.57', 'System', false, '2021-01-03 11:33:40.57', 'System', 'GENERAL', false,'paramètre générale', true)ON CONFLICT DO NOTHING;

ALTER TABLE sig.settings DROP COLUMN IF EXISTS type;

UPDATE sig.settings SET type_id = 'aa2303de-f71c-4831-9aa4-226a910921fd';

--
-- PostgreSQL database dump complete
--



