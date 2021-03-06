-- ************************************************************************
--
-- GIT Header
--
-- $Format:Git ID: (%h) %ci$
-- $Id$
-- Version hash: $Format:%H$
--
-- Description:
--
-- Rapid Enquiry Facility (RIF) - Common partitioning functions
--
-- Copyright:
--
-- The Rapid Inquiry Facility (RIF) is an automated tool devised by SAHSU 
-- that rapidly addresses epidemiological and public health questions using 
-- routinely collected health and population data and generates standardised 
-- rates and relative risks for any given health outcome, for specified age 
-- and year ranges, for any given geographical area.
--
-- Copyright 2014 Imperial College London, developed by the Small Area
-- Health Statistics Unit. The work of the Small Area Health Statistics Unit 
-- is funded by the Public Health England as part of the MRC-PHE Centre for 
-- Environment and Health. Funding for this project has also been received 
-- from the Centers for Disease Control and Prevention.  
--
-- This file is part of the Rapid Inquiry Facility (RIF) project.
-- RIF is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Lesser General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
--
-- RIF is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
-- GNU Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public License
-- along with RIF. If not, see <http://www.gnu.org/licenses/>; or write 
-- to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
-- Boston, MA 02110-1301 USA
--
-- Author:
--
-- Peter Hambly, SAHSU
--
\set ECHO all
\set ON_ERROR_STOP ON
\timing

--
-- Check user is rif40
--
DO LANGUAGE plpgsql $$
BEGIN
	IF user = 'rif40' THEN
		RAISE INFO 'User check: %', user;	
	ELSE
		RAISE EXCEPTION 'C20900: User check failed: % is not rif40', user;	
	END IF;
END;
$$;

CREATE OR REPLACE FUNCTION rif40_sql_pkg._rif40_hash(l_value VARCHAR, l_bucket INTEGER)
RETURNS INTEGER
AS 'SELECT (ABS(hashtext(l_value))%l_bucket)+1;' LANGUAGE sql IMMUTABLE STRICT;

COMMENT ON FUNCTION rif40_sql_pkg._rif40_hash(VARCHAR, INTEGER) IS 'Function: 	_rif40_hash()
Parameters:	Value (must be cast if required), number of buckets
Returns:	Hash in the range 1 .. l_bucket 
Description:	Hashing function';

--
-- CHECK constraint functions for partition elimination. Do not work - suspect IMMUTABLE functions not supported in C
-- All apart from INTEGER are commented out. Usage:
--
-- CREATE TABLE rif40_study_shares_p15 (
-- CONSTRAINT rif40_study_shares_p15_ck CHECK (hash_partition_number = 15 /* bucket requested */)
-- ) INHERITS (rif40_study_shares);
--
--CREATE OR REPLACE FUNCTION rif40_sql_pkg._rif40_hash_bucket_check(l_value VARCHAR, l_bucket INTEGER, l_bucket_requested INTEGER)
--RETURNS VARCHAR
--AS 'SELECT CASE WHEN l_bucket_requested IS NULL THEN NULL WHEN l_bucket_requested = (ABS(hashtext(l_value))%l_bucket)+1 THEN l_value ELSE NULL END;' LANGUAGE sql IMMUTABLE STRICT;
CREATE OR REPLACE FUNCTION rif40_sql_pkg._rif40_hash_bucket_check(l_value INTEGER, l_bucket INTEGER, l_bucket_requested INTEGER)
RETURNS INTEGER
AS 'SELECT CASE WHEN l_bucket_requested IS NULL THEN NULL WHEN l_bucket_requested = (ABS(hashtext(l_value::TEXT))%l_bucket)+1 THEN l_value ELSE NULL END;' LANGUAGE sql IMMUTABLE STRICT;

--COMMENT ON FUNCTION rif40_sql_pkg._rif40_hash_bucket_check(VARCHAR, INTEGER, INTEGER) IS 'Function: 	_rif40_hash()
--Parameters:	Value, number of buckets, bucket number requested
--Returns:	Value if bucket number requested = Hash computed in the range 1 .. l_bucket; NULL otherwise 
--Description:	Hashing function; suitable for partition elimination equalities';
COMMENT ON FUNCTION rif40_sql_pkg._rif40_hash_bucket_check(INTEGER, INTEGER, INTEGER) IS 'Function: 	_rif40_hash()
Parameters:	Value, number of buckets, bucket number requested
Returns:	Value if bucket number requested = Hash computed in the range 1 .. l_bucket; NULL otherwise 
Description:	Hashing function; suitable for partition elimination equalities';

CREATE OR REPLACE FUNCTION rif40_sql_pkg._rif40_hash_partition_functional_index(l_schema VARCHAR, l_table VARCHAR, l_column VARCHAR, 
	num_partitions INTEGER,
	OUT ddl_stmt VARCHAR[])
RETURNS VARCHAR[]
AS $func$
/*
Function: 	_rif40_hash_partition_functional_index()
Parameters:	Schema, table, columnn, number of partitions
Returns:	DDL statement array
Description:	Create indexes by partition on hashing function
 */
DECLARE
 	i INTEGER:=0;
--
BEGIN
	FOR i IN 1 .. num_partitions LOOP
--		ddl_stmt[i]:='CREATE INDEX '||l_table||'_p'||i||'_hash ON '||l_schema||'.'||l_table||'_p'||i||
--			'(rif40_sql_pkg._rif40_hash_bucket_check('||l_column||', '||num_partitions||' /* total buckets */, '||i||' /* bucket requested */))';
		ddl_stmt[i]:='CREATE INDEX '||l_table||'_p'||i||'_hash ON '||l_schema||'.'||l_table||'_p'||i||
			'(rif40_sql_pkg._rif40_hash('||l_column||'::VARCHAR, '||num_partitions||' /* total buckets */))';
	END LOOP;
END;
$func$ 
LANGUAGE plpgsql;

