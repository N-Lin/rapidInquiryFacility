RIF.chart.line_bivariate = ( function() {
  
  var chart = this,
		
	  dat = null,	
  
      settings = {
        element: "rr_chart",
		id_field: "gid",
		x_field: "x_order",
        line_field: "srr",
        line_field_color: "#8DB6CD",
        cl_field: "llsrr",
        cu_field: "ulsrr",
		margin: {
	      top:10,
		  right: 0,
		  bottom: 0,
		  left: 30
		},
		
		dimensions: {
	      width: function(){ return $( '#rr_chart' ).width()},
		  height: function(){ return $( '#rr_chart' ).height()}
        }
      },
	  
	  _render = function( update ){
	    _clear();
	    RIF.chart.line_bivariate.d3renderer( settings, d3.csv.parse(data), update );
		chart.facade.addResizableChart();
	  },	
	
	  _clear = function() {
        $( '#rr_chart' ).empty();
      },
   
      _p = {
	    
        renderLineBivariate: function() {
		   _render( true );
       },
	   
	   updateLine_bivariate: function( sett ) {
          var callback = function() {
            //_setLineBivariateField( sett.field );
			data = this;
            _render( false );
          };
         //_p.setHistoSettings( sett );
          RIF.getResultSet( callback, [ /*type, studyId, invId /*year*/]);
	    }
		
	  };	


  return _p;

} );