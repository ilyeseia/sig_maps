--
-- PostgreSQL database dump
--

-- Dumped from database version 12.1
-- Dumped by pg_dump version 12.0

-- Started on 2020-12-01 09:29:27

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



--
-- TOC entry 7 (class 2615 OID 585380)
-- Name: sig; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA IF NOT EXISTS sig;


--
-- TOC entry 2 (class 3079 OID 584378)
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;;


SET default_table_access_method = heap;


--
-- TOC entry 210 (class 1259 OID 585388)
-- Name: entity_element; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.entity_element (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    geom public.geometry,
    properties jsonb,
    layer_entity_element uuid NOT NULL
);


--
-- TOC entry 211 (class 1259 OID 585396)
-- Name: field; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.field (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    name character varying(255) NOT NULL,
    field_order integer,
    required boolean,
    slug character varying(255),
    type character varying(255) NOT NULL,
    visible boolean,
    layer_id uuid,
    resource_id uuid
);


--
-- TOC entry 212 (class 1259 OID 585404)
-- Name: group; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig."group" (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    description character varying(255),
    label character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);



--
-- TOC entry 215 (class 1259 OID 585418)
-- Name: layer; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.layer (
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
    name character varying(255) NOT NULL,
    layer_order integer,
    slug character varying(255),
    style character varying(255),
    symbology_type character varying(255),
    topo character varying(255),
    type character varying(255),
    type_limit character varying(255),
    visible boolean,
    view_element_id uuid
);


--
-- TOC entry 218 (class 1259 OID 585432)
-- Name: map; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.map (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    image character varying(255),
    name character varying(255) NOT NULL,
    slug character varying(255),
	privacy character varying(255)
);


--
-- TOC entry 222 (class 1259 OID 585449)
-- Name: notification; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.notification (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    template character varying(255),
    layer_id uuid
);


--
-- TOC entry 223 (class 1259 OID 585457)
-- Name: permissions; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.permissions (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    label character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


--
-- TOC entry 224 (class 1259 OID 585465)
-- Name: resource; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.resource (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    code character varying(255) NOT NULL,
    name character varying(255) NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 585473)
-- Name: resource_value; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.resource_value (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    parent_id uuid,
    ref_value uuid,
    value character varying(255) NOT NULL,
    resource_id uuid
);


--
-- TOC entry 226 (class 1259 OID 585481)
-- Name: roles; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.roles (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    label character varying(255) NOT NULL,
    module boolean,
    name character varying(255) NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 585492)
-- Name: settings; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.settings (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    code character varying(255) NOT NULL,
    type character varying(255),
    value character varying(255) NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 585500)
-- Name: tag; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.tag (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    message character varying(255),
    name character varying(255) NOT NULL,
    type character varying(255)
);


--
-- TOC entry 233 (class 1259 OID 585517)
-- Name: user; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig."user" (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    activation_date timestamp without time zone,
    avatar character varying(255),
    desactivation_date timestamp without time zone,
    email character varying(255) NOT NULL,
    enabled boolean,
    fax character varying(255),
    first_name character varying(255) NOT NULL,
    home_phone character varying(255),
    last_name character varying(255) NOT NULL,
    mobile character varying(255),
    password character varying(255) NOT NULL,
    user_name character varying(255) NOT NULL
);


--
-- TOC entry 235 (class 1259 OID 585528)
-- Name: user_log; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.user_log (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    browser_name character varying(255),
    browser_version character varying(255),
    client_os character varying(255),
    login_date timestamp without time zone,
    logout_date timestamp without time zone,
    token character varying(255),
    user_ip character varying(255),
    username character varying(255)
);


--
-- TOC entry 236 (class 1259 OID 585536)
-- Name: user_notification; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.user_notification (
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255),
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255),
    icon character varying(255),
    link public.geometry,
    message character varying(255),
    viewed boolean,
    viewed_date timestamp without time zone,
    notification_id uuid,
    user_id uuid
);


-- #####################--------------ASSOCIATIONS TABLES----------------#########################--



--
-- TOC entry 214 (class 1259 OID 585415)
-- Name: group_users; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.group_users (
    groups_id uuid NOT NULL,
    users_id uuid NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 585489)
