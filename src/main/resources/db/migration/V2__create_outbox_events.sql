create table outbox_events (
                               id varchar(64) primary key,

                               aggregate_id varchar(64) not null,
                               aggregate_type varchar(100) not null,

                               event_type varchar(200) not null,
                               payload text not null,

                               aggregate_version bigint not null,

                               status varchar(30) not null,
                               retry_count integer not null default 0,
                               last_error text,

                               created_at timestamp not null,
                               sent_at timestamp
);

create index idx_outbox_status
    on outbox_events (status);

create index idx_outbox_created_at
    on outbox_events (created_at);

create index idx_outbox_aggregate_id
    on outbox_events (aggregate_id);