COMMENT ON FUNCTION rif40_sql_pkg._rif40_hash_partition_functional_index(VARCHAR, VARCHAR, VARCHAR, INTEGER, OUT VARCHAR[]) IS 'Function: 	_rif40_hash_partition_functional_index()
Parameters:	Schema, table, columnn, number of partitions, partition value
Returns:	DDL statement array
Description:	Create indexes by partition on hashing function';

CREATE OR REPLACE FUNCTION rif40_sql_pkg.rif40_hash_partition(
	l_schema VARCHAR, l_table VARCHAR, l_column VARCHAR, l_num_partitions INTEGER DEFAULT 16)
RETURNS void
SECURITY INVOKER
AS $func$
/*
Function: 	rif40_hash_partition()
Parameters:	Schema, table, column, number of partitions
Returns:	Nothing
Description:	Hash partition schema.table on column

 */
DECLARE
 	c1gangep 		REFCURSOR;
	c2gangep CURSOR(l_schema VARCHAR, l_table VARCHAR) FOR			/* List of columns in original order */
		SELECT *
	          FROM information_schema.columns
	         WHERE table_schema = l_schema
	           AND table_name   = l_table
	         ORDER BY ordinal_position;
--
	c2_rec 			RECORD;
	create_setup 		RECORD;
	create_insert 		RECORD;
--
	sql_stmt 		VARCHAR;
	rec_list		VARCHAR;
	bind_list		VARCHAR;
	ddl_stmt 		VARCHAR[];
	fk_stmt 		VARCHAR[];
	l_ddl_stmt 		VARCHAR[];
	num_partitions		INTEGER;
	min_value		VARCHAR;
	total_rows		INTEGER;
	n_num_partitions	INTEGER;
	n_total_rows		INTEGER;
	i			INTEGER:=0;
	j			INTEGER:=0;
	warnings		INTEGER:=0;
	total_partitions	INTEGER;
--
	error_message 		VARCHAR;
	v_detail 		VARCHAR:='(Not supported until 9.2; type SQL statement into psql to see remote error)';
BEGIN
--
-- Check if table is already partitioned
--
	total_partitions:=rif40_sql_pkg._rif40_partition_count(l_schema, l_table);
	IF total_partitions >= 1 THEN
		PERFORM rif40_log_pkg.rif40_log('WARNING', 'rif40_hash_partition', 
			'Automatic hash partition by %: %.%; table name is already partitioned into: % partitions', 
			l_column::VARCHAR, l_schema::VARCHAR, l_table::VARCHAR, total_partitions::VARCHAR);
	END IF;

--
-- Add hash_partition_number if required
--
	ddl_stmt[1]:='ALTER TABLE '||l_schema||'.'||quote_ident(l_table)||' ADD COLUMN hash_partition_number INTEGER';
	ddl_stmt[2]:='COMMENT ON COLUMN '||l_schema||'.'||quote_ident(l_table)||'.hash_partition_number IS ''Hash partition number (for partition elimination)''';
--
-- Run
--
	PERFORM rif40_sql_pkg.rif40_ddl(ddl_stmt);

--
-- Call: _rif40_common_partition_create_setup()
--
	create_setup:=rif40_sql_pkg._rif40_common_partition_create_setup(l_schema, l_table, l_column, l_num_partitions);
--
-- Force creation - tables are mainly empty in dev.
--
--	IF create_setup.ddl_stmt IS NULL THEN /* Un partitionable */
--		RETURN;
--	END IF;

--
-- Copy out parameters
--
	ddl_stmt:=create_setup.ddl_stmt;
	fk_stmt:=create_setup.fk_stmt;
	num_partitions:=create_setup.num_partitions;
	min_value:=create_setup.min_value;
	warnings:=create_setup.warnings;
	total_rows:=create_setup.total_rows;
	PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_hash_partition', 
		'Automatic hash partitioning by %: %.%;  rows: %; partitions: %; warnings: %', 
		l_column::VARCHAR, l_schema::VARCHAR, l_table::VARCHAR, 
		total_rows::VARCHAR, num_partitions::VARCHAR, warnings::VARCHAR);

--
-- Create auto hash trigger function
--
	ddl_stmt[array_length(ddl_stmt, 1)+1]:='CREATE FUNCTION '||quote_ident(l_schema)||'.'||quote_ident(l_table||'_insert')||'()'||E'\n'||
