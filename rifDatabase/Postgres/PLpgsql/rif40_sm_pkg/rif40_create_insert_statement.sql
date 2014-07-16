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
--     				  rif40_insert_extract
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
-- rif40_create_insert_statement: 		56000 to 56199
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

CREATE OR REPLACE FUNCTION rif40_sm_pkg.rif40_create_insert_statement(study_id INTEGER, study_or_comparison VARCHAR, 
	year_start INTEGER DEFAULT NULL, year_stop INTEGER DEFAULT NULL)
RETURNS VARCHAR
SECURITY INVOKER
AS $func$
DECLARE
/*
Function:	rif40_create_insert_statement()
Parameter:	Study ID, study or comparison (S/C), year_start, year_stop
Returns:	SQL statement
Description:	Create INSERT SQL statement
 */
	c1insext CURSOR(l_study_id INTEGER) FOR
		SELECT * 
		  FROM rif40_studies a
		 WHERE a.study_id = l_study_id;
	c2insext CURSOR(l_table VARCHAR) FOR
		SELECT *
		  FROM information_schema.columns a
		 WHERE a.table_schema = 'rif_studies'
		   AND a.table_name   = l_table
		 ORDER BY a.ordinal_position;
	c3insext CURSOR(l_study_id INTEGER) FOR
		SELECT COUNT(DISTINCT(numer_tab)) AS distinct_numerators
		  FROM rif40_investigations a
		 WHERE a.study_id = l_study_id;
	c4insext CURSOR(l_study_id INTEGER) FOR
		WITH b AS (
			SELECT DISTINCT(a.numer_tab) AS numer_tab
			  FROM rif40_investigations a
		 	 WHERE a.study_id   = l_study_id
		)
		SELECT b.numer_tab,
 		       t.description, t.total_field, t.year_start, t.year_stop, 
   		       t.sex_field_name, t.age_group_field_name, t.age_sex_group_field_name, t.age_group_id
		  FROM b, rif40_tables t 
		 WHERE t.table_name = b.numer_tab;
	c5insext CURSOR(l_study_id INTEGER, l_numer_tab VARCHAR) FOR
		SELECT *
		  FROM rif40_investigations a
		 WHERE a.study_id  = l_study_id
		   AND a.numer_tab = l_numer_tab
 		 ORDER BY inv_id;
	c6insext CURSOR(l_study_id INTEGER, l_inv_id INTEGER) FOR
		SELECT *
		  FROM rif40_inv_conditions a
		 WHERE a.study_id = l_study_id
		   AND a.inv_id = l_inv_id
 		 ORDER BY inv_id, line_number;
	c7insext CURSOR(l_study_id INTEGER) FOR
		SELECT DISTINCT covariate_name AS covariate_name
		  FROM rif40_inv_covariates a
		 WHERE a.study_id = l_study_id
 		 ORDER BY covariate_name;
	c8insext CURSOR(l_study_id INTEGER) FOR
		SELECT b.denom_tab,
 		       t.description, t.total_field, t.year_start, t.year_stop,
   		       t.sex_field_name, t.age_group_field_name, t.age_sex_group_field_name, t.age_group_id, 
		       MIN(a.offset) AS min_age_group, MAX(a.offset) AS max_age_group
		  FROM rif40_studies b, rif40_tables t, rif40_age_groups a
		 WHERE t.table_name   = b.denom_tab
		   AND t.age_group_id = a.age_group_id
		   AND b.study_id     = l_study_id
		 GROUP BY b.denom_tab,
 		       t.description, t.total_field, t.year_start, t.year_stop,
   		       t.sex_field_name, t.age_group_field_name, t.age_sex_group_field_name, t.age_group_id;
	c1_rec RECORD;
	c2_rec RECORD;
	c3_rec RECORD;
	c4_rec RECORD;
	c5_rec RECORD;
	c6_rec RECORD;
	c7_rec RECORD;
	c8_rec RECORD;
--
	sql_stmt	VARCHAR;
	i		INTEGER:=0;
	j		INTEGER:=0;
	k		INTEGER:=0;
	inv_array	VARCHAR[];
	inv_join_array	VARCHAR[];
--
	areas_table	VARCHAR:='g_rif40_study_areas';
