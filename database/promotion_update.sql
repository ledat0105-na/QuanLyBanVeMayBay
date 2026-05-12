ALTER TABLE airline_booking_system.promotions
MODIFY COLUMN is_active TINYINT(1) NOT NULL DEFAULT 1;

UPDATE airline_booking_system.promotions
SET is_active = 0
WHERE id IN (3,4,5,6,7,8,9,10);