'  RETURNS trigger AS'||E'\n'||
'$BODY$'||E'\n'||
'DECLARE'||E'\n'||
'	sql_stmt 	VARCHAR;'||E'\n'||
'	p_table		VARCHAR;'||E'\n'||
'	p_hash		VARCHAR;'||E'\n'||
'	num_partitions	VARCHAR:='||l_num_partitions::Text||';'||E'\n'||
'--'||E'\n'||
'	error_message VARCHAR;'||E'\n'||
'	v_detail VARCHAR:=''(Not supported until 9.2; type SQL statement into psql to see remote error)'';'||E'\n'||
'BEGIN'||E'\n'||
'--'||E'\n'||
'-- Check partition field is not null'||E'\n'||
'--'||E'\n'||
'	IF new.'||quote_ident(l_column)||' IS NULL THEN'||E'\n'||
'		PERFORM rif40_log_pkg.rif40_error(-19001, '''||quote_ident(l_table||'_insert')||''','||E'\n'||
'		       	''NULL value for partition column '||quote_ident(l_column)||''');'||E'\n'||
'	END IF;'||E'\n'||
'	p_hash:=rif40_sql_pkg._rif40_hash(NEW.'||l_column||'::text, '||l_num_partitions::Text||')::Text;'||E'\n'||
'	p_table:=quote_ident('||''''||l_table||'_p''||p_hash);'||E'\n'||
'	BEGIN'||E'\n'||
'--'||E'\n'||
'-- Copy columns from NEW'||E'\n'||
'--'||E'\n';
	FOR c2_rec IN c2gangep(l_schema, l_table) LOOP
		i:=i+1;
		IF rec_list IS NULL THEN
			rec_list:='NEW.'||c2_rec.column_name;
			bind_list:='$'||i::Text;
		ELSE
			rec_list:=rec_list||', NEW.'||c2_rec.column_name;
			bind_list:=bind_list||', $'||i::Text;
		END IF;
	END LOOP;
	ddl_stmt[array_length(ddl_stmt, 1)]:=ddl_stmt[array_length(ddl_stmt, 1)]||
'		sql_stmt:= ''INSERT INTO ''||p_table||'' VALUES ('||bind_list||') /* Partition: ''||p_hash||'' of ''||num_partitions||'' */'';'||E'\n'||
'--		PERFORM rif40_log_pkg.rif40_log(''DEBUG3'', '''||quote_ident(l_table||'_insert')||''','||E'\n'||
'--			''Row N SQL> EXECUTE ''''%'''' USING '||rec_list||'; /* rec: % */'', sql_stmt::VARCHAR, NEW.*::VARCHAR);'||E'\n'||
'		EXECUTE sql_stmt USING '||rec_list||';'||E'\n'||
'	EXCEPTION'||E'\n'||
'		WHEN others THEN'||E'\n'||
'			GET STACKED DIAGNOSTICS v_detail = PG_EXCEPTION_DETAIL;'||E'\n'||
'			error_message:='''||quote_ident(l_table||'_insert')||'() caught: ''||SQLSTATE::VARCHAR||E''\n''||SQLERRM::VARCHAR||'' in SQL>''||E''\n''||sql_stmt||'';''||E''\n''||''(see previous trapped error)''||E''\n''||''Detail: ''||''(''||SQLSTATE||'') ''||v_detail::VARCHAR||'' '';'||E'\n'|| 
'			RAISE INFO ''3: %'', error_message;'||E'\n'||
'--'||E'\n'||
'			RAISE;'||E'\n'||
'	END;'||E'\n'||
'--'||E'\n'||
'	RETURN NULL /* You must return NULL or you will INSERT into the master table... */;'||E'\n'||
'END;'||E'\n'||
'$BODY$'||E'\n'||
'  LANGUAGE plpgsql';

	ddl_stmt[array_length(ddl_stmt, 1)+1]:='COMMENT ON FUNCTION  '||quote_ident(l_schema)||'.'||quote_ident(l_table||'_insert')||'()'||
       		' IS ''Hash partition INSERT function for table: '||l_table||'''';
--
-- If debug is enabled add newly created function 
--
	IF rif40_log_pkg.rif40_is_debug_enabled('rif40_hash_partition', 'DEBUG1') THEN
		ddl_stmt[array_length(ddl_stmt, 1)+1]:='SELECT rif40_log_pkg.rif40_add_to_debug('''||quote_ident(l_table||'_insert')||':DEBUG1'')';
	END IF;
-- 
-- Add trigger to existing table
--
	ddl_stmt[array_length(ddl_stmt, 1)+1]:='CREATE TRIGGER '||quote_ident(l_table||'_insert')||E'\n'||
'  BEFORE INSERT ON '||quote_ident(l_schema)||'.'||quote_ident(l_table)||E'\n'||
'  FOR EACH ROW'||E'\n'||
'  EXECUTE PROCEDURE '||quote_ident(l_table||'_insert')||'()';
	ddl_stmt[array_length(ddl_stmt, 1)+1]:='COMMENT ON TRIGGER '||quote_ident(l_table||'_insert')||E'\n'||
		' ON '||quote_ident(l_schema)||'.'||quote_ident(l_table)||E'\n'||
		' IS ''Partition INSERT trigger by '||quote_ident(l_column)||' for: '||quote_ident(l_schema)||'.'||quote_ident(l_table)||
		'; calls '||quote_ident(l_table||'_insert')||'(). Automatically creates partitions''';
--
-- Create hash partitions
--
	FOR i IN 1 .. l_num_partitions LOOP
		PERFORM _rif40_hash_partition_create(l_schema, l_table, l_table||'_p'||i::Text, l_column, i, l_num_partitions);
	END LOOP;

	IF l_table = 't_rif40_investigations' THEN
--		RAISE plpgsql_error;
	END IF;
--
-- Remove INSERT triggers from master table so they don't fire twice (replace with no re-enable) 
--
--	PERFORM rif40_sql_pkg._rif40_drop_master_trigger(l_schema, l_table);

--
-- Call: _rif40_hash_partition_create_insert()
--
	create_insert:=rif40_sql_pkg._rif40_hash_partition_create_insert(l_schema, l_table, l_column, total_rows, l_num_partitions);
	IF create_insert.ddl_stmt IS NULL THEN /* Un partitionable */
		RETURN;
	END IF;
--
-- Copy out parameters
--
	FOR i IN 1 .. array_length(create_insert.ddl_stmt, 1) LOOP
		ddl_stmt[array_length(ddl_stmt, 1)+1]:=create_insert.ddl_stmt[i];
	END LOOP;

--
-- Run
--
	PERFORM rif40_sql_pkg.rif40_ddl(ddl_stmt);


--
-- Check number of rows match original
--
	sql_stmt:='SELECT COUNT(DISTINCT('||quote_ident(l_column)||')) AS num_partitions, COUNT('||quote_ident(l_column)||') AS total_rows'||E'\n'||
		'FROM '||quote_ident(l_schema)||'.'||quote_ident(l_table)||' LIMIT 1'; 
	PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_hash_partition', 'SQL> %;', sql_stmt::VARCHAR);
	OPEN c1gangep FOR EXECUTE sql_stmt;
	FETCH c1gangep INTO n_num_partitions, n_total_rows;
	CLOSE c1gangep;