-- Name: roles_permissions; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.roles_permissions (
    role_id uuid NOT NULL,
    permissions_id uuid NOT NULL
);


--
-- TOC entry 230 (class 1259 OID 585508)
-- Name: tag_entity_elements; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.tag_entity_elements (
    tags_id uuid NOT NULL,
    entity_elements_id uuid NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 585511)
-- Name: tag_layers; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.tag_layers (
    tags_id uuid NOT NULL,
    layers_id uuid NOT NULL
);


--
-- TOC entry 232 (class 1259 OID 585514)
-- Name: tag_maps; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.tag_maps (
    tags_id uuid NOT NULL,
    maps_id uuid NOT NULL
);


--
-- TOC entry 234 (class 1259 OID 585525)
-- Name: user_entity_elements; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.user_entity_elements (
    users_id uuid NOT NULL,
    entity_elements_id uuid NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 585426)
-- Name: layer_groups; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.layer_groups (
    layers_id uuid NOT NULL,
    groups_id uuid NOT NULL
);


--
-- TOC entry 217 (class 1259 OID 585429)
-- Name: layer_users; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.layer_users (
    layers_id uuid NOT NULL,
    users_id uuid NOT NULL
);

--
-- TOC entry 219 (class 1259 OID 585440)
-- Name: map_groups; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.map_groups (
    maps_id uuid NOT NULL,
    groups_id uuid NOT NULL
);


--
-- TOC entry 220 (class 1259 OID 585443)
-- Name: map_layers; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.map_layers (
    maps_id uuid NOT NULL,
    layers_id uuid NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 585446)
-- Name: map_users; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.map_users (
    maps_id uuid NOT NULL,
    users_id uuid NOT NULL
);

--
-- TOC entry 213 (class 1259 OID 585412)
-- Name: group_permissions; Type: TABLE; Schema: sig; Owner: -
--

CREATE TABLE IF NOT EXISTS sig.group_permissions (
    group_id uuid NOT NULL,
    permissions_id uuid NOT NULL
);


