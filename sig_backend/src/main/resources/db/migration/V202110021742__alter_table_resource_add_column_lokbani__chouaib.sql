
--------
-------- Author:  LOKBANI Chouaib
-------- Created: 02 October, 2021  à 10:40
--------
--------scripts ::  alter resource table by adding a recursive relationship
--------
--

--

ALTER TABLE sig.resource
    ADD COLUMN IF NOT EXISTS parent_resource_id uuid;

ALTER TABLE sig.resource DROP CONSTRAINT IF EXISTS  fk_resource_resource_id;ALTER TABLE sig.resource DROP CONSTRAINT IF EXISTS  fk_resource_resource_id;
ALTER TABLE sig.resource
    ADD CONSTRAINT fk_resource_resource_id FOREIGN KEY (parent_resource_id)
        REFERENCES sig.resource (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE cascade;