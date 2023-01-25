-- Creation of table to hold filters connected to a feedback setting
    create table feedback_filters (
       setting_id varchar(255) not null,
        `key` varchar(255) not null,
        value varchar(255) not null
    ) engine=InnoDB;

    alter table feedback_filters 
       add constraint feedback_filters_unique_combinations_constraint unique (setting_id, `key`, value);
    
    alter table feedback_filters 
       add constraint fk_feedback_filters_feedback_settings 
       foreign key (setting_id) 
       references feedback_settings (id);
       
	alter table feedback_channels
       drop foreign key fk_feedback_settings_id,
       add constraint fk_feedback_channels_feedback_settings  
       foreign key (setting_id) 
       references feedback_settings (id);

       
-- Necessary line in order to document the change. 
insert into schema_history (schema_version,comment,applied) VALUES ('002','Created feedback filter table', NOW());
