-- --------
-- -------- Author:  LOKBANI Chouaib
-- -------- Created: 07 Juillet. 2021 a 10:50
-- --------
-- --------scripts :: add user_logged_action table
-- --------
--

CREATE TABLE IF NOT EXISTS sig.user_logged_actions
(
    id uuid NOT NULL,
    create_date timestamp without time zone,
    created_by character varying(255) COLLATE pg_catalog."default",
    deleted boolean,
    last_modified_date timestamp without time zone,
    modified_by character varying(255) COLLATE pg_catalog."default",
    action character varying(255) COLLATE pg_catalog."default",
    action_time timestamp without time zone,
    data text COLLATE pg_catalog."default",
    ip_address character varying(255) COLLATE pg_catalog."default",
    object character varying(255) COLLATE pg_catalog."default",
    object_id character varying(255) COLLATE pg_catalog."default",
    sql_query text COLLATE pg_catalog."default",
    url character varying(255) COLLATE pg_catalog."default",
    user_name character varying(255) COLLATE pg_catalog."default"
);