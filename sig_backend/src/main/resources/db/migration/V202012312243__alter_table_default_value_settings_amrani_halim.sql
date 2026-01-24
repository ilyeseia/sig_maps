

--------
 -------- Author:  AMRANi Halim
-------- Created: 31 decembre. 2020 a 22:40
--------
--------scripts :: set default value for  default_value & enabled
--------


ALTER TABLE sig.settings ALTER COLUMN  default_value  SET DEFAULT false;
ALTER TABLE sig.settings ALTER COLUMN enabled SET DEFAULT true; 

--update old data
UPDATE sig.settings 	SET default_value=false;
UPDATE sig.settings 	SET enabled=true;