--
	IF num_partitions = n_num_partitions AND total_rows = n_total_rows THEN
		PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_hash_partition', 'Partition of: %.% created % partitions, % rows total OK', 
			l_schema::VARCHAR, l_table::VARCHAR, num_partitions::VARCHAR, total_rows::VARCHAR);
	ELSIF total_rows != n_total_rows THEN
		PERFORM rif40_log_pkg.rif40_error(-20190, 'rif40_hash_partition', 'Partition of: %.% rows mismatch: expected: %, got % rows total', 
			l_schema::VARCHAR, l_table::VARCHAR, total_rows::VARCHAR, n_total_rows::VARCHAR);
	ELSIF l_num_partitions = num_partitions THEN
-- Hash partition - test not valid
	ELSE
		PERFORM rif40_log_pkg.rif40_error(-20191, 'rif40_hash_partition', 'Partition of: %.% partition mismatch: expected: %, got % partition total', 
			l_schema::VARCHAR, l_table::VARCHAR, num_partitions::VARCHAR, n_num_partitions::VARCHAR);
	END IF;

--
-- Call: _rif40_common_partition_create_complete()
--
	BEGIN
		PERFORM rif40_sql_pkg._rif40_common_partition_create_complete(l_schema, l_table, l_column, create_insert.index_name);
	EXCEPTION
		WHEN others THEN
			GET STACKED DIAGNOSTICS v_detail = PG_EXCEPTION_DETAIL;
			error_message:='rif40_hash_partition() caught in _rif40_common_partition_create_complete(): '||E'\n'||
				SQLERRM::VARCHAR||' in SQL> '||sql_stmt||E'\n'||'Detail: '||v_detail::VARCHAR;
			RAISE INFO '2: %', error_message;
--
			RAISE;
	END;

--
-- Put back foreign keys, e.g.
--
/*
CREATE TRIGGER t_rif40_investigations_p16_checks BEFORE INSERT OR UPDATE OF username, inv_name, inv_description, year_start, year_stop, max_age_group, min_age_group, genders, numer_tab, investigation_state, geography, study_id, inv_id, classifier, classifier_bands, mh_test_type ON t_rif40_investigations_p16 FOR EACH ROW WHEN ((((((((((((((((((new.username IS NOT NULL) AND ((new.username)::text <> ''::text)) OR ((new.inv_name IS NOT NULL) AND ((new.inv_name)::text <> ''::text))) OR ((new.inv_description IS NOT NULL) AND ((new.inv_description)::text <> ''::text))) OR ((new.year_start IS NOT NULL) AND ((new.year_start)::text <> ''::text))) OR ((new.year_stop IS NOT NULL) AND ((new.year_stop)::text <> ''::text))) OR ((new.max_age_group IS NOT NULL) AND ((new.max_age_group)::text <> ''::text))) OR ((new.min_age_group IS NOT NULL) AND ((new.min_age_group)::text <> ''::text))) OR ((new.genders IS NOT NULL) AND ((new.genders)::text <> ''::text))) OR ((new.investigation_state IS NOT NULL) AND ((new.investigation_state)::text <> ''::text))) OR ((new.numer_tab IS NOT NULL) AND ((new.numer_tab)::text <> ''::text))) OR ((new.geography IS NOT NULL) AND ((new.geography)::text <> ''::text))) OR ((new.study_id IS NOT NULL) AND ((new.study_id)::text <> ''::text))) OR ((new.inv_id IS NOT NULL) AND ((new.inv_id)::text <> ''::text))) OR ((new.classifier IS NOT NULL) AND ((new.classifier)::text <> ''::text))) OR ((new.classifier_bands IS NOT NULL) AND ((new.classifier_bands)::text <> ''::text))) OR ((new.mh_test_type IS NOT NULL) AND ((new.mh_test_type)::text <> ''::text)))) EXECUTE PROCEDURE rif40_trg_pkg.trigger_fct_t_rif40_investigations_checks();
 */
-- 
--
	IF fk_stmt IS NOT NULL THEN
		BEGIN
			PERFORM rif40_sql_pkg.rif40_ddl(fk_stmt);
--			RAISE plpgsql_error;
		EXCEPTION
			WHEN others THEN
--
-- Catch foregin key errors:
--
-- psql:../psql_scripts/v4_0_study_id_partitions.sql:145: INFO:  [DEBUG1] rif40_ddl(): SQL> ALTER TABLE rif40.rif40_study_shares_p10
--       ADD CONSTRAINT /* Add support for local partitions */ rif40_study_shares_p10_study_id_fk FOREIGN KEY (study_id) REFERENCES t_rif40_studies_p10(study_id)
-- /* Referenced foreign key table: rif40.rif40_study_shares_p10 has partitions: false, is a partition: true */
-- /* Referenced foreign key partition: 10 of 16 */;
-- psql:../psql_scripts/v4_0_study_id_partitions.sql:145: WARNING:  rif40_ddl(): SQL in error (42830)> ALTER TABLE rif40.rif40_study_shares_p10
--        ADD CONSTRAINT /* Add support for local partitions */ rif40_study_shares_p10_study_id_fk
-- FOREIGN KEY (study_id) REFERENCES t_rif40_studies_p10(study_id)
-- /* Referenced foreign key table: rif40.rif40_study_shares_p10 has partitions: false, is a partition: true */
-- /* Referenced foreign key partition: 10 of 16 */;
-- psql:../psql_scripts/v4_0_study_id_partitions.sql:145: ERROR:  there is no unique constraint matching given keys for referenced table "t_rif40_studies_p10"
-- Time: 205205.927 ms
--
-- [this is caused by a missing PRIMARY KEY on t_rif40_studies_p10]
--
				GET STACKED DIAGNOSTICS v_detail = PG_EXCEPTION_DETAIL;
				error_message:='rif40_hash_partition() caught in rif40_ddl(fk_stmt): '||E'\n'||
					SQLERRM::VARCHAR||' in SQL> '||sql_stmt||E'\n'||'Detail: '||v_detail::VARCHAR;
				RAISE INFO '2: %', error_message;