BEGIN
--
-- Use different areas_table for comparision (it has no band_id)
--	
	IF study_or_comparison = 'C' THEN
		areas_table:='g_rif40_comparison_areas';
	END IF;
	OPEN c1insext(study_id);
	FETCH c1insext INTO c1_rec;
	IF NOT FOUND THEN
		CLOSE c1insext;
		PERFORM rif40_log_pkg.rif40_error(-56000, 'rif40_create_insert_statement', 
			'Study ID % not found',
			study_id::VARCHAR		/* Study ID */);
	END IF;
	CLOSE c1insext;
--
-- Create INSERT statement
-- 
	sql_stmt:='INSERT INTO '||LOWER(c1_rec.extract_table)||' ('||E'\n';
--
-- Add columns
-- 
	FOR c2_rec IN c2insext(LOWER(c1_rec.extract_table)) LOOP
		i:=i+1;
		IF i = 1 THEN
			sql_stmt:=sql_stmt||E'\t'||c2_rec.column_name;
		ELSE
			sql_stmt:=sql_stmt||','||c2_rec.column_name;
		END IF;
	END LOOP;
	IF i = 0 THEN
		PERFORM rif40_log_pkg.rif40_error(-56001, 'rif40_create_insert_statement', 
			'Study ID % no columns found for extract table: %',
			study_id::VARCHAR		/* Study ID */,		
			c1_rec.extract_table::VARCHAR 	/* Extract table */);
	END IF;
--
-- Get number of distinct numerators
--
	OPEN c3insext(study_id);
	FETCH c3insext INTO c3_rec;
	IF NOT FOUND THEN
		CLOSE c3insext;
		PERFORM rif40_log_pkg.rif40_error(-56002, 'rif40_create_insert_statement', 
			'Study ID % not found',
			study_id::VARCHAR		/* Study ID */);
	END IF;
	CLOSE c3insext;
	sql_stmt:=sql_stmt||') /* '||c3_rec.distinct_numerators::VARCHAR||' numerator(s) */'||E'\n';
--
-- Get denominator setup
--
	OPEN c8insext(study_id);
	FETCH c8insext INTO c8_rec;
	IF NOT FOUND THEN
		CLOSE c8insext;
		PERFORM rif40_log_pkg.rif40_error(-56003, 'rif40_create_insert_statement', 
			'Study ID % not found',
			study_id::VARCHAR		/* Study ID */);
	END IF;
	CLOSE c8insext;
--
--
-- Loop through distinct numerators
--
	i:=0;
	FOR c4_rec IN c4insext(study_id) LOOP
		i:=i+1;
--
-- Open WITH clause (common table expression)
-- 
		IF i = 1 THEN
			sql_stmt:=sql_stmt||'WITH n'||i::VARCHAR||' AS ('||E'\t'||'/* '||c4_rec.numer_tab||' - '||c4_rec.description||' */'||E'\n';
		ELSE
			sql_stmt:=sql_stmt||', n'||i::VARCHAR||' AS ('||E'\t'||'/* '||c4_rec.numer_tab||' - '||c4_rec.description||' */'||E'\n';
		END IF;
--
-- Numerator JOINS
--
		inv_join_array[i]:=E'\t'||'LEFT OUTER JOIN n'||i::VARCHAR||' ON ( '||
			E'\t'||'/* '||c4_rec.numer_tab||' - '||c4_rec.description||' */'||E'\n'||
			E'\t'||E'\t'||'    d.area_id'||E'\t'||E'\t'||' = n'||i::VARCHAR||'.area_id'||E'\n'||
			E'\t'||E'\t'||'AND d.year'||E'\t'||E'\t'||' = n'||i::VARCHAR||'.year'||E'\n'||
--
-- [Add conversion support for differing age/sex/group names; convert to AGE_SEX_GROUP]
--
			E'\t'||E'\t'||'AND d.'||LOWER(c8_rec.age_sex_group_field_name)||E'\t'||' = n'||i::VARCHAR||'.n_age_sex_group)';
				/* List of numerator joins (for use in FROM clause) */
		sql_stmt:=sql_stmt||E'\t'||'SELECT s.area_id'||E'\t'||E'\t'||'/* Study or comparision resolution */,'||E'\n';
		sql_stmt:=sql_stmt||E'\t'||'       c.year,'||E'\n';