ALTER TABLE ONLY sig.user_notification DROP CONSTRAINT IF EXISTS fk_usernotification_user_id;
ALTER TABLE ONLY sig.user_notification DROP CONSTRAINT IF EXISTS fk_usernotification_notification_id;
ALTER TABLE ONLY sig.field DROP CONSTRAINT IF EXISTS fk_field_layer_id;
ALTER TABLE ONLY sig.map_users DROP CONSTRAINT IF EXISTS fk_user_map_id;
ALTER TABLE ONLY sig.layer_users DROP CONSTRAINT IF EXISTS fk_user_layer_id;
ALTER TABLE ONLY sig.group_users DROP CONSTRAINT IF EXISTS fk_user_groups_id;
ALTER TABLE ONLY sig.user_entity_elements DROP CONSTRAINT IF EXISTS fk_user_entity_element_id;
ALTER TABLE ONLY sig.tag_maps DROP CONSTRAINT IF EXISTS fk_tag_map_id;
ALTER TABLE ONLY sig.tag_layers DROP CONSTRAINT IF EXISTS fk_tag_layer_id;
ALTER TABLE ONLY sig.tag_entity_elements DROP CONSTRAINT IF EXISTS fk_tag_entity_element_id;
ALTER TABLE ONLY sig.roles_permissions DROP CONSTRAINT IF EXISTS fk_role_permission_id;
ALTER TABLE ONLY sig.resource_value DROP CONSTRAINT IF EXISTS fk_resource_id;
ALTER TABLE ONLY sig.roles_permissions DROP CONSTRAINT IF EXISTS fk_permission_role_id;
ALTER TABLE ONLY sig.group_permissions DROP CONSTRAINT IF EXISTS fk_permission_group_id;
ALTER TABLE ONLY sig.map_users DROP CONSTRAINT IF EXISTS fk_map_user_id;
ALTER TABLE ONLY sig.tag_maps DROP CONSTRAINT IF EXISTS fk_map_tag_id;
ALTER TABLE ONLY sig.map_layers DROP CONSTRAINT IF EXISTS fk_map_layer_id;
ALTER TABLE ONLY sig.map_groups DROP CONSTRAINT IF EXISTS fk_map_group_id;
ALTER TABLE ONLY sig.layer_users DROP CONSTRAINT IF EXISTS fk_layer_user_id;
ALTER TABLE ONLY sig.tag_layers DROP CONSTRAINT IF EXISTS fk_layer_tag_id;
ALTER TABLE ONLY sig.map_layers DROP CONSTRAINT IF EXISTS fk_layer_map_id;
ALTER TABLE ONLY sig.layer_groups DROP CONSTRAINT IF EXISTS fk_layer_group_id;
ALTER TABLE ONLY sig.entity_element DROP CONSTRAINT IF EXISTS fk_layer_entity_element_id;
ALTER TABLE ONLY sig.group_users DROP CONSTRAINT IF EXISTS fk_group_users_id;
ALTER TABLE ONLY sig.group_permissions DROP CONSTRAINT IF EXISTS fk_group_permission_id;
ALTER TABLE ONLY sig.map_groups DROP CONSTRAINT IF EXISTS fk_group_map_id;
ALTER TABLE ONLY sig.layer_groups DROP CONSTRAINT IF EXISTS fk_group_layer_id;
ALTER TABLE ONLY sig.field DROP CONSTRAINT IF EXISTS fk_field_resource;
ALTER TABLE ONLY sig.user_entity_elements DROP CONSTRAINT IF EXISTS fk_entity_element_user_id;
ALTER TABLE ONLY sig.tag_entity_elements DROP CONSTRAINT IF EXISTS fk_entity_element_tag_id;
ALTER TABLE ONLY sig.notification DROP CONSTRAINT IF EXISTS fk_notification_layer_id;
ALTER TABLE ONLY sig.layer DROP CONSTRAINT IF EXISTS fk_layer_viewelement_id;
ALTER TABLE ONLY sig."user" DROP CONSTRAINT IF EXISTS user_pkey;
ALTER TABLE ONLY sig.user_notification DROP CONSTRAINT IF EXISTS user_notification_pkey;
ALTER TABLE ONLY sig.user_log DROP CONSTRAINT IF EXISTS user_log_pkey;
ALTER TABLE ONLY sig."user" DROP CONSTRAINT IF EXISTS uk_user_username;
ALTER TABLE ONLY sig."user" DROP CONSTRAINT IF EXISTS uk_user_email;
ALTER TABLE ONLY sig.settings DROP CONSTRAINT IF EXISTS uk_settings_code;
ALTER TABLE ONLY sig.roles DROP CONSTRAINT IF EXISTS uk_role_name;
ALTER TABLE ONLY sig.resource DROP CONSTRAINT IF EXISTS uk_resource_name;
ALTER TABLE ONLY sig.permissions DROP CONSTRAINT IF EXISTS uk_permissions_name;
ALTER TABLE ONLY sig.map DROP CONSTRAINT IF EXISTS uk_map_name;
ALTER TABLE ONLY sig.layer DROP CONSTRAINT IF EXISTS uk_layer_name;
ALTER TABLE ONLY sig."group" DROP CONSTRAINT IF EXISTS uk_group_name;
ALTER TABLE ONLY sig.tag DROP CONSTRAINT IF EXISTS tag_pkey;
ALTER TABLE ONLY sig.settings DROP CONSTRAINT IF EXISTS settings_pkey;
ALTER TABLE ONLY sig.roles DROP CONSTRAINT IF EXISTS roles_pkey;
ALTER TABLE ONLY sig.resource_value DROP CONSTRAINT IF EXISTS resource_value_pkey;
ALTER TABLE ONLY sig.resource DROP CONSTRAINT IF EXISTS resource_pkey;
ALTER TABLE ONLY sig.permissions DROP CONSTRAINT IF EXISTS permissions_pkey;
ALTER TABLE ONLY sig.notification DROP CONSTRAINT IF EXISTS notification_pkey;
ALTER TABLE ONLY sig.map DROP CONSTRAINT IF EXISTS map_pkey;
ALTER TABLE ONLY sig.layer DROP CONSTRAINT IF EXISTS layer_pkey;
ALTER TABLE ONLY sig."group" DROP CONSTRAINT IF EXISTS group_pkey;
ALTER TABLE ONLY sig.field DROP CONSTRAINT IF EXISTS field_pkey;
ALTER TABLE ONLY sig.entity_element DROP CONSTRAINT IF EXISTS entity_element_pkey;

