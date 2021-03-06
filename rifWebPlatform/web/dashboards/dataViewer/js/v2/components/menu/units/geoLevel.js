RIF.menu.geoLevel = ( function() {

  var parent = this,

    _domObjects = {
      /* DOM elements */
      geolevels: $( '#geolevel select' ),
      zoomTo: $( '#zoomTo select' ),
      dataSet: $( '#dataSet select' ),
      avlbFlds: $( '' ),
    },

    /* events */
    _events = function() {
      _domObjects.zoomTo.on( 'change', function() {
        parent.facade.zoomTo( RIF.replaceAll( '_', ' ', this.value ) );
      } );

      _domObjects.dataSet.on( 'change', function() {
        parent.facade.addTabularData( this.value );
        parent.updateSettings( this.value );
      } );

      _domObjects.geolevels.on( 'change', function() {
        _p.setGeolevel( this.value );
        _p.getDataSets();
      } );
    },


    /* geolevel obj */
    _p = {

      initGeolevel: function() {
        _events();
        RIF.getGeolevels( _p.geolevelsClbk );
      },

      /* callbacks */
      geolevelsClbk: function() { // called once only
        if ( this.length > 0 ) {
          parent.dropDown( this, _domObjects.geolevels );
          _p.setGeolevel( this[ 0 ] ); //First geolevel in list
          _p.getDataSets();
        }
      },

      dataSetsClbk: function() {
        _p.setDataset( this[ 0 ] );
        parent.dropDown( this, _domObjects.dataSet );
        _p.updateOtherComponents();
      },

      getDataSets: function() {
        RIF.getDataSetsAvailable( _p.dataSetsClbk, [ _p.currentGeolvl ] );
      },

      updateOtherComponents: function() {
        parent.facade.addGeolevel( _p.currentGeolvl, _p.currentdataset );
        parent.facade.addTabularData( _p.currentdataset );
      },

      setGeolevel: function( geolvl ) {
        _p.currentGeolvl = geolvl;
      },

      setDataset: function( dataset ) {
        _p.currentdataset = dataset;
      },

      getGeolevel: function( geolvl ) {
        return _p.currentGeolvl;
      },

      getDataset: function( geolvl ) {
        return _p.currentdataset;
      },

      getGeoLevelDom: function( obj ) {
        return _domObjects[ obj ];
      },

    };

  _p.initGeolevel();

  return _p;
} );