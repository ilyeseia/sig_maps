
--------
-------- Author:  LOKBANI Chouaib
-------- Created: 04 Juillet. 2021 a 11:21
--------
--------scripts :: drop action_time column in the  user_logged_actions table
--------



ALTER TABLE IF EXISTS sig.user_logged_actions DROP COLUMN IF EXISTS action_time;
