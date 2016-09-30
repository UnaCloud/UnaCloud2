function activator(){
	var path = window.location.protocol+'//'+window.location.host+window.location.pathname;
	var current = $('a[href="'+path+'"]');
	if(current)current.parent().addClass('active');
	if(path.indexOf('services')>0){
		$('#services-tree').addClass('active');
		if(path.indexOf('image')>0)$('#services-image-tree').addClass('active');
		else if(path.indexOf('cluster')>0)$('#services-cluster-tree').addClass('active');
		else if(path.indexOf('deployment')>0)$('#services-deployment-tree').addClass('active');
	}
	else if(path.indexOf('external')>0){
		$('#external-tree').addClass('active');
	}
	else if(path.indexOf('admin')>0){
		$('#admin-tree').addClass('active');
		if(path.indexOf('user')>0)$('#admin-user-tree').addClass('active');
		else if(path.indexOf('group')>0)$('#admin-group-tree').addClass('active');
		else if(path.indexOf('platform')>0)$('#admin-platform-tree').addClass('active');
		else if(path.indexOf('os')>0&&path.indexOf('os')>path.indexOf('admin'))$('#admin-os-tree').addClass('active');
		else if(path.indexOf('lab')>0)$('#admin-lab-tree').addClass('active');
	}
	else if(path.indexOf('config')>0){
		$('#config-tree').addClass('active');
		if(path.indexOf('variables')>0)$('#config-variables-tree').addClass('active');
		else if(path.indexOf('agent')>0)$('#config-agent-tree').addClass('active');
	}
}

