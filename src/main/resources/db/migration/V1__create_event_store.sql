create table event_store (
                             id varchar(64) primary key,

                             aggregate_id varchar(64) not null,
                             aggregate_type varchar(100) not null,

                             event_type varchar(200) not null,
                             payload text not null,

                             version bigint not null,
                             created_at timestamp not null,

                             constraint uk_event_store_aggregate_version
                                 unique (aggregate_id, version)
);

create index idx_event_store_aggregate_id
    on event_store (aggregate_id);

create index idx_event_store_aggregate_version
    on event_store (aggregate_id, version);

create index idx_event_store_created_at
    on event_store (created_at);
