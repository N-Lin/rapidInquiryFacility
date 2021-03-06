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
-- Rapid Enquiry Facility (RIF) - RIF state machine
--     							  rif40_run_study
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
-- Error codes assignment (see PLpgsql\Error_codes.txt):
--
-- rif40_sm_pkg:
--
-- rif40_run_study: 		55200 to 55399
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

CREATE OR REPLACE FUNCTION rif40_sm_pkg.rif40_run_study(study_id INTEGER, recursion_level INTEGER DEFAULT 0)
RETURNS BOOLEAN
SECURITY INVOKER
AS $func$
DECLARE
/*
Function:	rif40_run_study()
Parameter:	Study ID
Returns:	Success or failure [BOOLEAN]
		Note this is to allow SQL executed by study extraction/results created to be logged (Postgres does not allow autonomous transactions)
		Verification and error checking raises EXCEPTIONS in the usual way; and will cause the SQL log to be lost
Description:	Run study 

Check study state - 

C: created, not verfied; 
V: verified, but no other work done; 
E - extracted imported or created, but no results or maps created; 
R: results computed; 
U: upgraded record from V3.1 RIF (has an indeterminate state; probably R).

Define transition
Create extract, call: rif40_sm_pkg.rif40_create_extract()
Runs as rif40_sm_pkg NOT the user. This is so all objects created can be explicitly granted to the user
Compute results, call: rif40_sm_pkg.rif40_compute_results()
Do update. This forces verification
(i.e. change in study_State on rif40_studies calls rif40_sm_pkg.rif40_verify_state_change)
Recurse until complete
 */
	c1_runst CURSOR(l_study_id INTEGER) FOR
		WITH b AS (
			SELECT COUNT(inv_id) AS investigation_count
			  FROM rif40_investigations c
			 WHERE l_study_id = c.study_id
		)
		SELECT study_state, a.study_id, b.investigation_count 
		  FROM t_rif40_studies a, b /* MUST USE TABLE NOT VIEWS WHEN USING LOCKS/WHERE CURRENT OF */
		 WHERE l_study_id = a.study_id
		   FOR UPDATE;
	c1_rec RECORD;
--
	new_study_state 	VARCHAR;
	investigation_count 	INTEGER;
	study_count 		INTEGER;
	n_recursion_level 	INTEGER:=recursion_level+1;
--
	stp		TIMESTAMP WITH TIME ZONE := clock_timestamp();
	etp		TIMESTAMP WITH TIME ZONE;
BEGIN
--
-- Check study state and define transition
--
	OPEN c1_runst(study_id);
	FETCH c1_runst INTO c1_rec;
	IF NOT FOUND THEN
		CLOSE c1_runst;
		PERFORM rif40_log_pkg.rif40_error(-55200, 'rif40_run_study', 
			'Study ID % not found',
			study_id::VARCHAR);
	END IF;
--
	IF c1_rec.study_state = 'C' THEN
		 new_study_state = 'V';
	ELSIF c1_rec.study_state = 'V' THEN
		 new_study_state = 'E';
	ELSIF c1_rec.study_state = 'E' THEN
		 new_study_state = 'R';
	ELSE
		CLOSE c1_runst;
		PERFORM rif40_log_pkg.rif40_error(-55201, 'rif40_run_study', 
			'Study ID % cannot be run, in state: %, needs to be in ''V'' or ''E''',
			study_id::VARCHAR,
			c1_rec.study_state::VARCHAR);
	END IF;
--
-- Create extract, call: rif40_sm_pkg.rif40_create_extract()
--
	IF new_study_state = 'E' THEN
		IF rif40_sm_pkg.rif40_create_extract(c1_rec.study_id) = FALSE THEN
			PERFORM rif40_log_pkg.rif40_log('WARNING', 'rif40_run_study',
				'[55202] Call rif40_create_extract() for study % failed, see previous warnings',
				c1_rec.study_id::VARCHAR);
			CLOSE c1_runst;
			RETURN FALSE;
		ELSE
			PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_run_study',
				'[55203] Call rif40_create_extract() for study % OK',
				c1_rec.study_id::VARCHAR);
		END IF;
--
-- Compute results, call: rif40_sm_pkg.rif40_compute_results()
--
	ELSIF new_study_state = 'R' THEN
		IF rif40_sm_pkg.rif40_compute_results(c1_rec.study_id) = FALSE THEN
			PERFORM rif40_log_pkg.rif40_log('WARNING', 'rif40_run_study',
				'[55204] Call rif40_compute_results() for study % failed, see previous warnings',
				c1_rec.study_id::VARCHAR);
			CLOSE c1_runst;
			RETURN FALSE;
		ELSE
			PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_run_study',
				'[55205] Call rif40_compute_results() for study % OK',
				c1_rec.study_id::VARCHAR);
		END IF;
	END IF;

