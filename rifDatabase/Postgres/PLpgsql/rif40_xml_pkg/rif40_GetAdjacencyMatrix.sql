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
-- Rapid Enquiry Facility (RIF) - Web services integration functions for middleware
--     				  Get bounding box Y max, X max, Y min, X min for <geography> <geolevel view>
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

--
-- Error codes assignment (see PLpgsql\Error_codes.txt):
--
-- rif40_GetAdjacencyMatrix:				52000 to 52050
--
CREATE OR REPLACE FUNCTION rif40_xml_pkg.rif40_GetAdjacencyMatrix(
	l_study_id			INTEGER)
RETURNS TABLE(
	 area_id 			VARCHAR,
	 num_adjacencies 	INTEGER, 
	 adjacency_list 	VARCHAR)
SECURITY INVOKER
AS $func$
/*
Function: 	rif40_GetAdjacencyMatrix()
Parameters:	study id
Returns:	area_id, num_adjacencies, adjacency_list as a table. 
Description: Get study area adjacency matrix required by INLA	

Generates and executes SQL>

WITH b AS (
        SELECT area_id, band_id
          FROM rif40_study_areas b1
         WHERE b1.study_id = $1
), c AS (
        SELECT b.area_id, b.band_id, c1.optimised_geometry
          FROM t_rif40_sahsu_geometry c1, b
    WHERE c1.geolevel_name = $2
      AND c1.area_id       = b.area_id
), d AS (
        SELECT d1.band_id,
                   d1.area_id,
                   d2.area_id AS adjacent_area_id,
                   COUNT(d2.area_id) OVER(PARTITION BY d1.area_id ORDER BY d2.area_id) AS num_adjacencies
          FROM c d1, c d2
         WHERE d1.area_id       != d2.area_id
           AND ST_Touches(d1.optimised_geometry, d2.optimised_geometry)
), e AS (
        SELECT d.area_id::VARCHAR AS area_id, COUNT(d.area_id)::INTEGER AS num_adjacencies, string_agg(d.area_id, ',')::VARCHAR AS adjacency_list
          FROM d
         GROUP BY d.area_id
)
SELECT e.*
  FROM e
  ORDER BY 1, 2;
  
Returns a table:

SELECT * FROM rif40_xml_pkg.rif40_GetAdjacencyMatrix(1) LIMIT 10;
     area_id     | num_adjacencies |                                                 adjacency_list
-----------------+-----------------+-----------------------------------------------------------------------------------------------------------------
 01.001.000100.1 |               7 | 01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1
 01.001.000100.2 |               1 | 01.001.000100.2
 01.002.000300.1 |               3 | 01.002.000300.1,01.002.000300.1,01.002.000300.1
 01.002.000300.2 |               4 | 01.002.000300.2,01.002.000300.2,01.002.000300.2,01.002.000300.2
 01.002.000300.3 |               2 | 01.002.000300.3,01.002.000300.3
 01.002.000300.4 |               5 | 01.002.000300.4,01.002.000300.4,01.002.000300.4,01.002.000300.4,01.002.000300.4
 01.002.000300.5 |               1 | 01.002.000300.5
 01.002.000400.1 |               5 | 01.002.000400.1,01.002.000400.1,01.002.000400.1,01.002.000400.1,01.002.000400.1
 01.002.000400.2 |               3 | 01.002.000400.2,01.002.000400.2,01.002.000400.2
 01.002.000400.4 |               1 | 01.002.000400.4
(10 rows)
  
 */
 DECLARE
 	c1adjacency CURSOR(l_study_id INTEGER) FOR
		SELECT study_id, geography, study_geolevel_name
		  FROM rif40_studies
		 WHERE study_id     = l_study_id
		   AND USER         != 'rif40'
		UNION /* So can run as rif40 for testing */
		SELECT study_id, geography, study_geolevel_name
		  FROM t_rif40_studies
		 WHERE study_id     = l_study_id
		   AND USER         = 'rif40';
	c2adjacency 	REFCURSOR;
--
	c1_rec 			RECORD;
	c2_rec 			RECORD;
--
	sql_stmt 		VARCHAR;
--
	error_message 	VARCHAR;
	v_detail 		VARCHAR:='(Not supported until 9.2; type SQL statement into psql to see remote error)';	
BEGIN
--
-- Must be rif40 or have rif_user or rif_manager role
--
	IF NOT rif40_sql_pkg.is_rif40_user_manager_or_schema() THEN
		PERFORM rif40_log_pkg.rif40_error(-52000, 'rif40_GetAdjacencyMatrix', 
			'User % must be rif40 or have rif_user or rif_manager role', 
			USER::VARCHAR	/* Username */);
	END IF;
--
	OPEN c1adjacency(l_study_id);
	FETCH c1adjacency INTO c1_rec;
	CLOSE c1adjacency;
	IF c1_rec.study_id IS NULL THEN
		PERFORM rif40_log_pkg.rif40_error(-52001, 'rif40_GetAdjacencyMatrix', 
			'Study ID (%) not found.', 
			l_study_id::VARCHAR		/* Study ID */);
	END IF;
--
	sql_stmt:='WITH b AS ('||E'\n'||
