-------------------------------------
-- FeedbackSettingsRepositoryTest.*
-- CreateFeedbackSettingsTest.*
-- ReadFeedbackSettingsTest.*
-- UpdateFeedbackSettingsTest.*
-- DeleteFeedbackSettingsTest.*
-------------------------------------
-- Private person 1
INSERT INTO feedback_settings(id, person_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e1', '49a974ea-9137-419b-bcb9-ad74c81a1d1f', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e1', 'SMS', 'Private mobile', '0796100001', true);

-- Private person 2
INSERT INTO feedback_settings(id, person_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e2', '49a974ea-9137-419b-bcb9-ad74c81a1d2f', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e2', 'SMS', 'Private mobile', '0796100002', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e2', 'EMAIL', 'person.2@company.com', 'person.2@company.com', true);

-- Private person 3
INSERT INTO feedback_settings(id, person_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e3', '49a974ea-9137-419b-bcb9-ad74c81a1d3f', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e3', 'SMS', 'Hubbys mobile', '0796100003', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e3', 'SMS', 'Wifeys mobile', '0786100003', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e3', 'EMAIL', 'Hubbys email', 'person.3@company.com', true);

-- Organizational representative 1 (person 3 has both private and representative settings with no filters)
INSERT INTO feedback_settings(id, person_id, organization_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e4', '49a974ea-9137-419b-bcb9-ad74c81a1d3f', '15aee472-46ab-4f03-9605-68bd64ebc84a', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e4', 'SMS', 'Victor the cleaner', '0796100004', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e4', 'EMAIL', 'Victor the cleaner', 'representative.1@company.com', false);

-- Organizational representative 2 (has only representative settings with no filters for same company as representative 1)
INSERT INTO feedback_settings(id, person_id, organization_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e5', '49a974ea-9137-419b-bcb9-ad74c81a1d4f', '15aee472-46ab-4f03-9605-68bd64ebc84a', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e5', 'SMS', 'Vincent Vega', '0796100005', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e5', 'SMS', 'Vincent Vega', '0786100005', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e5', 'EMAIL', 'Vincent Vega', 'representative.2@company.com', true);

-- Organizational representative 3 (has only representative settings with filters on category broadband, messagetype disturbance for same company as representative 1 and 2)
INSERT INTO feedback_settings(id, person_id, organization_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e6', '49a974ea-9137-419b-bcb9-ad74c81a1d5f', '15aee472-46ab-4f03-9605-68bd64ebc84a', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e6', 'SMS', 'Mister Pink', '0796100006', true);

INSERT INTO feedback_filters(setting_id, `key`, value)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e6', 'CATEGORIES', 'broadband'),
       ('9a24743c-5c19-4774-954e-a3ad67a734e6', 'MESSAGETYPES', 'disturbance');
       
-- Organizational representative 4 (has only representative settings with filters on category broadband for same company as representative 1, 2 and 3)
INSERT INTO feedback_settings(id, person_id, organization_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e7', '49a974ea-9137-419b-bcb9-ad74c81a1d6f', '15aee472-46ab-4f03-9605-68bd64ebc84a', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e7', 'SMS', '0796100007', '0796100007', true);

INSERT INTO feedback_filters(setting_id, `key`, value)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e7', 'CATEGORIES', 'broadband');

-- Organizational representative 5 (has only representative settings with filters on category electricity, messagetype information or disturbance for same company as representative 1, 2, 3 and 4)
INSERT INTO feedback_settings(id, person_id, organization_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e8', '49a974ea-9137-419b-bcb9-ad74c81a1d7f', '15aee472-46ab-4f03-9605-68bd64ebc84a', '2022-01-10 10:00:00.000');

INSERT INTO feedback_channels(setting_id, contact_method, alias, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e8', 'SMS', '0796100008', '0796100008', true);

INSERT INTO feedback_filters(setting_id, `key`, value)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e8', 'CATEGORIES', 'electricity'),
       ('9a24743c-5c19-4774-954e-a3ad67a734e8', 'MESSAGETYPES', 'information'),
       ('9a24743c-5c19-4774-954e-a3ad67a734e8', 'MESSAGETYPES', 'disturbance');