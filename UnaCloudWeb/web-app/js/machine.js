$( document ).ready(function() {
	$('.tab-header-monitor a').click(function (e) {
		  e.preventDefault()
		  $('.tab-header-monitor').removeClass( "active" );
		  $(this).parent().addClass('active');
		  var id = $(this).parent().attr('data-id');
		  $('.tab-monitor').hide();
		  if(id=='metrics'){
			  $('#metrics').show();
		  }else{			  
			  $("#input-report").val(id);	  
			  $('#report').show();
		  }		 
	});
	$('.tab-monitor').hide();
	$('#metrics').show();
});