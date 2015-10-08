$(function() {
	$(".content_alert_button").hide();
	$(".alert_button").on("click",function(e) {
		 var text = $(this).find("div .content_alert_button").text();
		 if(!text||text==(''))text = "You must use a 'div' with class 'content_alert_button' inside your click tag, and inside div write the text to show here.";
		 bootbox.alert(text);
	 });	
	$(".title_html_dialog_button").hide();
	$(".content_html_dialog_button").hide();
	$(".html_dialog_button").on("click",function(e){
		var tit = $(this).find("p.title_html_dialog_button").text();
		if(!tit||tit==(''))tit = "You must use a 'p' tag with class 'title_html_dialog_button' inside your click tag, and inside p write the text to show here.";
		var content = $(this).find("div.content_html_dialog_button").html();
		if(!content||content==(''))content = "You must use a 'div' tag with class 'content_html_dialog_button' inside your click tag, and inside div write the html to show here.";
		bootbox.dialog({
			  title: tit,
			  message: content
		});
	});	
});
function showAlert(message){
	if(message)bootbox.alert(message);
}
function showDialog(tit,message){
	if(message && tit){
		bootbox.dialog({
			  title: tit,
			  message: message
		});
	}
}
function showError(tit,content){
	if(content && tit){
		bootbox.dialog({
			  title: tit,
			  message: '<div class="alert alert-danger"><i class="fa fa-ban"></i><small>'+content+'</small></div>'
		});
	}
}

function showConfirm(tit,message,call){
	bootbox.dialog({
		  message: message,
		  title: tit,
		  buttons: {
		    success: {
		      label: "Confirm",
		      className: "btn-primary",
		      callback: call
		    },
		    main: {
		      label: "Cancel",
		      className: "btn-default"		      
		    }
		  }
		});
}

function showClose(tit,message,call){
	bootbox.dialog({
		  message: message,
		  title: tit,
		  buttons: {
		    success: {
		      label: "Confirm",
		      className: "btn-primary",
		      callback: call
		    }
		  }
		});
}

function addLabel(div, message, error){	
	$(div).html('');
	$(div).css("font-size","14px");
	if(error)$(div).html('<div class="alert alert-danger"><i class="fa fa-ban"></i><small>'+message+'</small><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button></div>');
	else $(div).html('<div class="alert alert-danger><i class="fa fa-info"></i><small>'+message+'</small><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button></div>');
}
function cleanLabel(div){
	$(div).html('');
	$(div).removeClass();
}

function getUrl(){
	var url = window.location.protocol+ "//" + window.location.host + "/"
	var pathArray = window.location.pathname.split( '/' );
	var secondLevelLocation = pathArray[1];
	return url + secondLevelLocation+ "/";
}
function showLoading(){
	bootbox.noClose('<div style="text-align:center;height:180px" ><img style="height:60%"src="'+getUrl()+'images/cloud_loading.gif"><h4>This can take a few minutes</h4></div>')
	
}
function showLoadingUploading(){
	bootbox.noClose('<div style="text-align:center;height:180px" ><img style="height:60%"src="'+getUrl()+'images/cloud_loading.gif"><h4>This can take a few minutes</h4><h4 id="upload-count">0%</h4></div>')
}
function updateUploading(e){
	 var progressCount = $("#upload-count");
	 if(progressCount){
		 var percentComplete = (e.loaded / e.total) * 100;	
		 if(parseInt(percentComplete)>=100){
			 progressCount.text('Saving file in server...');
			 progressCount.css('font-size','14px');
			 progressCount.css('font-weight','600');
		 }else progressCount.text(parseInt(percentComplete)+'%');
	 }	 
}
function hideLoading(){
	bootbox.hideAll();
}