
--------
-------- Author:  Chouaib LOKBANI
-------- Created: 02 Mai, 2021  à 15:48
--------
--------scripts ::  add filter_cloned_from column in user_layer_filter
--------
--

--

ALTER TABLE IF EXISTS sig.user_layer_filter ADD COLUMN  if not exists filter_cloned_from  uuid;