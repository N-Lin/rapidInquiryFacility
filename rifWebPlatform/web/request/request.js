(function() {
   var c = function(myFunc, msg) {
         //IE compatible
         return function(error, status, json) {
            try {
               var data = jQuery.parseJSON(json.responseText);
               for (var key in data[0]) {
                  if (key == 'errorMessages') {
                     RIF.statusBar(data[0][key], true, 1);
                     return;
                  };
               };
               callback(myFunc, data);
            } catch (e) {
               callback(myFunc, json.responseText); // This should change when working on results viewer
            }
            RIF.statusBar(msg, false);
         };
      },
      asynCall = function() {
         var args = Array.prototype.slice.call(arguments, 0),
            callback = args[1],
            mime = args[2] || "text/plain",
            parameters = args[0] + '&userID=' + RIF.user,
            url = 'http://localhost:8080/rifServices/' + parameters;
         $.ajax({
            url: url
         }).done(callback).error(function(jqXHR, textStatus, errorThrown) {
            var msg = "Something went wrong with the following service: <br/>" + url + '<br/><br/>' + textStatus + '<br/>' + errorThrown;
            RIF.statusBar(msg, true, 1);
         });
      },
      callback = function(myFuncts, data) {
         if (myFuncts instanceof Array) {
            var l = myFuncts.length;
            while (l--) {
               myFuncts[l].call(data);
            }
            return;
         } else if (typeof myFuncts === 'function') {
            myFuncts.call(data)
         };
      },
      xhr = function(url, clbk, msg, mime) {
         RIF.statusBar(msg, true);
         asynCall(url, c(clbk, msg), mime);
      },
      requests = {
         getGeneralRequest: function(url, myCallback) {
            xhr(url, myCallback);
            return {};
         },
         getGeolevels: function(myCallback) {
            var msg = "Retrieving geolevels";
            xhr("getGeoAvailable.php", myCallback, msg);
         },
         getZoomIdentifiers: function(myCallback, params) {
            var msg = "Retrieving zoom identifiers",
               args = "?geolevel=" + params[0] + "&identifier=" + "code";
            xhr("getIdentifiers.php" + args, myCallback, msg);
         },
         getSingleFieldData: function(myCallback, params) {
            var dataTable = params[0],
               msg = "Retrieving values",
               args = "?geolevel=" + dataTable + "&identifier=" + params[1];
            xhr("getIdentifiers.php" + args, myCallback, msg);
         },
         getSingleFieldChoro: function(myCallback, params) {
            var dataTable = params[0],
               msg = "Retrieving choropleth fields",
               args = "?geolevel=" + dataTable + "&identifier=" + params[1];
            xhr("getSingleFieldChoro.php" + args, myCallback, msg);
         },
         getFields: function(myCallback, params) {
            var dataTable = params[0],
               msg = "Retrieving available fields",
               args = "?table=" + dataTable;
            xhr("getFieldsAvailable.php" + args, myCallback, msg);
         },
         getTableFields: function(myCallback, params) {
            var dataTable = params[0],
               msg = "Retrieving table fields",
               args = "?table=" + dataTable;
            xhr("getTableFieldsAvailable.php" + args, myCallback, msg);
         },
         getDataSetsAvailable: function(myCallback, params) {
            var msg = "Retrieving Data Sets available",
               args = "?geolevel=" + params[0];
            xhr("getDataSetsAvailable.php" + args, myCallback, msg);
         },
         getNumericFields: function(myCallback, params) {
            var dataTable = params[0],
               msg = "Retrieving numeric fields",
               args = "?geolevel=" + dataTable;
            xhr("getNumericFields.php" + args, myCallback, msg);
         },
         /* Map */
         getBounds: function(myCallback, params) {
            var msg = "Retrieving bounds",
               args = '?table=' + params[0] + '&id=' + params[1];
            xhr("getBounds.php" + args, myCallback, msg);
         },
         getFullExtent: function(myCallback, params) {
            var msg = "Retrieving full extent",
               args = '?table=' + params[0];
            xhr('getFullExtent.php' + args, myCallback, msg);
         },
         getTabularData: function(myCallback, params) {
            var dataTable = params[0],
               msg = "Retrieving data for table",
               args = '?table=' + dataTable + '&from=' + params[1] + '&to=' + params[2]
            if (typeof params[3] !== 'undefined') {
               var l = params[3].length;
               while (l--) {
                  args += '&fields[]=' + params[3][l];
               }
            };
            if (typeof params[4] !== 'undefined') {
               var l = params[4].length;
               while (l--) {
                  args += '&gids[]=' + params[4][l];
               }
            };
            xhr('getDataTable.php' + args, myCallback, msg);
         },
         getTableRows: function(myCallback, params) {
            var dataTable = params[0],
               msg = "Retrieving rows for table",
               args = '?table=' + dataTable;
            if (typeof params[1] !== 'undefined') {
               var l = params[1].length;
               while (l--) {
                  args += '&fields[]=' + params[1][l];
               }
            }
            if (typeof params[2] !== 'undefined') {
               var l = params[2].length;
               while (l--) {
                  args += '&gids[]=' + params[2][l];
               }
            }
            xhr('getTableRows.php' + args, myCallback, msg);
         },
         getAgeGroups: function(myCallback, params) {
            var msg = "Retrieving Age groups structure",
               args = '?geolevel=' + params[0];
            xhr('getAgeGroupsStructure.php' + args, myCallback, msg);
         },
         getFieldsStratifiedByAgeGroup: function(myCallback, params) {
            var msg = "Retrieving fields stratified by age group",
               args = '?geolevel=' + params[0] + 'theme=' + params[1];
            xhr('getFieldsStratifiedByAgeGroup.php' + args, myCallback, msg);
         },
         getPyramidData: function(myCallback, params) {
            var msg = "Retrieving age group data",
               args = '?geolevel=' + params[0] + '&field=' + params[1];
            //in case of a selection	
            if (typeof params[2] !== 'undefined') {
               var l = params[2].length;
               while (l--) {
                  args += '&gids[]=' + params[2][l];
               }
            };
            //in case of a year selection	
            if (typeof params[3] !== 'undefined') {
               args += '&year=' + params[3];
            };
            xhr('getPyramidData.php' + args, myCallback, msg, "text/csv");
         },
         getHistogramData: function(myCallback, params) {
            var msg = "Retrieving histogram data",
               args = '?geolevel=' + params[0] + '&field=' + params[1];
            //in case of a selection	
            if (typeof params[2] !== 'undefined') {
               var l = params[2].length;
               while (l--) {
                  args += '&gids[]=' + params[2][l];
               }
            };
            //in case of a year selection	
            if (typeof params[3] !== 'undefined') {
               args += '&year=' + params[3];
            };
            xhr('getHistogramData.php' + args, myCallback, msg, "text/csv");
         },
         dropDatatable: function() {
            var empty = function() {};
            xhr('dropDatatable.php', empty);
         },
         /*
          *
          * Disease Mapping specific
          *
          */
         getStudies: function(myCallback, params) {
            var msg = "Retrieving studies";
            xhr('getStudies.php', myCallback, msg);
         },
         getInvestigations: function(myCallback, params) {
            var msg = "Retrieving investigations for study:" + params[0],
               args = '?studyID=' + params[0];
            xhr('getInvestigations.php' + args, myCallback, msg);
         },
         getResultsSetAvailable: function(myCallback, params) {
            var msg = "Retrieving results set available",
               args = '?studyID=' + params[1] + '&investigationID=' + params[2];
            //in case of a year selection	
            if (typeof params[3] !== 'undefined') {
               args += '&year=' + params[3];
            };
            xhr('getResultsSetAvailable.php' + args, myCallback, msg, "text/csv");
         },
         getResultSet: function(myCallback, params) {
            var msg = "Retrieving data for line bivariate chart",
               args = '?resultSet=' + params[0] + '&studyID=' + params[1] + '&investigationID=' + params[2];
            //in case of a year selection	
            if (typeof params[3] !== 'undefined') {
               args += '&year=' + params[3];
            };
            xhr('getResultsSet.php' + args, myCallback, msg, "text/csv");
         },
         getMinMaxResultSet: function(myCallback, params) {
            var msg = "Retrieving Min and Max: " + params[0],
               args = '?resultSet=' + params[0] + '&studyID=' + params[1] + '&investigationID=' + params[2];
            //in case of a year selection	
            if (typeof params[3] !== 'undefined') {
               args += '&year=' + params[3];
            };
            xhr('getMinMaxResultSet.php' + args, myCallback, msg);
         },
         getRiskResults: function(myCallback, params) {
            var msg = "Retrieving risk results: " + params[0],
               /*result set name*/
               args = '?resultSet=' + params[0] + '&studyID=' + params[1] + '&investigationID=' + params[2];
            //in case of a year selection	
            if (typeof params[3] !== 'undefined') {
               args += '&year=' + params[3];
            };
            xhr('getRiskResults.php' + args, myCallback, msg, "text/csv");
         },
         getYearsAvailableForStudy: function(myCallback, params) {
            var msg = "Retrieving years available for study: " + params[0],
               args = '?studyID=' + params[0] + '&investigationID=' + params[1];
            xhr('getYearsAvailableForStudy.php' + args, myCallback, msg);
         },
         getGenderAvailableForStudy: function(myCallback, params) {
            var msg = "Retrieving gender available for study: " + params[0],
               args = '?studyID=' + params[0] + '&investigationID=' + params[1];
            xhr('getGenderAvailableForStudy.php' + args, myCallback, msg);
         },
         getInvestigationInfo: function(myCallback, params) {
            var msg = "Retrieving Investigation info for study: " + params[0] + " investigation:" + params[1],
               args = '?studyID=' + params[0] + '&investigationID=' + params[1];
            xhr('getInvestigationInfo.php' + args, myCallback, msg);
         },
         getResultFigures: function(myCallback, params) {
            var msg = "Retrieving info  for area: " + params[2],
               args = '?studyID=' + params[0] + '&investigationID=' + params[1] + '&gid=' + params[2] + '&gender=' + params[3] + '&year=' + params[4];
            xhr('getResultFigures.php' + args, myCallback, msg);
         },
         /*
          *
          * Disease submission specific
          *
          */
         getIsLoggedIn: (function(myCallback, params) {
            var msg = " Checking if already logged in.. ",
               userName = params[0],
               pw = params[1],
               args = 'userID=' + userName;
            xhr('studySubmission/isLoggedIn?' + args, myCallback, msg);
         }),
         getLogOut: (function(myCallback, params) {
            var msg = " Checking if already logged in.. ",
               userName = params[0],
               pw = params[1],
               args = 'userID=' + userName;
            xhr('studySubmission/logout?' + args, myCallback, msg);
         }),
         getLogIn: (function(myCallback, params) {
            var msg = "Logging in.. ",
               userName = params[0],
               pw = params[1],
               args = 'userID=' + userName + '&password=' + pw;
            xhr('studySubmission/login?' + args, myCallback, msg);
         }),
         getHealthThemes: function(myCallback, params) {
            var msg = "Retrieving Health Themes ",
               args = 'geographyName=SAHSU';
            xhr('studySubmission/getHealthThemes?' + args, myCallback, msg);
         },
         getNumeratorDenominator: function(myCallback, params) {
            var msg = "Retrieving Numerator denominator pairs ",
               args = 'geographyName=SAHSU&healthThemeDescription=' + params[0];
            xhr('studySubmission/getNumerator?' + args, myCallback, msg);
         },
         getSelectAt: function(myCallback, params) {
            var msg = "Retrieving geolevel resolutions ",
               args = 'geographyName=SAHSU';
            xhr('studySubmission/getGeoLevelSelectValues?' + args, myCallback, msg, 'application/json');
         },
         getResolutions: function(myCallback, params) {
            var msg = "Retrieving geolevel resolutions ",
               args = 'geoLevelSelectName=' + params[1] + '&geographyName=SAHSU';
            xhr('studySubmission/getGeoLevelViews?' + args, myCallback, msg, 'application/json');
         },
         getGeolevelSelect: function(myCallback, params) {
            var msg = "Retrieving Area ids for geolevel:" + params[0];
            args = 'geographyName=SAHSU&geoLevelSelectName=LEVEL2&geoLevelAreaName=Elliot',
            args += '&geoLevelToMapName=' + params[0];
            xhr('studySubmission/getMapAreas?' + args, myCallback, msg, 'application/json');
         },
         getHealthTaxonomy: function(myCallback, params) {
            var msg = "Retrieving taxonomy codes",
               args = '';
            xhr('studySubmission/getHealthCodeTaxonomies?' + args, myCallback, msg, 'application/json');
         },
         getTopLevelHealthCodes: function(myCallback, params) {
            var msg = "Retrieving top level health codes",
               args = 'healthCodeTaxonomyNameSpace=' + params[0];
            xhr('studySubmission/getTopLevelCodes?' + args, myCallback, msg, 'application/json');
         },
         getSubHealthCodes: function(myCallback, params) {
            var msg = "Retrieving sub level health codes",
               args = 'healthCode=' + params[1] + '&healthCodeNameSpace=' + params[0];
            xhr('studySubmission/getImmediateChildHealthCodes?' + args, myCallback, msg, 'application/json');
         },
         getYearRange: function(myCallback, params) {
            var msg = "Retrieving Years",
               numerator = params[0],
               args = 'geographyName=SAHSU&numeratorTableName=' + numerator;
            xhr('studySubmission/getYearRange?' + args, myCallback, msg, 'application/json');
         },
         getSexes: function(myCallback, params) {
            var msg = "Retrieving Genders",
               args = '';
            xhr('studySubmission/getSexes?' + args, myCallback, msg, 'application/json');
         },
         getAgeGroups: function(myCallback, params) {
            var msg = "Retrieving Age groups",
               numerator = params[0],
               args = 'geographyName=SAHSU&numeratorTableName=' + numerator;
            xhr('studySubmission/getAgeGroups?' + args, myCallback, msg, 'application/json');
         },
         getCovariates: function(myCallback, params) {
            var msg = "Retrieving Covariates",
               args = 'geographyName=SAHSU&geoLevelSelectName=LEVEL1&geoLevelToMapName=LEVEL3';
            xhr('studySubmission/getCovariates?' + args, myCallback, msg, 'application/json');
         },
         getSearchHealthCodes: function(myCallback, params) {
            var msg = "Searching health codes",
               taxonomy = params[0],
               searchTxt = params[1],
               args = 'nameSpace=' + taxonomy + '&searchText=' + params[1] + '&isCaseSensitive=false';
            xhr('studySubmission/getHealthCodesMatchingSearchText?' + args, myCallback, msg, 'application/json');
         }
      };
   RIF.extend(requests, RIF);
}());