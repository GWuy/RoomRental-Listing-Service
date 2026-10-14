# Snowflake ID migration

All UUID columns in `listing_booking_service` are converted to PostgreSQL `BIGINT` by Liquibase changeset `v1.1-convert-uuid-ids-to-snowflake` (015). The migration creates one UUID-to-Snowflake mapping for the schema and applies it to every UUID column, so matching IDs across primary keys and references retain their relationships. Existing foreign keys are restored with their original delete behavior and validation state.

The v1.0 and earlier v1.1 changesets remain unchanged because Liquibase records their checksums after they run. Changeset 015 runs after the existing schema and seed changesets and leaves the resulting schema using `BIGINT` IDs.

New entity IDs are generated with a 2024-01-01 epoch, 10 worker bits, and 12 sequence bits. Set `LISTING_SNOWFLAKE_WORKER_ID` to a unique value from 1 through 1023 for each running instance. The default value `1` is intended for a single local instance. Worker ID `0` is reserved for the one-time conversion of existing UUIDs.

The conversion includes UUID-valued references such as `customer_id`, `staff_id`, `district_id`, and `room_type_id`, even where this service has no corresponding entity or foreign key. Those values are reassigned through this service's mapping, so any upstream service or stored client data that exchanges these IDs must migrate to the same mapping or be updated together with this service. Changeset 016 later removes the obsolete `district_id` and ward references and keeps the full address in `building.address`.
