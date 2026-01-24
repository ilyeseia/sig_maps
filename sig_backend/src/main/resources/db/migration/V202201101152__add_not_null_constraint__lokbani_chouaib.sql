
--------
-------- Author:  LOKBANI Chouaib
-------- Created: 10 January, 2022  à 11:53
--------
--------scripts ::  Add not null constraint to geom in entity element table
--------
--

--

alter table sig.entity_element alter column geom set not null;