--
-- [Add support for differing age/sex/group names; convert to AGE_SEX_GROUP]
--
		sql_stmt:=sql_stmt||E'\t'||'       c.'||LOWER(c4_rec.age_sex_group_field_name)||' AS n_age_sex_group,'||E'\n';
--
-- Individual investigations [add age group/sex/year filters]
-- 
		FOR c5_rec IN c5insext(study_id, c4_rec.numer_tab) LOOP
			j:=j+1;
			inv_array[j]:='       COALESCE('||
				'n'||i::VARCHAR||'.inv_'||c5_rec.inv_id::VARCHAR||'_'||LOWER(c5_rec.inv_name)||
				', 0) AS inv_'||c5_rec.inv_id::VARCHAR||'_'||LOWER(c5_rec.inv_name); 
				/* List of investigations (for use in final SELECT) */
			IF j > 1 THEN
				sql_stmt:=sql_stmt||','||E'\n';
			END IF;
			sql_stmt:=sql_stmt||E'\t'||'       SUM(CASE '||E'\t'||E'\t'||'/* Numerators - can overlap */'||E'\n';
			sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'WHEN (('||E'\t'||'/* Investigation '||j::VARCHAR||' ICD filters */'||E'\n';
--
-- Add conditions
--
			k:=0;
			FOR c6_rec IN c6insext(study_id, c5_rec.inv_id) LOOP
				k:=k+1;
				IF k = 1 THEN
					sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||E'\t'||'    '||c6_rec.condition||' /* Filter '||k::VARCHAR||' */';
				ELSE
					sql_stmt:=sql_stmt||E'\n'||E'\t'||E'\t'||E'\t'||E'\t'||' OR '||c6_rec.condition||' /* Filter '||k::VARCHAR||' */';
				END IF;
			END LOOP;
			sql_stmt:=sql_stmt||') /* '||k::VARCHAR||' lines of conditions: study: '||
				study_id::VARCHAR||', inv: '||c5_rec.inv_id::VARCHAR||' */'||E'\n';
			sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'AND (1=1'||E'\n';
--
-- Processing years filter
--
/*			IF year_start = year_stop THEN
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'   AND (c.year = $2'||E'\t'||'-* Denominator (INSERT) year filter *-'||E'\n';
			ELSE
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'   AND (c.year BETWEEN $2 AND $3'||E'\t'||'-* Denominator (INSERT) year filter *-'||E'\n';
			END IF; */
--
-- Investigation filters: year, age group, genders
--
			IF c5_rec.year_start = c5_rec.year_stop THEN
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'   AND  c.year = '||c5_rec.year_start::VARCHAR||E'\n';
			ELSIF c4_rec.year_start = c5_rec.year_start AND c4_rec.year_stop = c5_rec.year_stop THEN
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||E'\t'||'        /* No year filter required for investigation '||j::VARCHAR||' */'||E'\n';
			ELSE
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'   AND  c.year BETWEEN '||c5_rec.year_start::VARCHAR||
					' AND '||c5_rec.year_stop::VARCHAR||E'\n';
			END IF;
			IF c5_rec.genders = 3 THEN
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||E'\t'||'        /* No genders filter required for investigation '||j::VARCHAR||' */'||E'\n';
			ELSE
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'   AND  TRUNC(c.'||LOWER(c4_rec.age_sex_group_field_name)||'/100) = '||c5_rec.genders::VARCHAR||E'\n';
			END IF;
			IF c8_rec.min_age_group = c5_rec.min_age_group AND c8_rec.max_age_group = c5_rec.max_age_group THEN
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||E'\t'||'        /* No age group filter required for investigation '||j::VARCHAR||' */)'||E'\n';
			ELSE 
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'   AND  MOD(c.'||LOWER(c4_rec.age_sex_group_field_name)||', 100) BETWEEN '||
					c5_rec.min_age_group::VARCHAR||' AND '||c5_rec.max_age_group::VARCHAR||
					' /* Investigation '||j::VARCHAR||' year, age group filter */)'||E'\n';
			END IF;