--
-- TOC entry 3669 (class 2606 OID 585395)
-- Name: entity_element entity_element_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.entity_element
    ADD CONSTRAINT entity_element_pkey PRIMARY KEY (id);


--
-- TOC entry 3671 (class 2606 OID 585403)
-- Name: field field_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.field
    ADD CONSTRAINT field_pkey PRIMARY KEY (id);


--
-- TOC entry 3673 (class 2606 OID 585411)
-- Name: group group_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig."group"
    ADD CONSTRAINT group_pkey PRIMARY KEY (id);


--
-- TOC entry 3677 (class 2606 OID 585425)
-- Name: layer layer_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.layer
    ADD CONSTRAINT layer_pkey PRIMARY KEY (id);


--
-- TOC entry 3681 (class 2606 OID 585439)
-- Name: map map_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map
    ADD CONSTRAINT map_pkey PRIMARY KEY (id);


--
-- TOC entry 3685 (class 2606 OID 585456)
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- TOC entry 3687 (class 2606 OID 585464)
-- Name: permissions permissions_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- TOC entry 3691 (class 2606 OID 585472)
-- Name: resource resource_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.resource
    ADD CONSTRAINT resource_pkey PRIMARY KEY (id);


--
-- TOC entry 3695 (class 2606 OID 585480)
-- Name: resource_value resource_value_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.resource_value
    ADD CONSTRAINT resource_value_pkey PRIMARY KEY (id);


--
-- TOC entry 3697 (class 2606 OID 585488)
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- TOC entry 3701 (class 2606 OID 585499)
-- Name: settings settings_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.settings
    ADD CONSTRAINT settings_pkey PRIMARY KEY (id);


--
-- TOC entry 3705 (class 2606 OID 585507)
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- TOC entry 3675 (class 2606 OID 585545)
-- Name: group uk_group_name; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig."group"
    ADD CONSTRAINT uk_group_name UNIQUE (name);


--
-- TOC entry 3679 (class 2606 OID 585547)
-- Name: layer uk_layer_name; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.layer
    ADD CONSTRAINT uk_layer_name UNIQUE (name);


--
-- TOC entry 3683 (class 2606 OID 585549)
-- Name: map uk_map_name; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map
    ADD CONSTRAINT uk_map_name UNIQUE (name);


--
-- TOC entry 3689 (class 2606 OID 585551)
-- Name: permissions uk_permissions_name; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.permissions
    ADD CONSTRAINT uk_permissions_name UNIQUE (name);


--
-- TOC entry 3693 (class 2606 OID 585553)
-- Name: resource uk_resource_name; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.resource
    ADD CONSTRAINT uk_resource_name UNIQUE (name);


--
-- TOC entry 3699 (class 2606 OID 585555)
-- Name: roles uk_role_name; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.roles
    ADD CONSTRAINT uk_role_name UNIQUE (name);


--
-- TOC entry 3703 (class 2606 OID 585557)
-- Name: settings uk_settings_code; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.settings
    ADD CONSTRAINT uk_settings_code UNIQUE (code);


--
-- TOC entry 3707 (class 2606 OID 585561)
-- Name: user uk_user_email; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig."user"
    ADD CONSTRAINT uk_user_email UNIQUE (email);


--
-- TOC entry 3709 (class 2606 OID 585559)
-- Name: user uk_user_username; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig."user"
    ADD CONSTRAINT uk_user_username UNIQUE (user_name);


--
-- TOC entry 3713 (class 2606 OID 585535)
-- Name: user_log user_log_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.user_log
    ADD CONSTRAINT user_log_pkey PRIMARY KEY (id);


