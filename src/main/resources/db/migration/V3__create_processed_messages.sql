create table processed_messages (
                                    message_id varchar(64) primary key,

                                    event_type varchar(200) not null,
                                    aggregate_id varchar(64) not null,

                                    processed_at timestamp not null
);

create index idx_processed_messages_aggregate_id
    on processed_messages (aggregate_id);

create index idx_processed_messages_processed_at
    on processed_messages (processed_at);
