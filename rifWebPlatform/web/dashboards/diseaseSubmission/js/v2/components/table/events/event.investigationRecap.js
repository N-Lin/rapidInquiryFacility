RIF.table['event-investigationsRecap'] = (function(_dom) {
   var menuContext = this;
   $(_dom.container).on("click", _dom.removeInvestigation, function(aEvent) {
      var id = $(this).parent().attr('id');
      id = id.split('investigation');
      if (id.length == 2) {
         id = id[1];
         $(this).parent().remove();
         menuContext.proxy.removeInvestigationRow(id);
      }
   });
});