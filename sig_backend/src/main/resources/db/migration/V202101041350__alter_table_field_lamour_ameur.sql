

--------
 -------- Author:  Ameur LAMOUR
-------- Created: 04 janvier. 2021 a 13:50
--------
--------scripts ::  add publique to field TABLE
--------



ALTER TABLE ONLY sig.field ADD IF NOT EXISTS publique boolean DEFAULT true;















