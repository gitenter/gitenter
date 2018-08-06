GRANT USAGE ON SCHEMA auth TO gitenter_app;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA auth TO gitenter_app;
GRANT SELECT, INSERT, DELETE, UPDATE ON ALL TABLES IN SCHEMA auth TO gitenter_app;

--------------------------------------------------------------------------------

GRANT USAGE ON SCHEMA git TO gitenter_app;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA git TO gitenter_app;
GRANT SELECT, INSERT, DELETE, UPDATE ON ALL TABLES IN SCHEMA git TO gitenter_app;

--------------------------------------------------------------------------------

GRANT USAGE ON SCHEMA review TO gitenter_app;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA review TO gitenter_app;
GRANT SELECT, INSERT, DELETE, UPDATE ON ALL TABLES IN SCHEMA review TO gitenter_app;
