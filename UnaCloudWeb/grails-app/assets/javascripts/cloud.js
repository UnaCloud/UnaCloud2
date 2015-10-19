function activator(){
	var path = window.location.protocol+'//'+window.location.host+window.location.pathname;
	var current = $('a[href="'+path+'"]');
	if(current)current.parent().addClass('active');
	if(path.indexOf('services')>0)$('#services-tree').addClass('active');
	else if(path.indexOf('external')>0)$('#external-tree').addClass('active');
	else if(path.indexOf('admin')>0)$('#admin-tree').addClass('active');
	else if(path.indexOf('monitoring')>0)$('#monitoring-tree').addClass('active');
	else if(path.indexOf('config')>0)$('#config-tree').addClass('active');
}

