SELECT 'TRUNCATE ' || table_schema || '.' || table_name || ' CASCADE;'
	FROM information_schema.tables
	WHERE table_schema NOT IN ('pg_catalog', 'information_schema')
AND table_name !~ '^pg_';
