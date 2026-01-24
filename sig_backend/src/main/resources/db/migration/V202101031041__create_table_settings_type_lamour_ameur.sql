

--------
 -------- Author:  Ameur LAMOUR
-------- Created: 03 01 2020 a 10:41
--------
--------scripts ::  CREATE Table: sig.settings_type
--------



CREATE TABLE IF NOT EXISTS sig.settings_type
(
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255) ,
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    code character varying(255),
    default_value boolean,
    description character varying(255),
    enabled boolean

);

ALTER TABLE ONLY sig.settings_type DROP CONSTRAINT IF EXISTS settings_type_pkey;

ALTER TABLE ONLY sig.settings_type ADD CONSTRAINT settings_type_pkey PRIMARY KEY (id);

ALTER TABLE ONLY sig.settings_type DROP CONSTRAINT IF EXISTS uk_settings_type_code;

ALTER TABLE ONLY sig.settings_type ADD CONSTRAINT uk_settings_type_code UNIQUE (code);














