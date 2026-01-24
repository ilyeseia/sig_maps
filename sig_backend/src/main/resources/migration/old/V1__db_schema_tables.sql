
CREATE SCHEMA sig;
CREATE EXTENSION postgis;

--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1
-- Dumped by pg_dump version 12.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;


--
-- Name: entity_element; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.entity_element (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    geom public.geometry,
    properties jsonb,
    layer_entity_element uuid
);


ALTER TABLE sig.entity_element OWNER TO postgres;

--
-- Name: entity_element entity_element_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.entity_element
    ADD CONSTRAINT entity_element_pkey PRIMARY KEY (id);

--------------------------------------------------------------------------------------------------------------------------------------------------

CREATE TABLE sig.field (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    name character varying(255),
    field_order integer,
    required boolean,
    slug character varying(255),
    type character varying(255),
    layer_id uuid,
    resource_id uuid
);


ALTER TABLE sig.field OWNER TO postgres;

--
-- Name: field field_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.field
    ADD CONSTRAINT field_pkey PRIMARY KEY (id);

------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: group; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig."group" (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    description character varying(255),
    name character varying(255)
);


ALTER TABLE sig."group" OWNER TO postgres;

--
-- Name: group group_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig."group"
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);    
    
-------------------------------------------------------------------------------------------------------------------------------------------------------
    
--
-- Name: layer; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.layer (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    custom_icon boolean,
    icon_url character varying(255),
    identifiant character varying(255),
    labeling_enabled boolean,
    name character varying(255),
    layer_order integer,
    slug character varying(255),
    style character varying(255),
    symbology_type character varying(255),
    topo character varying(255),
    type_limit character varying(255),
    view_element_id uuid
);


ALTER TABLE sig.layer OWNER TO postgres;

--
-- Name: layer layer_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.layer
    ADD CONSTRAINT layer_pkey PRIMARY KEY (id);

--
-- Name: layer uk_layer_name; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.layer
    ADD CONSTRAINT uk_layer_name UNIQUE (name);

-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: layer_groups; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.layer_groups (
    layers_id uuid NOT NULL,
    groups_id uuid NOT NULL
);


ALTER TABLE sig.layer_groups OWNER TO postgres;

------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: layer_users; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.layer_users (
    layers_id uuid NOT NULL,
    users_id uuid NOT NULL
);


ALTER TABLE sig.layer_users OWNER TO postgres;

-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: map; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.map (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    image character varying(255),
    name character varying(255),
    slug character varying(255)
);


ALTER TABLE sig.map OWNER TO postgres;

--
-- Name: map map_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.map
    ADD CONSTRAINT map_pkey PRIMARY KEY (id);    

    
-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: map_groups; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.map_groups (
    maps_id uuid NOT NULL,
    groups_id uuid NOT NULL
);


ALTER TABLE sig.map_groups OWNER TO postgres;

 -------------------------------------------------------------------------------------------------------------------------------------------------------
 
 --
-- Name: map_layers; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.map_layers (
    maps_id uuid NOT NULL,
    layers_id uuid NOT NULL
);


ALTER TABLE sig.map_layers OWNER TO postgres;

-------------------------------------------------------------------------------------------------------------------------------------------------------


--
-- Name: map_users; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.map_users (
    maps_id uuid NOT NULL,
    users_id uuid NOT NULL
);


ALTER TABLE sig.map_users OWNER TO postgres;

-------------------------------------------------------------------------------------------------------------------------------------------------------


--
-- Name: permissions; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.permissions (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    label character varying(255),
    name character varying(255)
);


ALTER TABLE sig.permissions OWNER TO postgres;

--
-- Name: permissions permissions_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- Name: permissions uk_permissions_name; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.permissions
    ADD CONSTRAINT uk_permissions_name UNIQUE (name);
    
-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: resource; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.resource (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    code character varying(255),
    name character varying(255),
    resource_values bytea
);


ALTER TABLE sig.resource OWNER TO postgres;

--
-- Name: resource resource_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.resource
    ADD CONSTRAINT resource_pkey PRIMARY KEY (id);


--
-- Name: resource uk_resource_name; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.resource
    ADD CONSTRAINT uk_resource_name UNIQUE (name);
    