--
-- TOC entry 3715 (class 2606 OID 585543)
-- Name: user_notification user_notification_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.user_notification
    ADD CONSTRAINT user_notification_pkey PRIMARY KEY (id);


--
-- TOC entry 3711 (class 2606 OID 585524)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- TOC entry 3723 (class 2606 OID 585597)
-- Name: layer fk_layer_viewelement_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.layer
    ADD CONSTRAINT fk_layer_viewelement_id FOREIGN KEY (view_element_id) REFERENCES sig.entity_element(id);


--
-- TOC entry 3734 (class 2606 OID 585652)
-- Name: notification fk_notification_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.notification
    ADD CONSTRAINT fk_notification_layer_id FOREIGN KEY (layer_id) REFERENCES sig.layer(id);


--
-- TOC entry 3739 (class 2606 OID 585677)
-- Name: tag_entity_elements fk_entity_element_tag_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.tag_entity_elements
    ADD CONSTRAINT fk_entity_element_tag_id FOREIGN KEY (tags_id) REFERENCES sig.tag(id);


--
-- TOC entry 3745 (class 2606 OID 585707)
-- Name: user_entity_elements fk_entity_element_user_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.user_entity_elements
    ADD CONSTRAINT fk_entity_element_user_id FOREIGN KEY (users_id) REFERENCES sig."user"(id);


--
-- TOC entry 3718 (class 2606 OID 585572)
-- Name: field fk_field_resource; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.field
    ADD CONSTRAINT fk_field_resource FOREIGN KEY (resource_id) REFERENCES sig.resource(id);


--
-- TOC entry 3724 (class 2606 OID 585602)
-- Name: layer_groups fk_group_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.layer_groups
    ADD CONSTRAINT fk_group_layer_id FOREIGN KEY (groups_id) REFERENCES sig."group"(id);


--
-- TOC entry 3728 (class 2606 OID 585622)
-- Name: map_groups fk_group_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map_groups
    ADD CONSTRAINT fk_group_map_id FOREIGN KEY (groups_id) REFERENCES sig."group"(id);


--
-- TOC entry 3720 (class 2606 OID 585582)
-- Name: group_permissions fk_group_permission_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.group_permissions
    ADD CONSTRAINT fk_group_permission_id FOREIGN KEY (group_id) REFERENCES sig."group"(id);


--
-- TOC entry 3721 (class 2606 OID 585587)
-- Name: group_users fk_group_users_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.group_users
    ADD CONSTRAINT fk_group_users_id FOREIGN KEY (users_id) REFERENCES sig."user"(id);


--
-- TOC entry 3716 (class 2606 OID 585562)
-- Name: entity_element fk_layer_entity_element_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.entity_element
    ADD CONSTRAINT fk_layer_entity_element_id FOREIGN KEY (layer_entity_element) REFERENCES sig.layer(id);


--
-- TOC entry 3725 (class 2606 OID 585607)
-- Name: layer_groups fk_layer_group_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.layer_groups
    ADD CONSTRAINT fk_layer_group_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);


--
-- TOC entry 3730 (class 2606 OID 585632)
-- Name: map_layers fk_layer_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map_layers
    ADD CONSTRAINT fk_layer_map_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);


--
-- TOC entry 3741 (class 2606 OID 585687)
-- Name: tag_layers fk_layer_tag_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.tag_layers
    ADD CONSTRAINT fk_layer_tag_id FOREIGN KEY (tags_id) REFERENCES sig.tag(id);


--
-- TOC entry 3727 (class 2606 OID 585617)
-- Name: layer_users fk_layer_user_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.layer_users
    ADD CONSTRAINT fk_layer_user_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);


--
-- TOC entry 3729 (class 2606 OID 585627)
-- Name: map_groups fk_map_group_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map_groups
    ADD CONSTRAINT fk_map_group_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);


--
-- TOC entry 3731 (class 2606 OID 585637)
-- Name: map_layers fk_map_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map_layers
    ADD CONSTRAINT fk_map_layer_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);


--
-- TOC entry 3743 (class 2606 OID 585697)
-- Name: tag_maps fk_map_tag_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.tag_maps
    ADD CONSTRAINT fk_map_tag_id FOREIGN KEY (tags_id) REFERENCES sig.tag(id);