--
			IF c4_rec.total_field IS NULL THEN /* Handle total fields */
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||') THEN 1'||E'\n';
			ELSE
				sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||') THEN '||LOWER(c4_rec.total_field)||E'\n';
			END IF;
			sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'ELSE 0'||E'\n';
			sql_stmt:=sql_stmt||E'\t'||'       END) inv_'||c5_rec.inv_id::VARCHAR||'_'||LOWER(c5_rec.inv_name)||
				E'\t'||'/* Investigation '||j::VARCHAR||' - '||c5_rec.inv_description||' */ ';
		END LOOP;
--
-- Check at least one investigation
--
		IF j = 0 THEN
			PERFORM rif40_log_pkg.rif40_error(-56004, 'rif40_create_insert_statement', 
				'Study ID % no investigations created: distinct numerator: %',
				study_id::VARCHAR		/* Study ID */,		
				c4_rec.numer_tab::VARCHAR 	/* Distinct numerators */);
		END IF;
		sql_stmt:=sql_stmt||E'\n';

--
-- From clause
--
		sql_stmt:=sql_stmt||E'\t'||'  FROM '||LOWER(c4_rec.numer_tab)||' c, '||E'\t'||'/* '||c4_rec.description||' */'||E'\n';
		sql_stmt:=sql_stmt||E'\t'||'       '||areas_table||' s '||E'\t'||'/* Study or comparision area to be extracted */'||E'\n';
		IF study_or_comparison = 'C' THEN
			sql_stmt:=sql_stmt||E'\t'||' WHERE c.'||LOWER(c1_rec.comparison_geolevel_name)||' = s.area_id '||E'\t'||'/* Comparison selection */'||E'\n';
		ELSE
			sql_stmt:=sql_stmt||E'\t'||' WHERE c.'||LOWER(c1_rec.study_geolevel_name)||' = s.area_id '||E'\t'||'/* Study selection */'||E'\n';
		END IF;
--
-- [Add correct age_sex_group limits]
--
		IF c8_rec.min_age_group = c1_rec.min_age_group AND c8_rec.max_age_group = c1_rec.max_age_group THEN
			sql_stmt:=sql_stmt||E'\t'||'       /* No age group filter required for denominator */'||E'\n';
		ELSE 
			sql_stmt:=sql_stmt||E'\t'||'   AND MOD(c.'||LOWER(c8_rec.age_sex_group_field_name)||', 100) BETWEEN '||
				c1_rec.min_age_group::VARCHAR||' AND '||c1_rec.max_age_group::VARCHAR||
				' /* All valid age groups for denominator */'||E'\n';
		END IF;
		sql_stmt:=sql_stmt||E'\t'||'   AND s.study_id = $1'||E'\t'||E'\t'||'/* Current study ID */'||E'\n';
--
-- Processing years filter
--
		IF year_start = year_stop THEN
			sql_stmt:=sql_stmt||E'\t'||'   AND c.year = $2'||E'\t'||E'\t'||'/* Denominator (INSERT) year filter */'||E'\n';
		ELSE
			sql_stmt:=sql_stmt||E'\t'||'   AND c.year BETWEEN $2 AND $3'||E'\t'||'/* Denominator (INSERT) year filter */'||E'\n';
		END IF;
--
-- Group by clause
-- [Add support for differing age/sex/group names]
--
		sql_stmt:=sql_stmt||E'\t'||' GROUP BY c.year, s.area_id, c.'||LOWER(c4_rec.age_sex_group_field_name)||E'\n';

--
-- Close WITH clause (common table expression)
-- 
		sql_stmt:=sql_stmt||') /* '||c4_rec.numer_tab||' - '||c4_rec.description||' */'||E'\n';
--
	END LOOP;
