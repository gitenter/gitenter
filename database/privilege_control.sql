GRANT USAGE ON SCHEMA setting TO enterovirus_apps;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA setting TO enterovirus_apps;

GRANT SELECT ON ALL TABLES IN SCHEMA setting TO enterovirus_gitar;
GRANT SELECT ON ALL TABLES IN SCHEMA setting TO enterovirus_capsid;

--------------------------------------------------------------------------------

GRANT USAGE ON SCHEMA config TO enterovirus_apps;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA config TO enterovirus_apps;

GRANT SELECT ON ALL TABLES IN SCHEMA config TO enterovirus_gitar;
GRANT SELECT, INSERT, DELETE, UPDATE ON ALL TABLES IN SCHEMA config TO enterovirus_capsid;

--------------------------------------------------------------------------------

GRANT USAGE ON SCHEMA git TO enterovirus_apps;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA git TO enterovirus_apps;

GRANT SELECT, INSERT ON ALL TABLES IN SCHEMA git TO enterovirus_gitar;
GRANT SELECT ON ALL TABLES IN SCHEMA git TO enterovirus_capsid;

--------------------------------------------------------------------------------

GRANT USAGE ON SCHEMA review TO enterovirus_apps;
GRANT USAGE ON ALL SEQUENCES IN SCHEMA review TO enterovirus_apps;

GRANT SELECT ON ALL TABLES IN SCHEMA review TO enterovirus_gitar;
GRANT SELECT, INSERT, DELETE, UPDATE ON ALL TABLES IN SCHEMA review TO enterovirus_capsid;
