
--------
-------- Author:  LOKBANI Chouaib
-------- Created: 02 December, 2021  à 14:23
--------
--------scripts ::  Update sig entities schema definition for the new styling functionality
--------
--

--

--Alter layer table by dropping columns
ALTER TABLE IF EXISTS sig.layer DROP COLUMN IF EXISTS icon_url;
ALTER TABLE IF EXISTS sig.layer DROP COLUMN IF EXISTS custom_icon;
ALTER TABLE IF EXISTS sig.layer DROP COLUMN IF EXISTS labeling_enabled;
ALTER TABLE IF EXISTS sig.layer DROP COLUMN IF EXISTS visible;
ALTER TABLE IF EXISTS sig.layer DROP COLUMN IF EXISTS style;

--Alter map layer table by adding new columns
ALTER TABLE IF EXISTS  sig.map_layers ADD COLUMN IF NOT EXISTS  is_visible boolean;
ALTER TABLE IF EXISTS  sig.map_layers ADD COLUMN IF NOT EXISTS  layer_order integer;
ALTER TABLE IF EXISTS  sig.map_layers ADD COLUMN IF NOT EXISTS  random_id uuid NOT NULL;
ALTER TABLE IF EXISTS  sig.map_layers ADD COLUMN IF NOT EXISTS  map_layer_id uuid NOT NULL;
ALTER TABLE sig.map_layers DROP CONSTRAINT IF EXISTS uk_map_layer;
ALTER TABLE sig.map_layers ADD  CONSTRAINT  uk_map_layer UNIQUE (map_layer_id);
-- Table: sig.style

-- Create TABLE sig.style;
CREATE TABLE IF NOT EXISTS sig.style
(
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255) COLLATE pg_catalog."default",
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255) COLLATE pg_catalog."default",
    display_name character varying(255) COLLATE pg_catalog."default",
    is_default boolean,
    name character varying(255) COLLATE pg_catalog."default",
    style text COLLATE pg_catalog."default",
    symbology_type character varying(255) COLLATE pg_catalog."default",
    layer_map_style uuid,
    theme_style_id uuid,
    CONSTRAINT style_pkey PRIMARY KEY (id)
    );

-- Create TABLE sig.theme;
CREATE TABLE IF NOT EXISTS sig.theme
(
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255) COLLATE pg_catalog."default",
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255) COLLATE pg_catalog."default",
    is_default boolean,
    name character varying(255) COLLATE pg_catalog."default",
    theme_map uuid,
    CONSTRAINT theme_pkey PRIMARY KEY (id)
    );

ALTER TABLE sig.theme DROP CONSTRAINT IF EXISTS fk_theme_map_id;
ALTER TABLE sig.theme
    ADD  CONSTRAINT fk_theme_map_id FOREIGN KEY (theme_map)
        REFERENCES sig.map (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE sig.style DROP CONSTRAINT IF EXISTS  fk_layer_map_style;
ALTER TABLE sig.style
    ADD  CONSTRAINT fk_layer_map_style FOREIGN KEY (layer_map_style)
        REFERENCES sig.map_layers (map_layer_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE sig.style DROP CONSTRAINT IF EXISTS fk_theme_style_id;
ALTER TABLE sig.style
    ADD  CONSTRAINT fk_theme_style_id FOREIGN KEY (theme_style_id)
        REFERENCES sig.theme (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;