'	SELECT area_id, band_id'||E'\n'||
'	  FROM rif40_study_areas b1'||E'\n'||
'	 WHERE b1.study_id = $1'||E'\n'||
'), c AS ('||E'\n'||
'	SELECT b.area_id, b.band_id, c1.optimised_geometry'||E'\n'||
'	  FROM '||quote_ident('t_rif40_'||LOWER(c1_rec.geography)||'_geometry')||' c1, b'||E'\n'||
'    WHERE c1.geolevel_name = $2'||E'\n'||
'      AND c1.area_id       = b.area_id'||E'\n'||	  
'), d AS ('||E'\n'||
'	SELECT d1.band_id,'||E'\n'||
'		   d1.area_id,'||E'\n'|| 
'		   d2.area_id AS adjacent_area_id,'||E'\n'||
'		   COUNT(d2.area_id) OVER(PARTITION BY d1.area_id ORDER BY d2.area_id) AS num_adjacencies'||E'\n'||
'	  FROM c d1, c d2'||E'\n'||
'	 WHERE d1.area_id       != d2.area_id'||E'\n'||
'	   AND ST_Touches(d1.optimised_geometry, d2.optimised_geometry)'||E'\n'||
'), e AS ('||E'\n'||
'	SELECT d.area_id::VARCHAR AS area_id, COUNT(d.area_id)::INTEGER AS num_adjacencies, string_agg(d.area_id, '','')::VARCHAR AS adjacency_list'||E'\n'||
'	  FROM d'||E'\n'||
'	 GROUP BY d.area_id'||E'\n'||
')'||E'\n'||
'SELECT e.*'||E'\n'||
'  FROM e'||E'\n'||
'  ORDER BY 1, 2';
--
	PERFORM rif40_log_pkg.rif40_log('DEBUG1', 'rif40_GetAdjacencyMatrix', '[52002] SQL>'||E'\n'||'%;', sql_stmt::VARCHAR);
--
-- Execute
--
	BEGIN
		RETURN QUERY EXECUTE sql_stmt USING c1_rec.study_id, c1_rec.study_geolevel_name;
	EXCEPTION
		WHEN others THEN
--
-- Print exception to INFO, re-raise
--
			GET STACKED DIAGNOSTICS v_detail = PG_EXCEPTION_DETAIL;
			error_message:='rif40_GetAdjacencyMatrix() caught: '||E'\n'||
				SQLERRM::VARCHAR||' in SQL> '||sql_stmt||E'\n'||'Detail: '||v_detail::VARCHAR;
			RAISE INFO '52003: %', error_message;
--
			RAISE;
	END;
	
END;
$func$
LANGUAGE PLPGSQL;

COMMENT ON FUNCTION rif40_xml_pkg.rif40_GetAdjacencyMatrix(INTEGER) IS 'Function: 	rif40_GetAdjacencyMatrix()
Parameters:	study id
Returns:	area_id, num_adjacencies, adjacency_list as a table. 
Description: Get study area adjacency matrix required by INLA	

Generates and executes SQL>

WITH b AS (
        SELECT area_id, band_id
          FROM rif40_study_areas b1
         WHERE b1.study_id = $1
), c AS (
        SELECT b.area_id, b.band_id, c1.optimised_geometry
          FROM t_rif40_sahsu_geometry c1, b
    WHERE c1.geolevel_name = $2
      AND c1.area_id       = b.area_id
), d AS (
        SELECT d1.band_id,
                   d1.area_id,
                   d2.area_id AS adjacent_area_id,
                   COUNT(d2.area_id) OVER(PARTITION BY d1.area_id ORDER BY d2.area_id) AS num_adjacencies
          FROM c d1, c d2
         WHERE d1.area_id       != d2.area_id
           AND ST_Touches(d1.optimised_geometry, d2.optimised_geometry)
), e AS (
        SELECT d.area_id::VARCHAR AS area_id, COUNT(d.area_id)::INTEGER AS num_adjacencies, string_agg(d.area_id, '','')::VARCHAR AS adjacency_list
          FROM d
         GROUP BY d.area_id
)
SELECT e.*
  FROM e
  ORDER BY 1, 2;
  
Returns a table:

SELECT * FROM rif40_xml_pkg.rif40_GetAdjacencyMatrix(1) LIMIT 10;
     area_id     | num_adjacencies |                                                 adjacency_list
-----------------+-----------------+-----------------------------------------------------------------------------------------------------------------
 01.001.000100.1 |               7 | 01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1,01.001.000100.1
 01.001.000100.2 |               1 | 01.001.000100.2
 01.002.000300.1 |               3 | 01.002.000300.1,01.002.000300.1,01.002.000300.1
 01.002.000300.2 |               4 | 01.002.000300.2,01.002.000300.2,01.002.000300.2,01.002.000300.2
 01.002.000300.3 |               2 | 01.002.000300.3,01.002.000300.3
 01.002.000300.4 |               5 | 01.002.000300.4,01.002.000300.4,01.002.000300.4,01.002.000300.4,01.002.000300.4
 01.002.000300.5 |               1 | 01.002.000300.5
 01.002.000400.1 |               5 | 01.002.000400.1,01.002.000400.1,01.002.000400.1,01.002.000400.1,01.002.000400.1
 01.002.000400.2 |               3 | 01.002.000400.2,01.002.000400.2,01.002.000400.2
 01.002.000400.4 |               1 | 01.002.000400.4
(10 rows)';
	
--
-- Grants
--
GRANT EXECUTE ON FUNCTION rif40_xml_pkg.rif40_GetAdjacencyMatrix(INTEGER) TO rif40;
GRANT EXECUTE ON FUNCTION rif40_xml_pkg.rif40_GetAdjacencyMatrix(INTEGER) TO rif_user;
GRANT EXECUTE ON FUNCTION rif40_xml_pkg.rif40_GetAdjacencyMatrix(INTEGER) TO rif_manager;

--
-- Eof