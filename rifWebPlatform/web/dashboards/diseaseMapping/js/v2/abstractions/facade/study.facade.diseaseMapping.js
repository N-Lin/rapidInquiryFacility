RIF.study[ 'study-facade-diseaseMapping' ] = ( function() {

  var facade = {


    menusReady: function( currentSet ) {

      this.setCurrentMapField( this.getCurrentResultSet() );
      this.setCurrentlineBivariate( this.getCurrentResultSet() ); //First field in Current result set used to draw line bivariate

      this.addGeolevel();
      this.drawLineBivariateChart();

    },

    uHoverField: function( field ) {
      this.setCurrentMapField( field );
      this.fire( 'hoverFieldChangeApply', field );
    },

    //FIRERS
    addGeolevel: function() {
      var study = this.getCurrentStudy(),
        dataSet = this.getMapData(),
        field = this.getCurrentMapField();
      this.fire( 'addGeolevel', {
        geoLevel: study,
        dataset: dataSet,
        field: field
      } ); // retrieve geolevel, possibly a temp table or view with a name, i.e study_id_geom_userxx

    },

    changeAreaChartsSelection: function() {
      this.fire( 'changeAreaSelection', this.getResultSetsSelected() );
    },

    clearSelection: function() {
      this.setSelection( [] );
      this.fire( 'clearSelection', [] );
    },

    drawLineBivariateChart: function() {
      this.fire( 'drawLineBivariateChart', this.getCurrentlineBivariate() );
    },

    drawMultipleAreaCharts: function() {
      this.fire( 'drawMultipleAreaCharts', this.getResultSetsSelected() );
    },

    changeBasemap: function( url ) {
      this.setBaseMap( url );
      this.fire( 'changeBasemap', url );
    },

    changeTransparency: function( v ) {
      this.setTransparency( v );
      this.fire( 'changeTransparency', v );
    },

    changeMapStyle: function( s ) {
      this.fire( 'changeMapStyle', s );
    },


    //SuBSCRIBERS

    studyChanged: function( study ) {
      this.setCurrentStudy( study );
      this.updateStudyLabel();
    },

    investigationChanged: function( investigation ) {
      this.setCurrentInvestigation( investigation );
      this.updateInvestigationInfo();
      this.updateInvestigationLabel();
      this.fire( 'investigationReady', investigation );
    },

    resultSetChanged: function( resSet ) {
      this.setCurrentResultsSet( resSet );
    },

    resultSetsAvailableChanged: function( resSets ) {
      this.setCurrentResultSetsAvailable( resSets );
      this.fire( 'resultSetsReady', resSets );
    },

    yearChanged: function( yr ) {
      this.setYear( yr );
    },

    genderChanged: function( gender ) {
      this.setGender( gender );
      console.log( this )
    },


    uAreaSelection: function( params ) {
      var areasSelected = params[ 0 ],
        areaJustClicked = areasSelected[ areasSelected.length - 1 ];

      this.setSelection( areasSelected );
      this.updateResultFigures( areaJustClicked );
      this.fire( 'slctLineBivariateFromMap', areaJustClicked );
      this.fire( 'updateMultipleAreaChartsFromMap', areaJustClicked );
    },

    selectionFromAreaChartChange: function( args ) {
      /*args = { gid , resSet }*/
      var mapGid = "g" + args.gid;
      this.fire( 'slctMapAreaFromAreaChart', mapGid );
      this.fire( 'slctLineBivariateFromAreaChart', args );
    },

    enterClicked: function( args ) {
      var gidClicked = args.gid,
        selection = this.getSelection();

      selection.push( gidClicked );
      this.setSelection( selection );
      this.fire( 'selectFromEnterKeyDown', "g" + gidClicked );
    },

    areaChartSelectionChanged: function( resSetsChoice ) {
      if ( RIF.arraysEqual( resSetsChoice, this.getResultSetsSelected() ) ) {
        return;
      };

      this.setCurrentResultSetSelected( resSetsChoice );
      this.changeAreaChartsSelection();
    },

    areaChartKeyDown: function( incrementDecrement ) {
      this.fire( 'lineBivariateHighlighterStep', incrementDecrement );
    },

    areaChartBrushed: function( domain ) {
      this.fire( 'updateLineChartWithBrush', domain );
    },

    clearSelectionClicked: function() {
      this.clearSelection();
    },

    baseMapChanged: function( args ) {
      var url = args[ 0 ];
      if ( this.baseMap != url ) {
        this.changeBasemap( url );
      }
    },

    transparencyChanged: function( value ) {
      if ( this.transparency != value ) {
        this.changeTransparency( value );
      }
    },

    yearChanged: function( yr ) {
      this.setYear( yr );
    },

    genderChanged: function( gender ) {
      this.setGender( gender );
    },

    mapStyleChange: function( params ) {
      var fieldToMap = this.getCurrentResultSet(),
        mapStyle = RIF.extend( {
          field: fieldToMap
        }, params );
      this.changeMapStyle( mapStyle );

    }


  };


  return facade;
} );