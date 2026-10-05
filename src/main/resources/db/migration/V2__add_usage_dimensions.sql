ALTER TABLE booking_event_facts ADD COLUMN user_id VARCHAR(128);
ALTER TABLE booking_event_facts ADD COLUMN user_email VARCHAR(320);
ALTER TABLE booking_event_facts ADD COLUMN user_name VARCHAR(200);
ALTER TABLE booking_event_facts ADD COLUMN lab_name VARCHAR(200);

CREATE INDEX idx_booking_event_facts_occurred_at ON booking_event_facts (occurred_at);
CREATE INDEX idx_booking_event_facts_user_id ON booking_event_facts (user_id);
CREATE INDEX idx_booking_event_facts_status ON booking_event_facts (status);