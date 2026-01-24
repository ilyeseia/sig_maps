
--------
 -------- Author:  Ameur LAMOUR
-------- Created: 16 FEVRIER. 2021 a 13:50
--------
--------scripts ::  add unique constraint to field table
--------
--

--

ALTER TABLE ONLY sig.field DROP CONSTRAINT IF EXISTS uk_field_slug_layer;
ALTER TABLE ONLY sig.field ADD CONSTRAINT uk_field_slug_layer UNIQUE (slug, layer_id);




--
-- PostgreSQL database dump complete
--

