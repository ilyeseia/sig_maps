

--------
 -------- Author:  AMRANI Halim
-------- Created: 14 decembre. 2020 a 11:43  
--------
--------scripts ::  creation des indexs for tables of schemas SIG
--------


----------------------------------------------------------------------------
------------------ ---------------------------------------------------------------------------------------------------
  
 ----------------------------------------------------------------------
-------------------------sch SIG.---------------------------------
-----------------------------------------------------------------------
---Table layer_entity_element
CREATE INDEX if not exists  entity_element_layer_entity_element_index ON sig.entity_element USING btree (layer_entity_element) ;


---Table sig.field
CREATE INDEX if not exists  field_layer_id_index ON sig.field USING btree (layer_id) ;
CREATE INDEX if not exists  field_resource_id_index ON sig.field USING btree (resource_id) ;
CREATE INDEX if not exists  field_slug_index ON sig.field USING btree (slug) ;

---Table sig.layer
CREATE INDEX if not exists  layer_identifiant_index ON sig.layer USING btree (identifiant) ;
CREATE INDEX if not exists  layer_slug_index ON sig.layer USING btree (slug) ;
CREATE INDEX if not exists  layer_topo_index ON sig.layer USING btree (topo) ;

---Table sig.map
CREATE INDEX if not exists  map_name_index ON sig.map USING btree (name) ; 
CREATE INDEX if not exists  map_slug_index ON sig.map USING btree (slug) ; 

---Table sig.user
CREATE INDEX if not exists  user_name_index ON sig.user USING btree (email) ; 
CREATE INDEX if not exists  user_username_index ON sig.user USING btree (user_name) ; 

---Table sig.user_log
CREATE INDEX if not exists  user_log_name_index ON sig.user_log USING btree (username) ;  