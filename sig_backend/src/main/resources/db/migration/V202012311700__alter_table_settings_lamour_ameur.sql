

--------
 -------- Author:  Ameur LAMOUR
-------- Created: 31 decembre. 2020 a 17:00
--------
--------scripts ::  add tow fields to settings TABLE
--------



ALTER TABLE ONLY sig.settings ADD IF NOT EXISTS default_value boolean;

ALTER TABLE ONLY sig.settings ADD IF NOT EXISTS enabled boolean;














