
    create table feedback_channels (
       setting_id varchar(255) not null,
        alias varchar(255) not null,
        contact_method varchar(255) not null,
        destination varchar(255) not null,
        send_feedback bit not null
    ) engine=InnoDB;

    create table feedback_filters (
       setting_id varchar(255) not null,
        `key` varchar(255) not null,
        value varchar(255) not null
    ) engine=InnoDB;

    create table feedback_settings (
       id varchar(255) not null,
        created datetime(6) not null,
        modified datetime(6),
        organization_id varchar(255),
        person_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table schema_history (
       schema_version varchar(255) not null,
        applied datetime(6) not null,
        comment varchar(8192) not null,
        primary key (schema_version)
    ) engine=InnoDB;

    alter table feedback_channels 
       add constraint feedback_channels_unique_combinations_constraint unique (setting_id, contact_method, destination);

    alter table feedback_filters 
       add constraint feedback_filters_unique_combinations_constraint unique (setting_id, `key`, value);
create index feedback_settings_organization_id_index on feedback_settings (organization_id);
create index feedback_settings_person_id_index on feedback_settings (person_id);

    alter table feedback_settings 
       add constraint feedback_settings_person_id_organization_id_unique_constraint unique (person_id, organization_id);

    alter table feedback_channels 
       add constraint fk_feedback_channels_feedback_settings 
       foreign key (setting_id) 
       references feedback_settings (id);

    alter table feedback_filters 
       add constraint fk_feedback_filters_feedback_settings 
       foreign key (setting_id) 
       references feedback_settings (id);
