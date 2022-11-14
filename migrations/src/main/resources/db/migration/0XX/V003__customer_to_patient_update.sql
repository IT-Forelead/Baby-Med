ALTER TABLE payments RENAME COLUMN customers_id TO patient_id;
ALTER TABLE customers RENAME TO patients;
