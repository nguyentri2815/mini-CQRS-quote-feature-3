alter table outbox_events
    add column correlation_id varchar(100);

create index idx_outbox_correlation_id
    on outbox_events (correlation_id);
