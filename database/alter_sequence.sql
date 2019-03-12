-- This is to bypass dbUnit uncompatibility problem.
-- Typical error:
-- > ERROR: duplicate key value violates unique constraint "member_pkey"
-- >  Detail: Key (id)=(1) already exists.
ALTER SEQUENCE auth.member_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.organization_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.organization_member_map_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.repository_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.repository_member_map_id_seq RESTART WITH 2;
ALTER SEQUENCE auth.ssh_key_id_seq RESTART WITH 2;
