-- --------
-- -------- Author:  LOKBANI Chouaib
-- -------- Created: 25 mai. 2021 a 15:54
-- --------
-- --------scripts :: add filter, user_layer_filter table
-- --------
--
create table IF NOT EXISTS  sig.filter
(
    id                 uuid         not null
        constraint filter_pkey
            primary key,
    create_date        timestamp,
    created_by         varchar(255),
    deleted            boolean,
    last_modified_date timestamp,
    modified_by        varchar(255),
    description        varchar(255),
    filter_config      text         not null,
    name               varchar(255) not null
);

create table IF NOT EXISTS sig.user_layer_filter
(
    user_id   uuid not null,
    layer_id  uuid not null,
    filter_id uuid not null,
    constraint user_layer_filter_pkey
        primary key (filter_id, layer_id, user_id)
);

ALTER TABLE sig.user_layer_filter DROP CONSTRAINT IF EXISTS  fk_user_layer_filter_filter;
ALTER TABLE sig.user_layer_filter
    ADD CONSTRAINT fk_user_layer_filter_filter FOREIGN KEY (filter_id)
        REFERENCES sig.filter (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;

ALTER TABLE sig.user_layer_filter DROP CONSTRAINT IF EXISTS  fk_user_layer_filter_layer;
ALTER TABLE sig.user_layer_filter
    ADD CONSTRAINT fk_user_layer_filter_layer FOREIGN KEY (layer_id)
        REFERENCES sig.layer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;


ALTER TABLE sig.user_layer_filter DROP CONSTRAINT IF EXISTS  fk_user_layer_filter_user;
ALTER TABLE sig.user_layer_filter
    ADD CONSTRAINT fk_user_layer_filter_user FOREIGN KEY (user_id)
        REFERENCES sig."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION;