--				PERFORM rif40_sql_pkg.rif40_method4('SELECT * FROM rif40_study_shares_p10', 'rif40_study_shares_p10');
--				PERFORM rif40_sql_pkg.rif40_method4('SELECT * FROM rif40_study_shares', 'rif40_study_shares');
--				PERFORM rif40_sql_pkg.rif40_method4('SELECT * FROM t_rif40_studies_p10', 't_rif40_studies_p10');
--				PERFORM rif40_sql_pkg.rif40_method4('SELECT * FROM t_rif40_studies', 't_rif40_studies');
--
				RAISE;
		END;
	END IF;
	IF l_table = 't_rif40_studies' THEN
--		RAISE plpgsql_error;
	END IF;

--
-- Test partition exclusion. This currently does not work and Postgres only supports range values (e.g. year = 1999; not 
-- study_id = rif40_sql_pkg._rif40_hash_bucket_check(study_id, 16 /* total buckets */, 15 /* bucket requested */)
--
-- Need a simpler example and look at the code in predtest.c
--
--'  WHERE '||quote_ident(l_column)||'::VARCHAR = ''1''/* ||min_first_part_value */'||E'\n'||
	sql_stmt:='WITH a AS (SELECT rif40_sql_pkg._rif40_hash('||min_value||'::VARCHAR, 16) AS part_no )'||E'\n'||
	        'SELECT b.*, rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, 16) AS hash,'||E'\n'||
	        '         rif40_sql_pkg._rif40_hash_bucket_check('||quote_ident(l_column)||', 16, '||E'\n'||
	        '               rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, 16)) AS hash_check'||E'\n'||
		'  FROM '||quote_ident(l_schema)||'.'||quote_ident(l_table)||' b, a'||E'\n'||
		' WHERE '||quote_ident(l_column)||' = '||min_value||E'\n'||
		'   AND rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, 16) = hash_partition_number'||E'\n'||
		'   AND hash_partition_number = a.part_no'||E'\n'||
--		'   AND 8 = hash_partition_number'||E'\n'|| /* Eliminates */
	        ' ORDER BY 1 LIMIT 10'; 
--
-- Turn on parser debugging. This appears mainly in the eventlog/syslog; not stderr
--
-- debug_print_parse (boolean)
-- debug_print_rewritten (boolean)
-- debug_print_plan (boolean)
-- 
/*
	ddl_stmt:=NULL;
	ddl_stmt[1]:='SET SESSION client_min_messages = DEBUG5';
	ddl_stmt[array_length(ddl_stmt, 1)+1]:='SET SESSION log_min_messages = DEBUG5';
	ddl_stmt[array_length(ddl_stmt, 1)+1]:='SET SESSION debug_print_parse = TRUE';
	ddl_stmt[array_length(ddl_stmt, 1)+1]:='SET SESSION debug_print_rewritten = TRUE';
	ddl_stmt[array_length(ddl_stmt, 1)+1]:='SET SESSION debug_print_plan = TRUE';
	PERFORM rif40_sql_pkg.rif40_ddl(ddl_stmt);
 */
--
-- This will eliminate; at a cost in all queries needing to use this format
-- rif40_sql_pkg._rif40_hash(study_id) = hash_partition_number will NOT eliminate;  almost certainly because
-- it cannot be evavulated at parse time; this is also true of the earlier CHECK constraint which also does not work.
-- CONSTRAINT rif40_study_shares_p15_ck CHECK (study_id = rif40_sql_pkg._rif40_hash_bucket_check(study_id, 16 /* total buckets */, 15 /* bucket requested */))
--
	sql_stmt:='SELECT *, rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, 16) AS hash,'||E'\n'||
	        '         rif40_sql_pkg._rif40_hash_bucket_check('||quote_ident(l_column)||', 16, '||E'\n'||
	        '               rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, 16)) AS hash_check'||E'\n'||
		'  FROM '||quote_ident(l_schema)||'.'||quote_ident(l_table)||E'\n'||
		' WHERE rif40_sql_pkg._rif40_hash('||min_value||'::VARCHAR, 16) = hash_partition_number /* Elininates */'||E'\n'||
		'   AND '||quote_ident(l_column)||' = '||min_value||E'\n'||
	        ' ORDER BY 1 LIMIT 10'; 
	PERFORM rif40_sql_pkg.rif40_method4(sql_stmt, 'Partition EXPLAIN test 2 (Elinination)');
/*
psql:../psql_scripts/v4_0_study_id_partitions.sql:145: INFO:  [DEBUG1] rif40_ddl(): Limit  (cost=0.00..1.58 rows=2 width=210) (actual time=0.085..0.087 rows=1 loops=1)
  Output: rif40_study_shares.study_id, rif40_study_shares.grantor, rif40_study_shares.grantee_username, rif40_study_shares.hash_partition_number, (((abs(hashtext(((rif40_study_shar
es.study_id)::character varying)::text)) % 16) + 1)), (_rif40_hash_bucket_check(rif40_study_shares.study_id, 16, ((abs(hashtext(((rif40_study_shares.study_id)::character varying)::
text)) % 16) + 1)))
  ->  Result  (cost=0.00..1.58 rows=2 width=210) (actual time=0.082..0.083 rows=1 loops=1)
        Output: rif40_study_shares.study_id, rif40_study_shares.grantor, rif40_study_shares.grantee_username, rif40_study_shares.hash_partition_number, ((abs(hashtext(((rif40_study
_shares.study_id)::character varying)::text)) % 16) + 1), _rif40_hash_bucket_check(rif40_study_shares.study_id, 16, ((abs(hashtext(((rif40_study_shares.study_id)::character varying
)::text)) % 16) + 1))
        ->  Append  (cost=0.00..1.01 rows=2 width=210) (actual time=0.007..0.008 rows=1 loops=1)
              ->  Seq Scan on rif40.rif40_study_shares  (cost=0.00..0.00 rows=1 width=404) (actual time=0.001..0.001 rows=0 loops=1)
                    Output: rif40_study_shares.study_id, rif40_study_shares.grantor, rif40_study_shares.grantee_username, rif40_study_shares.hash_partition_number
                    Filter: ((8 = rif40_study_shares.hash_partition_number) AND (rif40_study_shares.study_id = 1))
              ->  Seq Scan on rif40.rif40_study_shares_p8  (cost=0.00..1.01 rows=1 width=16) (actual time=0.005..0.006 rows=1 loops=1)
                    Output: rif40_study_shares_p8.study_id, rif40_study_shares_p8.grantor, rif40_study_shares_p8.grantee_username, rif40_study_shares_p8.hash_partition_number
                    Filter: ((8 = rif40_study_shares_p8.hash_partition_number) AND (rif40_study_shares_p8.study_id = 1))
Total runtime: 1.310 ms
 */
--
-- Used to halt alter_1.sql for testing
--
--	RAISE plpgsql_error;
END;
$func$ LANGUAGE plpgsql;

COMMENT ON FUNCTION rif40_sql_pkg.rif40_hash_partition(VARCHAR, VARCHAR, VARCHAR, INTEGER) IS 'Function: 	rif40_hash_partition()
Parameters:	Schema, table, columnn, number of partitions
Returns:	Nothing
Description:	Hash partition schema.table on column
';

CREATE OR REPLACE FUNCTION rif40_sql_pkg._rif40_hash_partition_create(
	l_schema 	VARCHAR, 
	master_table 	VARCHAR, 
	partition_table VARCHAR, 
	l_column	VARCHAR, 
	l_value		INTEGER,
	num_partitions	INTEGER)
RETURNS void
SECURITY DEFINER
AS $func$
/*
Function: 	_rif40_hash_partition_create()
Parameters:	Schema, master table, partition table, column, hash value, total partitions
Returns:	Nothing
Description:	Create hash partition schema.table_<value> on column <column> value <value>, inheriting from <mnaster table>.
		Comment columns

Runs as RIF40 (so can create partition tables)

Generates the following SQL to create a partition>
	
CREATE TABLE rif40_study_shares_p15 (
 CONSTRAINT rif40_study_shares_p15_ck CHECK (hash_partition_number = 15 /- bucket requested -/)
) INHERITS (rif40_study_shares);

Call rif40_sql_pkg._rif40_common_partition_create to:

* Add indexes, primary key
* Add foreign keys
* Add trigger, unique, check and exclusion constraints
* Validation triggers
* Add grants
* Table and column comments

 */
DECLARE
	ddl_stmt VARCHAR[];
--
BEGIN
--
-- Must be rif40 or have rif_user or rif_manager role
--
	IF USER != 'rif40' AND NOT rif40_sql_pkg.is_rif40_user_manager_or_schema() THEN
		PERFORM rif40_log_pkg.rif40_error(-20999, '_rif40_hash_partition_create', 'User % must be rif40 or have rif_user or rif_manager role', 
			USER::VARCHAR);
	END IF;
--
	PERFORM rif40_log_pkg.rif40_log('INFO', '_rif40_hash_partition_create', 
		'Create hash partition: % for hash value % (of %) on column: %; master: %.%', 
		partition_table::VARCHAR	/* Partition table */,
		l_value::VARCHAR		/* Partition hash value */,
		num_partitions::VARCHAR		/* Total partitions */,
		l_column::VARCHAR		/* Partition column */,
		l_schema::VARCHAR		/* Schema */, 
		master_table::VARCHAR		/* Master table inheriting from */);

--
-- Create partition table inheriting from master
--
--	IF l_value ~ '^[0-9]*.?[0-9]*$' THEN /* isnumeric */	
-- May need type specific _rif40_hash_bucket_check functions to avoid implicit cast which may break the equality checks in partition elimination
--
	ddl_stmt[1]:='CREATE TABLE '||quote_ident(partition_table)||' ('||E'\n'||
--		' CONSTRAINT '||quote_ident(partition_table||'_ck')||' CHECK ('''||l_value||''' = rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, '||num_partitions::Text||')::VARCHAR)'||E'\n'||
--		' CONSTRAINT '||quote_ident(partition_table||'_ck')||' CHECK ('||l_column||' = rif40_sql_pkg._rif40_hash_bucket_check('||quote_ident(l_column)||', '||num_partitions||' /* total buckets */, '||l_value||' /* bucket requested */))'||E'\n'||
		' CONSTRAINT '||quote_ident(partition_table||'_ck')||' CHECK (hash_partition_number = '||l_value||' /* bucket requested */)'||E'\n'||
		') INHERITS ('||quote_ident(master_table)||')';
--
-- Run
--
	PERFORM rif40_sql_pkg.rif40_ddl(ddl_stmt);
	ddl_stmt:=NULL;
--
-- Call rif40_sql_pkg._rif40_common_partition_create to:
-- * Add indexes, primary key
-- * Add foreign keys
-- * Add trigger, unique, check and exclusion constraints
-- * Validation triggers
-- * Add grants
-- * Table and column comments
--
	PERFORM rif40_sql_pkg._rif40_common_partition_create(l_schema, master_table, partition_table, l_column, l_value::VARCHAR);

END;
$func$ 
LANGUAGE plpgsql;

COMMENT ON FUNCTION rif40_sql_pkg._rif40_hash_partition_create(VARCHAR, VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER) IS 'Function: 	_rif40_hash_partition_create()
Parameters:	Schema, master table, partition table, column, hash value, number of partitions
Returns:	Nothing
Description:	Create hash partition schema.table_<value> on column <column> value <value>, inheriting from <mnaster table>.
		Comment columns

Runs as RIF40 (so can create partition tables)

Generates the following SQL to create a partition>
	
CREATE TABLE rif40_study_shares_p15 (
 CONSTRAINT rif40_study_shares_p15_ck CHECK (hash_partition_number = 15 /* bucket requested */)
) INHERITS (rif40_study_shares);

Call rif40_sql_pkg._rif40_common_partition_create to:

* Add indexes, primary key
* Add foreign keys
* Add trigger, unique, check and exclusion constraints
* Validation triggers
* Add grants
* Table and column comments';

CREATE OR REPLACE FUNCTION rif40_sql_pkg._rif40_hash_partition_create_insert(l_schema VARCHAR, l_table VARCHAR, l_column VARCHAR, 
	total_rows INTEGER, num_partitions INTEGER,
	OUT ddl_stmt VARCHAR[], OUT index_name VARCHAR)
RETURNS RECORD
SECURITY DEFINER
AS $func$
/*
Function: 	_rif40_hash_partition_create_insert()
Parameters:	Schema, table, column, total rows, number of partitions
                [OUT] ddl statement array, [PK/UK] index name
Returns:	OUT parameters as a record
 		DDL statement array is NULL if the function is unable to partition
Description:	Automatic range/hash partition schema.table on column
		INSERT

* Foreach partition:
+	INSERT 1 rows. This creates the partition

	INSERT INTO sahsuland_cancer /- Create partition 1989 -/
	SELECT * FROM rif40_hash_partition /- Temporary table -/
	 WHERE year = '1989'
	 LIMIT 1;

+	TRUNCATE partition

	TRUNCATE TABLE rif40.sahsuland_cancer_1989 /- Empty newly created partition 1989 -/;

+ 	Bring data back by partition, order by range partition, primary key

	INSERT INTO sahsuland_cancer_1989 /- Directly populate partition: 1989, total rows expected: 8103 -/
	SELECT * FROM rif40_hash_partition /- Temporary table -/
	 WHERE year = '1989'
	 ORDER BY year /- Partition column -/, age_sex_group, icd, level4 /- [Rest of ] primary key -/;

* The trigger created earlier fires and calls sahsuland_cancer_insert();
  This then call _rif40_hash_partition_create() for the first row in a partition (detected by trapping the undefined_table EXCEPTION 
  e.g. 42p01: relation "rif40.rif40_population_europe_1991" does not exist) 

psql:../psql_scripts/v4_0_year_partitions.sql:150: INFO:  _rif40_hash_partition_create(): Create range partition: sahsuland_cancer_1989 for value 1989 on column: year; master: rif40.sahsuland_cancer

  The trigger then re-fires to redo the bind insert. NEW.<column name> must be explicitly defined unlike in conventional INSERT triggers

psql:../psql_scripts/v4_0_year_partitions.sql:150: INFO:  [DEBUG1] sahsuland_cancer_insert(): Row 1 SQL> EXECUTE 
'INSERT INTO sahsuland_cancer_1989 VALUES ($1, $2, $3, $4, $5, $6, $7, $8) /- Partition: 1989 -/' 
USING NEW.year, NEW.age_sex_group, NEW.level1, NEW.level2, NEW.level3, NEW.level4, NEW.icd, NEW.total; 
/- rec: (1989,100,01,01.008,01.008.006800,01.008.006800.1,1890,2) -/

 */
DECLARE
	c3gangep CURSOR(l_schema VARCHAR, l_table VARCHAR, l_column VARCHAR) FOR /* GET PK/unique index column */
		SELECT n.nspname AS schema_name, t.relname AS table_name, 
		       i.relname AS index_name, array_to_string(array_agg(a.attname), ', ') AS column_names, ix.indisprimary
		 FROM pg_class t, pg_class i, pg_index ix, pg_attribute a, pg_namespace n
		 WHERE t.oid          = ix.indrelid
		   AND i.oid          = ix.indexrelid
		   AND a.attrelid     = t.oid
		   AND a.attnum       = ANY(ix.indkey)
		   AND t.relkind      = 'r'
		   AND ix.indisunique = TRUE
		   AND t.relnamespace = n.oid 
		   AND n.nspname      = l_schema
		   AND t.relname      = l_table
		   AND a.attname      != l_column
		 GROUP BY n.nspname, t.relname, i.relname, ix.indisprimary
		 ORDER BY n.nspname, t.relname, i.relname, ix.indisprimary DESC;
	c3_rec 			RECORD;
	c6_rec 			RECORD;
--
	sql_stmt		VARCHAR;
	l_ddl_stmt		VARCHAR[];
	first_partition		VARCHAR;
	min_first_part_value	VARCHAR;
	first_hash		INTEGER;
--
	error_message 		VARCHAR;
	v_detail 		VARCHAR:='(Not supported until 9.2; type SQL statement into psql to see remote error)';
BEGIN
--
-- Must be rif40 or have rif_user or rif_manager role
--
	IF USER != 'rif40' AND NOT rif40_sql_pkg.is_rif40_user_manager_or_schema() THEN
		PERFORM rif40_log_pkg.rif40_error(-20999, '_rif40_hash_partition_create_insert', 'User % must be rif40 or have rif_user or rif_manager role', 
			USER::VARCHAR);
	END IF;
--
-- Disable ON-INSERT triggers to avoid:
--
-- /* psql:../psql_scripts/v4_0_study_id_partitions.sql:139: WARNING:  rif40_ddl(): SQL in error (P0001)> INSERT INTO rif40_study_shares /* Create partition 1 */
-- SELECT * FROM rif40_auto_partition /* Temporary table */
--  WHERE study_id = '1'
--  LIMIT 1;
-- psql:../psql_scripts/v4_0_study_id_partitions.sql:139: ERROR:  rif40_trg_pkg.trigger_fct_rif40_study_shares_checks(): RIF40_STUDY_SHARES study_id: 1 grantor username: pch is not USER: rif40 or a RIF40_MANAGER
--
	l_ddl_stmt:=rif40_sql_pkg._rif40_common_partition_triggers(l_schema, l_table, l_column, 'DISABLE'::VARCHAR);
	IF l_ddl_stmt IS NOT NULL THEN
--
-- Copy out parameters
--
		FOR i IN 1 .. array_length(l_ddl_stmt, 1) LOOP
			ddl_stmt[i]:=l_ddl_stmt[i];
		END LOOP;
	END IF;


--
-- GET PK/unique index column
--
	OPEN c3gangep(l_schema, l_table, l_column);
	FETCH c3gangep INTO c3_rec;
	CLOSE c3gangep;
	index_name:=c3_rec.index_name;
--
	PERFORM rif40_log_pkg.rif40_log('DEBUG1', '_rif40_hash_partition_create_insert', 'Restore data from temporary table: %.%', 
		l_schema::VARCHAR, l_table::VARCHAR);
--
-- Create list of potential partitions
--
	IF total_rows > 0 THEN
		BEGIN
			sql_stmt:='SELECT rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, '||
				num_partitions||') AS partition_value, '||
			        '         MIN('||quote_ident(l_column)||'::VARCHAR) AS min_first_part_value,'||E'\n'||
			        '         COUNT('||quote_ident(l_column)||') AS total_rows'||E'\n'||
				'  FROM '||quote_ident(l_schema)||'.'||quote_ident(l_table)||E'\n'||
				' GROUP BY rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, '||num_partitions||')'||E'\n'||
				' ORDER BY 1'; 
			PERFORM rif40_sql_pkg.rif40_method4(sql_stmt, 'Partition EXPLAIN test');
			
			PERFORM rif40_log_pkg.rif40_log('DEBUG1', '_rif40_hash_partition_create_insert', 'SQL> %;', sql_stmt::VARCHAR);
			FOR c6_rec IN EXECUTE sql_stmt LOOP
				IF first_hash IS NULL THEN
					first_hash:=c6_rec.partition_value;
					min_first_part_value:=c6_rec.min_first_part_value;
				END IF;

--				
-- Bring data back, order by range partition, primary key
--
				IF c3_rec.column_names IS NOT NULL THEN
					ddl_stmt[array_length(ddl_stmt, 1)+1]:='INSERT INTO '||quote_ident(l_table)||'_p'||c6_rec.partition_value||
						' /* Directly populate partition: p'||c6_rec.partition_value||
						', total rows expected: '||c6_rec.total_rows||' */'||E'\n'||
						'SELECT *'||E'\n'||
						'  FROM rif40_auto_partition /* Temporary table */'||E'\n'||
						' WHERE rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, '||num_partitions||')::VARCHAR = '''||c6_rec.partition_value||''''||E'\n'||
						' ORDER BY '||l_column||' /* Partition column */, '||
						c3_rec.column_names||' /* [Rest of ] primary key */';
				ELSE
					ddl_stmt[array_length(ddl_stmt, 1)+1]:='INSERT INTO '||quote_ident(l_table)||'_p'||c6_rec.partition_value||
						' /* Directly populate partition: p'||c6_rec.partition_value||
						', total rows expected: '||c6_rec.total_rows||' */'||E'\n'||
						'SELECT * FROM rif40_auto_partition /* Temporary table */'||E'\n'||
						' WHERE rif40_sql_pkg._rif40_hash('||quote_ident(l_column)||'::VARCHAR, '||num_partitions||')::VARCHAR = '''||c6_rec.partition_value||''''||E'\n'||
						' ORDER BY '||l_column||' /* Partition column */, '||
						' /* NO [Rest of ] primary key - no unique index found */';
				END IF;
				ddl_stmt[array_length(ddl_stmt, 1)+1]:='UPDATE '||quote_ident(l_table)||'_p'||c6_rec.partition_value||E'\n'||
					'SET hash_partition_number = '||c6_rec.partition_value;
--
			END LOOP;	
		EXCEPTION
			WHEN others THEN
				GET STACKED DIAGNOSTICS v_detail = PG_EXCEPTION_DETAIL;
				error_message:='_rif40_hash_partition_create_insert() caught: '||E'\n'||
					SQLERRM::VARCHAR||' in SQL> '||sql_stmt||E'\n'||'Detail: '||v_detail::VARCHAR;
				RAISE INFO '2: %', error_message;
--
				RAISE;
		END;

	END IF;
--
-- Re-enable ON-INSERT triggers
--
	l_ddl_stmt:=rif40_sql_pkg._rif40_common_partition_triggers(l_schema, l_table, l_column, 'ENABLE'::VARCHAR);
	IF l_ddl_stmt IS NOT NULL THEN
		FOR i IN 1 .. array_length(l_ddl_stmt, 1) LOOP
			ddl_stmt[array_length(ddl_stmt, 1)+1]:=l_ddl_stmt[i];
		END LOOP;
	END IF;
--
-- Add functional index on hash function to add sub partitions
--
	l_ddl_stmt:=rif40_sql_pkg._rif40_hash_partition_functional_index(l_schema, l_table, l_column, num_partitions);
	IF l_ddl_stmt IS NOT NULL THEN
--
-- Copy out parameters
--
		FOR i IN 1 .. array_length(l_ddl_stmt, 1) LOOP
			ddl_stmt[array_length(ddl_stmt, 1)+1]:=l_ddl_stmt[i];
		END LOOP;
	END IF;

--
END;
$func$ 
LANGUAGE plpgsql;

COMMENT ON FUNCTION rif40_sql_pkg._rif40_hash_partition_create_insert(VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER,
	OUT ddl_stmt VARCHAR[], OUT index_name VARCHAR) IS 'Function: 	_rif40_hash_partition_create_insert()
Parameters:	Schema, table, column, total rows, number of partitions,
                [OUT] ddl statement array, [PK/UK] index name
Returns:	OUT parameters as a record
 		DDL statement array is NULL if the function is unable to partition
Description:	Automatic range/hash partition schema.table on column
		INSERT

* Foreach partition:
Call: _rif40_hash_partition_create_insert()

* Foreach partition:
+	INSERT 1 rows. This creates the partition
+	TRUNCATE partition
+ 	Bring data back by partition, order by range partition, primary key
[End of _rif40_hash_partition_create_insert()]';

--
-- Eof