-------------------------------------------------------------------------------------------------------------------------------------------------------


--
-- Name: resource_value; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.resource_value (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    value character varying(255),
    resource_id uuid NOT NULL
);


ALTER TABLE sig.resource_value OWNER TO postgres;

--
-- Name: resource_value resource_value_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.resource_value
    ADD CONSTRAINT resource_value_pkey PRIMARY KEY (id);


-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: roles; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.roles (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    label character varying(255),
    module boolean,
    name character varying(255)
);


ALTER TABLE sig.roles OWNER TO postgres;

--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: roles uk_role_name; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.roles
    ADD CONSTRAINT uk_role_name UNIQUE (name);

------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: roles_permissions; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.roles_permissions (
    role_id uuid NOT NULL,
    permissions_id uuid NOT NULL
);


ALTER TABLE sig.roles_permissions OWNER TO postgres;

-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: settings; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.settings (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    code character varying(255),
    type character varying(255),
    value character varying(255)
);


ALTER TABLE sig.settings OWNER TO postgres;

--
-- Name: settings settings_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


--
-- Name: settings uk_settings_code; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.settings
    ADD CONSTRAINT uk_settings_code UNIQUE (code);
    
-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: tag; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.tag (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    message character varying(255),
    name character varying(255),
    type character varying(255)
);


ALTER TABLE sig.tag OWNER TO postgres;

--
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);
    
-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: tag_entity_elements; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.tag_entity_elements (
    tags_id uuid NOT NULL,
    entity_elements_id uuid NOT NULL
);


ALTER TABLE sig.tag_entity_elements OWNER TO postgres;

-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: tag_layers; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.tag_layers (
    tags_id uuid NOT NULL,
    layers_id uuid NOT NULL
);


ALTER TABLE sig.tag_layers OWNER TO postgres;

-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: tag_maps; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.tag_maps (
    tags_id uuid NOT NULL,
    maps_id uuid NOT NULL
);


ALTER TABLE sig.tag_maps OWNER TO postgres;

-------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: user; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig."user" (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    avatar character varying(255),
    email character varying(255),
    enabled boolean,
    password character varying(255),
    user_name character varying(255),
    group_id uuid
);


ALTER TABLE sig."user" OWNER TO postgres;


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: user uk_user_email; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig."user"
    ADD CONSTRAINT uk_user_email UNIQUE (email);


--
-- Name: user uk_user_username; Type: CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig."user"
    ADD CONSTRAINT uk_user_username UNIQUE (user_name);



------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: user_entity_elements; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.user_entity_elements (
    users_id uuid NOT NULL,
    entity_elements_id uuid NOT NULL
);


ALTER TABLE sig.user_entity_elements OWNER TO postgres;


------------------------------------------------------------------------------------------------------------------------------------------------------

--
-- Name: user_roles; Type: TABLE; Schema: sig; Owner: postgres
--

CREATE TABLE sig.user_roles (
    users_id uuid NOT NULL,
    roles_id uuid NOT NULL
);


ALTER TABLE sig.user_roles OWNER TO postgres;


------------------------------------------------FOREIGN KEYS-------------------------------------------------------------------------------

--
-- Name: entity_element fk_layer_entity_element_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.entity_element
    ADD CONSTRAINT fk_layer_entity_element_id FOREIGN KEY (layer_entity_element) REFERENCES sig.layer(id);

-----------------------------------------------------------------------------------------------------------------


--
-- Name: field fk_field_resource; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.field
    ADD CONSTRAINT fk_field_resource FOREIGN KEY (resource_id) REFERENCES sig.resource(id);

--
-- Name: field fk_field_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.field
    ADD CONSTRAINT fk_field_layer_id FOREIGN KEY (layer_id) REFERENCES sig.layer(id);

-----------------------------------------------------------------------------------------------------------------

--
-- Name: layer layer_entity_element_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.layer
    ADD CONSTRAINT fk_layer_entity_element_id FOREIGN KEY (view_element_id) REFERENCES sig.entity_element(id);
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: layer_groups fk_group_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.layer_groups
    ADD CONSTRAINT fk_group_layer_id FOREIGN KEY (groups_id) REFERENCES sig."group"(id);


