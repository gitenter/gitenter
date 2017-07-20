CREATE USER enterovirus CREATEDB PASSWORD 'zooo';

CREATE USER enterovirus_gitar PASSWORD 'zooo';
CREATE USER enterovirus_capsid PASSWORD 'zooo';

CREATE ROLE enterovirus_apps;
GRANT enterovirus_apps TO enterovirus_gitar;
GRANT enterovirus_apps TO enterovirus_capsid;
