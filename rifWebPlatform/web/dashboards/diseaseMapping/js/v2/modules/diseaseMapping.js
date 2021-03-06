/*
 *  The Manager module allows you to visualize raw data
 *  with maps , charts and tables.
 *  Data can be exported as .shp , csv , GEOJSON.
 *  The main purpose of this module is to set event handlers
 *  and  relations between the modules involved in Manager.
 *
 *  @components
 *      list of modules dynamically initialized
 *  @events
 *      list of events occurring within the manager module
 *      ->firer:
 *          objects that will fire the specific event
 *      ->subscriber
 *          objects that will handle the event fired
 *      ->method
 *          method which must be implemented in subscriber object
 */
RIF.diseaseMapping = ( function() {

  var _p = {

    components: {
      study: 'diseaseMapping',
      map: {
        studyType: 'diseaseMapping',
        layerType: 'tilesvg'
      },
      chart: {
        studyType: 'diseaseMapping',
        charts: [ 'lineBivariate', 'multipleAreaCharts' ]
      },
      menu: {
        studyType: 'diseaseMapping',
        menus: [ 'frontMapping', 'settings', 'choropleth', 'baseMap', 'transparency' ]
      },
      resizable: 'diseaseMapping'
    },

    events: {

      /**--STUDY OBJECT RELATED---**/

      clearSelectionClicked: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "clearSelectionClicked"
      },
      clearSelection: {
        subscribers: [ "map" /*, "chart" */ ],
        firer: [ "study" ],
        method: "clearSelection"
      },


      menusReady: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "menusReady"
      },


      addGeolevel: {
        subscribers: [ "map" ],
        firer: [ "study" ],
        method: "uGeolevel"
      },


      drawLineBivariateChart: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "updateLineBivariate"
      },


      drawMultipleAreaCharts: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "updateMultipleAreaCharts"
      },


      areaChartBrushed: {
        subscribers: [ "study" ],
        firer: [ "chart" ],
        method: "areaChartBrushed"
      },
      updateLineChartWithBrush: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "updateLineChartWithBrush"
      },

      areaChartKeyDown: {
        subscribers: [ "study" ],
        firer: [ "chart" ],
        method: "areaChartKeyDown"
      },

      lineBivariateHighlighterStep: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "lineBivariateHighlighterStep"
      },

      hoverFieldChange: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "uHoverField"
      },
      hoverFieldChangeApply: {
        subscribers: [ "map" ],
        firer: [ "study" ],
        method: "uHoverField"
      },


      selectionchange: {
        subscribers: [ "study" ],
        firer: [ "map" ],
        method: "uAreaSelection"
      },


      selectionFromAreaChartChange: {
        subscribers: [ "study" ],
        firer: [ "chart" ],
        method: "selectionFromAreaChartChange"
      },

      enterClicked: {
        subscribers: [ "study" ],
        firer: [ "chart" ],
        method: "enterClicked"
      },

      slctMapAreaFromAreaChart: {
        subscribers: [ "map" ],
        firer: [ "study" ],
        method: "slctMapAreaFromAreaChart"
      },

      updateMultipleAreaChartsFromMap: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "updateMultipleAreaChartsFromMap"
      },

      slctLineBivariateFromAreaChart: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "slctLineBivariateFromAreaChart"
      },

      slctLineBivariateFromMap: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "slctLineBivariateFromMap"
      },

      transparencyChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "transparencyChanged"
      },
      changeTransparency: {
        subscribers: [ "map" ],
        firer: [ "study" ],
        method: "changeTransparency"
      },


      baseMapChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "baseMapChanged"
      },
      changeBasemap: {
        subscribers: [ "map" ],
        firer: [ "study" ],
        method: "changeBasemap"
      },


      yearChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "yearChanged"
      },

      changeYear: {
        subscribers: [ "map", "chart" ],
        firer: [ "study" ],
        method: "changeYear"
      },

      genderChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "genderChanged"
      },

      changeGender: {
        subscribers: [ "map", "chart" ],
        firer: [ "study" ],
        method: "changeGender"
      },

      /**-- GET / SETTER --**/

      studyChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "studyChanged"
      },

      investigationChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "investigationChanged"
      },

      /*investigationReady: {
        subscribers: [ "map" ],// Map too
        firer: [ "study" ], 
        method: "investigationReady"
      },*/

      areaChartSelectionChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "areaChartSelectionChanged"
      },

      changeAreaSelection: {
        subscribers: [ "chart" ],
        firer: [ "study" ],
        method: "changeResultSetSelection"
      },

      resultSetsAvailableChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "resultSetsAvailableChanged"
      },

      resultSetChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "resultSetChanged"
      },

      yearChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "yearChanged"
      },

      genderChanged: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "genderChanged"
      },

      getStudy: {
        subscribers: [ "map", "chart" ],
        firer: [ "study" ],
        method: "getCurrentStudy"
      },


      //GETS
      getInvestigation: {
        subscribers: [ "map", "chart" ],
        firer: [ "study" ],
        method: "getCurrentInvestigation"
      },

      getResultSet: {
        subscribers: [ "map", "chart" ],
        firer: [ "study" ],
        method: "getCurrentResultSet"
      },

      getCurrentYear: {
        subscribers: [ "map", "chart" ],
        firer: [ "study" ],
        method: "getCurrentYear"
      },

      getCurrentGender: {
        subscribers: [ "map", "chart" ],
        firer: [ "study" ],
        method: "getCurrentGender"
      },

      /**-- END STUDY RELATED --**/

      scaleRangeReady: { // Not Sure what this is for, can probably delete it later.
        subscribers: [ "menu" ],
        firer: [ "map" ],
        method: "getScaleRange"
      },

      selectFromEnterKeyDown: {
        subscribers: [ "map" ],
        firer: [ "study" ],
        method: "selectFromEnterKeyDown"
      },

      zoomToExtent: {
        subscribers: [ "map" ],
        firer: [ "menu" ],
        method: "zoomToExtent"
      },

      zoomToArea: {
        subscribers: [ "map" ],
        firer: [ "study", "chart" ],
        method: "zoomTo"
      },

      mapStyleChange: {
        subscribers: [ "study" ],
        firer: [ "menu" ],
        method: "mapStyleChange"
      },

      changeMapStyle: {
        subscribers: [ "map" ],
        firer: [ "study" ],
        method: "uMapStyle"
      },

      editChoroplethBreaks: {
        subscribers: [ "map" ],
        firer: [ "menu" ],
        method: "editBreaks"
      },


      /*-- RESIZABLES --*/
      resizeMap: {
        subscribers: [ "map" ],
        firer: [ "resizable" ],
        method: "resizeMap"
      },

      addResizableChart: {
        subscribers: [ "resizable" ],
        firer: [ "chart" ],
        method: "rr_chart"
      },

      addResizableAreaCharts: {
        subscribers: [ "resizable" ],
        firer: [ "chart" ],
        method: "multipleAreaCharts"
      },

      resizeLineBivariateChart: {
        subscribers: [ "chart" ],
        firer: [ "resizable" ],
        method: "refreshLineBivariate"
      },

      resizeAreaCharts: {
        subscribers: [ "chart" ],
        firer: [ "resizable" ],
        method: "refreshMultipleArea"
      }

    },

    init: function() {
      RIF.dom();
      RIF.initComponents.call( this );
      RIF.addEvents.call( this );
    }

  };

  return {
    setUp: ( function( args ) {
      _p.init();
    }() )
  };

} );