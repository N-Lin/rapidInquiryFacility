RIF.table.renderer = ( function() {

  var grid,
    loader,
    table = this,
    request = 0,
    onDataLoaded = new Slick.Event(),

    _p = RIF.mix( RIF.table.settings(), {

      initGrid: function() {

        request = 0;
        loader = new Slick.Data.RemoteModel( _p );

        _p.dataView = new Slick.Data.DataView( {
          inlineFilters: true
        } );

        grid = new Slick.Grid( ".dataLbl", _p.dataView, _p.gridCols, _p.toptions );
        grid.setSelectionModel( new Slick.RowSelectionModel() );

        _p.evts = RIF.table.events( grid, loader, _p.fireRowClick, _p.dataView );

      },

      setUpGrid: function() {

        var callback = function() {
          _p.setFields( this );
          _p.initGrid();
        };

        RIF.getTableFields( callback, [ _p.geolevel ] );
      },

      request: function( from, to ) { /*Called by slick.remotemodel.ensureData() */
        var params = [ _p.geolevel, from, to, _p.fields, _p.missing ];
        RIF.getTabularData( _p.render, params );
      },

      render: function( isMapClick ) {
        // this = data 
        isMapClick = isMapClick || false;
        if ( request === 0 ) {
          request++;
          _p.setItems( this );
          table.resize( _p.defaultSize );
        } else if ( isMapClick ) {
          _p.addSlctdRows( this );
        } else {
          _p.addRows( this );
        }

        onDataLoaded.notify( {
          from: 0,
          to: _p.nRows
        } );
      },

      setGeolevel: function( geolevel ) {
        _p.geolevel = geolevel;
      },

      setFields: function( fields ) {
        _p.fields = fields; // array of fields
        _p.gridCols = _p.formatCols( fields ); // used to render grid columns
      },

      addRows: function( rows ) {
        var l = rows.length,
          data = _p.getRows();

        while ( l-- ) {
          data.push( rows[ l ] );
        };

        _p.setItems( data );
      },

      addSlctdRows: function( rows ) {
        var l = rows.length,
          data = _p.getRows(),
          slctd = _p.getSelected(),
          indxShift = 0;

        while ( l-- ) {
          data.splice( 0, 0, rows[ l ] );
          indxShift++;
        };

        _p.setItems( data );
        _p.updateSelected( slctd, indxShift );
      },

      updateSelected: function( slctd, shift ) {
        var uSlctdIndxs = [],
          l = slctd.length;
        while ( l-- ) {
          uSlctdIndxs.push( slctd[ l ] + shift );
        };

        _p.setSelected( uSlctdIndxs );
      },

      setItems: function( data ) {
        _p.dataView.setItems( data );
        grid.updateRowCount();
        grid.render();
      },

      setSelected: function( indxs ) {
        _p.evts.stopRowChange = true;
        grid.setSelectedRows( indxs );
        grid.invalidateAllRows();
        grid.updateRowCount();
        grid.render();
        _p.evts.stopRowChange = false;

      },

      setDataView: function( data ) {
        _p.dataView = new Slick.Data.DataView( {
          inlineFilters: true
        } );
        _p.setItems( data );
      },

      formatCols: function( gridCols ) {
        var columns = [];
        for ( var i = 0; i < gridCols.length; i++ ) {
          columns[ i ] = {
            id: gridCols[ i ],
            name: gridCols[ i ],
            field: gridCols[ i ],
            minWidth: _p.minColumnWidth
          };
        }

        return columns;
      },

      mapToRows: function( ids ) {

        var rowsSlctd = _p.getIdsRowSelected(),
          newSelection = RIF.difference( rowsSlctd, ids );

        _p.currentSel = RIF.unique( ids, rowsSlctd );

        //var missing = _p.getMissingRows(_p.currentSel);

        if ( _p.currentSel.length > 0 ) {

          //_p.missing = _p.missing.concat(missing);

          var params = [ _p.geolevel, _p.fields, _p.currentSel ],
            callback = function() {
              _p.render.call( this, true );
              _p.selectRows( _p.currentSel );
            };

          RIF.getTableRows( callback, params );
          return;
        };

        _p.selectRows( newSelection );

      },

      getIdsRowSelected: function() {
        var s = _p.getSelected(),
          l = s.length,
          ids = [];

        while ( l-- ) {
          var row = _p.dataView.getItemByIdx( s[ l ] ),
            id = row.id.split( '_' )[ 0 ];
          ids.push( id );
        };

        return RIF.unique( ids );
      },

      selectRows: function( ids ) {
        var l = ids.length,
          indxs = _p.getSelected();
        while ( l-- ) {
          var rowIndex = 1; //llop through all rows representing a single area
          do {
            var id = ids[ l ] + "_" + rowIndex++,
              rowN = _p.dataView.getIdxById( id ),
              indx = indxs.indexOf( rowN );

            if ( typeof rowN === 'undefined' ) {
              break
            };

            if ( indx >= 0 ) {
              indxs.splice( indx, 1 );
            } else {
              indxs.splice( 0, 0, rowN );
            };
          } while ( typeof rowN !== 'undefined' );
        };

        _p.setSelected( indxs );
      },

      removeRows: function( slct ) {
        var l = ids.length,
          missing = [];
        while ( l-- ) {
          if ( typeof _p.getRowIndex( ids[ l ] ) === 'undefined' ) {
            if ( _p.missing.indexOf( ids[ l ] ) === -1 ) {
              missing.push( ids[ l ] );
            }
          }
        };

        return missing;
      },

      getMissingRows: function( ids ) {
        var l = ids.length,
          missing = [];
        while ( l-- ) {
          if ( typeof _p.getRowIndex( ids[ l ] ) === 'undefined' ) {
            if ( _p.missing.indexOf( ids[ l ] ) === -1 ) {
              missing.push( ids[ l ] );
            }
          }
        };

        return missing;
      },

      getRows: function() {
        return _p.dataView.getItems();
      },

      getSelected: function() {
        if ( typeof grid !== 'undefined' ) {
          return grid.getSelectedRows();
        };
        return null;
      },

      getRowIndex: function( id ) {
        id = id + '_1'; // at least one rowIndex must exist if row exist
        var idx = _p.dataView.getIdxById( id );
        return idx;
      },

      fireRowClick: function( rows ) {
        table.selectedRows = rows;
        table.facade.rowClicked( rows );
      },

      resize: function() {
        grid.resizeCanvas();
      }

    } );


  return _p;

} );