--
-- TOC entry 3733 (class 2606 OID 585647)
-- Name: map_users fk_map_user_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map_users
    ADD CONSTRAINT fk_map_user_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);


--
-- TOC entry 3719 (class 2606 OID 585577)
-- Name: group_permissions fk_permission_group_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.group_permissions
    ADD CONSTRAINT fk_permission_group_id FOREIGN KEY (permissions_id) REFERENCES sig.permissions(id);


--
-- TOC entry 3736 (class 2606 OID 585662)
-- Name: roles_permissions fk_permission_role_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.roles_permissions
    ADD CONSTRAINT fk_permission_role_id FOREIGN KEY (permissions_id) REFERENCES sig.permissions(id);


--
-- TOC entry 3735 (class 2606 OID 585657)
-- Name: resource_value fk_resource_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.resource_value
    ADD CONSTRAINT fk_resource_id FOREIGN KEY (resource_id) REFERENCES sig.resource(id);


--
-- TOC entry 3737 (class 2606 OID 585667)
-- Name: roles_permissions fk_role_permission_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.roles_permissions
    ADD CONSTRAINT fk_role_permission_id FOREIGN KEY (role_id) REFERENCES sig.roles(id);


--
-- TOC entry 3738 (class 2606 OID 585672)
-- Name: tag_entity_elements fk_tag_entity_element_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.tag_entity_elements
    ADD CONSTRAINT fk_tag_entity_element_id FOREIGN KEY (entity_elements_id) REFERENCES sig.entity_element(id);


--
-- TOC entry 3740 (class 2606 OID 585682)
-- Name: tag_layers fk_tag_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.tag_layers
    ADD CONSTRAINT fk_tag_layer_id FOREIGN KEY (layers_id) REFERENCES sig.layer(id);


--
-- TOC entry 3742 (class 2606 OID 585692)
-- Name: tag_maps fk_tag_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.tag_maps
    ADD CONSTRAINT fk_tag_map_id FOREIGN KEY (maps_id) REFERENCES sig.map(id);


--
-- TOC entry 3744 (class 2606 OID 585702)
-- Name: user_entity_elements fk_user_entity_element_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.user_entity_elements
    ADD CONSTRAINT fk_user_entity_element_id FOREIGN KEY (entity_elements_id) REFERENCES sig.entity_element(id);


--
-- TOC entry 3722 (class 2606 OID 585592)
-- Name: group_users fk_user_groups_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.group_users
    ADD CONSTRAINT fk_user_groups_id FOREIGN KEY (groups_id) REFERENCES sig."group"(id);


--
-- TOC entry 3726 (class 2606 OID 585612)
-- Name: layer_users fk_user_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.layer_users
    ADD CONSTRAINT fk_user_layer_id FOREIGN KEY (users_id) REFERENCES sig."user"(id);


--
-- TOC entry 3732 (class 2606 OID 585642)
-- Name: map_users fk_user_map_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.map_users
    ADD CONSTRAINT fk_user_map_id FOREIGN KEY (users_id) REFERENCES sig."user"(id);


--
-- TOC entry 3717 (class 2606 OID 585567)
-- Name: field fk_field_layer_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.field
    ADD CONSTRAINT fk_field_layer_id FOREIGN KEY (layer_id) REFERENCES sig.layer(id);


--
-- TOC entry 3746 (class 2606 OID 585712)
-- Name: user_notification fk_usernotification_notification_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.user_notification
    ADD CONSTRAINT fk_usernotification_notification_id FOREIGN KEY (notification_id) REFERENCES sig.notification(id);


--
-- TOC entry 3747 (class 2606 OID 585717)
-- Name: user_notification fk_usernotification_user_id; Type: FK CONSTRAINT; Schema: sig; Owner: -
--

ALTER TABLE ONLY sig.user_notification
    ADD CONSTRAINT fk_usernotification_user_id FOREIGN KEY (user_id) REFERENCES sig."user"(id);


-- Completed on 2020-12-01 09:29:28

--
-- PostgreSQL database dump complete
--







