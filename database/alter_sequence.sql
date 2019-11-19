-- There's a bug on DbUnit setup, that right now we setup the primary
-- key manually in the XML data file. However, DbUnit doesn't reset
-- the sequence (e.g. `ALTER SEQUENCE schema_name.table_name_id_seq
-- RESTART WITH 2;`), so in the first round of unit tests (after database
-- is reset), the insert related ones will fail. It will pass after 2-3
-- rounds, through.
--
-- May refer:
-- https://stackoverflow.com/questions/20607704/reset-sequence-in-dbunit
--
-- Typical error:
-- > ERROR: duplicate key value violates unique constraint "person_pkey"
-- >  Detail: Key (id)=(1) already exists.
ALTER SEQUENCE auth.person_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.organization_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.organization_person_map_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.repository_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.repository_person_map_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.ssh_key_id_seq RESTART WITH 2;
