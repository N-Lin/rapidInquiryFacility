RIF.menu.events = ( function() {

  $( ".modal_close" ).click( function() {
    $( ".overlay" ).hide();
  } );

  $( ".dropdown dt > div" ).click( function() {
    $( ".dropdown .palette" ).toggle();
  } );

  $( "#clearSelection" ).click( function() {
    _p.facade.clearMapTable();
  } );

  $( "#zoomExtent" ).click( function() {
    _p.facade.zoomToExtent();
  } );

} );