--
-- Denominator CTE with covariates joined at study geolevel
--
/* /e.g. 
, d AS (
        SELECT d1.year, s.area_id, NULL::INTEGER AS band_id, d1.age_sex_group, 
	       c.ses, 
	       SUM(COALESCE(d1.total, 0)) AS total_pop
          FROM g_rif40_comparison_areas s, sahsuland_pop d1     /- Study or comparison area to be extracted -/
  	      LEFT OUTER JOIN sahsuland_covariates_level4 c ON (        /- Covariates -/
     	               d1.level2 = c.level2				/- Join at study geolevel -/
     	           AND c.year    = $2)
         WHERE d1.year    = $2          /- Denominator (INSERT) year filter -/
           AND s.area_id  = d1.level2   /- Comparison geolevel join -/
           AND s.area_id  IS NOT NULL   /- Exclude NULL geolevel -/
           AND s.study_id = $1          /- Current study ID -/
               /- No age group filter required for denominator -/
         GROUP BY d1.year, s.area_id, d1.age_sex_group, c.ses
)
 */
	sql_stmt:=sql_stmt||', d AS ('||E'\n';
	IF study_or_comparison = 'C' THEN
		sql_stmt:=sql_stmt||E'\t'||'SELECT d1.year, s.area_id, NULL::INTEGER AS band_id, d1.'||
			quote_ident(LOWER(c8_rec.age_sex_group_field_name))||','||E'\n';
	ELSE
		sql_stmt:=sql_stmt||E'\t'||'SELECT d1.year, s.area_id, s.band_id, d1.'||
			quote_ident(LOWER(c8_rec.age_sex_group_field_name))||','||E'\n';
	END IF;
	FOR c7_rec IN c7insext(study_id) LOOP
		k:=k+1;
		sql_stmt:=sql_stmt||E'\t'||'       c.'||quote_ident(LOWER(c7_rec.covariate_name))||','||E'\n';
	END LOOP;
	sql_stmt:=sql_stmt||E'\t'||'       SUM(COALESCE(d1.'||coalesce(quote_ident(LOWER(c8_rec.total_field)), 'total')||
			', 0)) AS total_pop'||E'\n';
	sql_stmt:=sql_stmt||E'\t'||'  FROM '||quote_ident(areas_table)||' s, '||
			quote_ident(LOWER(c1_rec.denom_tab))||' d1 '||
			E'\t'||'/* Study or comparison area to be extracted */'||E'\n';
	sql_stmt:=sql_stmt||E'\t'||E'\t'||'LEFT OUTER JOIN sahsuland_covariates_level4 c ON ('||E'\t'||'/* Covariates */'||E'\n';
--
-- This is joining at the study geolevel. For comparison areas this needs to be aggregated to the comparison area
--
	sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'    d1.'||quote_ident(LOWER(c1_rec.study_geolevel_name))||
		' = c.'||quote_ident(LOWER(c1_rec.study_geolevel_name))||E'\t'||E'\t'||'/* Join at study geolevel */'||E'\n';
	sql_stmt:=sql_stmt||E'\t'||E'\t'||E'\t'||'AND c.year = $2)'||E'\n';
	sql_stmt:=sql_stmt||E'\t'||' WHERE d1.year = $2'||E'\t'||E'\t'||'/* Denominator (INSERT) year filter */'||E'\n';
	IF study_or_comparison = 'C' THEN
		sql_stmt:=sql_stmt||E'\t'||'   AND s.area_id  = d1.'||quote_ident(LOWER(c1_rec.comparison_geolevel_name))||E'\t'||'/* Comparison geolevel join */'||E'\n';
	ELSE
		sql_stmt:=sql_stmt||E'\t'||'   AND s.area_id  = d1.'||quote_ident(LOWER(c1_rec.study_geolevel_name))||E'\t'||'/* Study geolevel join */'||E'\n';
	END IF;
	sql_stmt:=sql_stmt||E'\t'||'   AND s.area_id  IS NOT NULL'||E'\t'||'/* Exclude NULL geolevel */'||E'\n';
	sql_stmt:=sql_stmt||E'\t'||'   AND s.study_id = $1'||E'\t'||E'\t'||'/* Current study ID */'||E'\n';
--
-- [Add correct age_sex_group limits]
--
	IF c8_rec.min_age_group = c1_rec.min_age_group AND c8_rec.max_age_group = c1_rec.max_age_group THEN
		sql_stmt:=sql_stmt||E'\t'||'       /* No age group filter required for denominator */'||E'\n';
	ELSE 
		sql_stmt:=sql_stmt||E'\t'||'   AND MOD(c.'||quote_ident(LOWER(c8_rec.age_sex_group_field_name))||', 100) BETWEEN '||
			c1_rec.min_age_group::VARCHAR||' AND '||c1_rec.max_age_group::VARCHAR||
			' /* All valid age groups for denominator */'||E'\n';
	END IF;
