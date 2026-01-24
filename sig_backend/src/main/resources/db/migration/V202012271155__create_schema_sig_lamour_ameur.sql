

--------
 -------- Author:  Ameur LAMOUR
-------- Created: 27 decembre. 2020 a 11:50
--------
--------scripts ::  drop the roles TABLE and its CONSTRAINT
--------



ALTER TABLE ONLY sig.roles_permissions DROP CONSTRAINT IF EXISTS fk_role_permission_id;

ALTER TABLE ONLY sig.roles_permissions DROP CONSTRAINT IF EXISTS fk_permission_role_id;

ALTER TABLE ONLY sig.roles DROP CONSTRAINT IF EXISTS roles_pkey;

DROP TABLE IF EXISTS sig.roles_permissions;

DROP TABLE IF EXISTS sig.roles;













