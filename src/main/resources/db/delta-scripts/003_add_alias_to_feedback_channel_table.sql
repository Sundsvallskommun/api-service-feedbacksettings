-- Extend feedback_channels table with alias column, default set to 'temporary-alias'
	alter table feedback_channels
	   add column alias varchar(255) not null default 'temporary-alias';

-- Update existing rows with value for destination as alias column value
    update feedback_channels
       set alias = destination;
       
-- Drop default setting
	alter table feedback_channels
       alter alias drop default;

       
-- Necessary line in order to document the change. 
insert into schema_history (schema_version,comment,applied) VALUES ('003','Added alias column to feedback channels table', NOW());