--
-- [Add gender filter]
--

--
-- Add GROUP BY clause
--
	IF study_or_comparison = 'C' THEN
		sql_stmt:=sql_stmt||E'\t'||' GROUP BY d1.year, s.area_id, '||quote_ident(LOWER(c8_rec.age_sex_group_field_name));
	ELSE
		sql_stmt:=sql_stmt||E'\t'||' GROUP BY d1.year, s.area_id, s.band_id, d1.'||quote_ident(LOWER(c8_rec.age_sex_group_field_name));
	END IF;
	FOR c7_rec IN c7insext(study_id) LOOP
		k:=k+1;
		sql_stmt:=sql_stmt||', c.'||quote_ident(LOWER(c7_rec.covariate_name));
	END LOOP;
	sql_stmt:=sql_stmt||E'\n'||') /* End of denominator */'||E'\n';
--
-- Main SQL statement
--
	sql_stmt:=sql_stmt||'SELECT d.year,'||E'\n';
	sql_stmt:=sql_stmt||'       '''||study_or_comparison||''' AS study_or_comparison,'||E'\n';
	sql_stmt:=sql_stmt||'       $1 AS study_id,'||E'\n';
	sql_stmt:=sql_stmt||'       d.area_id,'||E'\n';
	sql_stmt:=sql_stmt||'       d.band_id,'||E'\n';
--
-- [Add support for differing age/sex/group names]
--
	sql_stmt:=sql_stmt||'       TRUNC(d.'||LOWER(c8_rec.age_sex_group_field_name)||'/100) AS sex,'||E'\n';
	sql_stmt:=sql_stmt||'       MOD(d.'||LOWER(c8_rec.age_sex_group_field_name)||', 100) AS age_group,'||E'\n';
--
-- Add covariate names (Assumes 1 covariate table)
--
	k:=0;
	FOR c7_rec IN c7insext(study_id) LOOP
		k:=k+1;
--		IF study_or_comparison = 'C' THEN
--			sql_stmt:=sql_stmt||'       NULL::INTEGER AS '||LOWER(c7_rec.covariate_name)||','||E'\n';
--		ELSE
			sql_stmt:=sql_stmt||'       d.'||LOWER(c7_rec.covariate_name)||','||E'\n';
--		END IF;
	END LOOP;
--
-- Add investigations 
--
	sql_stmt:=sql_stmt||array_to_string(inv_array, ','||E'\n')||', '||E'\n';
--
-- Add denominator
--
	sql_stmt:=sql_stmt||'       d.total_pop'||E'\n';
--
-- FROM clause
--
	sql_stmt:=sql_stmt||'  FROM d'||E'\t'||E'\t'||E'\t'||'/* Denominator - '||c8_rec.description||' */'||E'\n';
	sql_stmt:=sql_stmt||array_to_string(inv_join_array, E'\n')||E'\n';

--
-- ORDER BY clause
--
	sql_stmt:=sql_stmt||' ORDER BY 1, 2, 3, 4, 5, 6, 7';
--
	PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_create_insert_statement', 
		'[56005] SQL> %;', sql_stmt::VARCHAR);
--
	RETURN sql_stmt;
--
END;
$func$
LANGUAGE 'plpgsql';

GRANT EXECUTE ON FUNCTION rif40_sm_pkg.rif40_create_insert_statement(INTEGER, VARCHAR, INTEGER, INTEGER) TO rif40;
COMMENT ON FUNCTION rif40_sm_pkg.rif40_create_insert_statement(INTEGER, VARCHAR, INTEGER, INTEGER) IS 'psql:../psql_scripts/v4_0_sahsuland_examples.sql:239: INFO:  [DEBUG1] rif40_create_insert_statement(): [56005] SQL> 
INSERT INTO s27_extract (
        year,study_or_comparison,study_id,area_id,band_id,sex,age_group,ses,inv_1,total_pop) /* 1 numerator(s) */
WITH n1 AS (    /* SAHSULAND_CANCER - Cancer cases in SAHSU land */
        SELECT s.area_id                /* Study or comparision resolution */,
               c.year,
               c.age_sex_group AS n_age_sex_group,
               SUM(CASE                 /* Numerators - can overlap */
                        WHEN (( /* Investigation 1 ICD filters */
                                    "icd" LIKE ''C34%'' OR "icd" LIKE ''162%'' /* Filter 1 */) /* 1 lines of conditions: study: 27, inv:
 24 */
                        AND (1=1
                                        /* No year filter required for investigation 1 */
                                        /* No genders filter required for investigation 1 */
                                        /* No age group filter required for investigation 1 */)
                        ) THEN total
                        ELSE 0
               END) inv_24_inv_1        /* Investigation 1 - Lung cancer */
          FROM sahsuland_cancer c,      /* Cancer cases in SAHSU land */
               g_rif40_comparison_areas s       /* Study or comparision area to be extracted */
         WHERE c.level2 = s.area_id     /* Comparison selection */
               /* No age group filter required for denominator */
           AND s.study_id = $1          /* Current study ID */
           AND c.year = $2              /* Denominator (INSERT) year filter */
         GROUP BY c.year, s.area_id, c.age_sex_group
) /* SAHSULAND_CANCER - Cancer cases in SAHSU land */
, d AS (
        SELECT d1.year, s.area_id, NULL::INTEGER AS band_id, d1.age_sex_group,
               c.ses,
               SUM(COALESCE(d1.total, 0)) AS total_pop
          FROM g_rif40_comparison_areas s, sahsuland_pop d1     /* Study or comparison area to be extracted */
                LEFT OUTER JOIN sahsuland_covariates_level4 c ON (      /* Covariates */
                            d1.level4 = c.level4                /* Join at study geolevel */
                        AND c.year = $2)
         WHERE d1.year = $2             /* Denominator (INSERT) year filter */
           AND s.area_id  = d1.level2   /* Comparison geolevel join */
           AND s.area_id  IS NOT NULL   /* Exclude NULL geolevel */
           AND s.study_id = $1          /* Current study ID */
               /* No age group filter required for denominator */
         GROUP BY d1.year, s.area_id, age_sex_group, c.ses
) /* End of denominator */
SELECT d.year,
       ''C'' AS study_or_comparison,
       $1 AS study_id,
       d.area_id,
       d.band_id,
       TRUNC(d.age_sex_group/100) AS sex,
       MOD(d.age_sex_group, 100) AS age_group,
       d.ses,
       COALESCE(n1.inv_24_inv_1, 0) AS inv_24_inv_1,
       d.total_pop
  FROM d                        /* Denominator - SAHSU land population */
        LEFT OUTER JOIN n1 ON (         /* SAHSULAND_CANCER - Cancer cases in SAHSU land */
                    d.area_id            = n1.area_id
                AND d.year               = n1.year
                AND d.age_sex_group      = n1.n_age_sex_group)
 ORDER BY 1, 2, 3, 4, 5, 6, 7;

Impact of adding covariates to comparison areas

SELECT study_or_comparison, ses, SUM(total_pop) As total_op
  FROM rif_studies.s27_extract
 GROUP BY study_or_comparison, ses
 ORDER BY study_or_comparison, ses;

OLD:

 study_or_comparison | ses | total_op
---------------------+-----+----------
 C                   |     | 83039970
 S                   | 1   | 11288390
 S                   | 2   | 18949866
 S                   | 3   | 23303680
 S                   | 4   | 19504030
 S                   | 5   |  9641896
(6 rows)

NEW:

 study_or_comparison | ses | total_op
---------------------+-----+----------
 C                   | 1   | 11315008
 C                   | 2   | 19001328
 C                   | 3   | 23445780
 C                   | 4   | 19562214
 C                   | 5   |  9715640
 S                   | 1   | 11288390
 S                   | 2   | 18949866
 S                   | 3   | 23303680
 S                   | 4   | 19504030
 S                   | 5   |  9641896
(10 rows)

SELECT study_or_comparison, SUM(total_pop) As total_op
  FROM rif_studies.s27_extract
 GROUP BY study_or_comparison
 ORDER BY study_or_comparison;

 study_or_comparison | total_op
---------------------+----------
 C                   | 83039970
 S                   | 82687862
(2 rows)

i.e. population is stable
';

--
-- Eof