--
-- Do update. This forces verification
-- (i.e. change in study_State on rif40_studies calls rif40_sm_pkg.rif40_verify_state_change)
--
	PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_run_study',
		'[55206] Start state transition (%=>%) for study %',
		c1_rec.study_state::VARCHAR,
		new_study_state::VARCHAR,
		c1_rec.study_id::VARCHAR);
	UPDATE rif40_investigations a SET investigation_state = new_study_state WHERE a.study_id = c1_rec.study_id;
	GET DIAGNOSTICS investigation_count = ROW_COUNT;
	IF investigation_count != c1_rec.investigation_count THEN
		PERFORM rif40_log_pkg.rif40_error(-90708, 'rif40_run_study', 
			'[55207] Expecting to update % investigation(s), updated % during state transition (%=>%) for study %',
			c1_rec.investigation_count::VARCHAR,
			investigation_count::VARCHAR,
			c1_rec.study_state::VARCHAR,
			new_study_state::VARCHAR,
			c1_rec.study_id::VARCHAR);
	END IF;
--
-- MUST USE TABLE NOT VIEWS WHEN USING LOCKS/WHERE CURRENT OF
--
	UPDATE rif40_studies a SET study_state = new_study_state WHERE a.study_id = c1_rec.study_id;
	GET DIAGNOSTICS study_count = ROW_COUNT;
	etp:=clock_timestamp();
	PERFORM rif40_log_pkg.rif40_log('INFO', 'rif40_run_study',
		'[55208] Recurse [%] Completed state transition (%=>%) for study % with % investigation(s); time taken %',
		n_recursion_level::VARCHAR,
		c1_rec.study_state::VARCHAR,
		new_study_state::VARCHAR,
		c1_rec.study_id::VARCHAR,
		investigation_count::VARCHAR,
		age(etp, stp)::VARCHAR);
 
	CLOSE c1_runst;
--
-- Recurse until complete
--
	IF new_study_state != c1_rec.study_state AND new_study_state IN ('V', 'E') THEN
		PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_run_study',
			'[55209] Recurse [%] rif40_run_study using new state % for study %',
			n_recursion_level::VARCHAR,
			new_study_state::VARCHAR,
			c1_rec.study_id::VARCHAR);
		IF rif40_sm_pkg.rif40_run_study(c1_rec.study_id, n_recursion_level) = FALSE THEN /* Halt on failure */
			PERFORM rif40_log_pkg.rif40_log('WARNING', 'rif40_run_study',
				'[55210] Recurse [%] rif40_run_study to new state % for study % failed, see previous warnings',
				n_recursion_level::VARCHAR,
				new_study_state::VARCHAR,
				c1_rec.study_id::VARCHAR);
			RETURN FALSE;
		END IF;
	ELSIF new_study_state != c1_rec.study_state AND new_study_state = 'R' THEN
		PERFORM rif40_log_pkg.rif40_log('INFO', 'rif40_run_study', E'\n'||
'************************************************************************'||E'\n'||
'*                                                                      *'||E'\n'||
'* [55211] Completed study %                         *'||E'\n'||
'*                                                                      *'||E'\n'||
'************************************************************************',
			RPAD(c1_rec.study_id::VARCHAR, 20)::VARCHAR);
	ELSE
		OPEN c1_runst(study_id);
		FETCH c1_runst INTO c1_rec;
		IF NOT FOUND THEN
			CLOSE c1_runst;
			PERFORM rif40_log_pkg.rif40_error(-55212, 'rif40_run_study', 
				'Study ID % not found, study in unexpected and unknown state',
				study_id::VARCHAR);
		END IF;
		PERFORM rif40_log_pkg.rif40_error(-55213, 'rif40_run_study', 
			'Study % in unexpected state %',
			c1_rec.study_id::VARCHAR,
			c1_rec.study_state::VARCHAR);
	END IF;
--
-- All recursion unwound
--
	IF recursion_level = 0 THEN
		etp:=clock_timestamp();
		PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_run_study',
			'[55214] Recursion complete rif40_run_study study % with % investigation(s); time taken %',
			c1_rec.study_id::VARCHAR,
			investigation_count::VARCHAR,
			age(etp, stp)::VARCHAR);
	END IF;
--
	RETURN TRUE;
END;
$func$
LANGUAGE 'plpgsql';

GRANT EXECUTE ON FUNCTION rif40_sm_pkg.rif40_run_study(INTEGER, INTEGER) TO rif_manager;
GRANT EXECUTE ON FUNCTION rif40_sm_pkg.rif40_run_study(INTEGER, INTEGER) TO rif_user;
GRANT EXECUTE ON FUNCTION rif40_sm_pkg.rif40_run_study(INTEGER, INTEGER) TO rif40;
COMMENT ON FUNCTION rif40_sm_pkg.rif40_run_study(INTEGER, INTEGER) IS 'Function:	rif40_run_study()
Parameter:	Study ID
Returns:	Success or failure [BOOLEAN]
		Note this is to allow SQL executed by study extraction/results created to be logged (Postgres does not allow autonomous transactions)
		Verification and error checking raises EXCEPTIONS in the usual way; and will cause the SQL log to be lost
		
Description:	Run study 

Check study state - 

C: created, not verfied; 
V: verified, but no other work done; 
E: extracted imported or created, but no results or maps created; 
R: results computed; 
U: upgraded record from V3.1 RIF (has an indeterminate state; probably R).

Define transition
Create extract, call: rif40_sm_pkg.rif40_create_extract()
Runs as rif40_sm_pkg NOT the user. This is so all objects created can be explicitly granted to the user
Compute results, call: rif40_sm_pkg.rif40_compute_results()
Do update. This forces verification
(i.e. change in study_State on rif40_studies calls rif40_sm_pkg.rif40_verify_state_change)
Recurse until complete';

--
-- Eof