--
-- Name: layer_groups fk_layer_group_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.layer_groups
    ADD CONSTRAINT fk_layer_group_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);    
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: layer_users fk_layer_user_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.layer_users
    ADD CONSTRAINT fk_layer_user_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);


--
-- Name: layer_users fk_user_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.layer_users
    ADD CONSTRAINT fk_user_layer_id FOREIGN KEY (users_id) REFERENCES sig."user"(id);    
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: map_groups fk_group_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.map_groups
    ADD CONSTRAINT fk_group_map_id FOREIGN KEY (groups_id) REFERENCES sig."group"(id);


--
-- Name: map_groups fk_map_group_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.map_groups
    ADD CONSTRAINT fk_map_group_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);
   
-----------------------------------------------------------------------------------------------------------------

--
-- Name: map_layers fk_layer_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.map_layers
    ADD CONSTRAINT fk_layer_map_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);


--
-- Name: map_layers fk_map_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.map_layers
    ADD CONSTRAINT fk_map_layer_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);
    
-----------------------------------------------------------------------------------------------------------------


--
-- Name: map_users fk_map_user_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.map_users
    ADD CONSTRAINT fk_map_user_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);


--
-- Name: map_users fk_user_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.map_users
    ADD CONSTRAINT fk_user_map_id FOREIGN KEY (users_id) REFERENCES sig."user"(id); 
    
-----------------------------------------------------------------------------------------------------------------
        
--
-- Name: resource_value fk_resource_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.resource_value
    ADD CONSTRAINT fk_resource_id FOREIGN KEY (resource_id) REFERENCES sig.resource(id);

-----------------------------------------------------------------------------------------------------------------

--
-- Name: roles_permissions fk_permission_role_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.roles_permissions
    ADD CONSTRAINT fk_permission_role_id FOREIGN KEY (permissions_id) REFERENCES sig.permissions(id);


--
-- Name: roles_permissions fk_role_permission_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.roles_permissions
    ADD CONSTRAINT fk_role_permission_id FOREIGN KEY (role_id) REFERENCES sig.roles(id);
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: tag_entity_elements fk_entity_element_tag_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.tag_entity_elements
    ADD CONSTRAINT fk_entity_element_tag_id FOREIGN KEY (tags_id) REFERENCES sig.tag(id);
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: tag_layers fk_layer_tag_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.tag_layers
    ADD CONSTRAINT fk_layer_tag_id FOREIGN KEY (tags_id) REFERENCES sig.tag(id);


--
-- Name: tag_layers fk_tag_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.tag_layers
    ADD CONSTRAINT fk_tag_layer_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: tag_maps fk_map_tag_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.tag_maps
    ADD CONSTRAINT fk_map_tag_id FOREIGN KEY (tags_id) REFERENCES sig.tag(id);


--
-- Name: tag_maps fk_tag_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.tag_maps
    ADD CONSTRAINT fk_tag_map_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: user user_group_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig."user"
    ADD CONSTRAINT fk_user_group_id FOREIGN KEY (group_id) REFERENCES sig."group"(id);
    
-----------------------------------------------------------------------------------------------------------------

--
-- Name: user_entity_elements fk_entity_element_user_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.user_entity_elements
    ADD CONSTRAINT fk_entity_element_user_id FOREIGN KEY (users_id) REFERENCES sig."user"(id);


--
-- Name: user_entity_elements fk_user_entity_element_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.user_entity_elements
    ADD CONSTRAINT fk_user_entity_element_id FOREIGN KEY (entity_elements_id) REFERENCES sig.entity_element(id);
	

----------------------------------------------------------------------------------------------------------------


--
-- Name: user_roles fk_user_roles_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.user_roles
    ADD CONSTRAINT fk_roles_user_id FOREIGN KEY (users_id) REFERENCES sig."user"(id);


--
-- Name: user_roles fk_user_roles_id; Type: FK CONSTRAINT; Schema: sig; Owner: postgres
--

ALTER TABLE ONLY sig.user_roles
    ADD CONSTRAINT fk_user_roles_id FOREIGN KEY (roles_id) REFERENCES sig.roles(id);
	
	