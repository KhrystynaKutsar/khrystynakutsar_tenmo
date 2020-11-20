-- ********************************************************************************
-- This script creates the database users and grants them the necessary permissions
-- ********************************************************************************

CREATE USER tenmo_owner
WITH PASSWORD 'tebucks';

GRANT ALL
ON ALL TABLES IN SCHEMA public
TO tenmo_owner;

GRANT ALL
ON ALL SEQUENCES IN SCHEMA public
TO tenmo_owner;

CREATE USER tenmo_appuser
WITH PASSWORD 'tebucks';

GRANT SELECT, INSERT, UPDATE, DELETE
ON ALL TABLES IN SCHEMA public
TO tenmo_appuser;

GRANT USAGE, SELECT
ON ALL SEQUENCES IN SCHEMA public
TO tenmo_appuser;

SELECT balance FROM accounts INNER JOIN users ON users.user_id = accounts.user_id WHERE users.username = 'testAccount2';

SELECT * FROM transfers WHERE transfer_status_id = 1;

INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (1, 2, 5, 6, 50);

SELECT * FROM transfers WHERE transfer_status_id = 1;

SELECT * FROM transfers INNER JOIN accounts ON transfers.account_from = accounts.account_id OR transfers.account_to = accounts.account_id INNER JOIN users ON accounts.user_id = users.user_id WHERE users.username = 'testAccount2';

