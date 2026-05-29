create table quote_state (
                             id varchar(64) primary key,

                             customer_name varchar(255) not null,
                             product_code varchar(100) not null,
                             premium numeric(19, 2) not null,

                             status varchar(50) not null,

                             tenant_id varchar(64),
                             organization_id varchar(64),

                             created_by varchar(64),
                             created_by_name varchar(200),

                             submitted_by varchar(64),
                             submitted_by_name varchar(200),

                             approved_by varchar(64),
                             approved_by_name varchar(200),

                             created_at timestamp not null,
                             updated_at timestamp not null,

                             last_projected_version bigint not null default 0
);

create index idx_quote_state_status
    on quote_state (status);

create index idx_quote_state_product_code
    on quote_state (product_code);

create index idx_quote_state_tenant_id
    on quote_state (tenant_id);

create index idx_quote_state_organization_id
    on quote_state (organization_id);

create index idx_quote_state_created_at
    on quote_state (created_at);
