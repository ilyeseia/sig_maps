
--------
 -------- Author:  Ameur LAMOUR
-------- Created: 28 janvier. 2021 a 11:30
--------
--------scripts ::  add settings type 
--------
--

--

ALTER TABLE sig.settings ADD IF NOT EXISTS type_id UUID;

ALTER TABLE ONLY sig.settings DROP CONSTRAINT IF EXISTS fk_settings_settingstype_id;

ALTER TABLE ONLY sig.settings
    ADD CONSTRAINT fk_settings_settingstype_id FOREIGN KEY (type_id) REFERENCES sig.settings_type(id);



--
-- PostgreSQL database dump complete
--



