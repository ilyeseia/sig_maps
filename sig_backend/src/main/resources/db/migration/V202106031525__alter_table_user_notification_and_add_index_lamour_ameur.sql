

--------
 -------- Author:  Ameur LAMOUR
-------- Created: 03 juin. 2021 a 15:25
--------
--------scripts ::  alter user_notification change the type of message & add the index (object,user,viewed) 
--------



ALTER TABLE IF EXISTS sig.user_notification ALTER COLUMN message TYPE text;

CREATE INDEX if not exists  user_notification_object_index ON sig.user_notification USING btree (object) ;     

CREATE INDEX if not exists  user_notification_viewed_index ON sig.user_notification USING btree (viewed) ;  

CREATE INDEX if not exists  user_notification_user_id_index ON sig.user_notification USING btree (user_id) ;















