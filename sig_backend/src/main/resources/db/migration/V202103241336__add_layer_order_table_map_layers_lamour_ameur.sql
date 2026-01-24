

--------
 -------- Author:  Ameur LAMOUR
-------- Created: 24 mars. 2021 a 13:50
--------
--------scripts ::  add layer_order to map_layers TABLE
--------



ALTER TABLE ONLY sig.map_layers ADD IF NOT EXISTS layer_order int DEFAULT 1;















