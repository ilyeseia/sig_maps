--------
 -------- Author:  Ameur LAMOUR
-------- Created: 17 mai. 2021 a 15:00
--------
--------scripts :: alter user_notification table
--------


ALTER TABLE sig.user_notification ALTER COLUMN link TYPE character varying(255);

ALTER TABLE ONLY sig.user_notification ADD IF NOT EXISTS object character varying(255) ;
ALTER TABLE ONLY sig.user_notification ADD IF NOT EXISTS operation character varying(255) ;
ALTER TABLE ONLY sig.user_notification ADD IF NOT EXISTS level character varying(255) ;

ALTER TABLE ONLY sig.user_notification DROP CONSTRAINT IF EXISTS fk_usernotification_notification_id;
ALTER TABLE ONLY sig.user_notification DROP COLUMN IF EXISTS notification_id;
